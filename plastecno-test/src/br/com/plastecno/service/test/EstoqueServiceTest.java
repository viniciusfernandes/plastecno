package br.com.plastecno.service.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import br.com.plastecno.service.ClienteService;
import br.com.plastecno.service.EstoqueService;
import br.com.plastecno.service.MaterialService;
import br.com.plastecno.service.PedidoService;
import br.com.plastecno.service.RepresentadaService;
import br.com.plastecno.service.UsuarioService;
import br.com.plastecno.service.constante.FormaMaterial;
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
	private ClienteService clienteService;
	private EstoqueService estoqueService;
	private MaterialService materialService;
	private PedidoService pedidoService;
	private RepresentadaService representadaService;
	private UsuarioService usuarioService;

	private ItemEstoque gerarItemEstoque() {
		ItemPedido i = gerarItemPedidoCompra();
		Integer idItemEstoque = null;
		try {
			idItemEstoque = estoqueService.inserirItemPedido(i.getId());
		} catch (BusinessException e) {
			printMensagens(e);
		}

		return estoqueService.pesquisarItemEstoqueById(idItemEstoque);
	}

	private ItemPedido gerarItemPedido(TipoPedido tipoPedido) {
		Pedido pedido = gerarPedido(tipoPedido);

		ItemPedido itemPedido = eBuilder.buildItemPedido();
		try {
			final Integer id = pedidoService.inserirItemPedido(pedido.getId(), itemPedido);
			itemPedido.setId(id);
		} catch (BusinessException e1) {
			printMensagens(e1);
		}
		return itemPedido;
	}

	private ItemPedido gerarItemPedidoCompra() {
		return gerarItemPedido(TipoPedido.COMPRA);
	}

	private ItemPedido gerarItemPedidoRevenda() {
		return gerarItemPedido(TipoPedido.REVENDA);
	}

	private Pedido gerarPedido(TipoPedido tipoPedido) {
		Pedido pedido = eBuilder.buildPedido();
		pedido.setTipoPedido(tipoPedido);

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
		return pedido;
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
	public void testInclusaoItemInexistenteEstoque() {
		ItemEstoque itemEstoque = gerarItemEstoque();
		itemEstoque.setId(null);
		try {
			estoqueService.inserirItemEstoque(itemEstoque);
		} catch (BusinessException e) {
			printMensagens(e);
		}
	}

	@Test
	public void testInclusaoItemInvalidoComDescricao() {
		ItemEstoque itemEstoque = gerarItemEstoque();
		itemEstoque.setDescricaoPeca("ENGRENAGEM TESTES");
		boolean throwed = false;
		try {
			estoqueService.inserirItemEstoque(itemEstoque);
		} catch (BusinessException e) {
			throwed = true;
		}
		assertTrue("A descricao de peca eh apenas para pecas.", throwed);
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
	public void testInclusaoPecaExistenteEstoque() {
		ItemEstoque itemEstoque = gerarItemEstoque();
		itemEstoque.setFormaMaterial(FormaMaterial.PC);
		itemEstoque.setDescricaoPeca("ENGRENAGEM TESTES");
		itemEstoque.setMedidaExterna(null);
		itemEstoque.setMedidaInterna(null);
		itemEstoque.setComprimento(null);
		try {
			estoqueService.inserirItemEstoque(itemEstoque);
		} catch (BusinessException e) {
			printMensagens(e);
		}
	}

	@Test
	public void testRedefinicaoEstoque() {
		ItemPedido i = gerarItemPedidoCompra();
		Integer idItemEstoque = null;
		try {
			idItemEstoque = estoqueService.inserirItemPedido(i.getId());
		} catch (BusinessException e) {
			printMensagens(e);
		}

		ItemEstoque itemEstoque = estoqueService.pesquisarItemEstoqueById(idItemEstoque);
		Integer quantidadeDepois = itemEstoque.getQuantidade() + 100;
		itemEstoque.setQuantidade(quantidadeDepois);
		try {
			estoqueService.redefinirItemEstoque(itemEstoque);
		} catch (BusinessException e) {
			printMensagens(e);
		}
		assertEquals("A quantidade de item do estoque nao pode ser a mesma apos sua redefinicao", quantidadeDepois,
				pesquisarQuantidadeTotalItemEstoque(idItemEstoque));
	}

	@Test
	public void testRedefinicaoEstoqueFormaMaterialNulo() {
		ItemPedido i = gerarItemPedidoCompra();
		Integer idItemEstoque = null;
		try {
			idItemEstoque = estoqueService.inserirItemPedido(i.getId());
		} catch (BusinessException e) {
			printMensagens(e);
		}

		ItemEstoque itemEstoque = estoqueService.pesquisarItemEstoqueById(idItemEstoque);
		itemEstoque.setFormaMaterial(null);
		boolean throwed = false;
		try {
			estoqueService.redefinirItemEstoque(itemEstoque);
		} catch (BusinessException e) {
			throwed = true;
		}
		assertTrue("O item de estoque deve conter forma do material", throwed);
	}

	@Test
	public void testRedefinicaoEstoqueMaterialNulo() {
		ItemPedido i = gerarItemPedidoCompra();
		Integer idItemEstoque = null;
		try {
			idItemEstoque = estoqueService.inserirItemPedido(i.getId());
		} catch (BusinessException e) {
			printMensagens(e);
		}

		ItemEstoque itemEstoque = estoqueService.pesquisarItemEstoqueById(idItemEstoque);
		itemEstoque.setMaterial(null);
		boolean throwed = false;
		try {
			estoqueService.redefinirItemEstoque(itemEstoque);
		} catch (BusinessException e) {
			throwed = true;
		}
		assertTrue("O item de estoque deve conter material", throwed);
	}

	@Test
	public void testRedefinicaoEstoqueMaterialQuantidade() {
		ItemPedido i = gerarItemPedidoCompra();
		Integer idItemEstoque = null;
		try {
			idItemEstoque = estoqueService.inserirItemPedido(i.getId());
		} catch (BusinessException e) {
			printMensagens(e);
		}

		ItemEstoque itemEstoque = estoqueService.pesquisarItemEstoqueById(idItemEstoque);
		itemEstoque.setQuantidade(0);
		boolean throwed = false;
		try {
			estoqueService.redefinirItemEstoque(itemEstoque);
		} catch (BusinessException e) {
			throwed = true;
		}
		assertTrue("O item de estoque deve conter quantidade", throwed);
	}

	@Test
	public void testRedefinicaoEstoqueMaterialQuantidadeNegativa() {
		ItemPedido i = gerarItemPedidoCompra();
		Integer idItemEstoque = null;
		try {
			idItemEstoque = estoqueService.inserirItemPedido(i.getId());
		} catch (BusinessException e) {
			printMensagens(e);
		}

		ItemEstoque itemEstoque = estoqueService.pesquisarItemEstoqueById(idItemEstoque);
		itemEstoque.setQuantidade(-1);
		boolean throwed = false;
		try {
			estoqueService.redefinirItemEstoque(itemEstoque);
		} catch (BusinessException e) {
			throwed = true;
		}
		assertTrue("O item de estoque deve conter quantidade positiva", throwed);
	}

	@Test
	public void testRedefinicaoEstoquePecaDescricaoNulo() {
		ItemPedido i = gerarItemPedidoCompra();
		Integer idItemEstoque = null;
		try {
			idItemEstoque = estoqueService.inserirItemPedido(i.getId());
		} catch (BusinessException e) {
			printMensagens(e);
		}

		ItemEstoque itemEstoque = estoqueService.pesquisarItemEstoqueById(idItemEstoque);
		itemEstoque.setFormaMaterial(FormaMaterial.PC);
		itemEstoque.setDescricaoPeca(null);
		boolean throwed = false;
		try {
			estoqueService.redefinirItemEstoque(itemEstoque);
		} catch (BusinessException e) {
			throwed = true;
		}
		assertTrue("O item de estoque nao deve conter descricao nula", throwed);

		itemEstoque = estoqueService.pesquisarItemEstoqueById(idItemEstoque);
		itemEstoque.setFormaMaterial(FormaMaterial.PC);
		itemEstoque.setDescricaoPeca("");

		throwed = false;
		try {
			estoqueService.redefinirItemEstoque(itemEstoque);
		} catch (BusinessException e) {
			throwed = true;
		}
		assertTrue("O item de estoque deve conter descricao", throwed);
	}

	@Test
	public void testRedefinicaoEstoquePrecoMedio() {
		ItemPedido i = gerarItemPedidoCompra();
		Integer idItemEstoque = null;
		try {
			idItemEstoque = estoqueService.inserirItemPedido(i.getId());
		} catch (BusinessException e) {
			printMensagens(e);
		}

		ItemEstoque itemEstoque = estoqueService.pesquisarItemEstoqueById(idItemEstoque);
		itemEstoque.setPrecoMedio(-1d);
		boolean throwed = false;
		try {
			estoqueService.redefinirItemEstoque(itemEstoque);
		} catch (BusinessException e) {
			throwed = true;
		}
		assertTrue("O item de estoque deve conter preco medio positivo", throwed);

		itemEstoque = estoqueService.pesquisarItemEstoqueById(idItemEstoque);
		itemEstoque.setPrecoMedio(0d);
		throwed = false;
		try {
			estoqueService.redefinirItemEstoque(itemEstoque);
		} catch (BusinessException e) {
			throwed = true;
		}
		assertTrue("O item de estoque deve conter preco medio", throwed);
	}

	@Test
	public void testRedefinicaoEstoquePrecoMedioNulo() {
		ItemPedido i = gerarItemPedidoCompra();
		Integer idItemEstoque = null;
		try {
			idItemEstoque = estoqueService.inserirItemPedido(i.getId());
		} catch (BusinessException e) {
			printMensagens(e);
		}

		ItemEstoque itemEstoque = estoqueService.pesquisarItemEstoqueById(idItemEstoque);
		itemEstoque.setPrecoMedio(null);
		boolean throwed = false;
		try {
			estoqueService.redefinirItemEstoque(itemEstoque);
		} catch (BusinessException e) {
			throwed = true;
		}
		assertTrue("O item de estoque deve conter preco medio", throwed);
	}

	@Test
	public void testRedefinicaoEstoqueQuantidadeNegativa() {
		ItemPedido i = gerarItemPedidoCompra();
		Integer idItemEstoque = null;
		try {
			idItemEstoque = estoqueService.inserirItemPedido(i.getId());
		} catch (BusinessException e) {
			printMensagens(e);
		}

		ItemEstoque itemEstoque = estoqueService.pesquisarItemEstoqueById(idItemEstoque);
		itemEstoque.setQuantidade(-1);
		boolean throwed = false;
		try {
			estoqueService.redefinirItemEstoque(itemEstoque);
		} catch (BusinessException e) {
			throwed = true;
		}
		assertTrue("O item de estoque deve conter quantidade positiva", throwed);
	}

	@Test
	public void testRedefinicaoInvalidaPecaComComprimento() {
		ItemPedido i = gerarItemPedidoCompra();
		Integer idItemEstoque = null;
		try {
			idItemEstoque = estoqueService.inserirItemPedido(i.getId());
		} catch (BusinessException e) {
			printMensagens(e);
		}

		ItemEstoque itemEstoque = estoqueService.pesquisarItemEstoqueById(idItemEstoque);
		itemEstoque.setFormaMaterial(FormaMaterial.PC);
		itemEstoque.setMedidaExterna(null);
		itemEstoque.setMedidaInterna(null);
		itemEstoque.setComprimento(100d);
		boolean throwed = false;
		try {
			estoqueService.redefinirItemEstoque(itemEstoque);
		} catch (BusinessException e) {
			throwed = true;
		}
		assertTrue("Pecas devem ter descricao preenchida", throwed);
	}

	@Test
	public void testRedefinicaoInvalidaPecaComMedidaExterna() {
		ItemPedido i = gerarItemPedidoCompra();
		Integer idItemEstoque = null;
		try {
			idItemEstoque = estoqueService.inserirItemPedido(i.getId());
		} catch (BusinessException e) {
			printMensagens(e);
		}

		ItemEstoque itemEstoque = estoqueService.pesquisarItemEstoqueById(idItemEstoque);
		itemEstoque.setFormaMaterial(FormaMaterial.PC);
		itemEstoque.setMedidaExterna(100d);
		itemEstoque.setMedidaInterna(null);
		itemEstoque.setComprimento(null);
		boolean throwed = false;
		try {
			estoqueService.redefinirItemEstoque(itemEstoque);
		} catch (BusinessException e) {
			throwed = true;
		}
		assertTrue("Pecas nao podem ter medida externa", throwed);
	}

	@Test
	public void testRedefinicaoInvalidaPecaComMedidaInterna() {
		ItemPedido i = gerarItemPedidoCompra();
		Integer idItemEstoque = null;
		try {
			idItemEstoque = estoqueService.inserirItemPedido(i.getId());
		} catch (BusinessException e) {
			printMensagens(e);
		}

		ItemEstoque itemEstoque = estoqueService.pesquisarItemEstoqueById(idItemEstoque);
		itemEstoque.setFormaMaterial(FormaMaterial.PC);
		itemEstoque.setMedidaExterna(null);
		itemEstoque.setMedidaInterna(100d);
		itemEstoque.setComprimento(null);
		boolean throwed = false;
		try {
			estoqueService.redefinirItemEstoque(itemEstoque);
		} catch (BusinessException e) {
			throwed = true;
		}
		assertTrue("Pecas nao podem ter medida interna", throwed);
	}

	@Test
	public void testRedefinirItemPedidoFormaQuadrada() {
		ItemPedido i = gerarItemPedidoCompra();
		Integer idItemEstoque = null;
		try {
			idItemEstoque = estoqueService.inserirItemPedido(i.getId());
		} catch (BusinessException e) {
			printMensagens(e);
		}

		ItemEstoque itemEstoque = estoqueService.pesquisarItemEstoqueById(idItemEstoque);
		itemEstoque.setFormaMaterial(FormaMaterial.BQ);

		try {
			estoqueService.redefinirItemEstoque(itemEstoque);
		} catch (BusinessException e) {
			printMensagens(e);
		}
		assertEquals("As medidas externa e interna devem ser iguais para barra quadrada", itemEstoque.getMedidaExterna(),
				itemEstoque.getMedidaInterna());
	}

	@Test
	public void testReservaItemEstoqueNaoExistente() {
		gerarItemEstoque();
		ItemPedido itemPedido = eBuilder.buildItemPedido();
		itemPedido.setFormaMaterial(FormaMaterial.PC);
		boolean reservado = true;
		try {
			reservado = estoqueService.reservarItemPedido(itemPedido);
		} catch (BusinessException e) {
			printMensagens(e);
		}
		assertFalse("Um item inexistente no estoque nao pode ser reservado", reservado);
	}

	@Test
	public void testReservaItemEstoqueQuantidadeIgualAoItemPedido() {
		ItemEstoque itemEstoque = gerarItemEstoque();
		ItemPedido itemPedido = eBuilder.buildItemPedido();
		itemPedido.setQuantidade(itemEstoque.getQuantidade());
		boolean reservado = true;
		try {
			reservado = estoqueService.reservarItemPedido(itemPedido);
		} catch (BusinessException e) {
			printMensagens(e);
		}
		assertTrue("A quantidade do estoque eh inferior ao pedido, mas pode ser reservado", reservado);
		itemEstoque = estoqueService.pesquisarItemEstoqueById(itemEstoque.getId());

		Integer quantidadeReservada = 2;
		Integer quantidadeEstoque = 0;
		assertEquals(quantidadeReservada, itemEstoque.getQuantidadeReservada());
		assertEquals("A quantidade no estoque era igual ao pedido e foi toda reservada", quantidadeEstoque,
				itemEstoque.getQuantidade());
	}

	@Test
	public void testReservaItemEstoqueQuantidadeInferiorAoItemPedido() {
		ItemEstoque itemEstoque = gerarItemEstoque();
		ItemPedido itemPedido = eBuilder.buildItemPedido();
		itemPedido.setQuantidade(itemEstoque.getQuantidade() + 1);
		boolean reservado = true;
		try {
			reservado = estoqueService.reservarItemPedido(itemPedido);
		} catch (BusinessException e) {
			printMensagens(e);
		}
		assertTrue("A quantidade do estoque eh inferior ao pedido, mas pode ser reservado", reservado);
		itemEstoque = estoqueService.pesquisarItemEstoqueById(itemEstoque.getId());

		Integer quantidadeReservada = 2;
		Integer quantidadeEstoque = 0;
		assertEquals(quantidadeReservada, itemEstoque.getQuantidadeReservada());
		assertEquals("A quantidade no estoque era inferior ao pedido e foi toda reservada", quantidadeEstoque,
				itemEstoque.getQuantidade());
	}

	@Test
	public void testReservaItemEstoqueQuantidadeSuperiorAoItemPedido() {
		ItemEstoque itemEstoque = gerarItemEstoque();
		ItemPedido itemPedido = eBuilder.buildItemPedido();
		itemPedido.setQuantidade(itemEstoque.getQuantidade() - 1);
		boolean reservado = true;
		try {
			reservado = estoqueService.reservarItemPedido(itemPedido);
		} catch (BusinessException e) {
			printMensagens(e);
		}
		assertTrue("A quantidade do estoque eh inferior ao pedido, mas pode ser reservado", reservado);
		itemEstoque = estoqueService.pesquisarItemEstoqueById(itemEstoque.getId());

		Integer quantidadeReservada = 1;
		Integer quantidadeEstoque = 1;
		assertEquals(quantidadeReservada, itemEstoque.getQuantidadeReservada());
		assertEquals("A quantidade no estoque era superior ao pedido e foi toda reservada", quantidadeEstoque,
				itemEstoque.getQuantidade());
	}

	@Test
	public void testReservaPedidoComItemNaoExistenteEstoque() {
		Pedido pedido = gerarPedido(TipoPedido.REVENDA);
		ItemPedido item1 = eBuilder.buildItemPedido();
		ItemPedido item2 = eBuilder.buildItemPedidoPeca();

		try {
			final Integer idItemPedido = pedidoService.inserirItemPedido(pedido.getId(), item1);
			pedidoService.inserirItemPedido(pedido.getId(), item2);
			// Inserindo apenas um dos itens para fazermos os testes de pendencia
			estoqueService.inserirItemPedido(idItemPedido);
		} catch (BusinessException e1) {
			printMensagens(e1);
		}

		boolean throwed = false;
		try {
			estoqueService.reservarItemPedido(pedido.getId());
		} catch (BusinessException e) {
			throwed = true;
		}
		assertTrue("Pedidos de representacao nao", throwed);
	}

	@Test
	public void testReservaPedidoCompraInvalido() {
		Pedido pedido = gerarPedido(TipoPedido.COMPRA);
		ItemPedido item1 = eBuilder.buildItemPedido();

		try {
			final Integer idItemPedido = pedidoService.inserirItemPedido(pedido.getId(), item1);
			estoqueService.inserirItemPedido(idItemPedido);
		} catch (BusinessException e1) {
			printMensagens(e1);
		}

		boolean throwed = false;
		try {
			estoqueService.reservarItemPedido(pedido.getId());
		} catch (BusinessException e) {
			throwed = true;
		}
		assertTrue("Pedidos de compra nao pode fazer reserva de estoque", throwed);
	}

	@Test
	public void testReservaPedidoRepresentadaInvalido() {
		Pedido pedido = gerarPedido(TipoPedido.REPRESENTACAO);
		ItemPedido item1 = eBuilder.buildItemPedido();

		try {
			final Integer idItemPedido = pedidoService.inserirItemPedido(pedido.getId(), item1);
			estoqueService.inserirItemPedido(idItemPedido);
		} catch (BusinessException e1) {
			printMensagens(e1);
		}

		boolean throwed = false;
		try {
			estoqueService.reservarItemPedido(pedido.getId());
		} catch (BusinessException e) {
			throwed = true;
		}
		assertTrue("Pedidos de representacao nao pode fazer reserva de estoque", throwed);
	}

	@Test
	public void testSituacaoPedidoAposInclusaoVariosItensNoEstoque() {
		ItemPedido i1 = gerarItemPedidoCompra();
		ItemPedido i2 = i1.clone();

		try {
			pedidoService.inserirItemPedido(i2);
			pedidoService.enviarPedido(i1.getPedido().getId(), new byte[] {});
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
