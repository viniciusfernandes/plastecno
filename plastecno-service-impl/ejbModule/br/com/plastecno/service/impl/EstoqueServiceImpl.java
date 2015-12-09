package br.com.plastecno.service.impl;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
import br.com.plastecno.service.RepresentadaService;
import br.com.plastecno.service.constante.FormaMaterial;
import br.com.plastecno.service.constante.SituacaoPedido;
import br.com.plastecno.service.constante.SituacaoReservaEstoque;
import br.com.plastecno.service.constante.TipoPedido;
import br.com.plastecno.service.dao.ItemEstoqueDAO;
import br.com.plastecno.service.dao.ItemPedidoDAO;
import br.com.plastecno.service.dao.ItemReservadoDAO;
import br.com.plastecno.service.dao.PedidoDAO;
import br.com.plastecno.service.entity.Item;
import br.com.plastecno.service.entity.ItemEstoque;
import br.com.plastecno.service.entity.ItemPedido;
import br.com.plastecno.service.entity.ItemReservado;
import br.com.plastecno.service.entity.Material;
import br.com.plastecno.service.entity.Pedido;
import br.com.plastecno.service.exception.BusinessException;
import br.com.plastecno.service.impl.anotation.REVIEW;
import br.com.plastecno.service.impl.anotation.TODO;
import br.com.plastecno.service.impl.calculo.CalculadoraVolume;
import br.com.plastecno.util.NumeroUtils;
import br.com.plastecno.util.StringUtils;
import br.com.plastecno.validacao.ValidadorInformacao;

@Stateless
public class EstoqueServiceImpl implements EstoqueService {
	@PersistenceContext(name = "plastecno")
	private EntityManager entityManager;

	private ItemEstoqueDAO itemEstoqueDAO;

	private ItemPedidoDAO itemPedidoDAO;

	private ItemReservadoDAO itemReservadoDAO;

	private PedidoDAO pedidoDAO;

	@EJB
	private PedidoService pedidoService;

	@EJB
	private RepresentadaService representadaService;

	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public double calcularPrecoCustoItemEstoque(Item item) {
		if (item.getQuantidade() == null) {
			return 0;
		}

		final double precoMedio = pesquisarPrecoMedioItemEstoque(item);
		final double aliquotaIPI = item.getAliquotaIPI() == null ? 0 : item.getAliquotaIPI();
		return precoMedio * item.getQuantidade() * (1 + aliquotaIPI);
	}

	private Double calcularPrecoMedioFatorICMS(Double aliquotaICMSRevendedor, Double aliquotaICMSItem, Double precoMedio) {
		if (precoMedio == null) {
			return 0d;
		}
		aliquotaICMSRevendedor = aliquotaICMSRevendedor != null ? aliquotaICMSRevendedor : 0d;
		aliquotaICMSItem = aliquotaICMSItem != null ? aliquotaICMSItem : 0d;

		double fatorICMS = aliquotaICMSRevendedor - aliquotaICMSItem;
		return precoMedio * (1 + fatorICMS);
	}

	private void calcularPrecoMedioFatorICMS(ItemEstoque itemEstoque) {
		if (itemEstoque.getPrecoMedio() == null) {
			return;
		}

		itemEstoque.setPrecoMedioFatorICMS(calcularPrecoMedioFatorICMS(
				representadaService.pesquisarAliquotaICMSRevendedor(), itemEstoque.getAliquotaICMS(),
				itemEstoque.getPrecoMedio()));
	}

	private void calcularPrecoMedioFatorICMS(List<ItemEstoque> listaItemEstoque) {
		if (listaItemEstoque == null || listaItemEstoque.isEmpty()) {
			return;
		}
		Double aliquotaICMSRevendedor = representadaService.pesquisarAliquotaICMSRevendedor();
		for (ItemEstoque itemEstoque : listaItemEstoque) {

			itemEstoque.setPrecoMedioFatorICMS(calcularPrecoMedioFatorICMS(aliquotaICMSRevendedor,
					itemEstoque.getAliquotaICMS(), itemEstoque.getPrecoMedio()));

		}
	}

	private void calcularPrecoMedioItemEstoque(ItemEstoque itemCadastrado, ItemEstoque itemEstoque) {
		if (itemCadastrado == null) {
			return;
		}
		removerValoresNulos(itemCadastrado);
		removerValoresNulos(itemEstoque);

		final boolean contemPrecoMedio = itemEstoque.getPrecoMedio() > 0d;
		final double quantidadeItem = contemPrecoMedio ? itemEstoque.getQuantidade() : 0;

		final double precoEstoque = itemCadastrado.getQuantidade()
				* (itemCadastrado.getPrecoMedio() != null ? itemCadastrado.getPrecoMedio() : 0d);

		final double precoEstoqueFatorICMS = itemCadastrado.getQuantidade()
				* (itemCadastrado.getPrecoMedioFatorICMS() != null ? itemCadastrado.getPrecoMedioFatorICMS() : 0d);

		final double precoItem = itemEstoque.getQuantidade() * itemEstoque.getPrecoMedio();
		final double precoItemFatorICMS = itemEstoque.getQuantidade() * itemEstoque.getPrecoMedioFatorICMS();

		final double quantidadeTotal = itemCadastrado.getQuantidade() + quantidadeItem;
		final double precoMedio = (precoEstoque + precoItem) / quantidadeTotal;
		final double precoMedioFatorICMS = (precoEstoqueFatorICMS + precoItemFatorICMS) / quantidadeTotal;

		final double ipiEstoque = itemCadastrado.getQuantidade() * itemCadastrado.getAliquotaIPI();
		final double ipiItem = itemEstoque.getQuantidade() * itemEstoque.getAliquotaIPI();
		final double ipiMedio = (ipiEstoque + ipiItem) / quantidadeTotal;

		final double icmsEstoque = itemCadastrado.getQuantidade() * itemCadastrado.getAliquotaICMS();
		final double icmsItem = itemEstoque.getQuantidade() * itemEstoque.getAliquotaICMS();
		final double icmsMedio = (icmsEstoque + icmsItem) / quantidadeTotal;

		itemCadastrado.setPrecoMedio(precoMedio);
		itemCadastrado.setPrecoMedioFatorICMS(precoMedioFatorICMS);
		itemCadastrado.setAliquotaIPI(ipiMedio);
		itemCadastrado.setAliquotaICMS(icmsMedio);
		itemCadastrado.setQuantidade((int) quantidadeTotal);
	}

	private Double calcularPrecoMinimo(Double precoMedio, Double margemMinimaLucro) {
		// Esse eh o algoritmo para o preco sugerido de venda de cada item do
		// estoque.
		if (precoMedio == null) {
			return null;
		}

		if (margemMinimaLucro == null) {
			margemMinimaLucro = 0.0;
		}

		// Precisamos arredondar
		return NumeroUtils.arredondarValorMonetario(precoMedio * (1 + margemMinimaLucro));
	}

	private void calcularPrecoMinimo(ItemEstoque itemEstoque) {
		Double preco = itemEstoque.getPrecoMedioFatorICMS() == null ? itemEstoque.getPrecoMedio() : itemEstoque
				.getPrecoMedioFatorICMS();
		itemEstoque.setPrecoMinimo(calcularPrecoMinimo(preco, itemEstoque.getMargemMinimaLucro()));
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public Double calcularPrecoMinimoItemEstoque(Item filtro) throws BusinessException {
		// Temos que pesquisar o ID pois o usuario pode estar inserindo um item novo
		// e ele pode nao existir no estoque ainda.
		Integer idItemEstoque = pesquisarIdItemEstoque(filtro);
		if (idItemEstoque == null) {
			return null;
		}

		Object[] valores = itemEstoqueDAO.pesquisarMargemMininaEPrecoMedio(idItemEstoque);
		Double margemMinimaLucro = (Double) valores[0];
		Double precoMedioFatorICMS = (Double) valores[1];
		Double precoMedio = (Double) valores[2];
		boolean contemFatorICMS = precoMedioFatorICMS != null;

		return calcularPrecoMinimo(contemFatorICMS ? precoMedioFatorICMS : precoMedio, margemMinimaLucro);
	}

	@Override
	public Double calcularValorEstoque(Integer idMaterial, FormaMaterial formaMaterial) {
		return NumeroUtils.arredondarValorMonetario(itemEstoqueDAO.pesquisarValorEQuantidadeItemEstoque(idMaterial,
				formaMaterial));
	}

	@Override
	public void cancelarReservaEstoqueByIdPedido(Integer idPedido) throws BusinessException {
		pedidoService.alterarSituacaoPedidoByIdPedido(idPedido, SituacaoPedido.CANCELADO);
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
				&& !SituacaoPedido.COMPRA_AGUARDANDO_RECEBIMENTO.equals(situacaoPedido)) {
			throw new BusinessException("A devolução é permitida apenas para os itens de pedido de compra já efetuados");
		}
		List<ItemPedido> listaItem = pedidoService.pesquisarItemPedidoByIdPedido(idPedido);
		for (ItemPedido itemPedido : listaItem) {
			if (!itemPedido.isItemRecebido()) {
				continue;
			}
			ItemEstoque itemEstoque = pesquisarItemEstoque(itemPedido);

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
	public void empacotarPedido(List<Integer> listaIdPedido) {
		if (listaIdPedido == null || listaIdPedido.isEmpty()) {
			return;
		}

		List<Integer> listaIdItemPedido = null;
		for (Integer idPedido : listaIdPedido) {
			listaIdItemPedido = pedidoService.pesquisarIdItemPedidoByIdPedido(idPedido);

			for (Integer idItemPedido : listaIdItemPedido) {

				itemReservadoDAO.removerByIdItemPedido(idItemPedido);
				pedidoService.alterarQuantidadeReservadaByIdItemPedido(idItemPedido);

				if (!contemItemPedidoReservado(idPedido)) {
					pedidoService.alterarSituacaoPedidoByIdPedido(idPedido, SituacaoPedido.EMPACOTADO);
				}
			}
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
		itemEstoque.setAliquotaIPI(itemPedido.getAliquotaIPI());
		itemEstoque.setAliquotaICMS(itemPedido.getAliquotaICMS());
		return itemEstoque;
	}

	private ItemEstoque gerarItemEstoqueByIdItemPedido(Integer idItemPedido) throws BusinessException {
		ItemPedido itemPedido = pedidoService.pesquisarItemPedido(idItemPedido);
		if (itemPedido == null) {
			throw new BusinessException("O item de pedido No: " + idItemPedido + " não existe no sistema");
		}

		SituacaoPedido situacaoPedido = pedidoService.pesquisarSituacaoPedidoByIdItemPedido(idItemPedido);
		if (!SituacaoPedido.COMPRA_AGUARDANDO_RECEBIMENTO.equals(situacaoPedido)) {
			throw new BusinessException("Não é possível gerar um item de estoque pois a situacao do pedido é \""
					+ situacaoPedido.getDescricao() + "\" e deve ser apenas \""
					+ SituacaoPedido.COMPRA_AGUARDANDO_RECEBIMENTO.getDescricao() + "\"");
		}

		ItemEstoque itemEstoque = gerarItemEstoque(itemPedido);

		Pedido pedido = itemPedido.getPedido();
		long qtdePendente = pedidoService.pesquisarTotalItemCompradoNaoRecebido(pedido.getId());
		if (qtdePendente <= 0) {
			pedido.setSituacaoPedido(SituacaoPedido.COMPRA_RECEBIDA);
		}
		return itemEstoque;
	}

	@PostConstruct
	public void init() {
		itemEstoqueDAO = new ItemEstoqueDAO(entityManager);
		itemReservadoDAO = new ItemReservadoDAO(entityManager);
		pedidoDAO = new PedidoDAO(entityManager);
		itemPedidoDAO = new ItemPedidoDAO(entityManager);
	}

	@Override
	public Integer inserirItemEstoque(ItemEstoque itemEstoque) throws BusinessException {
		if (itemEstoque == null) {
			throw new BusinessException("Item de estoque nulo");
		}

		if (itemEstoque.getPrecoMedioFatorICMS() == null) {
			itemEstoque.setPrecoMedioFatorICMS(itemEstoque.getPrecoMedio());
		}

		ValidadorInformacao.validar(itemEstoque);

		if (!itemEstoque.isPeca() && StringUtils.isNotEmpty(itemEstoque.getDescricaoPeca())) {
			throw new BusinessException("A descrição é apenas itens do tipo peças. Remova a descrição.");
		}

		itemEstoque.configurarMedidaInterna();

		CalculadoraVolume.validarVolume(itemEstoque);

		// Verificando se existe item equivalente no estoque, caso nao exista
		// vamos criar um novo.
		ItemEstoque itemCadastrado = pesquisarItemEstoque(itemEstoque);
		calcularPrecoMedioItemEstoque(itemCadastrado, itemEstoque);

		if (itemCadastrado == null) {
			itemCadastrado = itemEstoque;
		}

		calcularPrecoMedioFatorICMS(itemCadastrado);

		return itemEstoqueDAO.alterar(itemCadastrado).getId();
	}

	@Override
	@Deprecated
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public Integer inserirItemPedido(Integer idItemPedido) throws BusinessException {
		return inserirItemEstoque(gerarItemEstoqueByIdItemPedido(idItemPedido));
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public void inserirLimiteMinimoPadrao(ItemEstoque limite) throws BusinessException {
		if (limite.getMaterial() == null || limite.getMaterial().getId() == null || limite.getFormaMaterial() == null) {
			throw new BusinessException("Forma item e material são obrigatórios para a criação do limite mínimo de estoque");
		}

		if (limite.getQuantidadeMinima() != null && limite.getQuantidadeMinima() <= 0) {
			limite.setQuantidadeMinima(null);
		}

		if (limite.getMargemMinimaLucro() != null && limite.getMargemMinimaLucro() <= 0) {
			limite.setMargemMinimaLucro(null);
		}

		itemEstoqueDAO.inserirLimiteMinimoEstoque(limite);
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public Integer pesquisarIdItemEstoque(Item filtro) {
		ItemEstoque itemCadastrado = null;
		if (filtro.isPeca()) {
			itemCadastrado = itemEstoqueDAO.pesquisarPecaByDescricao(filtro.getMaterial().getId(), filtro.getDescricaoPeca(),
					true);
		} else {
			itemCadastrado = itemEstoqueDAO.pesquisarItemEstoqueByMedida(filtro.getMaterial().getId(),
					filtro.getFormaMaterial(), filtro.getMedidaExterna(), filtro.getMedidaInterna(), filtro.getComprimento(),
					true);
		}
		return itemCadastrado == null ? null : itemCadastrado.getId();
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public List<ItemEstoque> pesquisarItemEstoque(Integer idMaterial, FormaMaterial formaMaterial) {
		List<ItemEstoque> listaItem = itemEstoqueDAO.pesquisarItemEstoque(idMaterial, formaMaterial, null, true);
		for (ItemEstoque itemEstoque : listaItem) {
			calcularPrecoMinimo(itemEstoque);
		}
		return listaItem;
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public ItemEstoque pesquisarItemEstoque(Item filtro) {
		// Verificando se existe item equivalente no estoque, caso nao exista vamos
		// criar um novo.
		ItemEstoque itemCadastrado = null;
		if (filtro.isPeca()) {
			itemCadastrado = itemEstoqueDAO.pesquisarPecaByDescricao(filtro.getMaterial().getId(), filtro.getDescricaoPeca(),
					false);
		} else {
			itemCadastrado = itemEstoqueDAO.pesquisarItemEstoqueByMedida(filtro.getMaterial().getId(),
					filtro.getFormaMaterial(), filtro.getMedidaExterna(), filtro.getMedidaInterna(), filtro.getComprimento(),
					false);
		}
		return itemCadastrado;
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public ItemEstoque pesquisarItemEstoqueById(Integer idItemEstoque) {
		return itemEstoqueDAO.pesquisarById(idItemEstoque);
	}

	@Override
	public List<ItemEstoque> pesquisarItemEstoqueEscasso() {
		List<ItemEstoque> listaItem = itemEstoqueDAO.pesquisarItemEstoqueEscasso();
		for (ItemEstoque itemEstoque : listaItem) {
			calcularPrecoMinimo(itemEstoque);
		}
		return listaItem;
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
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public List<ItemEstoque> pesquisarPecaByDescricao(String descricao) {
		return itemEstoqueDAO.pesquisarItemEstoque(null, FormaMaterial.PC, descricao);
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public Double pesquisarPrecoMedioByIdItemEstoque(Integer idItemEstoque) {
		return (Double) itemEstoqueDAO.pesquisarMargemMininaEPrecoMedio(idItemEstoque)[2];
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public double pesquisarPrecoMedioItemEstoque(Item filtro) {
		ItemEstoque itemEstoque = pesquisarItemEstoque(filtro);
		return itemEstoque == null ? 0 : itemEstoque.getPrecoMedio();
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public void reajustarPrecoItemEstoque(ItemEstoque itemEstoque) throws BusinessException {
		if (itemEstoque == null || itemEstoque.getAliquotaReajuste() == null || itemEstoque.getAliquotaReajuste() == 0) {
			throw new BusinessException("A aliquota é obrigatória para o reajuste de precos de item de estoque.");
		}

		if (itemEstoque.getId() == null
				&& (itemEstoque.getFormaMaterial() == null || itemEstoque.getMaterial() == null || itemEstoque.getMaterial()
						.getId() == null)) {
			throw new BusinessException(
					"É necessário a forma do materia e o tipo do material para efetuar os reajuste de um grupo de itens");
		}

		FormaMaterial formaMaterial = itemEstoque.getFormaMaterial();
		Integer idMaterial = itemEstoque.getMaterial() != null ? itemEstoque.getMaterial().getId() : null;

		List<ItemEstoque> listaItem = itemEstoqueDAO.pesquisarPrecoMedioAliquotaICMSItemEstoque(itemEstoque.getId(),
				idMaterial, formaMaterial);
		for (ItemEstoque item : listaItem) {

			// Reajustando o preco medio do item
			item.setPrecoMedio(item.getPrecoMedio() * (1 + itemEstoque.getAliquotaReajuste()));
		}

		calcularPrecoMedioFatorICMS(listaItem);
		itemEstoqueDAO.alterarPrecoMedioFatorICMS(listaItem);

	}

	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public Integer recepcionarItemCompra(Integer idItemPedido, Integer quantidadeRecepcionada) throws BusinessException {
		if (quantidadeRecepcionada == null) {
			quantidadeRecepcionada = 0;
		}
		Integer quantidadeItem = pedidoService.pesquisarQuantidadeRecepcionadaItemPedido(idItemPedido);
		quantidadeItem += quantidadeRecepcionada;
		pedidoService.alterarQuantidadeRecepcionada(idItemPedido, quantidadeItem);

		ItemEstoque itemEstoque = gerarItemEstoqueByIdItemPedido(idItemPedido);
		itemEstoque.setQuantidade(quantidadeRecepcionada);

		itemEstoque.setPrecoMedio(itemEstoque.calcularPrecoUnidadeIPI());
		/*
		 * O fator de correcao de icms deve ser gravado durante a inclusao do item
		 * no estoque para que seja utilizado no calculo do preco minimo de venda
		 * posteriormente, pois existe um esquema de debito/credito de icms em cada
		 * item do estoque que deve ser repassado para o cliente.
		 */
		calcularPrecoMedioFatorICMS(itemEstoque);
		return inserirItemEstoque(itemEstoque);
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public Integer recortarItemEstoque(ItemEstoque itemRecortado) throws BusinessException {
		if (itemRecortado.isPeca()) {
			throw new BusinessException("Não é possível recortar uma peça do estoque");
		}
		ItemEstoque itemEstoque = pesquisarItemEstoqueById(itemRecortado.getId());
		if (itemEstoque == null) {
			throw new BusinessException("O item \""
					+ (itemRecortado.isPeca() ? itemRecortado.getDescricaoPeca() : itemRecortado.getDescricao())
					+ "\" não existe no estoque e não pode ser recortado");
		}

		if (itemRecortado.getMedidaExterna() > itemEstoque.getMedidaExterna()) {
			throw new BusinessException("Não é possível que a medida externa recortada seja maior do que a medida no estoque");
		}

		if (itemRecortado.contemLargura() && itemRecortado.getMedidaInterna() > itemEstoque.getMedidaInterna()) {
			throw new BusinessException("Não é possível que a medida interna recortada seja maior do que a medida no estoque");
		}

		if (itemRecortado.getComprimento() > itemEstoque.getComprimento()) {
			throw new BusinessException(
					"Não é possível que o comprimento recortado seja maior do que o comprimento no estoque");
		}

		Integer quantidadeEstoque = itemEstoque.getQuantidade() - itemRecortado.getQuantidade();

		if (quantidadeEstoque < 0) {
			throw new BusinessException("A quantidade recortada não pode ser superior a quantidade em estoque");
		}

		return null;
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public void redefinirItemEstoque(ItemEstoque itemEstoque) throws BusinessException {
		if (itemEstoque.isNovo()) {
			throw new BusinessException("Não é possivel realizar a redefinição de estoque para itens não existentes");
		}

		itemEstoque.configurarMedidaInterna();

		ItemEstoque itemCadastrado = pesquisarItemEstoqueById(itemEstoque.getId());
		if (itemCadastrado == null) {
			throw new BusinessException("O item de codigo \"" + itemEstoque.getId()
					+ "\" nao exite no estoque para ser redefinido.");
		}

		boolean isPrecoRedefinido = itemCadastrado.getPrecoMedio() != null
				&& !itemCadastrado.getPrecoMedio().equals(itemEstoque.getPrecoMedio());

		// Temos que igualar o preco do fator com icms pois aqui nao temos como
		// recuperar o fator icms para recalcular o preco medio com esse fator.
		if (isPrecoRedefinido || itemCadastrado.getPrecoMedioFatorICMS() == null) {
			itemCadastrado.setPrecoMedioFatorICMS(itemEstoque.getPrecoMedio());
		}

		// Aqui estamos forcando a copia dos atributos para garantir que um item
		// nunca ser alterado, por exemplo, as medidas nunca podem mudar.
		itemCadastrado.copiar(itemEstoque);

		ValidadorInformacao.validar(itemCadastrado);

		calcularPrecoMedioFatorICMS(itemCadastrado);

		itemEstoqueDAO.alterar(itemCadastrado);
		// redefinirItemReservadoByItemEstoque(idItemEstoque);
	}

	@TODO(data = "14/04/2015", descricao = "Esse metodo sera utilizado na execucao do metodo de redefinao de itens do estoque redefinirItemEstoque")
	private void redefinirItemReservadoByItemEstoque(Integer idItemEstoque) throws BusinessException {

		List<ItemReservado> listaItemReservado = itemReservadoDAO.pesquisarItemReservadoByIdItemEstoque(idItemEstoque);

		Set<Integer> listaIdPedido = new HashSet<Integer>();
		for (ItemReservado itemReservado : listaItemReservado) {
			itemReservadoDAO.remover(itemReservado);
			listaIdPedido.add(pedidoService.pesquisarIdPedidoByIdItemPedido(itemReservado.getItemPedido().getId()));
		}

		// Apos a remocao das reservas, estamos supondo que o estoque ja foi
		// redefinido, assim devemos tentar reservar os itens novamente,
		// consequentemente alteraremos os estado do pedido para revenda com
		// pendencia, e enfim, o setor de comprar podera monitorar os pedidos
		// novamente.
		for (Integer idPedido : listaIdPedido) {
			reservarItemPedido(idPedido);
		}
	}

	private void reinserirItemPedidoEstoque(Integer idPedido) throws BusinessException {
		List<ItemPedido> listaItemPedido = pedidoService.pesquisarItemPedidoByIdPedido(idPedido);
		ItemEstoque itemEstoque = null;
		for (ItemPedido itemPedido : listaItemPedido) {
			itemEstoque = pesquisarItemEstoque(itemPedido);
			if (itemEstoque != null) {
				itemEstoque.addQuantidade(itemPedido.getQuantidade());
				itemEstoqueDAO.alterar(itemEstoque);
			}
		}
	}

	private void removerItemReservadoByIdPedido(Integer idPedido) {
		List<ItemReservado> listaItem = itemReservadoDAO.pesquisarItemReservadoByIdPedido(idPedido);
		for (ItemReservado itemReservado : listaItem) {
			itemReservadoDAO.remover(itemReservado);
		}
	}

	private void removerValoresNulos(ItemEstoque itemEstoque) {
		if (itemEstoque == null) {
			return;
		}

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

	@REVIEW(data = "27/06/2015", descricao = "Na ultima linha do metodo estamos persistindo as alteracoes do pedido atraves do servico e esta consumindo muito tempo, podemos alterar para o pedidoDAO.")
	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public boolean reservarItemPedido(Integer idPedido) throws BusinessException {
		Pedido pedido = pedidoService.pesquisarPedidoById(idPedido);
		if (!TipoPedido.REVENDA.equals(pedido.getTipoPedido())) {
			throw new BusinessException("O pedido não pode ter seus itens encomendados pois não é um pedido de revenda.");
		}
		if (!SituacaoPedido.DIGITACAO.equals(pedido.getSituacaoPedido())
				&& !SituacaoPedido.ITEM_AGUARDANDO_MATERIAL.equals(pedido.getSituacaoPedido())
				&& !SituacaoPedido.ITEM_AGUARDANDO_COMPRA.equals(pedido.getSituacaoPedido())) {
			throw new BusinessException(
					"O pedido esta na situação de \""
							+ pedido.getSituacaoPedido().getDescricao()
							+ "\" e não pode ter seus itens encomendados pois não esta em digitação e não é revenda pendente de encomenda.");
		}
		List<ItemPedido> listaItem = pedidoService.pesquisarItemPedidoByIdPedido(idPedido);
		boolean todosReservados = true;
		SituacaoReservaEstoque situacaoReserva = null;
		for (ItemPedido itemPedido : listaItem) {
			situacaoReserva = reservarItemPedido(itemPedido);
			todosReservados &= SituacaoReservaEstoque.UNIDADES_TODAS_RESERVADAS.equals(situacaoReserva);
		}
		pedido.setSituacaoPedido(todosReservados ? SituacaoPedido.REVENDA_AGUARDANDO_EMPACOTAMENTO
				: SituacaoPedido.ITEM_AGUARDANDO_COMPRA);

		pedidoDAO.alterar(pedido);
		return todosReservados;
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public SituacaoReservaEstoque reservarItemPedido(ItemPedido itemPedido) throws BusinessException {
		if (itemPedido.isItemReservado()) {
			return SituacaoReservaEstoque.UNIDADES_TODAS_RESERVADAS;
		}

		ItemEstoque itemEstoque = pesquisarItemEstoque(itemPedido);

		Integer quantidadeReservada = 0;
		Integer quantidadePedido = itemPedido.contemAlgumaReserva() ? itemPedido.getQuantidadeEncomendada() : itemPedido
				.getQuantidade();
		Integer quantidadeEstoque = itemEstoque != null ? itemEstoque.getQuantidade() : 0;

		SituacaoReservaEstoque situacao = null;
		if (quantidadeEstoque <= 0) {
			situacao = SituacaoReservaEstoque.NAO_CONTEM_ESTOQUE;
		} else if (quantidadePedido > quantidadeEstoque) {
			quantidadeReservada = quantidadeEstoque;
			situacao = SituacaoReservaEstoque.UNIDADES_PARCIALEMENTE_RESERVADAS;
		} else if (quantidadePedido <= quantidadeEstoque) {
			quantidadeReservada = quantidadePedido;
			situacao = SituacaoReservaEstoque.UNIDADES_TODAS_RESERVADAS;
		}

		if (quantidadeEstoque > 0) {
			itemEstoque.setQuantidade(quantidadeEstoque - quantidadeReservada);
			itemEstoqueDAO.alterar(itemEstoque);

			if (!itemPedido.contemAlgumaReserva()) {
				itemReservadoDAO.inserir(new ItemReservado(new Date(), itemEstoque, itemPedido));
			}
		}

		itemPedido.addQuantidadeReservada(quantidadeReservada);
		itemPedidoDAO.alterar(itemPedido);
		return situacao;
	}
}
