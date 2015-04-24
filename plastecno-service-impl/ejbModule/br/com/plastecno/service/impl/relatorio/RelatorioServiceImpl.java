package br.com.plastecno.service.impl.relatorio;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import br.com.plastecno.service.ClienteService;
import br.com.plastecno.service.PedidoService;
import br.com.plastecno.service.RamoAtividadeService;
import br.com.plastecno.service.RepresentadaService;
import br.com.plastecno.service.UsuarioService;
import br.com.plastecno.service.entity.Cliente;
import br.com.plastecno.service.entity.Contato;
import br.com.plastecno.service.entity.ItemPedido;
import br.com.plastecno.service.entity.Pedido;
import br.com.plastecno.service.entity.RamoAtividade;
import br.com.plastecno.service.entity.Usuario;
import br.com.plastecno.service.exception.BusinessException;
import br.com.plastecno.service.impl.anotation.REVIEW;
import br.com.plastecno.service.relatorio.RelatorioService;
import br.com.plastecno.service.validacao.exception.InformacaoInvalidaException;
import br.com.plastecno.service.wrapper.ClienteWrapper;
import br.com.plastecno.service.wrapper.ComissaoVendaWrapper;
import br.com.plastecno.service.wrapper.Periodo;
import br.com.plastecno.service.wrapper.ReceitaWrapper;
import br.com.plastecno.service.wrapper.RelatorioClienteRamoAtividade;
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

	@PersistenceContext(name = "plastecno")
	private EntityManager entityManager;

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
		double valorComissionadoRepresentacao = 0;
		for (ItemPedido itemPedido : listaItemVendido) {
			precoItem = itemPedido.calcularPrecoItem();
			valorVendido += precoItem;
			valorComissionado += itemPedido.getValorComissionado();
			valorComissionadoRepresentacao += precoItem * itemPedido.getAliquotaComissaoPedido();
		}

		valorReceita += valorComissionadoRepresentacao;

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
	public RelatorioClienteRamoAtividade gerarRelatorioClienteRamoAtividade(Integer idRamoAtividade)
			throws BusinessException {

		if (idRamoAtividade == null) {
			throw new BusinessException("O ramo de atividade é obrigatório");
		}

		RamoAtividade ramoAtividade = this.ramoAtividadeService.pesquisarById(idRamoAtividade);
		List<Cliente> listaCliente = this.clienteService.pesquisarByRamoAtividade(idRamoAtividade);
		RelatorioClienteRamoAtividade relatorio = new RelatorioClienteRamoAtividade(
				"Relatório de Clientes com o ramo de atividades " + ramoAtividade.getSigla());

		StringBuilder descricaoContato = new StringBuilder();
		for (Cliente cliente : listaCliente) {

			if (cliente.isListaContatoPreenchida()) {
				Contato c = cliente.getContatoPrincipal();
				descricaoContato.append(c.getNome());

				if (StringUtils.isNotEmpty(c.getEmail())) {
					descricaoContato.append(" - ").append(c.getEmail());
				}

				if (StringUtils.isNotEmpty(c.getTelefone())) {
					descricaoContato.append(" - ").append(c.getTelefoneFormatado());
				}
			}

			relatorio.addCliente(new ClienteWrapper(cliente.getVendedor().getNomeCompleto(), cliente.getRazaoSocial(),
					descricaoContato.toString()));

			descricaoContato.delete(0, descricaoContato.length());
		}

		return relatorio;
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public RelatorioWrapper<Integer, ItemPedido> gerarRelatorioComissaoVendedor(Integer idVendedor, Periodo periodo) {

		StringBuilder titulo = new StringBuilder();
		titulo.append("Comissão do Vendedor de ").append(StringUtils.formatarData(periodo.getInicio())).append(" à ")
				.append(StringUtils.formatarData(periodo.getFim()));
		List<ItemPedido> listaItemPedido = pedidoService.pesquisarItemPedidoVendaByPeriodo(periodo, idVendedor);

		RelatorioWrapper<Integer, ItemPedido> relatorio = gerarRelatorioItensPorPedido(titulo.toString(), listaItemPedido);

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
	public List<Pedido> gerarRelatorioCompra(Periodo periodo) throws InformacaoInvalidaException {
		return this.pedidoService.pesquisarPedidoCompraByPeriodo(periodo);
	}

	@Override
	@REVIEW(data = "10/03/2015", descricao = "Devemos implementar uma melhoria o esquema de consulta dos itens de estoque para recuperar apenas a informacao necessaria.")
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public RelatorioWrapper<Integer, ItemPedido> gerarRelatorioCompraAguardandoRecebimento(Integer idRepresentada,
			Periodo periodo) {
		return gerarRelatorioItensPorPedido("Pedidos de Compras para Recebimento",
				pedidoService.pesquisarCompraAguardandoRecebimento(idRepresentada, periodo));
	}

	@Override
	public List<Pedido> gerarRelatorioEntrega(Periodo periodo) throws InformacaoInvalidaException {
		return pedidoService.pesquisarEntregaVendaByPeriodo(periodo);
	}

	@Override
	public RelatorioWrapper<Integer, ItemPedido> gerarRelatorioItemEncomenda(Integer idCliente, Periodo periodo) {
		/*
		 * TODO: devemos implementar uma melhoria o esquema de consulta dos itens de
		 * estoque para recuperar apenas a informacao necessaria.
		 */
		return gerarRelatorioItensPorPedido("Itens para Encomendar",
				pedidoService.pesquisarItemEncomenda(idCliente, periodo));
	}

	@REVIEW(descricao = "Nem sempre eh necessario carregar as informacoes da representada")
	private RelatorioWrapper<Integer, ItemPedido> gerarRelatorioItensPorPedido(String titulo, List<ItemPedido> listaItem) {
		/*
		 * TODO: devemos implementar uma melhoria o esquema de consulta dos itens de
		 * estoque para recuperar apenas a informacao necessaria.
		 */
		RelatorioWrapper<Integer, ItemPedido> relatorio = new RelatorioWrapper<Integer, ItemPedido>(titulo);
		Pedido pedido = null;
		for (ItemPedido item : listaItem) {
			pedido = item.getPedido();
			pedido.setRepresentada(pedidoService.pesquisarRepresentadaResumidaByIdPedido(pedido.getId()));

			item.setMedidaExternaFomatada(NumeroUtils.formatarValorMonetario(item.getMedidaExterna()));
			item.setMedidaInternaFomatada(NumeroUtils.formatarValorMonetario(item.getMedidaInterna()));
			item.setComprimentoFormatado(NumeroUtils.formatarValorMonetario(item.getComprimento()));
			item.setPrecoUnidadeFormatado(NumeroUtils.formatarValorMonetario(item.getPrecoUnidade()));
			item.setPrecoItemFormatado(NumeroUtils.formatarValorMonetario(item.calcularPrecoItem()));
			item.setAliquotaComissaoFormatado(NumeroUtils.formatarValorMonetario(item.getPercentualComissao()));
			item.setNomeProprietario(pedido.getProprietario().getNomeCompleto());
			item.setNomeRepresentada(pedido.getRepresentada().getNomeFantasia());
			item.setValorComissionadoFormatado(NumeroUtils.formatarValorMonetario(item.getValorComissionado()));
			item.setPrecoCustoItemFormatado(NumeroUtils.formatarValorMonetario(item.getPrecoCusto()));

			relatorio.addGrupo(pedido.getId(), item).setPropriedade("dataEntrega",
					StringUtils.formatarData(pedido.getDataEntrega()));
		}
		return relatorio;
	}

	@Override
	public RelatorioWrapper<Integer, ItemPedido> gerarRelatorioRevendaEmpacotamento(Integer idCliente, Periodo periodo) {
		/*
		 * TODO: devemos implementar uma melhoria o esquema de consulta dos itens de
		 * estoque para recuperar apenas a informacao necessaria.
		 */
		return gerarRelatorioItensPorPedido("Pedidos de Revenda para Empacotar",
				pedidoService.pesquisarRevendaEmpacotamento(idCliente, periodo));
	}

	@Override
	@REVIEW(data = "10/03/2015", descricao = "Devemos implementar uma melhoria o esquema de consulta dos itens de estoque para recuperar apenas a informacao necessaria.")
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public RelatorioWrapper<Integer, ItemPedido> gerarRelatorioRevendaEncomendada(Integer idRepresentada, Periodo periodo) {
		StringBuilder titulo = new StringBuilder();
		titulo.append("Itens Aguardando Material de ");
		titulo.append(StringUtils.formatarData(periodo.getInicio())).append(" à ");
		titulo.append(StringUtils.formatarData(periodo.getFim()));

		return gerarRelatorioItensPorPedido(titulo.toString(),
				pedidoService.pesquisarRevendaEncomendada(idRepresentada, periodo));
	}

	@Override
	public RelatorioWrapper<Integer, TotalizacaoPedidoWrapper> gerarRelatorioValorTotalPedidoCompraPeriodo(Periodo periodo)
			throws BusinessException {

		final List<TotalizacaoPedidoWrapper> resultados = pedidoService.pesquisarTotalCompraResumidaByPeriodo(periodo);

		final StringBuilder titulo = new StringBuilder();
		titulo.append("Relatório das Compras do Período de ");
		titulo.append(StringUtils.formatarData(periodo.getInicio()));
		titulo.append(" à ");
		titulo.append(StringUtils.formatarData(periodo.getFim()));

		return gerarRelatorioValorTotalPedidoPeriodo(resultados, titulo.toString());
	}

	private RelatorioWrapper<Integer, TotalizacaoPedidoWrapper> gerarRelatorioValorTotalPedidoPeriodo(
			List<TotalizacaoPedidoWrapper> resultados, String titulo) throws BusinessException {

		RelatorioValorTotalPedidoWrapper relatorio = new RelatorioValorTotalPedidoWrapper(titulo);

		// Criando os agrupamentos e acumulando os valores totais dos pedidos.
		for (TotalizacaoPedidoWrapper totalizacao : resultados) {
			// Criando os agrupamentos pelo ID do proprietario do pedido.
			relatorio.addGrupo(totalizacao.getIdProprietario(), totalizacao);

			// Armazenando o valor negociado com cada representada para efetuarmos a
			// totalizacao logo abaixo.
			relatorio.addElemento(totalizacao.getIdRepresentada(), totalizacao);

		}
		return relatorio.formatarValores();
	}

	@Override
	public RelatorioWrapper<Integer, TotalizacaoPedidoWrapper> gerarRelatorioValorTotalPedidoVendaPeriodo(Periodo periodo)
			throws BusinessException {

		final List<TotalizacaoPedidoWrapper> resultados = pedidoService.pesquisarTotalPedidoVendaResumidaByPeriodo(periodo);

		final StringBuilder titulo = new StringBuilder();
		titulo.append("Relatório das Vendas do Período de ");
		titulo.append(StringUtils.formatarData(periodo.getInicio()));
		titulo.append(" à ");
		titulo.append(StringUtils.formatarData(periodo.getFim()));

		return gerarRelatorioValorTotalPedidoPeriodo(resultados, titulo.toString());
	}

	@Override
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
		List<TotalizacaoPedidoWrapper> listaPedido = this.pedidoService.pesquisarValorVendaClienteByPeriodo(periodo,
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
	public RelatorioVendaVendedorByRepresentada gerarRelatorioVendaVendedor(boolean orcamento, Periodo periodo,
			Integer idVendedor) throws BusinessException {
		Usuario vendedor = this.usuarioService.pesquisarVendedorById(idVendedor);

		if (vendedor == null) {
			throw new BusinessException("O vendedor é obrigatório para a geração do relatório");
		}

		final StringBuilder titulo = new StringBuilder(orcamento ? "Orçamento " : "Vendas ").append(" do Vendedor ")
				.append(vendedor.getNome()).append(" de ").append(StringUtils.formatarData(periodo.getInicio())).append(" à ")
				.append(StringUtils.formatarData(periodo.getFim()));

		final RelatorioVendaVendedorByRepresentada relatorio = new RelatorioVendaVendedorByRepresentada(titulo.toString());
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

	@Override
	public List<Cliente> pesquisarClienteByIdVendedor(Integer idVendedor) {
		return this.clienteService.pesquisarByIdVendedor(idVendedor);
	}
}
