package br.com.svr.service.test;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Test;

import br.com.svr.service.NegociacaoService;
import br.com.svr.service.PedidoService;
import br.com.svr.service.constante.SituacaoPedido;
import br.com.svr.service.constante.crm.CategoriaNegociacao;
import br.com.svr.service.constante.crm.SituacaoNegociacao;
import br.com.svr.service.constante.crm.TipoNaoFechamento;
import br.com.svr.service.entity.ItemPedido;
import br.com.svr.service.entity.Pedido;
import br.com.svr.service.entity.crm.IndiceConversao;
import br.com.svr.service.entity.crm.Negociacao;
import br.com.svr.service.exception.BusinessException;
import br.com.svr.service.mensagem.email.AnexoEmail;
import br.com.svr.service.test.builder.ServiceBuilder;
import br.com.svr.service.test.gerador.GeradorPedido;
import br.com.svr.util.NumeroUtils;

public class NegociacaoServiceTest extends AbstractTest {
	private GeradorPedido gPedido = GeradorPedido.getInstance();
	private NegociacaoService negociacaoService;
	private PedidoService pedidoService;

	public NegociacaoServiceTest() {
		negociacaoService = ServiceBuilder.buildService(NegociacaoService.class);
		pedidoService = ServiceBuilder.buildService(PedidoService.class);
	}

	@Test
	public void testAceiteNegociacao() {
		Pedido o = gPedido.gerarOrcamento();
		List<Negociacao> lNeg = negociacaoService.pesquisarNegociacaoAbertaByIdVendedor(o.getVendedor().getId());
		assertEquals("Deve existir apenas 1 negociacao por orcamento incluido.", (Integer) 1, (Integer) lNeg.size());

		Negociacao n = lNeg.get(0);
		try {
			negociacaoService.aceitarNegocicacaoByIdNegociacao(n.getId());
		} catch (BusinessException e) {
			printMensagens(e);
		}
		lNeg = negociacaoService.pesquisarNegociacaoAbertaByIdVendedor(o.getVendedor().getId());
		assertEquals("Nao deve existir negociacao apos o orcamento aceito.", (Integer) 0, (Integer) lNeg.size());
	}

	@Test
	public void testCancelamentoNegociacao() {
		Pedido o = gPedido.gerarOrcamento();
		ItemPedido i = gPedido.gerarItemPedido();
		try {
			pedidoService.inserirItemPedido(o.getId(), i);
			pedidoService.enviarPedido(o.getId(), new AnexoEmail(new byte[] {}));
		} catch (BusinessException e) {
			printMensagens(e);
		}
		Negociacao n = negociacaoService.pesquisarNegociacaoByIdOrcamento(o.getId());
		assertNotNull("Todo orcamento enviado deve ter uma negociacao", n);

		TipoNaoFechamento tpNFechamento = TipoNaoFechamento.FORMA_PAGAMENTO;
		try {
			negociacaoService.cancelarNegocicacao(n.getId(), tpNFechamento);
		} catch (BusinessException e) {
			printMensagens(e);
		}
		n = negociacaoService.pesquisarNegociacaoByIdOrcamento(o.getId());
		assertEquals("O tipo de nao fechamento na negociacao dever ser igual ao que foi cadastrado.", tpNFechamento,
				n.getTipoNaoFechamento());

		SituacaoPedido stOrc = pedidoService.pesquisarSituacaoPedidoById(o.getId());
		assertEquals("O orcamento deve ser cancelado apos o cancelamento da negociacao",
				SituacaoPedido.ORCAMENTO_CANCELADO, stOrc);
	}

	@Test
	public void testCancelamentoOrcamentoENegociacao() {
		Pedido o = gPedido.gerarOrcamento();
		ItemPedido i = gPedido.gerarItemPedido();
		try {
			pedidoService.inserirItemPedido(o.getId(), i);
			pedidoService.enviarPedido(o.getId(), new AnexoEmail(new byte[] {}));
		} catch (BusinessException e) {
			printMensagens(e);
		}
		Negociacao n = negociacaoService.pesquisarNegociacaoByIdOrcamento(o.getId());
		assertNotNull("Todo orcamento enviado deve ter uma negociacao", n);

		try {
			pedidoService.cancelarOrcamentoRemoverNegociacao(o.getId());
		} catch (BusinessException e) {
			printMensagens(e);
		}
		n = negociacaoService.pesquisarNegociacaoByIdOrcamento(o.getId());
		assertNull("Os orcamentos cancelados nao devem conter negociacao no sistema.", n);
	}

	@Test
	public void testInclusaoNegociacao() {
		Pedido o = gPedido.gerarOrcamento();
		Integer idNeg = null;
		try {
			idNeg = negociacaoService.inserirNegociacao(o.getId(), o.getCliente().getId(), o.getVendedor().getId());
		} catch (BusinessException e) {
			printMensagens(e);
		}
		Negociacao n = negociacaoService.pesquisarById(idNeg);

		assertEquals("A negociacao criada deve estar em aberto.", SituacaoNegociacao.ABERTO, n.getSituacaoNegociacao());
		assertEquals("A categoria da negociacao criada deve ser uma proposta ao cliente.",
				CategoriaNegociacao.PROPOSTA_CLIENTE, n.getCategoriaNegociacao());
		assertEquals("O vendedor da negociacao deve ser o mesmo do orcamento.", o.getVendedor().getId(),
				n.getIdVendedor());
		assertEquals("O id do orcamento da negociacao deve ser o mesmo do orcamento.", o.getId(), n.getOrcamento()
				.getId());
		assertEquals("O tipo de nao fechamento da negociacao deve ser OK na inclusao.", TipoNaoFechamento.OK,
				n.getTipoNaoFechamento());

	}

	@Test
	public void testInclusaoOrcamentoInclusaoNegociacao() {
		Pedido o = gPedido.gerarOrcamento();
		assertEquals("Para a inclusao de uma negociacao deve-se ter um orcamento em digitacao.",
				SituacaoPedido.ORCAMENTO_DIGITACAO, o.getSituacaoPedido());

		List<Negociacao> lNeg = negociacaoService.pesquisarNegociacaoAbertaByIdVendedor(o.getVendedor().getId());

		assertEquals("Para cada orcamento incluido deve-se ter apenas uma negociacao incluida", new Integer(1),
				(Integer) lNeg.size());

		Negociacao n = lNeg.get(0);

		assertEquals("A negociacao criada deve estar em aberto.", SituacaoNegociacao.ABERTO, n.getSituacaoNegociacao());
		assertEquals("A categoria da negociacao criada deve ser uma proposta ao cliente.",
				CategoriaNegociacao.PROPOSTA_CLIENTE, n.getCategoriaNegociacao());
		assertEquals("O vendedor da negociacao deve ser o mesmo do orcamento.", o.getVendedor().getId(),
				n.getIdVendedor());
		assertEquals("O id do orcamento da negociacao deve ser o mesmo do orcamento.", o.getId(), n.getOrcamento()
				.getId());
		assertEquals("O tipo de nao fechamento da negociacao deve ser OK na inclusao.", TipoNaoFechamento.OK,
				n.getTipoNaoFechamento());

	}

	@Test
	public void testRecalculoIndiceConversao() {
		Pedido o = gPedido.gerarOrcamento();
		pedidoService.pesquisarValorPedidoIPI(o.getId());
		Integer idCliente = pedidoService.pesquisarIdClienteByIdPedido(o.getId());

		IndiceConversao idxConv = negociacaoService.pesquisarIndiceConversaoByIdCliente(idCliente);
		assertNull("Na inclusao de um orcamento nao pode ser criado o indice de conversao", idxConv);

		List<Negociacao> lNeg = negociacaoService.pesquisarNegociacaoAbertaByIdVendedor(o.getVendedor().getId());
		assertEquals("Deve existir apenas 1 negociacao por orcamento incluido.", (Integer) 1, (Integer) lNeg.size());

		Negociacao n = lNeg.get(0);
		Integer idPedido = null;
		try {
			idPedido = negociacaoService.aceitarNegocicacaoByIdNegociacao(n.getId());
			pedidoService.pesquisarValorPedidoIPI(o.getId());
		} catch (BusinessException e) {
			printMensagens(e);
		}

		idxConv = negociacaoService.pesquisarIndiceConversaoByIdCliente(idCliente);
		assertNull("No aceite de uma negociacao nao pode ser criado o indice de conversao", idxConv);

		try {
			// Aqui estamos enviando o pedido para que seja refeito o calculo do
			// indice.
			pedidoService.enviarPedido(idPedido, new AnexoEmail(new byte[] {}));
		} catch (BusinessException e) {
			printMensagens(e);
		}
		pedidoService.pesquisarValorPedidoIPI(o.getId());
		pedidoService.pesquisarValorPedidoIPI(idPedido);
		idCliente = pedidoService.pesquisarIdClienteByIdPedido(idPedido);
		idxConv = negociacaoService.pesquisarIndiceConversaoByIdCliente(idCliente);

		assertNotNull("No envio do pedido deve ser gerado um indice de conversao", idxConv);
		assertEquals("Apos o envio de pedido sem alteracao de preco o indice de conversao deve ser 1", (Double) 1d,
				(Double) idxConv.getIndiceValor());

		int idIdxConv = idxConv.getId();
		double idxValorAntigo = idxConv.getIndiceValor();

		try {
			// Aqui estamos reenviando o pedido para que seja atualizado o
			// calculo do
			// indice, mas sem alteracao do valor dos itens, pois isso pode ser
			// exigido pelo cliente.
			pedidoService.enviarPedido(idPedido, new AnexoEmail(new byte[] {}));
		} catch (BusinessException e) {
			printMensagens(e);
		}
		idxConv = negociacaoService.pesquisarIndiceConversaoByIdCliente(idCliente);
		assertNotNull("No reenvio do pedido deve ser gerado um indice de conversao", idxConv);
		assertEquals(
				"O cliente ja tem um indice de conversao, entao apos o envio do pedido o indice de conversao deve ser o mesmo",
				(Integer) idIdxConv, idxConv.getId());
		assertEquals("Apos o reenvio do pedido sem alteracao de valores os valores do indice devem ser os mesmos",
				(Double) idxValorAntigo, (Double) idxConv.getIndiceValor());

		// Aqui estamos efetuando a alteracao da quanitdade dos itens para que
		// altere o valor do indice de conversao apos o reenvio do pedido.
		List<ItemPedido> lItem = pedidoService.pesquisarItemPedidoByIdPedido(idPedido);
		for (ItemPedido i : lItem) {
			i.setQuantidade(i.getQuantidade() * 2);
			try {
				pedidoService.inserirItemPedido(idPedido, i);
			} catch (BusinessException e) {
				printMensagens(e);
			}
		}
		try {
			pedidoService.enviarPedido(idPedido, new AnexoEmail(new byte[] {}));
		} catch (BusinessException e) {
			printMensagens(e);
		}

		idxConv = negociacaoService.pesquisarIndiceConversaoByIdCliente(idCliente);
		assertEquals("Apos o reenvio do pedido com alteracao de valores os valores do indice devem ser recalculados",
				(Double) 1.333d, (Double) NumeroUtils.arredondar(idxConv.getIndiceValor(), 3));
	}
}
