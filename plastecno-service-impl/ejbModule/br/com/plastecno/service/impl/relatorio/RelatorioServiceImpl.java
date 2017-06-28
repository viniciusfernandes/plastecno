package br.com.plastecno.service.impl.relatorio;

import java.util.Comparator;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import br.com.plastecno.service.ClienteService;
import br.com.plastecno.service.DuplicataService;
import br.com.plastecno.service.NFeService;
import br.com.plastecno.service.PedidoService;
import br.com.plastecno.service.RamoAtividadeService;
import br.com.plastecno.service.RepresentadaService;
import br.com.plastecno.service.UsuarioService;
import br.com.plastecno.service.constante.TipoPedido;
import br.com.plastecno.service.entity.Cliente;
import br.com.plastecno.service.entity.ItemPedido;
import br.com.plastecno.service.entity.NFeDuplicata;
import br.com.plastecno.service.entity.NFeItemFracionado;
import br.com.plastecno.service.entity.Pedido;
import br.com.plastecno.service.entity.Usuario;
import br.com.plastecno.service.exception.BusinessException;
import br.com.plastecno.service.impl.anotation.REVIEW;
import br.com.plastecno.service.nfe.constante.TipoSituacaoDuplicata;
import br.com.plastecno.service.relatorio.RelatorioService;
import br.com.plastecno.service.validacao.exception.InformacaoInvalidaException;
import br.com.plastecno.service.wrapper.ComissaoVendaWrapper;
import br.com.plastecno.service.wrapper.GrupoWrapper;
import br.com.plastecno.service.wrapper.Periodo;
import br.com.plastecno.service.wrapper.ReceitaWrapper;
import br.com.plastecno.service.wrapper.RelatorioValorTotalPedidoWrapper;
import br.com.plastecno.service.wrapper.RelatorioVendaVendedorByRepresentada;
import br.com.plastecno.service.wrapper.RelatorioWrapper;
import br.com.plastecno.service.wrapper.TotalizacaoPedidoWrapper;
import br.com.plastecno.service.wrapper.VendaClienteWrapper;
import br.com.plastecno.util.NumeroUtils;
import br.com.plastecno.util.StringUtils;

@Stateless
public class RelatorioServiceImpl implements RelatorioService {
	@EJB
	private ClienteService clienteService;

	@EJB
	private DuplicataService duplicataService;

	@PersistenceContext(name = "plastecno")
	private EntityManager entityManager;

	@EJB
	private NFeService nFeService;

	private Comparator<ItemPedido> ordenacaoItemPedido = new Comparator<ItemPedido>() {
		@Override
		public int compare(ItemPedido i1, ItemPedido i2) {
			return i1.getSequencial().compareTo(i2.getSequencial());
		}

	};

	@EJB
	private PedidoService pedidoService;

	@EJB
	private RamoAtividadeService ramoAtividadeService;

	@EJB
	private RepresentadaService representadaService;

	@EJB
	private UsuarioService usuarioService;

	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public ReceitaWrapper gerarReceitaEstimada(Periodo periodo) {
		double valorComprado = 0;
		double valorVendido = 0;
		double valorReceita = 0;
		double valorDebitoIPI = 0;
		double valorCreditoIPI = 0;
		double valorDebitoICMS = 0;
		double valorCreditoICMS = 0;
		double aliquota = 0;
		double precoItem = 0;
		double valorComissionado = 0;

		// Acumulando os valores dos itens comprados
		List<ItemPedido> listaItemComprado = pedidoService.pesquisarItemPedidoCompradoResumidoByPeriodo(periodo);
		for (ItemPedido itemPedido : listaItemComprado) {
			precoItem = itemPedido.calcularPrecoItem();

			aliquota = itemPedido.getAliquotaICMS() == null ? 0 : itemPedido.getAliquotaICMS();
			valorCreditoIPI += precoItem * aliquota;

			aliquota = itemPedido.getAliquotaIPI() == null ? 0 : itemPedido.getAliquotaIPI();
			valorCreditoIPI += precoItem * aliquota;

			valorComprado += precoItem;
		}

		// Acumulando os valores dos itens de revenda
		List<ItemPedido> listaItemVendido = pedidoService.pesquisarItemPedidoRevendaByPeriodo(periodo);
		for (ItemPedido itemPedido : listaItemVendido) {
			precoItem = itemPedido.calcularPrecoItem();
			valorVendido += precoItem;

			aliquota = itemPedido.getAliquotaICMS() == null ? 0 : itemPedido.getAliquotaICMS();
			valorDebitoICMS += precoItem * aliquota;

			aliquota = itemPedido.getAliquotaIPI() == null ? 0 : itemPedido.getAliquotaIPI();
			valorDebitoIPI += precoItem * aliquota;

			valorComissionado += itemPedido.getValorComissionado() == null ? 0 : itemPedido.getValorComissionado();
		}
		valorReceita = valorVendido;

		// Acumulando os valores dos itens de venda por representacao
		listaItemVendido = pedidoService.pesquisarItemPedidoRepresentacaoByPeriodo(periodo);

		for (ItemPedido itemPedido : listaItemVendido) {

			precoItem = itemPedido.getValorComissionado();
			valorVendido += precoItem;
			// valorComissionado += precoItem *
			// itemPedido.getAliquotaComissao();
		}

		valorReceita += valorVendido;

		double valorIPI = valorDebitoIPI - valorCreditoIPI;
		double valorICMS = valorDebitoICMS - valorCreditoICMS;
		double valorLiquido = valorReceita - valorIPI - valorICMS - valorComissionado;

		ReceitaWrapper receita = new ReceitaWrapper();
		receita.setValorCompradoFormatado(NumeroUtils.formatarValorMonetario(valorComprado));
		receita.setValorVendidoFormatado(NumeroUtils.formatarValorMonetario(valorVendido));
		receita.setValorCreditoICMSFormatado(NumeroUtils.formatarValorMonetario(valorCreditoICMS));
		receita.setValorDebitoICMSFormatado(NumeroUtils.formatarValorMonetario(valorDebitoICMS));
		receita.setValorCreditoIPIFormatado(NumeroUtils.formatarValorMonetario(valorCreditoIPI));
		receita.setValorDebitoIPIFormatado(NumeroUtils.formatarValorMonetario(valorDebitoIPI));
		receita.setValorICMSFormatado(NumeroUtils.formatarValorMonetario(valorICMS));
		receita.setValorIPIFormatado(NumeroUtils.formatarValorMonetario(valorIPI));
		receita.setValorComissionadoFormatado(NumeroUtils.formatarValorMonetario(valorComissionado));
		receita.setValorLiquidoFormatado(NumeroUtils.formatarValorMonetario(valorLiquido));
		receita.setValorReceitaFormatado(NumeroUtils.formatarValorMonetario(valorReceita));
		return receita;
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public RelatorioWrapper<String, Cliente> gerarRelatorioClienteRamoAtividade(Integer idRamoAtividade)
			throws BusinessException {

		if (idRamoAtividade == null) {
			throw new BusinessException("O ramo de atividade é obrigatório");
		}

		String sigla = ramoAtividadeService.pesquisarSigleById(idRamoAtividade);
		List<Cliente> listaCliente = clienteService.pesquisarClienteByIdRamoAtividade(idRamoAtividade);

		RelatorioWrapper<String, Cliente> relatorio = new RelatorioWrapper<String, Cliente>(
				"Relatório de Clientes com o ramo de atividades " + (sigla != null ? sigla : "\"Não definido\""));

		for (Cliente cl : listaCliente) {
			relatorio.addGrupo(cl.getNomeVendedor(), cl);
		}
		return relatorio;
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public List<Cliente> gerarRelatorioClienteVendedor(Integer idVendedor, boolean clienteInativo)
			throws BusinessException {
		return clienteService.pesquisarClienteCompradorByIdVendedor(idVendedor, clienteInativo);
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public RelatorioWrapper<Integer, ItemPedido> gerarRelatorioComissaoVendedor(Integer idVendedor, Periodo periodo) {

		StringBuilder titulo = new StringBuilder();
		titulo.append("Comissão do Vendedor de ").append(StringUtils.formatarData(periodo.getInicio())).append(" à ")
				.append(StringUtils.formatarData(periodo.getFim()));
		List<ItemPedido> listaItemPedido = pedidoService.pesquisarItemPedidoVendaByPeriodo(periodo, idVendedor);

		RelatorioWrapper<Integer, ItemPedido> relatorio = gerarRelatorioItensPorPedido(titulo.toString(),
				listaItemPedido, true);

		double valorTotalComissionado = 0;
		for (ItemPedido itemPedido : listaItemPedido) {
			valorTotalComissionado += itemPedido.getValorComissionado() == null ? 0 : itemPedido.getValorComissionado();
		}
		relatorio.setValorTotal(NumeroUtils.formatarValorMonetario(valorTotalComissionado));
		return relatorio;
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public RelatorioWrapper<Integer, ComissaoVendaWrapper> gerarRelatorioComissaoVendedores(Periodo periodo) {

		StringBuilder titulo = new StringBuilder();
		titulo.append("Comissão das Vendas de ").append(StringUtils.formatarData(periodo.getInicio())).append(" à ")
				.append(StringUtils.formatarData(periodo.getFim()));
		List<ItemPedido> listaItemPedido = pedidoService.pesquisarItemPedidoVendaResumidaByPeriodo(periodo);

		RelatorioWrapper<Integer, ComissaoVendaWrapper> relatorio = new RelatorioWrapper<Integer, ComissaoVendaWrapper>(
				titulo.toString());

		double valorComissionado = 0;
		double valorTotalComissionado = 0;
		ComissaoVendaWrapper comissao = null;
		String nomeVendedor = null;
		for (ItemPedido itemPedido : listaItemPedido) {
			nomeVendedor = itemPedido.getNomeProprietario() + " " + itemPedido.getSobrenomeProprietario();
			comissao = relatorio.getElemento(itemPedido.getIdProprietario());

			if (comissao == null) {
				comissao = new ComissaoVendaWrapper();
				comissao.setIdVendedor(itemPedido.getIdProprietario());
				comissao.setNomeVendedor(nomeVendedor);
				relatorio.addElemento(itemPedido.getIdProprietario(), comissao);
			}

			valorComissionado = itemPedido.getValorComissionado() == null ? 0 : itemPedido.getValorComissionado();
			valorTotalComissionado += valorComissionado;

			comissao.addPedido(itemPedido.getIdPedido());
			comissao.addValorComissionado(valorComissionado);
			comissao.addValorVendido(itemPedido.calcularPrecoItem());
		}

		for (ComissaoVendaWrapper c : relatorio.getListaElemento()) {
			c.setValorVendidoFormatado(NumeroUtils.formatarValorMonetario(c.getValorVendido()));
			c.setValorComissaoFormatado(NumeroUtils.formatarValorMonetario(c.getValorComissao()));
		}

		relatorio.setValorTotal(NumeroUtils.formatarValorMonetario(valorTotalComissionado));
		return relatorio;
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public List<Pedido> gerarRelatorioCompra(Periodo periodo) throws InformacaoInvalidaException {
		return this.pedidoService.pesquisarPedidoCompraByPeriodo(periodo);
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public RelatorioWrapper<Integer, ItemPedido> gerarRelatorioCompraAguardandoRecebimento(Integer idRepresentada,
			Periodo periodo) {
		RelatorioWrapper<Integer, ItemPedido> relatorio = gerarRelatorioItensPorPedido(
				"Pedidos de Compras para Recebimento",
				pedidoService.pesquisarCompraAguardandoRecebimento(idRepresentada, periodo), false);

		relatorio.addPropriedade("tipoPedido", TipoPedido.COMPRA);
		return relatorio;
	}

	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	private RelatorioWrapper<Date, NFeDuplicata> gerarRelatorioDuplicata(List<NFeDuplicata> lDuplic, String titulo)
			throws BusinessException {
		RelatorioWrapper<Date, NFeDuplicata> relatorio = new RelatorioWrapper<Date, NFeDuplicata>(titulo);
		Date dtAtual = new Date();
		for (NFeDuplicata d : lDuplic) {
			// Vamos definir a situação da duplicata
			if (!d.isLiquidado() && dtAtual.after(d.getDataVencimento())) {
				d.setTipoSituacaoDuplicata(TipoSituacaoDuplicata.VENCIDO);
			}
			relatorio.addGrupo(d.getDataVencimento(), d).setPropriedade("dataVencimentoFormatada",
					StringUtils.formatarData(d.getDataVencimento()));
		}

		relatorio.sortGrupo(new Comparator<GrupoWrapper<Date, NFeDuplicata>>() {

			@Override
			public int compare(GrupoWrapper<Date, NFeDuplicata> o1, GrupoWrapper<Date, NFeDuplicata> o2) {
				return o1.getId() != null && o1.getId() != null ? o2.getId().compareTo(o1.getId()) : 0;
			}
		});

		relatorio.sortElementoByGrupo(new Comparator<NFeDuplicata>() {

			@Override
			public int compare(NFeDuplicata o1, NFeDuplicata o2) {
				return o1.getNumeroNFe() != null && o2.getNumeroNFe() != null ? o2.getNumeroNFe().compareTo(
						o1.getNumeroNFe()) : 0;
			}
		});
		return relatorio;
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public RelatorioWrapper<Date, NFeDuplicata> gerarRelatorioDuplicata(Periodo periodo) throws BusinessException {
		if (periodo == null) {
			throw new BusinessException("Não é possível gerar relatório de duplicatas pois o período esta nulo.");
		}

		StringBuilder titulo = new StringBuilder();
		titulo.append("Duplicatas de ").append(StringUtils.formatarData(periodo.getInicio())).append(" à ")
				.append(StringUtils.formatarData(periodo.getFim()));

		return gerarRelatorioDuplicata(duplicataService.pesquisarDuplicataByPeriodo(periodo), titulo.toString());
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public RelatorioWrapper<Date, NFeDuplicata> gerarRelatorioDuplicataByIdPedido(Integer idPedido)
			throws BusinessException {
		if (idPedido == null) {
			throw new BusinessException("Não é possível gerar relatório de duplicatas pois número do pedido esta nulo.");
		}

		StringBuilder titulo = new StringBuilder();
		titulo.append("Duplicatas do pedido No. ").append(idPedido);

		return gerarRelatorioDuplicata(duplicataService.pesquisarDuplicataByIdPedido(idPedido), titulo.toString());
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public RelatorioWrapper<Date, NFeDuplicata> gerarRelatorioDuplicataByNumeroNFe(Integer numeroNFe)
			throws BusinessException {
		if (numeroNFe == null) {
			throw new BusinessException("Não é possível gerar relatório de duplicatas pois o número da NFe esta nulo.");
		}

		StringBuilder titulo = new StringBuilder();
		titulo.append("Duplicatas do pedido da NFe ").append(numeroNFe);

		return gerarRelatorioDuplicata(duplicataService.pesquisarDuplicataByNumeroNFe(numeroNFe), titulo.toString());
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public List<Pedido> gerarRelatorioEntrega(Periodo periodo) throws InformacaoInvalidaException {
		return pedidoService.pesquisarEntregaVendaByPeriodo(periodo);
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public RelatorioWrapper<Integer, ItemPedido> gerarRelatorioItemAguardandoCompra(Integer idCliente, Periodo periodo) {
		return gerarRelatorioItensPorPedido("Itens para Comprar",
				pedidoService.pesquisarItemAguardandoCompra(idCliente, periodo), false);
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public RelatorioWrapper<Integer, ItemPedido> gerarRelatorioItemAguardandoMaterial(Integer idRepresentada,
			Periodo periodo) {

		return gerarRelatorioItensPorPedido("Itens Aguardando Material",
				pedidoService.pesquisarItemAguardandoMaterial(idRepresentada, periodo), false);
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public RelatorioWrapper<Pedido, ItemPedido> gerarRelatorioItemPedidoByIdClienteIdVendedorIdFornecedor(
			Integer idCliente, Integer idVendedor, Integer idFornecedor, boolean isOrcamento, boolean isCompra,
			Integer indiceRegistroInicial, Integer numeroMaximoRegistros, ItemPedido itemVendido) {
		RelatorioWrapper<Pedido, ItemPedido> relatorio = new RelatorioWrapper<Pedido, ItemPedido>("");
		if (idCliente == null) {
			return relatorio;
		}

		if (idVendedor == null || usuarioService.isVendaPermitida(idCliente, idVendedor)) {
			List<ItemPedido> listaItemPedido = pedidoService.pesquisarItemPedidoByIdClienteIdVendedorIdFornecedor(
					idCliente, null, idFornecedor, isOrcamento, isCompra, indiceRegistroInicial, numeroMaximoRegistros,
					itemVendido);

			for (ItemPedido i : listaItemPedido) {
				relatorio.addGrupo(i.getPedido(), i);
			}

			relatorio.addPropriedade("totalPesquisado", pedidoService.pesquisarTotalPedidoByIdClienteIdFornecedor(
					idCliente, idFornecedor, isOrcamento, isCompra));
		}

		relatorio.sortGrupo(new Comparator<GrupoWrapper<Pedido, ItemPedido>>() {

			@Override
			public int compare(GrupoWrapper<Pedido, ItemPedido> o1, GrupoWrapper<Pedido, ItemPedido> o2) {
				Date d1 = o1.getId().getDataEnvio();
				Date d2 = o2.getId().getDataEnvio();

				return d1 != null && d2 != null ? d2.compareTo(d1) : 0;
			}
		});

		relatorio.sortElementoByGrupo(ordenacaoItemPedido);
		return relatorio;
	}

	@REVIEW(descricao = "Nem sempre eh necessario carregar as informacoes da representada")
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	private RelatorioWrapper<Integer, ItemPedido> gerarRelatorioItensPorPedido(String titulo,
			List<ItemPedido> listaItem, boolean isComissaoFormatado) {
		RelatorioWrapper<Integer, ItemPedido> relatorio = new RelatorioWrapper<Integer, ItemPedido>(titulo);
		for (ItemPedido item : listaItem) {

			item.setMedidaExternaFomatada(NumeroUtils.formatarValorMonetario(item.getMedidaExterna()));
			item.setMedidaInternaFomatada(NumeroUtils.formatarValorMonetario(item.getMedidaInterna()));
			item.setComprimentoFormatado(NumeroUtils.formatarValorMonetario(item.getComprimento()));
			item.setPrecoUnidadeFormatado(NumeroUtils.formatarValorMonetario(item.getPrecoUnidade()));
			item.setPrecoItemFormatado(NumeroUtils.formatarValorMonetario(item.calcularPrecoItem()));
			item.setPrecoCustoItemFormatado(NumeroUtils.formatarValorMonetario(item.getPrecoCusto()));

			if (isComissaoFormatado) {
				item.setAliquotaComissaoFormatado(NumeroUtils.formatarPercentual(item.getAliquotaComissao(), 2));
				item.setAliquotaComissaoRepresentadaFormatado(NumeroUtils.formatarPercentualInteiro(item
						.getAliquotaComissaoRepresentada()));

				item.setValorComissionadoFormatado(NumeroUtils.formatarValorMonetario(item.getValorComissionado()));
				item.setValorComissionadoRepresentadaFormatado(NumeroUtils.formatarValorMonetario(item
						.getValorComissionadoRepresentada()));
			}
			relatorio.addGrupo(item.getIdPedido(), item).setPropriedade("dataEntrega",
					StringUtils.formatarData(item.getDataEntrega()));
		}
		// Reordenando os itens pelo numero de sequencia de inclusao no pedido.
		relatorio.sortElementoByGrupo(ordenacaoItemPedido);
		return relatorio;
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public RelatorioWrapper<Integer, NFeItemFracionado> gerarRelatorioPedidoFracionado() {
		RelatorioWrapper<Integer, NFeItemFracionado> relatorio = new RelatorioWrapper<Integer, NFeItemFracionado>(
				"Relatório Pedido Fracionado NFe");
		List<NFeItemFracionado> lista = nFeService.pesquisarItemFracionado();
		for (NFeItemFracionado i : lista) {
			relatorio.addGrupo(i.getIdPedido(), i);
		}
		return relatorio;
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public RelatorioWrapper<Integer, ItemPedido> gerarRelatorioRevendaEmpacotamento(Integer idCliente) {
		return gerarRelatorioItensPorPedido("Pedidos de Revenda para Empacotar",
				pedidoService.pesquisarItemPedidoAguardandoEmpacotamento(idCliente), false);
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public RelatorioWrapper<Integer, TotalizacaoPedidoWrapper> gerarRelatorioValorTotalPedidoCompraPeriodo(
			Periodo periodo) throws BusinessException {

		final List<TotalizacaoPedidoWrapper> resultados = pedidoService.pesquisarTotalCompraResumidaByPeriodo(periodo);

		final StringBuilder titulo = new StringBuilder();
		titulo.append("Relatório das Compras do Período de ");
		titulo.append(StringUtils.formatarData(periodo.getInicio()));
		titulo.append(" à ");
		titulo.append(StringUtils.formatarData(periodo.getFim()));

		return gerarRelatorioValorTotalPedidoPeriodo(resultados, titulo.toString());
	}

	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	private RelatorioWrapper<Integer, TotalizacaoPedidoWrapper> gerarRelatorioValorTotalPedidoPeriodo(
			List<TotalizacaoPedidoWrapper> resultados, String titulo) throws BusinessException {

		RelatorioValorTotalPedidoWrapper relatorio = new RelatorioValorTotalPedidoWrapper(titulo);

		// Criando os agrupamentos e acumulando os valores totais dos pedidos.
		for (TotalizacaoPedidoWrapper totalizacao : resultados) {
			// Criando os agrupamentos pelo ID do proprietario do pedido.
			relatorio.addGrupo(totalizacao.getIdProprietario(), totalizacao);

			// Armazenando o valor negociado com cada representada para
			// efetuarmos a
			// totalizacao logo abaixo.
			relatorio.addElemento(totalizacao.getIdRepresentada(), totalizacao);

		}
		return relatorio.formatarValores();
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public RelatorioWrapper<Integer, TotalizacaoPedidoWrapper> gerarRelatorioValorTotalPedidoVendaPeriodo(
			Periodo periodo) throws BusinessException {

		final List<TotalizacaoPedidoWrapper> resultados = pedidoService
				.pesquisarTotalPedidoVendaResumidaByPeriodo(periodo);

		final StringBuilder titulo = new StringBuilder();
		titulo.append("Relatório das Vendas do Período de ");
		titulo.append(StringUtils.formatarData(periodo.getInicio()));
		titulo.append(" à ");
		titulo.append(StringUtils.formatarData(periodo.getFim()));

		return gerarRelatorioValorTotalPedidoPeriodo(resultados, titulo.toString());
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public List<Pedido> gerarRelatorioVenda(Periodo periodo) throws InformacaoInvalidaException {
		return this.pedidoService.pesquisarPedidoVendaByPeriodo(periodo);
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public RelatorioWrapper<String, TotalizacaoPedidoWrapper> gerarRelatorioVendaCliente(boolean orcamento,
			Periodo periodo, Integer idCliente) throws BusinessException {
		String nomeCliente = clienteService.pesquisarNomeFantasia(idCliente);

		final StringBuilder titulo = new StringBuilder(orcamento ? "Orçamento para " : "Vendas para ");
		if (StringUtils.isNotEmpty(nomeCliente)) {
			titulo.append("o Cliente ").append(nomeCliente).append(" ");
		} else {
			titulo.append("os Clientes ");
		}
		titulo.append(" de ").append(StringUtils.formatarData(periodo.getInicio())).append(" à ")
				.append(StringUtils.formatarData(periodo.getFim()));

		final RelatorioWrapper<String, TotalizacaoPedidoWrapper> relatorio = new RelatorioWrapper<String, TotalizacaoPedidoWrapper>(
				titulo.toString());

		List<TotalizacaoPedidoWrapper> listaPedido = pedidoService.pesquisarValorVendaClienteByPeriodo(periodo,
				idCliente, orcamento);

		double valorTotal = 0d;
		for (TotalizacaoPedidoWrapper totalizacao : listaPedido) {
			try {
				totalizacao.setValorTotalFormatado(NumeroUtils.formatarValorMonetario(totalizacao.getValorTotal()));
				relatorio.addGrupo(totalizacao.getNomeCliente(), totalizacao);
				valorTotal += totalizacao.getValorTotal();
			} catch (Exception e) {
				throw new BusinessException("Falha na geracao do relatorio de vendas para o cliente " + idCliente, e);
			}
		}
		relatorio.setValorTotal(NumeroUtils.formatarValorMonetario(valorTotal));
		return relatorio;
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public RelatorioVendaVendedorByRepresentada gerarRelatorioVendaVendedor(boolean orcamento, Periodo periodo,
			Integer idVendedor) throws BusinessException {
		Usuario vendedor = this.usuarioService.pesquisarVendedorById(idVendedor);

		if (vendedor == null) {
			throw new BusinessException("O vendedor é obrigatório para a geração do relatório");
		}

		final StringBuilder titulo = new StringBuilder(orcamento ? "Orçamento " : "Vendas ").append(" do Vendedor ")
				.append(vendedor.getNome()).append(" de ").append(StringUtils.formatarData(periodo.getInicio()))
				.append(" à ").append(StringUtils.formatarData(periodo.getFim()));

		final RelatorioVendaVendedorByRepresentada relatorio = new RelatorioVendaVendedorByRepresentada(
				titulo.toString());
		List<Pedido> listaPedido = this.pedidoService.pesquisarVendaByPeriodoEVendedor(orcamento, periodo, idVendedor);
		for (Pedido pedido : listaPedido) {
			try {
				pedido.setDataEnvioFormatada(StringUtils.formatarData(pedido.getDataEnvio()));
				relatorio.addRepresentada(pedido.getRepresentada().getNomeFantasia(), new VendaClienteWrapper(pedido));
			} catch (Exception e) {
				throw new BusinessException("Falha na geracao do relatorio de vendas do vendedor " + idVendedor, e);
			}
		}

		return relatorio;
	}
}
