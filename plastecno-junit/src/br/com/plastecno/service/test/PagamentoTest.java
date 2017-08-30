package br.com.plastecno.service.test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import br.com.plastecno.service.PagamentoService;
import br.com.plastecno.service.entity.Pagamento;
import br.com.plastecno.service.exception.BusinessException;
import br.com.plastecno.service.test.builder.ServiceBuilder;

public class PagamentoTest extends AbstractTest {
	private PagamentoService pagamentoService;

	public PagamentoTest() {
		pagamentoService = ServiceBuilder.buildService(PagamentoService.class);
	}

	@Test
	public void testInclusaoPagamentoAVencer() {
		Pagamento p = eBuilder.buildPagamento();
		Integer idPag = null;
		try {
			idPag = pagamentoService.inserir(p);
		} catch (BusinessException e) {
			printMensagens(e);
		}

		p = pagamentoService.pesquisarById(idPag);
		assertFalse("O pagamento ainda nao venceu, verifique a configuracao da situacao do pagamento", p.isVencido());
	}

	@Test
	public void testInclusaoPagamentoVencido() {
		Pagamento p = eBuilder.buildPagamento();
		p.setDataVencimento(TestUtils.gerarDataOntem());
		Integer idPag = null;
		try {
			idPag = pagamentoService.inserir(p);
		} catch (BusinessException e) {
			printMensagens(e);
		}

		p = pagamentoService.pesquisarById(idPag);
		assertTrue("O pagamento ja venceu, verifique a configuracao da situacao do pagamento", p.isVencido());
	}
}
