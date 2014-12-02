package br.com.plastecno.test.service;

import org.junit.Test;

import br.com.plastecno.service.ClienteService;
import br.com.plastecno.service.PedidoService;
import br.com.plastecno.service.UsuarioService;
import br.com.plastecno.service.constante.FinalidadePedido;
import br.com.plastecno.service.constante.SituacaoPedido;
import br.com.plastecno.service.entity.Cliente;
import br.com.plastecno.service.entity.Contato;
import br.com.plastecno.service.entity.ContatoCliente;
import br.com.plastecno.service.entity.Pedido;
import br.com.plastecno.service.entity.Representada;
import br.com.plastecno.service.entity.Usuario;
import br.com.plastecno.service.exception.BusinessException;
import br.com.plastecno.service.impl.ClienteServiceImpl;
import br.com.plastecno.service.impl.PedidoServiceImpl;
import br.com.plastecno.service.impl.UsuarioServiceImpl;
import br.com.plastecno.test.mock.ClienteDAOMock;
import br.com.plastecno.test.mock.PedidoDAOMock;
import br.com.plastecno.test.mock.UsuarioDAOMock;

public class PedidoServiceTest extends AbstractTest {
	private UsuarioService usuarioService = new UsuarioServiceImpl(new UsuarioDAOMock());
	private ClienteService clienteService = new ClienteServiceImpl(new ClienteDAOMock());
	private PedidoService pedidoService = new PedidoServiceImpl(new PedidoDAOMock(), usuarioService, clienteService);

	@Test
	public void testInclusao() {
		Cliente cliente = new Cliente(1, "Vinicius");
		Representada representada = new Representada(1, "COBEX");
		Usuario vendedor = new Usuario(1, "Claudio", "Abreu");
		Contato contato = new Contato();
		contato.setNome("Adriano");

		Pedido pedido = new Pedido();
		pedido.setCliente(cliente);
		pedido.setRepresentada(representada);
		pedido.setVendedor(vendedor);
		pedido.setSituacaoPedido(SituacaoPedido.DIGITACAO);
		pedido.setFinalidadePedido(FinalidadePedido.CONSUMO);
		pedido.setContato(contato);
		try {
			pedidoService.inserir(pedido);
		} catch (BusinessException e) {
			printMensagens(e);
		}
	}
}
