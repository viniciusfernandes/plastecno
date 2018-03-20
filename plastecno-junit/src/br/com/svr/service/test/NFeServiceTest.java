package br.com.svr.service.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import br.com.svr.service.DuplicataService;
import br.com.svr.service.NFeService;
import br.com.svr.service.PedidoService;
import br.com.svr.service.constante.TipoPedido;
import br.com.svr.service.entity.Cliente;
import br.com.svr.service.entity.ItemPedido;
import br.com.svr.service.entity.LogradouroCliente;
import br.com.svr.service.entity.NFeDuplicata;
import br.com.svr.service.entity.NFeItemFracionado;
import br.com.svr.service.entity.Pedido;
import br.com.svr.service.entity.Transportadora;
import br.com.svr.service.exception.BusinessException;
import br.com.svr.service.mensagem.email.AnexoEmail;
import br.com.svr.service.nfe.COFINS;
import br.com.svr.service.nfe.COFINSGeral;
import br.com.svr.service.nfe.CobrancaNFe;
import br.com.svr.service.nfe.DadosNFe;
import br.com.svr.service.nfe.DetalhamentoProdutoServicoNFe;
import br.com.svr.service.nfe.DuplicataNFe;
import br.com.svr.service.nfe.EnderecoNFe;
import br.com.svr.service.nfe.ICMS;
import br.com.svr.service.nfe.ICMSGeral;
import br.com.svr.service.nfe.IdentificacaoDestinatarioNFe;
import br.com.svr.service.nfe.IdentificacaoNFe;
import br.com.svr.service.nfe.NFe;
import br.com.svr.service.nfe.PIS;
import br.com.svr.service.nfe.PISGeral;
import br.com.svr.service.nfe.ProdutoServicoNFe;
import br.com.svr.service.nfe.TransportadoraNFe;
import br.com.svr.service.nfe.TransporteNFe;
import br.com.svr.service.nfe.TributosProdutoServico;
import br.com.svr.service.nfe.constante.TipoDestinoOperacao;
import br.com.svr.service.nfe.constante.TipoEmissao;
import br.com.svr.service.nfe.constante.TipoFinalidadeEmissao;
import br.com.svr.service.nfe.constante.TipoFormaPagamento;
import br.com.svr.service.nfe.constante.TipoModalidadeDeterminacaoBCICMS;
import br.com.svr.service.nfe.constante.TipoModalidadeFrete;
import br.com.svr.service.nfe.constante.TipoOperacaoConsumidorFinal;
import br.com.svr.service.nfe.constante.TipoOperacaoNFe;
import br.com.svr.service.nfe.constante.TipoOrigemMercadoria;
import br.com.svr.service.nfe.constante.TipoPresencaComprador;
import br.com.svr.service.nfe.constante.TipoTributacaoCOFINS;
import br.com.svr.service.nfe.constante.TipoTributacaoICMS;
import br.com.svr.service.nfe.constante.TipoTributacaoPIS;
import br.com.svr.service.test.builder.ServiceBuilder;
import br.com.svr.service.test.gerador.GeradorPedido;

public class NFeServiceTest extends AbstractTest {

	private DuplicataService duplicataService;
	private GeradorPedido gPedido = GeradorPedido.getInstance();
	private NFeService nFeService;
	private PedidoService pedidoService;

	public NFeServiceTest() {
		nFeService = ServiceBuilder.buildService(NFeService.class);
		pedidoService = ServiceBuilder.buildService(PedidoService.class);
		duplicataService = ServiceBuilder.buildService(DuplicataService.class);
	}

	private NFe gerarNFe(Integer idPedido, boolean apenasItensRestantes) {
		Cliente cli = pedidoService.pesquisarClienteByIdPedido(idPedido);
		boolean isVenda = TipoPedido.isVenda(pedidoService.pesquisarTipoPedidoByIdPedido(idPedido));

		Transportadora transPed = pedidoService.pesquisarTransportadoraByIdPedido(idPedido);
		LogradouroCliente endFaturamento = cli.recuperarLogradouroFaturamento();

		List<DuplicataNFe> listaDuplicata = nFeService.gerarDuplicataDataAmericanaByIdPedido(idPedido);

		assertTrue("A lista de duplicatas deve conter ao menos 1 elemento para pedidos a prazo", listaDuplicata != null
				&& listaDuplicata.size() >= 1);

		CobrancaNFe cobrNfe = new CobrancaNFe();
		cobrNfe.setListaDuplicata(listaDuplicata);

		Object[] telefone = pedidoService.pesquisarTelefoneContatoByIdPedido(idPedido);

		EnderecoNFe endDest = new EnderecoNFe();
		endDest.setBairro(endFaturamento.getBairro());
		endDest.setCep(endFaturamento.getCep());
		endDest.setComplemento(endFaturamento.getComplemento());
		endDest.setLogradouro(endFaturamento.getEndereco());
		endDest.setNomeMunicipio(endFaturamento.getCidade());
		endDest.setNomePais(endFaturamento.getPais());
		endDest.setNumero(endFaturamento.getNumero());
		endDest.setTelefone(telefone[0].toString() + telefone[1].toString());
		endDest.setUF(endFaturamento.getUf());
		endDest.setNomeMunicipio(endFaturamento.getCidade());
		endDest.setCodigoMunicipio("65412");

		IdentificacaoDestinatarioNFe iDest = new IdentificacaoDestinatarioNFe();
		iDest.setRazaoSocial(cli.getRazaoSocial());
		iDest.setEnderecoDestinatarioNFe(endDest);

		IdentificacaoNFe iNfe = new IdentificacaoNFe();
		iNfe.setDestinoOperacao(TipoDestinoOperacao.NORMAL.getCodigo());
		iNfe.setFinalidadeEmissao(Integer.parseInt(TipoFinalidadeEmissao.NORMAL.getCodigo()));
		iNfe.setIndicadorFormaPagamento(Integer.parseInt(TipoFormaPagamento.PRAZO.getCodigo()));
		iNfe.setTipoEmissao(TipoEmissao.NORMAL.getCodigo());
		iNfe.setTipoOperacao(isVenda ? TipoOperacaoNFe.SAIDA.getCodigo() : TipoOperacaoNFe.ENTRADA.getCodigo());
		iNfe.setTipoPresencaComprador(TipoPresencaComprador.NAO_PRESENCIAL_OUTROS.getCodigo());
		iNfe.setNaturezaOperacao("NATUREZA DA OPERACAO DE ENVIO TESTE");
		iNfe.setOperacaoConsumidorFinal(TipoOperacaoConsumidorFinal.NORMAL.getCodigo());
		iNfe.setMunicipioOcorrenciaFatorGerador("3550308");
		iNfe.setCodigoUFEmitente("SP");

		// Aqui estamos inserindo os valores no sistema manualmente, mas apos a
		// inclusao da primeita NFe o sistema deve gerar os dados
		// automaticamente.
		iNfe.setNumero(String.valueOf(1000));
		iNfe.setSerie(String.valueOf(2));
		iNfe.setModelo(String.valueOf(55));

		TransportadoraNFe tNfe = new TransportadoraNFe();
		tNfe.setCnpj(transPed.getCnpj());
		tNfe.setEnderecoCompleto(transPed.getEnderecoNumeroBairro());
		tNfe.setInscricaoEstadual(transPed.getInscricaoEstadual());
		tNfe.setMunicipio(transPed.getMunicipio());
		tNfe.setRazaoSocial(transPed.getRazaoSocial());
		tNfe.setUf(transPed.getUf());

		TransporteNFe t = new TransporteNFe();
		t.setTransportadoraNFe(tNfe);
		t.setModalidadeFrete(TipoModalidadeFrete.EMITENTE.getCodigo());

		List<DetalhamentoProdutoServicoNFe> lDet = new ArrayList<DetalhamentoProdutoServicoNFe>();

		List<ItemPedido> lItem = null;
		// Aqui devemos sempre recuperar as quantidades restantes de itens do
		// pedido para gerar a nfe pois do contrario o sistema nao vai conseguir
		// controlar as quantidades fracionadas inseridas.
		if (apenasItensRestantes) {
			lItem = nFeService.pesquisarQuantitadeItemRestanteByIdPedido(idPedido);
		} else {
			lItem = pedidoService.pesquisarItemPedidoByIdPedido(idPedido);
		}

		DetalhamentoProdutoServicoNFe det = null;
		ProdutoServicoNFe prod = null;
		TributosProdutoServico trib = null;
		for (ItemPedido item : lItem) {
			prod = new ProdutoServicoNFe();
			prod.setCfop("1101");
			prod.setCodigo(item.getMaterial().getDescricaoFormatada());
			prod.setDescricao(item.getDescricaoItemMaterial());
			prod.setNcm("39169090");
			prod.setNumeroPedidoCompra("ped. 12346");
			prod.setQuantidadeComercial((double) item.getQuantidade());
			prod.setQuantidadeTributavel(item.getQuantidade());
			prod.setUnidadeComercial(item.getTipoVenda().toString());
			prod.setUnidadeTributavel(item.getTipoVenda().toString());
			prod.setValorUnitarioComercializacao(item.getPrecoUnidade());
			prod.setValorUnitarioTributacao(item.getPrecoUnidade());

			ICMSGeral icmsGeral = new ICMSGeral();
			icmsGeral.setCodigoSituacaoTributaria(TipoTributacaoICMS.ICMS_00.getCodigo());
			icmsGeral.setAliquota(0.10);
			icmsGeral.setValorBC(item.calcularPrecoTotalVenda());
			icmsGeral.setModalidadeDeterminacaoBC(TipoModalidadeDeterminacaoBCICMS.VALOR_OPERACAO.getCodigo());
			icmsGeral.setOrigemMercadoria(TipoOrigemMercadoria.NACIONAL.getCodigo());

			COFINSGeral cofinsGeral = new COFINSGeral();
			cofinsGeral.setAliquota(0.10);
			cofinsGeral.setCodigoSituacaoTributaria(TipoTributacaoCOFINS.COFINS_1.getCodigo());
			cofinsGeral.setQuantidadeVendida((double) item.getQuantidade());
			cofinsGeral.setValorBC(item.calcularPrecoTotalVenda());

			PISGeral pisGeral = new PISGeral();
			pisGeral.setAliquota(0.20);
			pisGeral.setCodigoSituacaoTributaria(TipoTributacaoPIS.PIS_1.getCodigo());
			pisGeral.setQuantidadeVendida((double) item.getQuantidade());
			pisGeral.setValorBC(item.calcularPrecoTotalVenda());

			trib = new TributosProdutoServico();
			trib.setIcms(new ICMS(icmsGeral));
			trib.setCofins(new COFINS(cofinsGeral));
			trib.setPis(new PIS(pisGeral));

			det = new DetalhamentoProdutoServicoNFe();
			det.setInformacoesAdicionais("Apenas informacoes adicionais de teste");
			det.setNumeroItem(item.getSequencial());
			det.setProdutoServicoNFe(prod);
			det.setTributosProdutoServico(trib);

			lDet.add(det);
		}
		DadosNFe d = new DadosNFe();
		d.setCobrancaNFe(cobrNfe);
		d.setIdentificacaoNFe(iNfe);
		d.setIdentificacaoDestinatarioNFe(iDest);
		d.setTransporteNFe(t);
		d.setListaDetalhamentoProdutoServicoNFe(lDet);

		return new NFe(d);
	}

	private NFe gerarNFeItensNaoEmitidos(Integer idPedido) {
		return gerarNFe(idPedido, true);
	}

	private NFe gerarNFeItensTodosItensPedido(Integer idPedido) {
		return gerarNFe(idPedido, false);
	}

	private Integer enviarPedidoRevendaComItem() {
		Pedido p = gPedido.gerarPedidoRevendaComItem();
		Integer id = p.getId();
		try {
			pedidoService.enviarPedido(id, new AnexoEmail(new byte[] {}));
			return id;
		} catch (BusinessException e) {
			printMensagens(e);
			return null;
		}
	}

	@Test
	public void testEmissaoNFe() {
		Integer idPedido = enviarPedidoRevendaComItem();
		NFe nFe = gerarNFeItensNaoEmitidos(idPedido);
		Integer numeroNFe = null;

		try {
			numeroNFe = Integer.parseInt(nFeService.emitirNFeEntrada(nFe, idPedido));
		} catch (BusinessException e) {
			printMensagens(e);
		}

		Integer totFrac = null;
		for (DetalhamentoProdutoServicoNFe d : nFe.getDadosNFe().getListaDetalhamentoProdutoServicoNFe()) {
			totFrac = nFeService.pesqusisarQuantidadeTotalFracionadoByIdItemPedidoNFeExcluida(d.getNumeroItem(),
					numeroNFe);
			assertTrue("Todos os itens do pedido foram emitidos e nao deve haver itens fracionados", totFrac.equals(0));
		}
	}

	@Test
	public void testEmissaoNFeApenasUmDosItensDoPedido() {
		Integer idPedido = enviarPedidoRevendaComItem();
		NFe nFe = gerarNFeItensNaoEmitidos(idPedido);
		List<DetalhamentoProdutoServicoNFe> lDet = nFe.getDadosNFe().getListaDetalhamentoProdutoServicoNFe();
		lDet.remove(0);
		Integer num = null;
		try {
			num = Integer.parseInt(nFeService.emitirNFeEntrada(nFe, idPedido));
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (BusinessException e) {
			printMensagens(e);
		}

		try {
			nFe = nFeService.gerarNFeByNumero(num);
		} catch (BusinessException e) {
			printMensagens(e);
		}

		Integer qtdeFrac = null;
		for (DetalhamentoProdutoServicoNFe det : nFe.getDadosNFe().getListaDetalhamentoProdutoServicoNFe()) {
			qtdeFrac = nFeService
					.pesqusisarQuantidadeTotalFracionadoByIdItemPedidoNFeExcluida(det.getNumeroItem(), num);
			assertEquals("Todos os itens do pedido foram emitidos e nao deve haver itens fracionados", (Integer) 0,
					qtdeFrac);
		}

	}

	@Test
	public void testEmissaoNFeAPrazoSemDuplicada() {
		Integer idPedido = enviarPedidoRevendaComItem();
		NFe nFe = gerarNFeItensNaoEmitidos(idPedido);

		nFe.getDadosNFe().getIdentificacaoNFe()
				.setIndicadorFormaPagamento(Integer.parseInt(TipoFormaPagamento.PRAZO.getCodigo()));
		nFe.getDadosNFe().getCobrancaNFe().setListaDuplicata(null);

		boolean throwed = false;
		try {
			nFeService.emitirNFeSaida(nFe, idPedido);
		} catch (BusinessException e) {
			throwed = true;
		}
		assertTrue("NFe a prazo nao pode ser emitida sem duplicatas. Verificar a validacao", throwed);
	}

	@Test
	public void testEmissaoNFeAVistaComDuplicada() {
		Integer idPedido = enviarPedidoRevendaComItem();
		NFe nFe = gerarNFeItensNaoEmitidos(idPedido);

		nFe.getDadosNFe().getIdentificacaoNFe()
				.setIndicadorFormaPagamento(Integer.parseInt(TipoFormaPagamento.VISTA.getCodigo()));

		boolean throwed = false;
		try {
			nFeService.emitirNFeSaida(nFe, idPedido);
		} catch (BusinessException e) {
			throwed = true;
		}
		assertTrue("NFe a vista nao pode ser emitida com duplicatas. Verificar a validacao", throwed);
	}

	@Test
	public void testEmissaoNFeDevolucaoComDuplicada() {
		Integer idPedido = enviarPedidoRevendaComItem();
		NFe nFe = gerarNFeItensNaoEmitidos(idPedido);
		nFe.getDadosNFe().getIdentificacaoNFe()
				.setIndicadorFormaPagamento(Integer.parseInt(TipoFormaPagamento.PRAZO.getCodigo()));

		try {
			nFeService.emitirNFeSaida(nFe, idPedido);
		} catch (BusinessException e2) {
			printMensagens(e2);
		}

		Integer[] novosNumeros = null;
		try {
			novosNumeros = nFeService.gerarNumeroSerieModeloNFe();
		} catch (BusinessException e1) {
			printMensagens(e1);
		}
		IdentificacaoNFe i = nFe.getDadosNFe().getIdentificacaoNFe();
		i.setNumero(novosNumeros[0].toString());
		i.setSerie(novosNumeros[1].toString());
		i.setModelo(novosNumeros[2].toString());

		String numNFe = null;
		try {
			numNFe = nFeService.emitirNFeDevolucao(nFe, idPedido);
		} catch (BusinessException e) {
			printMensagens(e);
		}

		List<NFeDuplicata> lDupl = duplicataService.pesquisarDuplicataByNumeroNFe(Integer.parseInt(numNFe));
		assertTrue("NFe de devolucao nao pode conter duplicatas para gerar os boletos. Verificar a validacao.",
				lDupl == null || lDupl.isEmpty());
	}

	@Test
	public void testEmissaoNFeFracionandoApenasUmDosItensDoPedido() {
		Integer idPedido = enviarPedidoRevendaComItem();
		NFe nFe = gerarNFeItensNaoEmitidos(idPedido);
		List<DetalhamentoProdutoServicoNFe> lDet = nFe.getDadosNFe().getListaDetalhamentoProdutoServicoNFe();

		DetalhamentoProdutoServicoNFe d = lDet.get(0);
		ProdutoServicoNFe p = d.getProduto();

		Integer numItemFrac = d.getNumeroItem();

		int qRemov = 1;
		int qEmit = p.getQuantidadeTributavel() - qRemov;
		p.setQuantidadeTributavel(qEmit);
		p.setQuantidadeComercial((double) qEmit);

		Integer num = null;
		try {
			num = Integer.parseInt(nFeService.emitirNFeEntrada(nFe, idPedido));
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (BusinessException e) {
			printMensagens(e);
		}

		try {
			nFe = nFeService.gerarNFeByNumero(num);
		} catch (BusinessException e) {
			printMensagens(e);
		}

		int qtdeFrac = 0;
		List<ItemPedido> lItem = pedidoService.pesquisarItemPedidoByIdPedido(idPedido);
		for (ItemPedido i : lItem) {
			if (!numItemFrac.equals(i.getSequencial())) {
				continue;
			}
			qtdeFrac = nFeService.pesquisarTotalItemFracionadoByNumeroItemNumeroNFe(i.getSequencial(), num);
			assertEquals(
					"Esse item teve quantidade fracionada e deve ter a mesma quantidade do que foi emitido na nota",
					qEmit, qtdeFrac);
		}

		for (DetalhamentoProdutoServicoNFe det : nFe.getDadosNFe().getListaDetalhamentoProdutoServicoNFe()) {
			qtdeFrac = nFeService.pesquisarTotalItemFracionadoByNumeroItemNumeroNFe(det.getNumeroItem(), num);
			assertEquals("As quantidades fracionadas e quantidades tributaveis da NFe devem ser as mesmas", (int) det
					.getProduto().getQuantidadeTributavel(), qtdeFrac);
		}

		lItem = nFeService.pesquisarQuantitadeItemRestanteByIdPedido(idPedido);
		List<NFeItemFracionado> lFrac = nFeService.pesquisarNFeItemFracionadoQuantidades(num);
		for (NFeItemFracionado ifrac : lFrac) {
			for (ItemPedido i : lItem) {
				if (!i.getSequencial().equals(ifrac.getNumeroItem())) {
					continue;
				}
				assertEquals("As quantidades fracionadas e restantes do item do pedido nao conferem",
						(int) i.getQuantidade() + (int) ifrac.getQuantidadeFracionada(), (int) ifrac.getQuantidade());
			}
		}
	}

	@Test
	public void testEmissaoNFeFracionandoQuantidadeSuperiorAoItemPedido() {
		Integer idPedido = enviarPedidoRevendaComItem();
		NFe nFe = gerarNFeItensNaoEmitidos(idPedido);
		List<DetalhamentoProdutoServicoNFe> lDet = nFe.getDadosNFe().getListaDetalhamentoProdutoServicoNFe();

		DetalhamentoProdutoServicoNFe d = lDet.get(0);
		ProdutoServicoNFe p = d.getProduto();
		p.setQuantidadeComercial(p.getQuantidadeComercial() + 1);
		p.setQuantidadeTributavel(p.getQuantidadeTributavel() + 1);

		boolean throwed = false;
		try {
			nFeService.emitirNFeEntrada(nFe, idPedido);
		} catch (BusinessException e) {
			throwed = true;
		}

		assertTrue(
				"Nao eh possivel emitir uma NFe de item fracionado contendo quantidade maior do que a do item do pedido. Verificar as regras.",
				throwed);
	}

	@Test
	public void testEmissaoNFeFracionandoQuantidadeSuperiorAoTotalJaFracionado() {
		Integer idPedido = enviarPedidoRevendaComItem();
		NFe nFe = gerarNFeItensNaoEmitidos(idPedido);
		List<DetalhamentoProdutoServicoNFe> lDet = nFe.getDadosNFe().getListaDetalhamentoProdutoServicoNFe();

		DetalhamentoProdutoServicoNFe d = lDet.get(0);
		ProdutoServicoNFe p = d.getProduto();
		p.setQuantidadeComercial(p.getQuantidadeComercial() - 1);
		p.setQuantidadeTributavel(p.getQuantidadeTributavel() - 1);

		try {
			// Aqui estamos fracionando o item
			nFeService.emitirNFeEntrada(nFe, idPedido);
		} catch (BusinessException e) {
			printMensagens(e);
		}

		nFe = gerarNFeItensTodosItensPedido(idPedido);
		lDet = nFe.getDadosNFe().getListaDetalhamentoProdutoServicoNFe();

		d = lDet.get(0);
		p = d.getProduto();
		p.setQuantidadeComercial(p.getQuantidadeComercial() + 1);
		p.setQuantidadeTributavel(p.getQuantidadeTributavel() + 1);
		boolean throwed = false;
		try {
			// Aqui estamos inserindo uma quantidade superior o que ja foi
			// fracionado
			nFeService.emitirNFeEntrada(nFe, idPedido);
		} catch (BusinessException e) {
			throwed = true;
		}

		assertTrue(
				"Nao eh possivel emitir uma NFe de item fracionado contendo quantidade maior do que quantidade que ja foi fracionada. Verificar as regras.",
				throwed);
	}

	@Test
	public void testEmissaoNFeTriangularizacaoComDuplicada() {
		Integer idPedido = enviarPedidoRevendaComItem();
		NFe nFe = gerarNFeItensNaoEmitidos(idPedido);
		nFe.getDadosNFe().getIdentificacaoNFe()
				.setIndicadorFormaPagamento(Integer.parseInt(TipoFormaPagamento.PRAZO.getCodigo()));

		try {
			nFeService.emitirNFeSaida(nFe, idPedido);
		} catch (BusinessException e1) {
			printMensagens(e1);
		}

		String numNFe = null;
		try {
			numNFe = nFeService.emitirNFeTriangularizacao(nFe, idPedido);
		} catch (BusinessException e) {
			printMensagens(e);
		}

		List<NFeDuplicata> lDupl = duplicataService.pesquisarDuplicataByNumeroNFe(Integer.parseInt(numNFe));
		assertTrue("NFe de triangularizacao nao pode conter duplicatas para gerar os boletos. Verificar a validacao.",
				lDupl == null || lDupl.isEmpty());
	}

	@Test
	public void testEmissaoNFeTriangularizacaoEItemFracionado() {
		Integer idPedido = enviarPedidoRevendaComItem();
		NFe nFe = gerarNFeItensNaoEmitidos(idPedido);
		nFe.getDadosNFe().getIdentificacaoNFe()
				.setIndicadorFormaPagamento(Integer.parseInt(TipoFormaPagamento.PRAZO.getCodigo()));

		try {
			nFeService.emitirNFeSaida(nFe, idPedido);
		} catch (BusinessException e1) {
			printMensagens(e1);
		}

		String numNFe = null;
		try {
			numNFe = nFeService.emitirNFeTriangularizacao(nFe, idPedido);
		} catch (BusinessException e) {
			printMensagens(e);
		}

		List<Integer[]> lFrac = nFeService.pesquisarTotalItemFracionadoByNumeroNFe(Integer.parseInt(numNFe));
		assertTrue("NFe de triangularizacao nao pode conter itens fracionados. Verificar a validacao.", lFrac == null
				|| lFrac.isEmpty());
	}

	@Test
	public void testRemovecaoNFeComItemFracionado() {
		Integer idPedido = enviarPedidoRevendaComItem();
		NFe nFe = gerarNFeItensNaoEmitidos(idPedido);
		List<DetalhamentoProdutoServicoNFe> lDet = nFe.getDadosNFe().getListaDetalhamentoProdutoServicoNFe();

		DetalhamentoProdutoServicoNFe d = lDet.get(0);
		ProdutoServicoNFe p = d.getProduto();

		int qRemov = 1;
		int qEmit = p.getQuantidadeTributavel() - qRemov;
		p.setQuantidadeTributavel(qEmit);
		p.setQuantidadeComercial((double) qEmit);

		Integer num = null;
		try {
			num = Integer.parseInt(nFeService.emitirNFeSaida(nFe, idPedido));
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (BusinessException e) {
			printMensagens(e);
		}

		List<NFeItemFracionado> lItem = nFeService.pesquisarItemFracionadoByNumeroNFe(num);
		assertTrue("A NFe foi fracionada e deveria ter um item fracionado", lItem != null && !lItem.isEmpty());

		List<NFeDuplicata> lDup = duplicataService.pesquisarDuplicataByNumeroNFe(num);
		assertTrue("A NFe foi emitida e deveria duplicatas associadas", lDup != null && !lDup.isEmpty());

		nFeService.removerNFe(num);

		try {
			nFe = nFeService.gerarNFeByNumero(num);
		} catch (BusinessException e) {
			printMensagens(e);
		}
		assertNull("A NFe foi removida e nao deve existir no sistema nem no diretorio de NFes", nFe);

		lItem = nFeService.pesquisarItemFracionadoByNumeroNFe(num);
		assertTrue("A NFe foi removida e nao deve conter item fracionado", lItem == null || lItem.isEmpty());

		lDup = duplicataService.pesquisarDuplicataByNumeroNFe(num);
		assertTrue("A NFe foi removida e nao deve conter duplicatas", lDup == null || lDup.isEmpty());
	}
}
