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
import br.com.plastecno.service.constante.FormaMaterial;
import br.com.plastecno.service.constante.SituacaoPedido;
import br.com.plastecno.service.constante.SituacaoReservaEstoque;
import br.com.plastecno.service.constante.TipoPedido;
import br.com.plastecno.service.dao.ItemEstoqueDAO;
import br.com.plastecno.service.dao.ItemReservadoDAO;
import br.com.plastecno.service.dao.LimiteMinimoEstoqueDAO;
import br.com.plastecno.service.dao.PedidoDAO;
import br.com.plastecno.service.entity.Item;
import br.com.plastecno.service.entity.ItemEstoque;
import br.com.plastecno.service.entity.ItemPedido;
import br.com.plastecno.service.entity.ItemReservado;
import br.com.plastecno.service.entity.LimiteMinimoEstoque;
import br.com.plastecno.service.entity.Material;
import br.com.plastecno.service.entity.Pedido;
import br.com.plastecno.service.exception.BusinessException;
import br.com.plastecno.service.impl.anotation.REVIEW;
import br.com.plastecno.service.impl.anotation.TODO;
import br.com.plastecno.service.impl.anotation.WARNING;
import br.com.plastecno.service.impl.calculo.CalculadoraVolume;
import br.com.plastecno.util.StringUtils;
import br.com.plastecno.validacao.ValidadorInformacao;

@Stateless
public class EstoqueServiceImpl implements EstoqueService {
	@PersistenceContext(name = "plastecno")
	private EntityManager entityManager;

	private ItemEstoqueDAO itemEstoqueDAO;

	private ItemReservadoDAO itemReservadoDAO;

	private LimiteMinimoEstoqueDAO limiteMinimoEstoqueDAO;

	private PedidoDAO pedidoDAO;

	@EJB
	private PedidoService pedidoService;

	// Essa eh a tolerancia de 1mm
	private final double tolerancia = 0.01d;

	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public Integer associarLimiteMinimoEstoque(LimiteMinimoEstoque limite) throws BusinessException {
		boolean contemMedida = limite.getMedidaExterna() != null || limite.getMedidaInterna() != null
				|| limite.getComprimento() != null;

		if (!contemMedida) {
			throw new BusinessException("O limite minimo de estoque deve conter alguma medida, mas todas estão em branco");
		}

		LimiteMinimoEstoque limiteCadastrado = limiteMinimoEstoqueDAO.pesquisarLimiteMinimoEstoque(limite, tolerancia);
		if (limiteCadastrado != null) {
			limiteCadastrado.setQuantidadeMinima(limite.getQuantidadeMinima());
			limite = limiteCadastrado;
		}

		if (limite.getId() != null && !limite.contemQuantidadeMinima()) {
			limiteMinimoEstoqueDAO.remover(limite);
			limiteMinimoEstoqueDAO.desassociarLimiteMinimoItemEstoque(limite.getId());
			return -1;
		}
		return inserirLimiteMinimo(limite);
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public double calcularPrecoCustoItemEstoque(Item filtro) {
		if (filtro.getQuantidade() == null) {
			return 0;
		}

		final double precoMedio = pesquisarPrecoMedioItemEstoque(filtro);
		final double aliquotaIPI = filtro.getAliquotaIPI() == null ? 0 : filtro.getAliquotaIPI();
		return precoMedio * filtro.getQuantidade() * (1 + aliquotaIPI);
	}

	private void calcularValorMedio(ItemEstoque itemCadastrado, ItemEstoque itemIncluido) {
		removerValoresNulos(itemCadastrado);
		removerValoresNulos(itemIncluido);

		final boolean contemPrecoMedio = itemIncluido.getPrecoMedio() > 0d;
		final double quantidadeItem = contemPrecoMedio ? itemIncluido.getQuantidade() : 0;

		final double valorEstoque = itemCadastrado.getQuantidade() * itemCadastrado.getPrecoMedio();
		final double valorItem = itemIncluido.getQuantidade() * itemIncluido.getPrecoMedio();
		final double quantidadeTotal = itemCadastrado.getQuantidade() + quantidadeItem;
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
			if (!itemPedido.isRecebido()) {
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

	private ItemEstoque gerarItemEstoqueByIdItemPedido(Integer idItemPedido, boolean isRecepcaoItemCompra)
			throws BusinessException {
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

		// Aqui temos essa condicao pois o usuario pode incluir um item
		// diretamento
		// no estoque, sendo que ele nao passa pelo setor de compras.
		itemPedido.setRecebido(isRecepcaoItemCompra ? itemPedido.isTodasUnidadesRecepcionadas() : true);

		ItemEstoque itemEstoque = gerarItemEstoque(itemPedido);
		if (isRecepcaoItemCompra) {
			itemEstoque.setQuantidade(itemPedido.getQuantidadeRecepcionada() == null ? 0 : itemPedido
					.getQuantidadeRecepcionada());
		}

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
		limiteMinimoEstoqueDAO = new LimiteMinimoEstoqueDAO(entityManager);
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

		itemEstoque.configurarMedidaInterna();

		CalculadoraVolume.validarVolume(itemEstoque);

		// Verificando se existe item equivalente no estoque, caso nao exista
		// vamos
		// criar um novo.
		ItemEstoque itemCadastrado = pesquisarItemEstoque(itemEstoque);

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
		return inserirItemEstoque(gerarItemEstoqueByIdItemPedido(idItemPedido, false));
	}

	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	@WARNING(data = "06/07/2015", descricao = "Aqui nao esta funcionando a associacao entre o item do estoque e o limite minimo atraves do metodo ADD do limite. Tivemos que construir um update.")
	private Integer inserirLimiteMinimo(LimiteMinimoEstoque limite) throws BusinessException {
		if (limite == null) {
			throw new BusinessException("Limite minimo de estoque nulo");
		}
		ValidadorInformacao.validar(limite);

		boolean isNovo = limite.getId() == null;

		if (isNovo) {
			limite = limiteMinimoEstoqueDAO.inserir(limite);
		} else {
			limite = limiteMinimoEstoqueDAO.alterar(limite);
		}

		List<Integer> listaIdItemEstoque = limiteMinimoEstoqueDAO.pesquisarIdItemEstoqueDentroLimiteMinimo(limite,
				tolerancia);

		if (!listaIdItemEstoque.isEmpty()) {
			limiteMinimoEstoqueDAO.associarLimiteMinimoItemEstoque(limite.getId(), listaIdItemEstoque);
		}
		return limite.getId();
	}

	@Override
	public List<ItemEstoque> pesquisarEscassezItemEstoque() {
		return itemEstoqueDAO.pesquisarEscassezItemEstoque();
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public ItemEstoque pesquisarItemEstoque(Item filtro) {
		// Verificando se existe item equivalente no estoque, caso nao exista
		// vamos
		// criar um novo.
		ItemEstoque itemCadastrado = null;
		if (filtro.isPeca()) {
			itemCadastrado = itemEstoqueDAO.pesquisarPecaByDescricao(filtro.getMaterial().getId(), filtro.getDescricaoPeca());
		} else {
			itemCadastrado = itemEstoqueDAO.pesquisarItemEstoqueByMedida(tolerancia, filtro.getMaterial().getId(),
					filtro.getFormaMaterial(), filtro.getMedidaExterna(), filtro.getMedidaInterna(), filtro.getComprimento());
		}
		return itemCadastrado;
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public ItemEstoque pesquisarItemEstoqueById(Integer idItemEstoque) {
		return itemEstoqueDAO.pesquisarById(idItemEstoque);
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public List<ItemEstoque> pesquisarItemEstoqueNaoZerados(Integer idMaterial, FormaMaterial formaMaterial) {
		return itemEstoqueDAO.pesquisarItemEstoque(idMaterial, formaMaterial, null, true);
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
	public double pesquisarPrecoMedioItemEstoque(Item filtro) {
		ItemEstoque itemEstoque = pesquisarItemEstoque(filtro);
		return itemEstoque == null ? 0 : itemEstoque.getPrecoMedio();
	}

	@Override
	public Double pesquisarValorEstoque(Integer idMaterial, FormaMaterial formaMaterial) {
		return itemEstoqueDAO.pesquisarValorEQuantidadeItemEstoque(idMaterial, formaMaterial);
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public Integer recepcionarItemCompra(Integer idItemPedido) throws BusinessException {
		return inserirItemEstoque(gerarItemEstoqueByIdItemPedido(idItemPedido, true));
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public Integer recepcionarParcialmenteItemCompra(Integer idItemPedido, Integer quantidadeParcial)
			throws BusinessException {
		if (quantidadeParcial == null) {
			quantidadeParcial = 0;
		}
		Integer quantidadeRecepcionada = pedidoService.pesquisarQuantidadeRecepcionadaItemPedido(idItemPedido);
		quantidadeRecepcionada += quantidadeParcial;
		pedidoService.alterarQuantidadeRecepcionada(idItemPedido, quantidadeRecepcionada);

		ItemEstoque itemEstoque = gerarItemEstoqueByIdItemPedido(idItemPedido, true);
		itemEstoque.setQuantidade(quantidadeParcial);
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
		ValidadorInformacao.validar(itemEstoque);
		CalculadoraVolume.validarVolume(itemEstoque);

		if (itemEstoque.isNovo()) {
			throw new BusinessException("Não é possivel realizar a redefinição de estoque para itens não existentes");
		}
		if (itemEstoque.isPeca() && StringUtils.isEmpty(itemEstoque.getDescricaoPeca())) {
			throw new BusinessException("Descrição da peca do item do pedido é obrigatório");
		}

		itemEstoque.configurarMedidaInterna();

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

		// redefinirItemReservadoByItemEstoque(itemCadastrado.getId());
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
		if (itemPedido.isTodasUnidadesReservadas()) {
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
		pedidoService.inserirItemPedido(itemPedido);
		return situacao;
	}
}
