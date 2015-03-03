package br.com.plastecno.service.impl;

import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import br.com.plastecno.service.EstoqueService;
import br.com.plastecno.service.PedidoService;
import br.com.plastecno.service.constante.FormaMaterial;
import br.com.plastecno.service.constante.SituacaoPedido;
import br.com.plastecno.service.constante.TipoPedido;
import br.com.plastecno.service.dao.ItemEstoqueDAO;
import br.com.plastecno.service.dao.ItemReservadoDAO;
import br.com.plastecno.service.entity.Item;
import br.com.plastecno.service.entity.ItemEstoque;
import br.com.plastecno.service.entity.ItemPedido;
import br.com.plastecno.service.entity.ItemReservado;
import br.com.plastecno.service.entity.Material;
import br.com.plastecno.service.entity.Pedido;
import br.com.plastecno.service.exception.BusinessException;
import br.com.plastecno.service.impl.calculo.CalculadoraVolume;
import br.com.plastecno.util.StringUtils;
import br.com.plastecno.validacao.ValidadorInformacao;

@Stateless
public class EstoqueServiceImpl implements EstoqueService {
	@PersistenceContext(name = "plastecno")
	private EntityManager entityManager;

	private ItemEstoqueDAO itemEstoqueDAO;
	private ItemReservadoDAO itemReservadoDAO;

	@EJB
	private PedidoService pedidoService;

	private void calcularValorMedio(ItemEstoque itemCadastrado, ItemEstoque itemIncluido) {
		removerValoresNulos(itemCadastrado);
		removerValoresNulos(itemIncluido);

		final double valorEstoque = itemCadastrado.getQuantidade() * itemCadastrado.getPrecoMedio();
		final double valorItem = itemIncluido.getQuantidade() * itemIncluido.getPrecoMedio();
		final double quantidadeTotal = itemCadastrado.getQuantidade() + itemIncluido.getQuantidade();
		final double precoMedio = (valorEstoque + valorItem) / quantidadeTotal;

		final double ipiEstoque = itemCadastrado.getQuantidade() * itemCadastrado.getAliquotaIPI();
		final double ipiItem = itemIncluido.getQuantidade() * itemIncluido.getAliquotaIPI();
		final double ipiMedio = (ipiEstoque + ipiItem) / quantidadeTotal;

		final double icmsEstoque = itemCadastrado.getQuantidade() * itemCadastrado.getAliquotaICMS();
		final double icmsItem = itemIncluido.getQuantidade() * itemIncluido.getAliquotaICMS();
		final double icmsMedio = (icmsEstoque + icmsItem) / quantidadeTotal;

		itemCadastrado.setPrecoMedio(precoMedio);
		itemCadastrado.setAliquotaIPI(ipiMedio);
		itemCadastrado.setAliquotaICMS(icmsMedio);
		itemCadastrado.setQuantidade((int) quantidadeTotal);
	}

	@Override
	public void cancelarReservaEstoqueByIdPedido(Integer idPedido) throws BusinessException {
		removerItemReservadoByIdPedido(idPedido);
		reinserirItemPedidoEstoque(idPedido);
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public boolean contemItemPedidoReservado(Integer idPedido) {
		return itemReservadoDAO.pesquisarTotalItemPedidoReservado(idPedido) >= 1;
	}

	@Override
	public void devolverItemCompradoEstoqueByIdPedido(Integer idPedido) throws BusinessException {
		SituacaoPedido situacaoPedido = pedidoService.pesquisarSituacaoPedidoById(idPedido);
		if (!SituacaoPedido.COMPRA_RECEBIDA.equals(situacaoPedido)
				&& !SituacaoPedido.COMPRA_PENDENTE_RECEBIMENTO.equals(situacaoPedido)) {
			throw new BusinessException("A devolução é permitida apenas para os itens de pedido de compra já efetuados");
		}
		List<ItemPedido> listaItem = pedidoService.pesquisarItemPedidoByIdPedido(idPedido);
		for (ItemPedido itemPedido : listaItem) {
			if (!itemPedido.isRecebido()) {
				continue;
			}
			ItemEstoque itemEstoque = pesquisarItemCadastradoEstoque(itemPedido);

			if (itemEstoque != null) {
				Integer quantidadeEstoque = itemEstoque.getQuantidade();
				Integer quantidadePedido = itemPedido.getQuantidade();
				if (quantidadePedido > quantidadeEstoque) {
					throw new BusinessException(
							"O pedido No. "
									+ idPedido
									+ " contém item \""
									+ (itemPedido.isPeca() ? itemPedido.getDescricaoPeca() : itemPedido.getDescricao())
									+ "\" sendo devolvido com quantidade maior do que a quantidade existente em estoque. Isso inidica uma provável inconsistência entre estoque e compras.");
				}
				itemEstoque.setQuantidade(quantidadeEstoque - quantidadePedido);
				itemEstoqueDAO.alterar(itemEstoque);
			}
		}
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public void empacotarItemPedido(Integer idItemPedido) {
		itemReservadoDAO.removerByIdItemPedido(idItemPedido);
		Integer idPedido = pedidoService.pesquisarIdPedidoByIdItemPedido(idItemPedido);
		if (!contemItemPedidoReservado(idPedido)) {
			Pedido pedido = pedidoService.pesquisarPedidoById(idPedido);
			pedido.setSituacaoPedido(SituacaoPedido.EMPACOTADO);
		}
	}

	private ItemEstoque gerarItemEstoque(ItemPedido itemPedido) {

		ItemEstoque itemEstoque = new ItemEstoque();

		itemEstoque.setComprimento(itemPedido.getComprimento());
		itemEstoque.setDescricaoPeca(itemPedido.getDescricaoPeca());
		itemEstoque.setFormaMaterial(itemPedido.getFormaMaterial());
		itemEstoque.setMaterial(itemPedido.getMaterial());
		itemEstoque.setMedidaExterna(itemPedido.getMedidaExterna());
		itemEstoque.setMedidaInterna(itemPedido.getMedidaInterna());
		itemEstoque.setQuantidade(itemPedido.getQuantidade());
		itemEstoque.setPrecoMedio(itemPedido.getPrecoUnidade());
		return itemEstoque;
	}

	@PostConstruct
	public void init() {
		itemEstoqueDAO = new ItemEstoqueDAO(entityManager);
		itemReservadoDAO = new ItemReservadoDAO(entityManager);
	}

	@Override
	public Integer inserirItemEstoque(ItemEstoque itemEstoque) throws BusinessException {
		if (itemEstoque == null) {
			throw new BusinessException("Item de estoque nulo");
		}

		ValidadorInformacao.validar(itemEstoque);
		if (!itemEstoque.isPeca() && StringUtils.isNotEmpty(itemEstoque.getDescricaoPeca())) {
			throw new BusinessException("A descrição é apenas itens do tipo peças. Remova a descrição.");
		}
		CalculadoraVolume.validarVolume(itemEstoque);

		// Verificando se existe item equivalente no estoque, caso nao exista vamos
		// criar um novo.
		ItemEstoque itemCadastrado = pesquisarItemCadastradoEstoque(itemEstoque);

		boolean isNovo = itemCadastrado == null;
		if (isNovo) {
			return itemEstoqueDAO.inserir(itemEstoque).getId();
		}

		calcularValorMedio(itemCadastrado, itemEstoque);
		return itemEstoqueDAO.alterar(itemCadastrado).getId();
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public Integer inserirItemPedido(Integer idItemPedido) throws BusinessException {
		ItemPedido itemPedido = pedidoService.pesquisarItemPedido(idItemPedido);
		if (itemPedido == null) {
			throw new BusinessException("O item de pedido No: " + idItemPedido + " não existe no sistema");
		}
		Pedido pedido = itemPedido.getPedido();

		long qtdePendente = pedidoService.pesquisarTotalItemPendente(pedido.getId());
		if (qtdePendente <= 1) {
			pedido.setSituacaoPedido(SituacaoPedido.COMPRA_RECEBIDA);
		}
		itemPedido.setRecebido(true);

		return inserirItemEstoque(gerarItemEstoque(itemPedido));
	}

	private boolean isEquivalente(Double val1, Double val2) {
		if (val1 == null && val2 == null) {
			return true;
		}

		if ((val1 != null && val2 == null) || (val1 == null && val2 != null)) {
			return false;
		}
		final double tolerancia = 0.001;
		return Math.abs(1 - val1 / val2) <= tolerancia;
	}

	private ItemEstoque pesquisarItemCadastradoEstoque(Item filtro) {
		Integer idMaterial = filtro.getMaterial().getId();
		FormaMaterial formaMaterial = filtro.getFormaMaterial();
		Double medidaExt = filtro.getMedidaExterna();
		Double medidaInt = filtro.getMedidaInterna();
		Double comp = filtro.getComprimento();

		// Verificando se existe item equivalente no estoque, caso nao exista vamos
		// criar um novo.
		ItemEstoque itemCadastrado = null;
		if (filtro.isPeca()) {
			itemCadastrado = pesquisarItemEstoque(idMaterial, formaMaterial, filtro.getDescricaoPeca());
		} else {
			itemCadastrado = pesquisarItemEstoque(idMaterial, formaMaterial, medidaExt, medidaInt, comp);
		}
		return itemCadastrado;
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public List<ItemEstoque> pesquisarItemEstoque(Integer idMaterial, FormaMaterial formaMaterial) {
		return itemEstoqueDAO.pesquisarItemEstoque(idMaterial, formaMaterial, null);
	}

	@Override
	public ItemEstoque pesquisarItemEstoque(Integer idMaterial, FormaMaterial formaMaterial, Double medidaExterna,
			Double medidaInterna, Double comprimento) {
		List<ItemEstoque> listItem = pesquisarItemEstoque(idMaterial, formaMaterial);
		for (ItemEstoque item : listItem) {
			if (!isEquivalente(medidaExterna, item.getMedidaExterna())) {
				continue;
			}
			if (!isEquivalente(medidaInterna, item.getMedidaInterna())) {
				continue;
			}
			if (!isEquivalente(comprimento, item.getComprimento())) {
				continue;
			}
			return item;
		}
		return null;
	}

	@Override
	public ItemEstoque pesquisarItemEstoque(Integer idMaterial, FormaMaterial formaMaterial, String descricaoPeca) {
		List<ItemEstoque> listItem = itemEstoqueDAO.pesquisarItemEstoque(idMaterial, formaMaterial, descricaoPeca);
		if (listItem.isEmpty()) {
			return null;
		}
		if (listItem.size() > 1) {
			throw new IllegalStateException("Foi encontrado mais de um registro para o codigo de material \"" + idMaterial
					+ "\", foma de material \"" + formaMaterial.getDescricao() + "\" e descrição de peça \"" + descricaoPeca
					+ "\"");
		}
		return listItem.get(0);
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public ItemEstoque pesquisarItemEstoqueById(Integer idItemEstoque) {
		return itemEstoqueDAO.pesquisarById(idItemEstoque);
	}

	@SuppressWarnings("unchecked")
	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public List<Material> pesquisarMateriaEstoque(String sigla) {
		Query query = this.entityManager
				.createQuery("select distinct new Material(m.id, m.sigla, m.descricao) from ItemEstoque i inner join i.material m where m.sigla like :sigla order by m.sigla ");
		query.setParameter("sigla", "%" + sigla + "%");
		return query.getResultList();
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public void redefinirItemEstoque(ItemEstoque itemEstoque) throws BusinessException {
		ValidadorInformacao.validar(itemEstoque);
		CalculadoraVolume.validarVolume(itemEstoque);

		if (itemEstoque.isNovo()) {
			throw new BusinessException("Não é possivel realizar a redefinição de estoque para itens não existentes");
		}
		if (itemEstoque.isPeca() && StringUtils.isEmpty(itemEstoque.getDescricaoPeca())) {
			throw new BusinessException("Descrição da peca do item do pedido é obrigatório");
		}

		if (itemEstoque.isMedidaExternaIgualInterna()) {
			itemEstoque.setMedidaInterna(itemEstoque.getMedidaExterna());
		}

		ItemEstoque itemCadastrado = pesquisarItemEstoqueById(itemEstoque.getId());
		if (itemCadastrado == null) {
			throw new BusinessException("O item de codigo \"" + itemEstoque.getId()
					+ "\" nao exite no estoque para ser redefinido.");
		}

		itemCadastrado.setAliquotaICMS(itemEstoque.getAliquotaICMS());
		itemCadastrado.setAliquotaIPI(itemEstoque.getAliquotaIPI());
		itemCadastrado.setPrecoMedio(itemEstoque.getPrecoMedio());
		itemCadastrado.setQuantidade(itemEstoque.getQuantidade());
		if (!itemCadastrado.isPeca()) {
			itemCadastrado.setMedidaExterna(itemEstoque.getMedidaExterna());
			itemCadastrado.setMedidaInterna(itemEstoque.getMedidaInterna());
			itemCadastrado.setComprimento(itemEstoque.getComprimento());
		}

		itemEstoqueDAO.alterar(itemCadastrado);
	}

	private void reinserirItemPedidoEstoque(Integer idPedido) throws BusinessException {
		List<ItemPedido> listaItemPedido = pedidoService.pesquisarItemPedidoByIdPedido(idPedido);
		for (ItemPedido itemPedido : listaItemPedido) {
			inserirItemPedido(itemPedido.getId());
		}
	}

	private void removerItemReservadoByIdPedido(Integer idPedido) {
		List<ItemReservado> listaItem = itemReservadoDAO.pesquisarItemReservadoByIdPedido(idPedido);
		for (ItemReservado itemReservado : listaItem) {
			itemReservadoDAO.remover(itemReservado);
		}
	}

	private void removerValoresNulos(ItemEstoque itemEstoque) {
		if (itemEstoque.getQuantidade() == null) {
			itemEstoque.setQuantidade(0);
		}
		if (itemEstoque.getPrecoMedio() == null) {
			itemEstoque.setPrecoMedio(0d);
		}
		if (itemEstoque.getAliquotaIPI() == null) {
			itemEstoque.setAliquotaIPI(0d);
		}
		if (itemEstoque.getAliquotaICMS() == null) {
			itemEstoque.setAliquotaICMS(0d);
		}
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public boolean reservarItemPedido(Integer idPedido) throws BusinessException {
		Pedido pedido = pedidoService.pesquisarPedidoById(idPedido);
		if (!TipoPedido.REVENDA.equals(pedido.getTipoPedido())) {
			throw new BusinessException("O pedido não pode ter seus itens encomendados pois não é um pedido de revenda.");
		}
		if (!SituacaoPedido.DIGITACAO.equals(pedido.getSituacaoPedido())) {
			throw new BusinessException("O pedido não pode ter seus itens encomendados pois não esta em digitação.");
		}
		List<ItemPedido> listaItem = pedidoService.pesquisarItemPedidoByIdPedido(idPedido);
		boolean todosReservados = true;
		for (ItemPedido itemPedido : listaItem) {
			todosReservados &= reservarItemPedido(itemPedido);
		}
		pedido
				.setSituacaoPedido(todosReservados ? SituacaoPedido.EMPACOTAMENTO : SituacaoPedido.REVENDA_PENDENTE_ENCOMENDA);
		pedidoService.inserir(pedido);
		return todosReservados;
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public boolean reservarItemPedido(ItemPedido itemPedido) throws BusinessException {
		ItemEstoque itemEstoque = null;
		if (itemPedido.isPeca()) {
			itemEstoque = pesquisarItemEstoque(itemPedido.getMaterial().getId(), itemPedido.getFormaMaterial(),
					itemPedido.getDescricaoPeca());
		} else {
			itemEstoque = pesquisarItemEstoque(itemPedido.getMaterial().getId(), itemPedido.getFormaMaterial(),
					itemPedido.getMedidaExterna(), itemPedido.getMedidaInterna(), itemPedido.getComprimento());
		}
		Integer quantidadeReservada = 0;
		Integer quantidadePedido = itemPedido.getQuantidade();
		Integer quantidadeEstoque = itemEstoque != null ? itemEstoque.getQuantidade() : 0;

		boolean reservado = true;
		if (quantidadeEstoque <= 0) {
			reservado = false;
		} else if (quantidadePedido >= quantidadeEstoque) {
			quantidadeReservada = quantidadeEstoque;
		} else if (quantidadePedido < quantidadeEstoque) {
			quantidadeReservada = quantidadePedido;
		}
		
		itemPedido.setQuantidadeEncomenda(quantidadePedido - quantidadeReservada);
		pedidoService.inserirItemPedido(itemPedido);
		
		if (itemEstoque != null) {
			itemEstoqueDAO.alterar(itemEstoque);
			itemReservadoDAO.inserir(new ItemReservado(new Date(), itemEstoque, itemPedido));
		}

		return reservado;
	}
}
