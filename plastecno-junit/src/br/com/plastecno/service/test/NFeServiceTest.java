package br.com.plastecno.service.test;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import br.com.plastecno.service.DuplicataService;
import br.com.plastecno.service.NFeService;
import br.com.plastecno.service.PedidoService;
import br.com.plastecno.service.entity.Cliente;
import br.com.plastecno.service.entity.ItemPedido;
import br.com.plastecno.service.entity.Logradouro;
import br.com.plastecno.service.entity.NFeDuplicata;
import br.com.plastecno.service.entity.NFeItemFracionado;
import br.com.plastecno.service.entity.Pedido;
import br.com.plastecno.service.entity.Transportadora;
import br.com.plastecno.service.exception.BusinessException;
import br.com.plastecno.service.nfe.COFINS;
import br.com.plastecno.service.nfe.COFINSGeral;
import br.com.plastecno.service.nfe.CobrancaNFe;
import br.com.plastecno.service.nfe.DadosNFe;
import br.com.plastecno.service.nfe.DetalhamentoProdutoServicoNFe;
import br.com.plastecno.service.nfe.DuplicataNFe;
import br.com.plastecno.service.nfe.EnderecoNFe;
import br.com.plastecno.service.nfe.ICMS;
import br.com.plastecno.service.nfe.ICMSGeral;
import br.com.plastecno.service.nfe.IdentificacaoDestinatarioNFe;
import br.com.plastecno.service.nfe.IdentificacaoNFe;
import br.com.plastecno.service.nfe.NFe;
import br.com.plastecno.service.nfe.PIS;
import br.com.plastecno.service.nfe.PISGeral;
import br.com.plastecno.service.nfe.ProdutoServicoNFe;
import br.com.plastecno.service.nfe.TransportadoraNFe;
import br.com.plastecno.service.nfe.TransporteNFe;
import br.com.plastecno.service.nfe.TributosProdutoServico;
import br.com.plastecno.service.nfe.constante.TipoDestinoOperacao;
import br.com.plastecno.service.nfe.constante.TipoEmissao;
import br.com.plastecno.service.nfe.constante.TipoFinalidadeEmissao;
import br.com.plastecno.service.nfe.constante.TipoFormaPagamento;
import br.com.plastecno.service.nfe.constante.TipoModalidadeDeterminacaoBCICMS;
import br.com.plastecno.service.nfe.constante.TipoModalidadeFrete;
import br.com.plastecno.service.nfe.constante.TipoOperacaoConsumidorFinal;
import br.com.plastecno.service.nfe.constante.TipoOperacaoNFe;
import br.com.plastecno.service.nfe.constante.TipoOrigemMercadoria;
import br.com.plastecno.service.nfe.constante.TipoPresencaComprador;
import br.com.plastecno.service.nfe.constante.TipoTributacaoCOFINS;
import br.com.plastecno.service.nfe.constante.TipoTributacaoICMS;
import br.com.plastecno.service.nfe.constante.TipoTributacaoPIS;
import br.com.plastecno.service.test.builder.ServiceBuilder;

public class NFeServiceTest extends AbstractTest {

	private DuplicataService duplicataService;
	private NFeService nFeService;
	private PedidoService pedidoService;
	private PedidoServiceTest pedidoServiceTest;

	public NFeServiceTest() {
		pedidoServiceTest = new PedidoServiceTest();
		nFeService = ServiceBuilder.buildService(NFeService.class);
		pedidoService = ServiceBuilder.buildService(PedidoService.class);
		duplicataService = ServiceBuilder.buildService(DuplicataService.class);
	}

	private NFe gerarNFe(Integer idPedido) {
		Cliente cli = pedidoService.pesquisarClienteResumidoByIdPedido(idPedido);
		Transportadora transPed = pedidoService.pesquisarTransportadoraByIdPedido(idPedido);
		Logradouro endFaturamento = cli.getLogradouroFaturamento();

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

		IdentificacaoNFe i = new IdentificacaoNFe();
		i.setDestinoOperacao(TipoDestinoOperacao.NORMAL.getCodigo());
		i.setFinalidadeEmissao(Integer.parseInt(TipoFinalidadeEmissao.NORMAL.getCodigo()));
		i.setIndicadorFormaPagamento(Integer.parseInt(TipoFormaPagamento.PRAZO.getCodigo()));
		i.setTipoEmissao(TipoEmissao.NORMAL.getCodigo());
		i.setTipoOperacao(TipoOperacaoNFe.ENTRADA.getCodigo());
		i.setTipoPresencaComprador(TipoPresencaComprador.NAO_PRESENCIAL_OUTROS.getCodigo());
		i.setNaturezaOperacao("NATUREZA DA OPERACAO DE ENVIO TESTE");
		i.setOperacaoConsumidorFinal(TipoOperacaoConsumidorFinal.NORMAL.getCodigo());

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
		// Aqui devemos sempre recuperar as quantidades restantes de itens do
		// pedido para gerar a nfe pois do contrario o sistema nao vai conseguir
		// controlar as quantidades fracionadas inseridas.
		List<ItemPedido> lItem = nFeService.pesquisarQuantitadeItemRestanteByIdPedido(idPedido);
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
			icmsGeral.setValorBC(item.calcularPrecoTotal());
			icmsGeral.setModalidadeDeterminacaoBC(TipoModalidadeDeterminacaoBCICMS.VALOR_OPERACAO.getCodigo());
			icmsGeral.setOrigemMercadoria(TipoOrigemMercadoria.NACIONAL.getCodigo());

			COFINSGeral cofinsGeral = new COFINSGeral();
			cofinsGeral.setAliquota(0.10);
			cofinsGeral.setCodigoSituacaoTributaria(TipoTributacaoCOFINS.COFINS_1.getCodigo());
			cofinsGeral.setQuantidadeVendida((double) item.getQuantidade());
			cofinsGeral.setValorBC(item.calcularPrecoTotal());

			PISGeral pisGeral = new PISGeral();
			pisGeral.setAliquota(0.20);
			pisGeral.setCodigoSituacaoTributaria(TipoTributacaoPIS.PIS_1.getCodigo());
			pisGeral.setQuantidadeVendida((double) item.getQuantidade());
			pisGeral.setValorBC(item.calcularPrecoTotal());

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
		d.setIdentificacaoNFe(i);
		d.setIdentificacaoDestinatarioNFe(iDest);
		d.setTransporteNFe(t);
		d.setListaDetalhamentoProdutoServicoNFe(lDet);

		return new NFe(d);
	}

	private Integer gerarPedidoRevenda() {
		Pedido p = pedidoServiceTest.gerarPedidoRevendaComItem();
		Integer id = p.getId();
		try {
			pedidoService.enviarPedido(id, new byte[] {});
			return id;
		} catch (BusinessException e) {
			printMensagens(e);
			return null;
		}
	}

	@Test
	public void testEmissaoNFe() {
		Integer idPedido = gerarPedidoRevenda();
		NFe nFe = gerarNFe(idPedido);
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
		Integer idPedido = gerarPedidoRevenda();
		NFe nFe = gerarNFe(idPedido);
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
		Integer idPedido = gerarPedidoRevenda();
		NFe nFe = gerarNFe(idPedido);

		nFe.getDadosNFe().getIdentificacaoNFe()
				.setIndicadorFormaPagamento(Integer.parseInt(TipoFormaPagamento.PRAZO.getCodigo()));
		nFe.getDadosNFe().getCobrancaNFe().setListaDuplicata(null);

		boolean throwed = false;
		try {
			nFeService.emitirNFeEntrada(nFe, idPedido);
		} catch (BusinessException e) {
			throwed = true;
		}
		assertTrue("NFe a prazo nao pode ser emitida sem duplicatas. Verificar a validacao", throwed);
	}

	@Test
	public void testEmissaoNFeAVistaComDuplicada() {
		Integer idPedido = gerarPedidoRevenda();
		NFe nFe = gerarNFe(idPedido);

		nFe.getDadosNFe().getIdentificacaoNFe()
				.setIndicadorFormaPagamento(Integer.parseInt(TipoFormaPagamento.VISTA.getCodigo()));

		boolean throwed = false;
		try {
			nFeService.emitirNFeEntrada(nFe, idPedido);
		} catch (BusinessException e) {
			throwed = true;
		}
		assertTrue("NFe a vista nao pode ser emitida com duplicatas. Verificar a validacao", throwed);
	}

	@Test
	public void testEmissaoNFeDevolucaoComDuplicada() {
		Integer idPedido = gerarPedidoRevenda();
		NFe nFe = gerarNFe(idPedido);
		nFe.getDadosNFe().getIdentificacaoNFe()
				.setIndicadorFormaPagamento(Integer.parseInt(TipoFormaPagamento.VISTA.getCodigo()));

		Integer[] numeros = null;
		try {
			numeros = nFeService.gerarNumeroSerieModeloNFe();
		} catch (BusinessException e1) {
			printMensagens(e1);
		}
		IdentificacaoNFe i = nFe.getDadosNFe().getIdentificacaoNFe();
		i.setNumero(numeros[0].toString());
		i.setSerie(numeros[1].toString());
		i.setModelo(numeros[2].toString());

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
		Integer idPedido = gerarPedidoRevenda();
		NFe nFe = gerarNFe(idPedido);
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
	public void testEmissaoNFeTriangularizacaoComDuplicada() {
		Integer idPedido = gerarPedidoRevenda();
		NFe nFe = gerarNFe(idPedido);
		nFe.getDadosNFe().getIdentificacaoNFe()
				.setIndicadorFormaPagamento(Integer.parseInt(TipoFormaPagamento.VISTA.getCodigo()));

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
		Integer idPedido = gerarPedidoRevenda();
		NFe nFe = gerarNFe(idPedido);
		nFe.getDadosNFe().getIdentificacaoNFe()
				.setIndicadorFormaPagamento(Integer.parseInt(TipoFormaPagamento.VISTA.getCodigo()));

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
}
