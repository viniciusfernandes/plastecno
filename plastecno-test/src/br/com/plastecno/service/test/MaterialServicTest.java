package br.com.plastecno.service.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import br.com.plastecno.service.ClienteService;
import br.com.plastecno.service.PedidoService;
import br.com.plastecno.service.constante.SituacaoPedido;
import br.com.plastecno.service.constante.TipoEntrega;
import br.com.plastecno.service.constante.TipoPedido;
import br.com.plastecno.service.entity.Cliente;
import br.com.plastecno.service.entity.Pedido;
import br.com.plastecno.service.exception.BusinessException;

public class MaterialServicTest extends AbstractTest {

	private Pedido buildPedidoClienteProspectado() {
		Pedido pedido = eBuilder.buildPedido();
		Cliente cliente = pedido.getCliente();
		cliente.setProspeccaoFinalizada(true);
		ClienteService clienteService = ServiceBuilder.buildService(ClienteService.class);
		try {
			clienteService.inserir(cliente);
		} catch (BusinessException e) {
			printMensagens(e);
		}
		return pedido;
	}

	@Before
	public void init() {
	}

	@Test
	public void testEnvioPedidoCompra() {
		Pedido pedido = buildPedidoClienteProspectado();
		pedido.setDataEntrega(TestUtils.gerarDataPosterior());
		pedido.setFormaPagamento("30 dias a vista");
		pedido.setTipoEntrega(TipoEntrega.CIF);
		pedido.setTipoPedido(TipoPedido.COMPRA);
		PedidoService pedidoService = ServiceBuilder.buildService(PedidoService.class);
		;
		try {

			pedido = pedidoService.inserir(pedido);
		} catch (BusinessException e) {
			printMensagens(e);
		}

		assertEquals("Antes do envio do pedido de compra ele deve estar como em digitacao", SituacaoPedido.DIGITACAO,
				pedido.getSituacaoPedido());

		try {
			pedidoService.enviar(pedido.getId(), new byte[] {});
		} catch (BusinessException e) {
			printMensagens(e);
		}
		assertEquals("Apos o envio do pedido de compra, seu estado deve ser como pendente",
				SituacaoPedido.COMPRA_PENDENTE_RECEBIMENTO, pedido.getSituacaoPedido());

	}

	@Test
	public void testInclusaoPedidoDataEntregaInvalida() {
		Pedido pedido = eBuilder.buildPedido();
		pedido.setDataEntrega(TestUtils.gerarDataAnterior());
		boolean throwed = false;
		try {
			PedidoService pedidoService = ServiceBuilder.buildService(PedidoService.class);
			pedido = pedidoService.inserir(pedido);
		} catch (BusinessException e) {
			throwed = true;
		}
		assertTrue("A data de entrega nao pode ser anterior a data atual na inclusao de pedidos", throwed);
	}

}
