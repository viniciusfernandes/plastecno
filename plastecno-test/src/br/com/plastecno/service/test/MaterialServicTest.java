package br.com.plastecno.service.test;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import br.com.plastecno.service.PedidoService;
import br.com.plastecno.service.entity.Pedido;
import br.com.plastecno.service.exception.BusinessException;

public class MaterialServicTest extends AbstractTest {

	@Test
	public void testInclusaoPedidoDataEntregaInvalida() {
		Pedido pedido = gerador.gerarPedido();
		pedido.setDataEntrega(TestUtils.gerarDataAnterior());
		boolean throwed = false;
		try {
			PedidoService pedidoService = GeradorServico.gerarServico(PedidoService.class);
			pedido = pedidoService.inserir(pedido);
		} catch (BusinessException e) {
			throwed = true;
		}
		assertTrue("A data de entrega nao pode ser anterior a data atual na inclusao de pedidos", throwed);
	}

}
