package br.com.plastecno.service.test;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import br.com.plastecno.service.NFeService;
import br.com.plastecno.service.PedidoService;
import br.com.plastecno.service.entity.Cliente;
import br.com.plastecno.service.entity.ItemPedido;
import br.com.plastecno.service.entity.Logradouro;
import br.com.plastecno.service.entity.Pedido;
import br.com.plastecno.service.entity.Transportadora;
import br.com.plastecno.service.exception.BusinessException;
import br.com.plastecno.service.nfe.DadosNFe;
import br.com.plastecno.service.nfe.DetalhamentoProdutoServicoNFe;
import br.com.plastecno.service.nfe.DuplicataNFe;
import br.com.plastecno.service.nfe.EnderecoNFe;
import br.com.plastecno.service.nfe.ICMS;
import br.com.plastecno.service.nfe.ICMSGeral;
import br.com.plastecno.service.nfe.IdentificacaoDestinatarioNFe;
import br.com.plastecno.service.nfe.IdentificacaoNFe;
import br.com.plastecno.service.nfe.NFe;
import br.com.plastecno.service.nfe.ProdutoServicoNFe;
import br.com.plastecno.service.nfe.TransportadoraNFe;
import br.com.plastecno.service.nfe.TransporteNFe;
import br.com.plastecno.service.nfe.TributosProdutoServico;
import br.com.plastecno.service.nfe.constante.TipoDestinoOperacao;
import br.com.plastecno.service.nfe.constante.TipoEmissao;
import br.com.plastecno.service.nfe.constante.TipoFinalidadeEmissao;
import br.com.plastecno.service.nfe.constante.TipoFormaPagamento;
import br.com.plastecno.service.nfe.constante.TipoModalidadeFrete;
import br.com.plastecno.service.nfe.constante.TipoOperacaoConsumidorFinal;
import br.com.plastecno.service.nfe.constante.TipoOperacaoNFe;
import br.com.plastecno.service.nfe.constante.TipoPresencaComprador;
import br.com.plastecno.service.nfe.constante.TipoTributacaoICMS;
import br.com.plastecno.service.test.builder.ServiceBuilder;

public class NFeServiceTest extends AbstractTest {

	private NFeService nFeService;
	private PedidoService pedidoService;
	private PedidoServiceTest pedidoServiceTest;

	public NFeServiceTest() {
		pedidoServiceTest = new PedidoServiceTest();
		nFeService = ServiceBuilder.buildService(NFeService.class);
		pedidoService = ServiceBuilder.buildService(PedidoService.class);
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
		Cliente cli = pedidoService.pesquisarClienteResumidoByIdPedido(idPedido);
		Transportadora transPed = pedidoService.pesquisarTransportadoraByIdPedido(idPedido);
		Logradouro endFaturamento = cli.getLogradouroFaturamento();

		List<DuplicataNFe> listaDuplicata = nFeService.gerarDuplicataByIdPedido(idPedido);

		Assert.assertTrue("A lista de duplicatas deve conter ao menos 1 elemento para pedidos a prazo",
				listaDuplicata != null && listaDuplicata.size() >= 1);

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

			trib = new TributosProdutoServico();
			trib.setIcms(new ICMS(icmsGeral));

			det = new DetalhamentoProdutoServicoNFe();
			det.setInformacoesAdicionais("Apenas informacoes adicionais de teste");
			det.setNumeroItem(item.getSequencial());
			det.setProdutoServicoNFe(prod);
			det.setTributosProdutoServico(trib);

			lDet.add(det);
		}
		DadosNFe d = new DadosNFe();
		d.setIdentificacaoNFe(i);
		d.setIdentificacaoDestinatarioNFe(iDest);
		d.setTransporteNFe(t);
		d.setListaDetalhamentoProdutoServicoNFe(lDet);
		NFe nFe = new NFe(d);

		try {
			nFeService.emitirNFeEntrada(nFe, idPedido);
		} catch (BusinessException e) {
			printMensagens(e);
		}
	}
}
