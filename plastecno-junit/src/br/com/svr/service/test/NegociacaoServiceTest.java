package br.com.svr.service.test;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import br.com.svr.service.NegociacaoService;
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
		assertEquals("O id do orcamento da negociacao deve ser o mesmo do orcamento.", o.getId(), n.getIdOrcamento());
		assertEquals("O tipo de nao fechamento da negociacao deve ser OK na inclusao.", TipoNaoFechamento.OK,
				n.getTipoNaoFechamento());

	}
}
