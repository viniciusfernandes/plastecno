package br.com.svr.service.test;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Test;

import br.com.svr.service.NegociacaoService;
import br.com.svr.service.constante.SituacaoPedido;
import br.com.svr.service.constante.crm.CategoriaNegociacao;
import br.com.svr.service.constante.crm.SituacaoNegociacao;
import br.com.svr.service.constante.crm.TipoNaoFechamento;
import br.com.svr.service.entity.Pedido;
import br.com.svr.service.entity.crm.Negociacao;
import br.com.svr.service.exception.BusinessException;
import br.com.svr.service.test.builder.ServiceBuilder;
import br.com.svr.service.test.gerador.GeradorPedido;

public class NegociacaoServiceTest extends AbstractTest {
	private GeradorPedido gPedido = GeradorPedido.getInstance();
	private NegociacaoService negociacaoService;

	public NegociacaoServiceTest() {
		negociacaoService = ServiceBuilder.buildService(NegociacaoService.class);
	}

	@Test
	public void testAceiteNegociacao() {
		Pedido o = gPedido.gerarOrcamento();
		List<Negociacao> lNeg = negociacaoService.pesquisarNegociacaoAbertaByIdVendedor(o.getVendedor().getId());
		assertEquals("Deve existir apenas 1 negociacao por orcamento incluido.", (Integer) 1, (Integer) lNeg.size());

		Negociacao n = lNeg.get(0);
		try {
			negociacaoService.aceitarNegocicacao(n.getId());
		} catch (BusinessException e) {
			printMensagens(e);
		}
		lNeg = negociacaoService.pesquisarNegociacaoAbertaByIdVendedor(o.getVendedor().getId());
		assertEquals("Nao deve existir negociacao apos o orcamento aceito.", (Integer) 0, (Integer) lNeg.size());
	}

	@Test
	public void testInclusaoNegociacao() {
		Pedido o = gPedido.gerarOrcamento();
		Integer idNeg = null;
		try {
			idNeg = negociacaoService.inserirNegociacao(o.getId(), o.getVendedor().getId());
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
}
