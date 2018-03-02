package br.com.svr.service.test;

import org.junit.Test;

import br.com.svr.service.PedidoService;
import br.com.svr.service.test.builder.ServiceBuilder;

public class JPATeste extends AbstractTest {
	private PedidoService pedidoService;

	public JPATeste() {
		pedidoService = ServiceBuilder.buildService(PedidoService.class);
	}

	@Test
	public void testInicial() {

		pedidoService.pesquisarByIdCliente(10);
		System.out.println("inicio...");
	}
}
