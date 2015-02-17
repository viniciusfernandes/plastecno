package br.com.plastecno.service.test;

import static org.junit.Assert.*;

import org.junit.Test;

import br.com.plastecno.service.ClienteService;
import br.com.plastecno.service.EstoqueService;
import br.com.plastecno.service.MaterialService;
import br.com.plastecno.service.PedidoService;
import br.com.plastecno.service.RepresentadaService;
import br.com.plastecno.service.UsuarioService;
import br.com.plastecno.service.constante.SituacaoPedido;
import br.com.plastecno.service.constante.TipoApresentacaoIPI;
import br.com.plastecno.service.constante.TipoPedido;
import br.com.plastecno.service.entity.Cliente;
import br.com.plastecno.service.entity.ItemEstoque;
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

	public ItemPedido gerarItemPedidoCompra() {
		Pedido pedido = eBuilder.buildPedido();
		pedido.setTipoPedido(TipoPedido.COMPRA);
		
		Usuario vendedor = eBuilder.buildVendedor();
		try {
			usuarioService.inserir(vendedor, true);
		} catch (BusinessException e2) {
			printMensagens(e2);
		}

		Cliente cliente = pedido.getCliente();
		cliente.setProspeccaoFinalizada(true);
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

	private Integer pesquisarQuantidadeTotalItemEstoque(Integer idItemEstoque) {
		ItemEstoque i = estoqueService.pesquisarItemEstoqueById(idItemEstoque);
		return i != null ? i.getQuantidade() : 0;

	}

	@Test
	public void testAlteracaoItemPedidoNoEstoque() {
		ItemPedido i = gerarItemPedidoCompra();
		Integer idItemEstoque = null;
		try {
			idItemEstoque = estoqueService.inserirItemPedido(i.getId());
		} catch (BusinessException e) {
			printMensagens(e);
		}

		verificarQuantidadeTotalItemEstoque(i.getQuantidade(), idItemEstoque);
		Integer estoqueAntes = pesquisarQuantidadeTotalItemEstoque(idItemEstoque);
		i.setQuantidade(i.getQuantidade() + 100);
		try {
			idItemEstoque = estoqueService.inserirItemPedido(i.getId());
		} catch (BusinessException e) {
			printMensagens(e);
		}
		estoqueAntes += i.getQuantidade();
		verificarQuantidadeTotalItemEstoque(estoqueAntes, idItemEstoque);
	}

	@Test
	public void testInclusaoItemPedidoInexistenteNoEstoque() {
		boolean throwed = false;
		try {
			estoqueService.inserirItemPedido(null);
		} catch (BusinessException e) {
			throwed = true;
		}
		assertTrue("Um item de pedido inexistente nao pode ser incluido no estoque", throwed);
	}

	@Test
	public void testInclusaoItemPedidoNoEstoque() {
		ItemPedido i = gerarItemPedidoCompra();
		Integer idItemEstoque = null;
		try {
			idItemEstoque = estoqueService.inserirItemPedido(i.getId());
		} catch (BusinessException e) {
			printMensagens(e);
		}

		verificarQuantidadeTotalItemEstoque(i.getQuantidade(), idItemEstoque);
		assertEquals(SituacaoPedido.COMPRA_RECEBIDA, i.getPedido().getSituacaoPedido());
	}

	@Test
	public void testSituacaoPedidoAposInclusaoVariosItensNoEstoque() {
		ItemPedido i1 = gerarItemPedidoCompra();
		ItemPedido i2 = i1.clone();

		try {
			pedidoService.inserirItemPedido(i2);
			pedidoService.enviar(i1.getPedido().getId(), new byte[] {});
		} catch (BusinessException e1) {
			printMensagens(e1);
		}

		try {
			estoqueService.inserirItemPedido(i1.getId());
		} catch (BusinessException e) {
			printMensagens(e);
		}
		assertEquals(SituacaoPedido.COMPRA_PENDENTE_RECEBIMENTO, i1.getPedido().getSituacaoPedido());

		try {
			estoqueService.inserirItemPedido(i2.getId());
		} catch (BusinessException e) {
			printMensagens(e);
		}
		assertEquals(SituacaoPedido.COMPRA_PENDENTE_RECEBIMENTO, i2.getPedido().getSituacaoPedido());

	}

	private void verificarQuantidadeTotalItemEstoque(Integer quantidade, Integer idItemEstoque) {
		Integer quantidadeEstoque = pesquisarQuantidadeTotalItemEstoque(idItemEstoque);
		assertEquals("As quantidades dos itens devem ser as mesmas apos inclusao no estoque", quantidade, quantidadeEstoque);

	}
}
