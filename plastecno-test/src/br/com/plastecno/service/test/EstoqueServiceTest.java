package br.com.plastecno.service.test;

import org.junit.Test;

import br.com.plastecno.service.ClienteService;
import br.com.plastecno.service.EstoqueService;
import br.com.plastecno.service.MaterialService;
import br.com.plastecno.service.PedidoService;
import br.com.plastecno.service.RepresentadaService;
import br.com.plastecno.service.UsuarioService;
import br.com.plastecno.service.constante.TipoApresentacaoIPI;
import br.com.plastecno.service.entity.Cliente;
import br.com.plastecno.service.entity.ItemPedido;
import br.com.plastecno.service.entity.Material;
import br.com.plastecno.service.entity.Pedido;
import br.com.plastecno.service.entity.Representada;
import br.com.plastecno.service.entity.Usuario;
import br.com.plastecno.service.exception.BusinessException;

public class EstoqueServiceTest extends AbstractTest {
	private EstoqueService estoqueService;
	private PedidoService pedidoService;
	private UsuarioService usuarioService;
	private ClienteService clienteService;
	private MaterialService materialService;
	private RepresentadaService representadaService;

	public ItemPedido gerarItemPedido() {
		Pedido pedido = eBuilder.buildPedido();
		Usuario vendedor = eBuilder.buildVendedor();
		try {
			usuarioService.inserir(vendedor, true);
		} catch (BusinessException e2) {
			printMensagens(e2);
		}

		Cliente cliente = pedido.getCliente();
		cliente.setVendedor(vendedor);
		try {
			clienteService.inserir(cliente);
		} catch (BusinessException e) {
			printMensagens(e);
		}
		try {
			pedidoService.inserir(pedido);
		} catch (BusinessException e1) {
			printMensagens(e1);
		}

		Representada representada = eBuilder.buildRepresentada();
		representada.setTipoApresentacaoIPI(TipoApresentacaoIPI.SEMPRE);
		try {
			representadaService.inserir(representada);
		} catch (BusinessException e3) {
			printMensagens(e3);
		}

		Material material = eBuilder.buildMaterial();
		material.addRepresentada(representada);
		try {
			materialService.inserir(material);
		} catch (BusinessException e2) {
			printMensagens(e2);
		}

		ItemPedido itemPedido = eBuilder.buildItemPedido();
		try {
			final Integer id = pedidoService.inserirItemPedido(pedido.getId(), itemPedido);
			itemPedido.setId(id);
		} catch (BusinessException e1) {
			printMensagens(e1);
		}
		return itemPedido;
	}

	@Override
	public void init() {
		estoqueService = ServiceBuilder.buildService(EstoqueService.class);
		pedidoService = ServiceBuilder.buildService(PedidoService.class);
		usuarioService = ServiceBuilder.buildService(UsuarioService.class);
		clienteService = ServiceBuilder.buildService(ClienteService.class);
		materialService = ServiceBuilder.buildService(MaterialService.class);
		representadaService = ServiceBuilder.buildService(RepresentadaService.class);

	}

	@Test
	public void testInclusaoItemPedidoNoEstoque() {
		ItemPedido i = gerarItemPedido();
		try {
			estoqueService.inserirItemPedido(i.getId());
		} catch (BusinessException e) {
			printMensagens(e);
		}

	}
}
