package br.com.svr.service.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Date;
import java.util.List;

import org.junit.Test;

import br.com.svr.service.PagamentoService;
import br.com.svr.service.PedidoService;
import br.com.svr.service.entity.ItemPedido;
import br.com.svr.service.entity.Pagamento;
import br.com.svr.service.entity.Pedido;
import br.com.svr.service.entity.Representada;
import br.com.svr.service.exception.BusinessException;
import br.com.svr.service.mensagem.email.AnexoEmail;
import br.com.svr.service.test.builder.ServiceBuilder;
import br.com.svr.service.test.gerador.GeradorPedido;
import br.com.svr.service.test.gerador.GeradorRepresentada;
import br.com.svr.util.DateUtils;
import br.com.svr.util.NumeroUtils;

public class PagamentoServiceTest extends AbstractTest {
	private GeradorPedido gPedido = GeradorPedido.getInstance();

	private GeradorRepresentada gRepresentada = GeradorRepresentada.getInstance();

	private PagamentoService pagamentoService;

	private PedidoService pedidoService;

	public PagamentoServiceTest() {
		pagamentoService = ServiceBuilder.buildService(PagamentoService.class);
		pedidoService = ServiceBuilder.buildService(PedidoService.class);
	}

	@Test
	public void testInclusaoPagamentoAVencer() {
		Pagamento p = eBuilder.buildPagamento();
		Integer idPag = null;
		try {
			idPag = pagamentoService.inserirPagamento(p);
		} catch (BusinessException e) {
			printMensagens(e);
		}

		p = pagamentoService.pesquisarById(idPag);
		assertFalse("O pagamento ainda nao venceu, verifique a configuracao da situacao do pagamento", p.isVencido());
	}

	@Test
	public void testInclusaoPagamentoItemServico() {
		String formaPagamento = "10/20/30 dias";
		Representada fornc = gRepresentada.gerarFornecedor();
		Pedido ped1 = gPedido.gerarPedidoCompra();
		Pedido ped2 = gPedido.gerarPedidoCompra();

		// Aqui estamos configurando 3 parcelas pois uma mesma nota deve ter
		// apenas uma forma de pagamento.
		ped1.setFormaPagamento(formaPagamento);
		ped2.setFormaPagamento(formaPagamento);

		// Na mesma nota os pedido devem ser do mesmo fornecedor.
		ped1.setRepresentada(fornc);
		ped2.setRepresentada(fornc);
		try {
			pedidoService.inserirPedido(ped1);
			pedidoService.inserirPedido(ped2);
		} catch (BusinessException e1) {
			printMensagens(e1);
		}

		ItemPedido i1 = gPedido.gerarItemPedido();
		i1.setQuantidade(1000);
		i1.setAliquotaIPI(0.1d);

		ItemPedido i2 = gPedido.gerarItemPedido();
		i2.setQuantidade(2000);
		i1.setAliquotaIPI(0.2d);

		ItemPedido i3 = gPedido.gerarItemPedido();
		i3.setQuantidade(3000);

		Integer id1 = null;
		Integer id2 = null;
		Integer id3 = null;
		try {
			id1 = pedidoService.inserirItemPedido(ped1.getId(), i1);
			id2 = pedidoService.inserirItemPedido(ped1.getId(), i2);
			id3 = pedidoService.inserirItemPedido(ped2.getId(), i3);
		} catch (BusinessException e1) {
			printMensagens(e1);
		}

		try {
			pedidoService.enviarPedido(ped1.getId(), new AnexoEmail(new byte[] {}));
			pedidoService.enviarPedido(ped2.getId(), new AnexoEmail(new byte[] {}));
		} catch (BusinessException e1) {
			printMensagens(e1);
		}

		Integer numeroNF = 12000;
		double valorNF = 1000d;

		// Geranado 2 pedidos que serao pagos na mesma nota fiscal
		Pagamento pag1 = pagamentoService.gerarPagamentoItemCompra(id1);
		Pagamento pag2 = pagamentoService.gerarPagamentoItemCompra(id2);
		Pagamento pag3 = pagamentoService.gerarPagamentoItemCompra(id3);

		pag1.setDataEmissao(new Date());
		pag2.setDataEmissao(new Date());
		pag3.setDataEmissao(new Date());

		pag1.setNumeroNF(numeroNF);
		pag2.setNumeroNF(numeroNF);
		pag3.setNumeroNF(numeroNF);

		pag1.setValorNF(valorNF);
		pag2.setValorNF(valorNF);
		pag3.setValorNF(valorNF);

		assertEquals("Os pagamentos dos itens do mesmo pedido devem conter o mesmo id do fornecedor.",
				pag1.getIdFornecedor(), pag2.getIdFornecedor());
		assertEquals("Os pagamentos dos itens do mesmo pedido devem conter o mesmo id do pedido.", pag1.getIdPedido(),
				pag2.getIdPedido());

		try {
			pagamentoService.inserirPagamentoParceladoItemCompra(pag1);
			pagamentoService.inserirPagamentoParceladoItemCompra(pag2);
			pagamentoService.inserirPagamentoParceladoItemCompra(pag3);
		} catch (BusinessException e) {
			printMensagens(e);
		}

		List<Pagamento> listaPagParcelado = pagamentoService.pesquisarPagamentoByNF(numeroNF);
		assertEquals("A lista de pagamentos da nota deve conter 3 pagamentos", (Integer) 9,
				(Integer) listaPagParcelado.size());

		Integer totParc = 3;
		double totICMS = 0d;
		double totValNF = 0d;
		for (Pagamento p : listaPagParcelado) {
			assertEquals("O total de parcelas do pagamento da NF parcelada foi calculado errado", totParc,
					p.getTotalParcelas());

			totICMS += p.getValorCreditoICMS();
			totValNF += p.getValor();
		}
		double valNF = i1.getValorTotalIPI() + i2.getValorTotalIPI() + i3.getValorTotalIPI();
		double valICMS = i1.getValorICMS() + i2.getValorICMS() + i3.getValorICMS();

		assertEquals("Os valores totais dos pagamentos da NF nao conferem com os valores totais dos itens dos pedidos",
				NumeroUtils.formatarValorMonetario(valNF), NumeroUtils.formatarValorMonetario(totValNF));
		assertEquals(
				"Os valores totais do icms dos pagamentos da NF nao conferem com os valores totais do icms dos itens dos pedidos",
				NumeroUtils.formatarValorMonetario(valICMS), NumeroUtils.formatarValorMonetario(totICMS));
	}

	@Test
	public void testInclusaoPagamentoVencido() {
		Pagamento p = eBuilder.buildPagamentoNF();
		p.setDataVencimento(DateUtils.retrocederData(new Date()));
		Integer idPag = null;
		try {
			idPag = pagamentoService.inserirPagamento(p);
		} catch (BusinessException e) {
			printMensagens(e);
		}

		p = pagamentoService.pesquisarById(idPag);
		assertTrue("O pagamento ja venceu, verifique a configuracao da situacao do pagamento", p.isVencido());
	}
}
