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
import br.com.plastecno.service.wrapper.Periodo;
import br.com.plastecno.service.wrapper.RelatorioClienteRamoAtividade;
import br.com.plastecno.service.wrapper.RelatorioPedidoPeriodo;
import br.com.plastecno.service.wrapper.RelatorioVendaVendedorByRepresentada;
import br.com.plastecno.service.wrapper.RelatorioWrapper;
import br.com.plastecno.service.wrapper.RepresentadaValorWrapper;
import br.com.plastecno.service.wrapper.VendaClienteWrapper;
import br.com.plastecno.service.wrapper.exception.AgrupamentoException;
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
	public RelatorioPedidoPeriodo gerarRelatorioCompraPeriodo(Periodo periodo) throws BusinessException {

		final List<Object[]> resultados = pedidoService.pesquisarTotalCompraResumidaByPeriodo(periodo);

		final StringBuilder titulo = new StringBuilder();
		titulo.append("Relatório das Compras do Período de ");
		titulo.append(StringUtils.formatarData(periodo.getInicio()));
		titulo.append(" à ");
		titulo.append(StringUtils.formatarData(periodo.getFim()));
		final RelatorioPedidoPeriodo relatorio = new RelatorioPedidoPeriodo(titulo.toString());

		for (Object[] resultado : resultados) {

			try {
				relatorio.addVenda(resultado[0].toString(), new RepresentadaValorWrapper(resultado[1].toString(),
						(Double) resultado[2]));
			} catch (AgrupamentoException e) {
				throw new BusinessException("Falha na construcao do relatorio de vendas da representada por vendedor", e);
			}
		}

		return relatorio;
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
			item.setPrecoItemFormatado(NumeroUtils.formatarValorMonetario(item.getPrecoItem()));

			item.setNomeProprietario(pedido.getProprietario().getNomeCompleto());
			item.setNomeRepresentada(pedido.getRepresentada().getNomeFantasia());
			relatorio.addElemento(pedido.getId(), item);
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
		return gerarRelatorioItensPorPedido("Pedidos de Revenda para Encomendados",
				pedidoService.pesquisarRevendaEncomendada(idRepresentada, periodo));
	}

	@Override
	public List<Pedido> gerarRelatorioVenda(Periodo periodo) throws InformacaoInvalidaException {
		return this.pedidoService.pesquisarPedidoVendaByPeriodo(periodo);
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public RelatorioWrapper<String, Pedido> gerarRelatorioVendaCliente(boolean orcamento, Periodo periodo,
			Integer idCliente) throws BusinessException {
		String nomeCliente = clienteService.pesquisarNomeFantasia(idCliente);

		final StringBuilder titulo = new StringBuilder(orcamento ? "Orçamento para " : "Vendas para ");
		if (StringUtils.isNotEmpty(nomeCliente)) {
			titulo.append("o Cliente ").append(nomeCliente).append(" ");
		} else {
			titulo.append("os Clientes ");
		}
		titulo.append(" de ").append(StringUtils.formatarData(periodo.getInicio())).append(" à ")
				.append(StringUtils.formatarData(periodo.getFim()));

		final RelatorioWrapper<String, Pedido> relatorio = new RelatorioWrapper<String, Pedido>(titulo.toString());
		List<Pedido> listaPedido = this.pedidoService.pesquisarVendaClienteByPeriodo(periodo, idCliente, orcamento);
		double valorTotal = 0d;
		for (Pedido pedido : listaPedido) {
			try {
				pedido.setDataEnvioFormatada(StringUtils.formatarData(pedido.getDataEnvio()));
				relatorio.addElemento(pedido.getCliente().getNomeFantasia(), pedido);
				valorTotal += pedido.getValorPedido();

				pedido.setValorPedidoFormatado(NumeroUtils.formatarValorMonetario(pedido.getValorPedido()));
				pedido.setDataEnvioFormatada(StringUtils.formatarData(pedido.getDataEnvio()));
			} catch (Exception e) {
				throw new BusinessException("Falha na geracao do relatorio de vendas para o cliente " + idCliente, e);
			}
		}
		relatorio.setValorTotal(NumeroUtils.formatarValorMonetario(valorTotal));
		return relatorio;
	}

	@Override
	public RelatorioPedidoPeriodo gerarRelatorioVendaPeriodo(Periodo periodo) throws BusinessException {

		final List<Object[]> resultados = pedidoService.pesquisarTotalVendaResumidaByPeriodo(periodo);

		final StringBuilder titulo = new StringBuilder();
		titulo.append("Relatório das Vendas do Período de ");
		titulo.append(StringUtils.formatarData(periodo.getInicio()));
		titulo.append(" à ");
		titulo.append(StringUtils.formatarData(periodo.getFim()));
		final RelatorioPedidoPeriodo relatorio = new RelatorioPedidoPeriodo(titulo.toString());

		for (Object[] resultado : resultados) {

			try {
				relatorio.addVenda(resultado[0].toString(), new RepresentadaValorWrapper(resultado[1].toString(),
						(Double) resultado[2]));
			} catch (AgrupamentoException e) {
				throw new BusinessException("Falha na construcao do relatorio de vendas da representada por vendedor", e);
			}
		}

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
