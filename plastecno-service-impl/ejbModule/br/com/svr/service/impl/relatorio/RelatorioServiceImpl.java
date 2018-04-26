package br.com.svr.service.impl.relatorio;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import jxl.Workbook;
import jxl.format.Alignment;
import jxl.format.Border;
import jxl.format.BorderLineStyle;
import jxl.format.Colour;
import jxl.write.Label;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import br.com.svr.service.ClienteService;
import br.com.svr.service.DuplicataService;
import br.com.svr.service.NFeService;
import br.com.svr.service.PedidoService;
import br.com.svr.service.RamoAtividadeService;
import br.com.svr.service.RepresentadaService;
import br.com.svr.service.UsuarioService;
import br.com.svr.service.constante.TipoPedido;
import br.com.svr.service.entity.Cliente;
import br.com.svr.service.entity.Contato;
import br.com.svr.service.entity.ItemPedido;
import br.com.svr.service.entity.NFeDuplicata;
import br.com.svr.service.entity.NFeItemFracionado;
import br.com.svr.service.entity.Pedido;
import br.com.svr.service.entity.Usuario;
import br.com.svr.service.exception.BusinessException;
import br.com.svr.service.impl.anotation.REVIEW;
import br.com.svr.service.relatorio.RelatorioService;
import br.com.svr.service.validacao.InformacaoInvalidaException;
import br.com.svr.service.wrapper.ComissaoVendaWrapper;
import br.com.svr.service.wrapper.GrupoWrapper;
import br.com.svr.service.wrapper.Periodo;
import br.com.svr.service.wrapper.ReceitaWrapper;
import br.com.svr.service.wrapper.RelatorioValorTotalPedidoWrapper;
import br.com.svr.service.wrapper.RelatorioVendaVendedorByRepresentada;
import br.com.svr.service.wrapper.RelatorioWrapper;
import br.com.svr.service.wrapper.TotalizacaoPedidoWrapper;
import br.com.svr.service.wrapper.VendaClienteWrapper;
import br.com.svr.util.NumeroUtils;
import br.com.svr.util.StringUtils;

@Stateless
public class RelatorioServiceImpl implements RelatorioService {
	@EJB
	private ClienteService clienteService;

	@EJB
	private DuplicataService duplicataService;

	@PersistenceContext(name = "svr")
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

	private void configurarPlanilha(WritableSheet sheet) throws IOException, WriteException {
		sheet.setColumnView(0, 15);
		sheet.setColumnView(1, 40);
		sheet.setColumnView(2, 40);
		sheet.setColumnView(3, 40);
		sheet.setColumnView(4, 150);

		WritableFont cellFont = new WritableFont(WritableFont.TIMES, 12);
		cellFont.setColour(Colour.WHITE);

		WritableCellFormat cf = new WritableCellFormat(cellFont);
		cf.setBorder(Border.ALL, BorderLineStyle.THIN);
		cf.setBackground(Colour.GREEN);
		cf.setAlignment(Alignment.CENTRE);

		// Criando o header
		sheet.addCell(new Label(0, 0, "Dt. COMRPA", cf));
		sheet.addCell(new Label(1, 0, "CLIENTE", cf));
		sheet.addCell(new Label(2, 0, "CONTATO", cf));
		sheet.addCell(new Label(3, 0, "TELEFONE", cf));
		sheet.addCell(new Label(4, 0, "EMAIL", cf));
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public byte[] gerarPlanilhaClienteVendedor(Integer idVendedor, boolean clienteInativo) throws BusinessException {
		List<Cliente> l = gerarRelatorioClienteVendedor(idVendedor, clienteInativo);

		ByteArrayOutputStream out = new ByteArrayOutputStream(1024);
		try {
			WritableWorkbook excel = Workbook.createWorkbook(out);
			WritableSheet sheet = excel.createSheet("Contato Cliente", 0);
			configurarPlanilha(sheet);

			WritableFont f1 = new WritableFont(WritableFont.TIMES, 10);
			f1.setColour(Colour.BLACK);
			WritableCellFormat cf1 = new WritableCellFormat(f1);
			cf1.setBorder(Border.ALL, BorderLineStyle.THIN);

			WritableFont f2 = new WritableFont(WritableFont.TIMES, 10);
			f2.setColour(Colour.BLACK);
			WritableCellFormat cf2 = new WritableCellFormat(f2);
			cf2.setBorder(Border.ALL, BorderLineStyle.THIN);
			cf2.setBackground(Colour.GREY_25_PERCENT);

			// Vamos incluir as linhas a partir do indice row=1 pois row=0 eh o
			// header da planilha
			int row = 0;
			int cont = -1;
			WritableCellFormat cf = null;

			for (Cliente c : l) {
				cf = ++cont % 2 == 0 ? cf1 : cf2;
				if (!c.contemContato()) {
					++row;
					sheet.addCell(new Label(0, row, c.getDataUltimoPedidoFormatado(), cf));
					sheet.addCell(new Label(1, row, c.getNomeFantasia(), cf));
					sheet.addCell(new Label(2, row, "", cf));
					sheet.addCell(new Label(3, row, "", cf));
					sheet.addCell(new Label(4, row, "", cf));
				} else {
					for (Contato ct : c.getListaContato()) {
						++row;
						sheet.addCell(new Label(0, row, c.getDataUltimoPedidoFormatado(), cf));
						sheet.addCell(new Label(1, row, c.getNomeFantasia(), cf));
						sheet.addCell(new Label(2, row, ct.getNome(), cf));
						sheet.addCell(new Label(3, row, ct.getDDDTelefoneFormatado(), cf));
						sheet.addCell(new Label(4, row, ct.getEmail(), cf));
					}
				}
			}
			excel.write();
			excel.close();
		} catch (WriteException | IOException e) {
			throw new BusinessException("Falha na geração da planilha excel dos clientes dos vendedores", e);
		}
		return out.toByteArray();
	}

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
		receita.setValorCompradoFormatado(NumeroUtils.formatarValor2Decimais(valorComprado));
		receita.setValorVendidoFormatado(NumeroUtils.formatarValor2Decimais(valorVendido));
		receita.setValorCreditoICMSFormatado(NumeroUtils.formatarValor2Decimais(valorCreditoICMS));
		receita.setValorDebitoICMSFormatado(NumeroUtils.formatarValor2Decimais(valorDebitoICMS));
		receita.setValorCreditoIPIFormatado(NumeroUtils.formatarValor2Decimais(valorCreditoIPI));
		receita.setValorDebitoIPIFormatado(NumeroUtils.formatarValor2Decimais(valorDebitoIPI));
		receita.setValorICMSFormatado(NumeroUtils.formatarValor2Decimais(valorICMS));
		receita.setValorIPIFormatado(NumeroUtils.formatarValor2Decimais(valorIPI));
		receita.setValorComissionadoFormatado(NumeroUtils.formatarValor2Decimais(valorComissionado));
		receita.setValorLiquidoFormatado(NumeroUtils.formatarValor2Decimais(valorLiquido));
		receita.setValorReceitaFormatado(NumeroUtils.formatarValor2Decimais(valorReceita));
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
		List<Cliente> l = clienteService.pesquisarClienteCompradorByIdVendedor(idVendedor, clienteInativo);
		for (Cliente c : l) {
			c.formatarContatoPrincipal();
		}
		return l;
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
		relatorio.setValorTotal(NumeroUtils.formatarValor2Decimais(valorTotalComissionado));
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
			c.setValorVendidoFormatado(NumeroUtils.formatarValor2Decimais(c.getValorVendido()));
			c.setValorComissaoFormatado(NumeroUtils.formatarValor2Decimais(c.getValorComissao()));
		}

		relatorio.setValorTotal(NumeroUtils.formatarValor2Decimais(valorTotalComissionado));
		return relatorio;
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public List<Pedido> gerarRelatorioCompra(Periodo periodo) throws InformacaoInvalidaException {
		return this.pedidoService.pesquisarPedidoCompraByPeriodo(periodo);
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public RelatorioWrapper<Integer, ItemPedido> gerarRelatorioCompraAguardandoRecepcao(Integer idRepresentada,
			List<Integer> listaNumeroPedido, Periodo periodo) {
		RelatorioWrapper<Integer, ItemPedido> relatorio = gerarRelatorioItensPorPedido(
				"Pedidos de Compras para Recepção",
				pedidoService.pesquisarItemPedidoCompraAguardandoRecepcao(idRepresentada, listaNumeroPedido, periodo),
				false);

		relatorio.addPropriedade("tipoPedido", TipoPedido.COMPRA);
		return relatorio;
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public RelatorioWrapper<Integer, ItemPedido> gerarRelatorioCompraAguardandoRecepcao(Integer idRepresentada,
			Periodo periodo) {
		return gerarRelatorioCompraAguardandoRecepcao(idRepresentada, null, periodo);
	}

	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	private RelatorioWrapper<Date, NFeDuplicata> gerarRelatorioDuplicata(List<NFeDuplicata> lDuplic, String titulo)
			throws BusinessException {
		RelatorioWrapper<Date, NFeDuplicata> relatorio = new RelatorioWrapper<Date, NFeDuplicata>(titulo);
		int qtde = 0;
		boolean okVal = false;
		boolean liqd = false;
		Double tot = 0d;
		Double totReceber = 0d;
		Double totDia = null;
		Double totReceberDia = null;
		GrupoWrapper<Date, NFeDuplicata> g = null;
		for (NFeDuplicata d : lDuplic) {
			totReceberDia = 0d;
			totDia = 0d;

			okVal = d.getValor() != null;
			liqd = d.isLiquidado();
			qtde++;

			if (okVal) {
				tot += d.getValor();
			}

			if (okVal && !liqd) {
				totReceber += d.getValor();
			}

			g = relatorio.addGrupo(d.getDataVencimento(), d);
			if ((totDia = (Double) g.getPropriedade("totDia")) == null && okVal) {
				totDia = d.getValor();
			} else if (totDia != null && okVal) {
				totDia += d.getValor();
			} else if (totDia == null) {
				totDia = 0d;
			}

			if ((totReceberDia = (Double) g.getPropriedade("totReceberDia")) == null && !liqd) {
				totReceberDia = d.getValor();
			} else if (totReceberDia != null && okVal && !liqd) {
				totReceberDia += d.getValor();
			} else if (totReceberDia == null) {
				totReceberDia = 0d;
			}

			g.setPropriedade("totDia", totDia);
			g.setPropriedade("totReceberDia", totReceberDia);
			g.setPropriedade("dataVencimentoFormatada", StringUtils.formatarData(d.getDataVencimento()));
		}

		// Apenas formantando os valores apos as totalizacoes para evitar
		// diferencas nos arredondamentos.
		relatorio.addPropriedade("qtde", qtde);
		relatorio.addPropriedade("totReceber", NumeroUtils.arredondarValor2Decimais(totReceber));
		relatorio.addPropriedade("tot", NumeroUtils.arredondarValor2Decimais(tot));

		for (GrupoWrapper<Date, NFeDuplicata> gr : relatorio.getListaGrupo()) {
			gr.setPropriedade("totDia", NumeroUtils.arredondarValor2Decimais((Double) gr.getPropriedade("totDia")));
			gr.setPropriedade("totReceberDia",
					NumeroUtils.arredondarValor2Decimais((Double) gr.getPropriedade("totReceberDia")));
		}

		relatorio.sortGrupo(new Comparator<GrupoWrapper<Date, NFeDuplicata>>() {

			@Override
			public int compare(GrupoWrapper<Date, NFeDuplicata> o1, GrupoWrapper<Date, NFeDuplicata> o2) {
				return o1.getId() != null && o2.getId() != null ? o1.getId().compareTo(o2.getId()) : 0;
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
	public RelatorioWrapper<Date, NFeDuplicata> gerarRelatorioDuplicataByIdCliente(Integer idCliente)
			throws BusinessException {
		if (idCliente == null) {
			throw new BusinessException("Não é possível gerar relatório de duplicatas pois o ID do cliente esta nulo.");
		}

		StringBuilder titulo = new StringBuilder();

		titulo.append("Duplicatas do Cliente ").append(clienteService.pesquisarNomeFantasia(idCliente));

		return gerarRelatorioDuplicata(duplicataService.pesquisarDuplicataByIdCliente(idCliente), titulo.toString());
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

		if (idVendedor == null || usuarioService.isVendaPermitida(idCliente, idVendedor)
				|| usuarioService.isCompraPermitida(idVendedor)) {
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

	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public RelatorioWrapper<Integer, ItemPedido> gerarRelatorioItemPedidoCompraEfetivada(Integer idRepresentada,
			List<Integer> listaNumeroPedido, Periodo periodo) {
		RelatorioWrapper<Integer, ItemPedido> relatorio = gerarRelatorioItensPorPedido(
				"Compras de " + StringUtils.formatarData(periodo.getInicio()) + " a "
						+ StringUtils.formatarData(periodo.getFim()),
				pedidoService.pesquisarItemPedidoCompraEfetivada(idRepresentada, listaNumeroPedido, periodo), false);

		relatorio.addPropriedade("tipoPedido", TipoPedido.COMPRA);
		return relatorio;
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public RelatorioWrapper<Integer, ItemPedido> gerarRelatorioItemPedidoCompraEfetivada(Integer idRepresentada,
			Periodo periodo) {
		return gerarRelatorioItemPedidoCompraEfetivada(idRepresentada, null, periodo);
	}

	@REVIEW(descricao = "Nem sempre eh necessario carregar as informacoes da representada")
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	private RelatorioWrapper<Integer, ItemPedido> gerarRelatorioItensPorPedido(String titulo,
			List<ItemPedido> listaItem, boolean isComissaoFormatado) {
		RelatorioWrapper<Integer, ItemPedido> relatorio = new RelatorioWrapper<Integer, ItemPedido>(titulo);
		for (ItemPedido item : listaItem) {

			item.setMedidaExternaFomatada(NumeroUtils.formatarValor2Decimais(item.getMedidaExterna()));
			item.setMedidaInternaFomatada(NumeroUtils.formatarValor2Decimais(item.getMedidaInterna()));
			item.setComprimentoFormatado(NumeroUtils.formatarValor2Decimais(item.getComprimento()));
			item.setPrecoUnidadeFormatado(NumeroUtils.formatarValor2Decimais(item.getPrecoUnidade()));
			item.setPrecoItemFormatado(NumeroUtils.formatarValor2Decimais(item.calcularPrecoItem()));
			item.setPrecoCustoItemFormatado(NumeroUtils.formatarValor2Decimais(item.getPrecoCusto()));

			if (isComissaoFormatado) {
				item.setAliquotaComissaoFormatado(NumeroUtils.formatarPercentual(item.getAliquotaComissao(), 2));
				item.setAliquotaComissaoRepresentadaFormatado(NumeroUtils.formatarPercentualInteiro(item
						.getAliquotaComissaoRepresentada()));

				item.setValorComissionadoFormatado(NumeroUtils.formatarValor2Decimais(item.getValorComissionado()));
				item.setValorComissionadoRepresentadaFormatado(NumeroUtils.formatarValor2Decimais(item
						.getValorComissionadoRepresentada()));
			}
			relatorio.addGrupo(item.getIdPedido(), item).setPropriedade("dataEntrega",
					StringUtils.formatarData(item.getDataEntrega()));
		}
		// Reordenando os itens pelo numero de sequencia de inclusao no pedido.
		relatorio.sortElementoByGrupo(ordenacaoItemPedido);

		// Reordenando os grupos pelo numero do pedido.
		relatorio.sortGrupo(new Comparator<GrupoWrapper<Integer, ItemPedido>>() {
			@Override
			public int compare(GrupoWrapper<Integer, ItemPedido> o1, GrupoWrapper<Integer, ItemPedido> o2) {
				return o1.getId() != null && o2.getId() != null ? o1.getId().compareTo(o2.getId()) : -1;
			}
		});
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
				totalizacao.setValorTotalFormatado(NumeroUtils.formatarValor2Decimais(totalizacao.getValorTotal()));
				relatorio.addGrupo(totalizacao.getNomeCliente(), totalizacao);
				valorTotal += totalizacao.getValorTotal();
			} catch (Exception e) {
				throw new BusinessException("Falha na geracao do relatorio de vendas para o cliente " + idCliente, e);
			}
		}
		relatorio.setValorTotal(NumeroUtils.formatarValor2Decimais(valorTotal));
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

		final StringBuilder titulo = new StringBuilder(orcamento ? "Orçamentos " : "Vendas ").append(" do Vendedor ")
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
