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
import br.com.plastecno.service.constante.SituacaoReservaEstoque;
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
import br.com.plastecno.service.impl.anotation.TODO;

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
		itemPedido.setMaterial(gerarMaterial(pedido.getRepresentada().getId()));

		try {
			final Integer id = pedidoService.inserirItemPedido(pedido.getId(), itemPedido);
			itemPedido.setId(id);
		} catch (BusinessException e1) {
			printMensagens(e1);
		}

		try {
			pedidoService.enviarPedido(pedido.getId(), new byte[] {});
		} catch (BusinessException e) {
			printMensagens(e);
		}
		return itemPedido;
	}

	private ItemPedido gerarItemPedidoCompra() {
		return gerarItemPedido(TipoPedido.COMPRA);
	}

	private ItemPedido gerarItemPedidoRevenda() {
		return gerarItemPedido(TipoPedido.REVENDA);
	}

	private Material gerarMaterial(Integer idRepresentada) {
		Material material = eBuilder.buildMaterial();
		material.addRepresentada(representadaService.pesquisarById(idRepresentada));
		try {
			material.setId(materialService.inserir(material));
		} catch (BusinessException e) {
			printMensagens(e);
		}
		return material;
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
		cliente.setVendedor(vendedor);
		try {
			clienteService.inserir(cliente);
		} catch (BusinessException e) {
			printMensagens(e);
		}

		Representada representada = eBuilder.buildRepresentada();
		representada.setTipoApresentacaoIPI(TipoApresentacaoIPI.SEMPRE);
		try {
			representadaService.inserir(representada);
		} catch (BusinessException e3) {
			printMensagens(e3);
		}

		pedido.setRepresentada(representada);
		try {
			pedido = pedidoService.inserir(pedido);
		} catch (BusinessException e1) {
			printMensagens(e1);
		}

		Material material = eBuilder.buildMaterial();
		material.addRepresentada(representada);
		try {
			material.setId(materialService.inserir(material));
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

		try {
			pedidoService.enviarPedido(i.getPedido().getId(), new byte[] {});
		} catch (BusinessException e1) {
			printMensagens(e1);
		}

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
	public void testRecepcionarItemPedidoCompra() {
		ItemPedido i = gerarItemPedidoCompra();
		try {
			pedidoService.alterarQuantidadeRecepcionada(i.getId(), i.getQuantidade());
		} catch (BusinessException e) {
			printMensagens(e);
		}

		try {
			estoqueService.recepcionarItemCompra(i.getId());
		} catch (BusinessException e) {
			printMensagens(e);
		}

		assertEquals(SituacaoPedido.COMPRA_RECEBIDA, pedidoService.pesquisarSituacaoPedidoById(i.getPedido().getId()));
	}

	@Test
	public void testRecepcionarItemPedidoCompraQuantidadeInferior() {
		ItemPedido i = gerarItemPedidoCompra();
		Integer quantidadeRecepcionada = i.getQuantidade() - 1;
		try {
			pedidoService.alterarQuantidadeRecepcionada(i.getId(), quantidadeRecepcionada);
		} catch (BusinessException e) {
			printMensagens(e);
		}

		try {
			estoqueService.recepcionarItemCompra(i.getId());
		} catch (BusinessException e) {
			printMensagens(e);
		}

		assertEquals(SituacaoPedido.COMPRA_AGUARDANDO_RECEBIMENTO,
				pedidoService.pesquisarSituacaoPedidoById(i.getPedido().getId()));
	}

	@TODO
	public void testRecortarItemEstoque() {
		ItemEstoque itemEstoque = gerarItemEstoque();
		ItemEstoque itemRecortado = itemEstoque.clone();

		itemRecortado.setId(itemEstoque.getId());
		itemRecortado.setQuantidade(itemEstoque.getQuantidade() - 1);
		// Configurando a reducao ou recorte do item do estoque.
		itemRecortado.setMedidaExterna(itemEstoque.getMedidaExterna() - 10);
		itemRecortado.setMedidaInterna(itemEstoque.getMedidaInterna() - 2);
		itemRecortado.setComprimento(itemEstoque.getComprimento() - 1);

		Integer idItemNovo = null;
		try {
			idItemNovo = estoqueService.recortarItemEstoque(itemRecortado);
		} catch (BusinessException e) {
			printMensagens(e);
		}
		ItemEstoque itemNovo = estoqueService.pesquisarItemEstoqueById(idItemNovo);
		assertEquals("A quantidade do item novo deve ser a mesma que a do item recortado apos a inclusao no estoque",
				itemNovo.getQuantidade(), itemRecortado.getQuantidade());
	}

	@Test
	public void testRecortarItemEstoqueComprimentoInvalido() {
		ItemEstoque itemEstoque = gerarItemEstoque();
		ItemEstoque itemRecortado = itemEstoque.clone();

		itemRecortado.setId(itemEstoque.getId());
		itemRecortado.setComprimento(itemEstoque.getComprimento() + 1);

		boolean throwed = false;
		try {
			estoqueService.recortarItemEstoque(itemRecortado);
		} catch (BusinessException e) {
			throwed = true;
		}
		assertTrue("A comprimento recortado eh superior ao comprimento do estoque", throwed);
	}

	@TODO
	public void testRecortarItemEstoqueDimensaoInvalida() {
		ItemEstoque itemEstoque = gerarItemEstoque();
		ItemEstoque itemRecortado = itemEstoque.clone();

		itemRecortado.setId(itemEstoque.getId());
		itemRecortado.setQuantidade(itemEstoque.getQuantidade() - 1);
		// Configurando a reducao ou recorte do item do estoque.
		itemRecortado.setMedidaExterna(itemEstoque.getMedidaExterna() - 1);
		itemRecortado.setMedidaInterna(itemEstoque.getMedidaInterna() - 1);
		itemRecortado.setComprimento(itemEstoque.getComprimento() - 1);

		Integer idItemNovo = null;
		try {
			idItemNovo = estoqueService.recortarItemEstoque(itemRecortado);
		} catch (BusinessException e) {
			printMensagens(e);
		}
		ItemEstoque itemNovo = estoqueService.pesquisarItemEstoqueById(idItemNovo);
		assertEquals("A quantidade do item novo deve ser a mesma que a do item recortado apos a inclusao no estoque",
				itemNovo.getQuantidade(), itemRecortado.getQuantidade());
	}

	@Test
	public void testRecortarItemEstoqueInexistente() {
		ItemEstoque itemEstoque = gerarItemEstoque();
		ItemEstoque itemRecortado = new ItemEstoque();
		// Alterando o ID para simular uma pesquisa de um item inexistente no
		// estoque.
		itemRecortado.setId(itemEstoque.getId() + 1);
		boolean throwed = false;
		try {
			estoqueService.recortarItemEstoque(itemRecortado);
		} catch (BusinessException e) {
			throwed = true;
		}
		assertTrue("Nao se pode recortar um item que nao existe no estoque", throwed);
	}

	@Test
	public void testRecortarItemEstoqueMedidaExternaInvalida() {
		ItemEstoque itemEstoque = gerarItemEstoque();
		ItemEstoque itemRecortado = itemEstoque.clone();

		itemRecortado.setId(itemEstoque.getId());
		itemRecortado.setMedidaExterna(itemEstoque.getMedidaExterna() + 1);

		boolean throwed = false;
		try {
			estoqueService.recortarItemEstoque(itemRecortado);
		} catch (BusinessException e) {
			throwed = true;
		}
		assertTrue("A medida externa recortada eh superior a medida do estoque", throwed);
	}

	@Test
	public void testRecortarItemEstoqueMedidaInternaInvalida() {
		ItemEstoque itemEstoque = gerarItemEstoque();
		ItemEstoque itemRecortado = itemEstoque.clone();

		itemRecortado.setId(itemEstoque.getId());
		itemRecortado.setMedidaInterna(itemEstoque.getMedidaInterna() + 1);

		boolean throwed = false;
		try {
			estoqueService.recortarItemEstoque(itemRecortado);
		} catch (BusinessException e) {
			throwed = true;
		}
		assertTrue("A medida interna recortada eh superior a medida do estoque", throwed);
	}

	@Test
	public void testRecortarItemEstoqueQuantidadeInvalida() {
		ItemEstoque itemEstoque = gerarItemEstoque();
		ItemEstoque itemRecortado = itemEstoque.clone();

		itemRecortado.setId(itemEstoque.getId());
		itemRecortado.setQuantidade(itemEstoque.getQuantidade() + 1);
		boolean throwed = false;
		try {
			estoqueService.recortarItemEstoque(itemRecortado);
		} catch (BusinessException e) {
			throwed = true;
		}
		assertTrue("A quantidade do item recortado nao pode ser superior ao item do estoque", throwed);
	}

	@Test
	public void testRecortarPecaEstoque() {
		ItemEstoque itemEstoque = gerarItemEstoque();
		itemEstoque.setFormaMaterial(FormaMaterial.PC);
		boolean throwed = false;
		try {
			estoqueService.recortarItemEstoque(itemEstoque);
		} catch (BusinessException e) {
			throwed = true;
		}
		assertTrue("Nao se pode recortar uma peca do estoque", throwed);
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
		try {
			estoqueService.redefinirItemEstoque(itemEstoque);
		} catch (BusinessException e) {
			printMensagens(e);
		}
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
			printMensagens(e);
		}
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

		ItemPedido itemPedido = gerarItemPedidoRevenda();

		SituacaoReservaEstoque situacaoReservaEstoque = null;
		try {
			situacaoReservaEstoque = estoqueService.reservarItemPedido(itemPedido);
		} catch (BusinessException e) {
			printMensagens(e);
		}
		assertEquals("Um item inexistente no estoque nao pode ser reservado", SituacaoReservaEstoque.NAO_CONTEM_ESTOQUE,
				situacaoReservaEstoque);
	}

	@Test
	public void testReservaItemEstoqueQuantidadeIgualAoItemPedido() {
		ItemEstoque itemEstoque = gerarItemEstoque();
		ItemPedido itemPedido = gerarItemPedidoRevenda();
		// Garantindo que o material eh o mesmo para manter a consistencia dos dados
		// entre item pedido e item estoque.
		itemPedido.setMaterial(itemEstoque.getMaterial());
		itemPedido.setQuantidade(itemEstoque.getQuantidade());
		SituacaoReservaEstoque situacaoReservaEstoque = null;
		try {
			situacaoReservaEstoque = estoqueService.reservarItemPedido(itemPedido);
		} catch (BusinessException e) {
			printMensagens(e);
		}
		assertEquals("A quantidade do estoque eh inferior ao pedido, mas pode ser reservado",
				SituacaoReservaEstoque.UNIDADES_TODAS_RESERVADAS, situacaoReservaEstoque);
		itemEstoque = estoqueService.pesquisarItemEstoqueById(itemEstoque.getId());

		Integer quantidadeEstoque = 0;
		assertEquals("A quantidade no estoque era igual ao pedido e foi toda reservada", quantidadeEstoque,
				itemEstoque.getQuantidade());
	}

	@Test
	public void testReservaItemEstoqueQuantidadeInferiorAoItemPedido() {
		ItemEstoque itemEstoque = gerarItemEstoque();
		ItemPedido itemPedido = gerarItemPedidoRevenda();

		// Garantindo que o material eh o mesmo para manter a consistencia dos dados
		// entre item pedido e item estoque.
		itemPedido.setFormaMaterial(itemEstoque.getFormaMaterial());
		itemPedido.setMaterial(itemEstoque.getMaterial());
		itemPedido.setQuantidade(itemEstoque.getQuantidade() + 1);

		SituacaoReservaEstoque situacaoReservaEstoque = null;
		try {
			situacaoReservaEstoque = estoqueService.reservarItemPedido(itemPedido);
		} catch (BusinessException e) {
			printMensagens(e);
		}
		assertEquals("A quantidade do estoque eh inferior ao pedido, mas pode ser reservado",
				SituacaoReservaEstoque.UNIDADES_PARCIALEMENTE_RESERVADAS, situacaoReservaEstoque);
		itemEstoque = estoqueService.pesquisarItemEstoqueById(itemEstoque.getId());

		Integer quantidadeEstoque = 0;
		assertEquals("A quantidade no estoque era inferior ao pedido e foi toda reservada", quantidadeEstoque,
				itemEstoque.getQuantidade());
	}

	/*
	 * @Test public void testReservaPedidoCompraInvalido() { Pedido pedido =
	 * gerarPedido(TipoPedido.COMPRA); ItemPedido item1 =
	 * eBuilder.buildItemPedido();
	 * 
	 * try { final Integer idItemPedido =
	 * pedidoService.inserirItemPedido(pedido.getId(), item1);
	 * estoqueService.inserirItemPedido(idItemPedido); } catch (BusinessException
	 * e1) { printMensagens(e1); }
	 * 
	 * boolean throwed = false; try {
	 * estoqueService.reservarItemPedido(pedido.getId()); } catch
	 * (BusinessException e) { throwed = true; }
	 * assertTrue("Pedidos de compra nao pode fazer reserva de estoque", throwed);
	 * }
	 */

	@Test
	public void testReservaItemEstoqueQuantidadeSuperiorAoItemPedido() {
		ItemEstoque itemEstoque = gerarItemEstoque();

		ItemPedido itemPedido = gerarItemPedidoRevenda();
		// Os materiais devem ser o mesmo para manter a consistencia do negocio.
		itemPedido.setMaterial(itemEstoque.getMaterial());
		itemPedido.setQuantidade(itemEstoque.getQuantidade() - 1);

		SituacaoReservaEstoque situacaoReservaEstoque = null;
		try {
			situacaoReservaEstoque = estoqueService.reservarItemPedido(itemPedido);
		} catch (BusinessException e) {
			printMensagens(e);
		}
		assertEquals("A quantidade do estoque eh superior ao pedido e deve ser reservado",
				SituacaoReservaEstoque.UNIDADES_TODAS_RESERVADAS, situacaoReservaEstoque);
		itemEstoque = estoqueService.pesquisarItemEstoqueById(itemEstoque.getId());

		Integer quantidadeEstoque = 1;
		assertEquals("A quantidade no estoque era superior ao pedido e foi toda reservada", quantidadeEstoque,
				itemEstoque.getQuantidade());
	}

	@Test
	public void testReservaPedidoComTodosItensExistentesEstoque() {
		Pedido pedido = gerarPedido(TipoPedido.COMPRA);
		Representada representada = pedido.getRepresentada();
		Material material = gerarMaterial(pedido.getRepresentada().getId());

		ItemPedido item1 = eBuilder.buildItemPedido();
		ItemPedido item2 = eBuilder.buildItemPedidoPeca();

		item1.setMaterial(material);
		item2.setMaterial(material);

		try {
			pedidoService.inserirItemPedido(pedido.getId(), item1);
			pedidoService.inserirItemPedido(pedido.getId(), item2);

			pedidoService.enviarPedido(pedido.getId(), new byte[] {});

			// Inserindo apenas um dos itens para fazermos os testes de pendencia
			estoqueService.inserirItemPedido(item1.getId());
			estoqueService.inserirItemPedido(item2.getId());
		} catch (BusinessException e1) {
			printMensagens(e1);
		}

		Pedido pedidoRevenda = gerarPedido(TipoPedido.REVENDA);
		pedidoRevenda.setRepresentada(representada);
		ItemPedido itemRevenda1 = eBuilder.buildItemPedido();
		ItemPedido itemRevenda2 = eBuilder.buildItemPedidoPeca();

		itemRevenda1.setMaterial(material);
		itemRevenda2.setMaterial(material);

		try {
			pedidoService.inserirItemPedido(pedidoRevenda.getId(), itemRevenda1);
		} catch (BusinessException e1) {
			printMensagens(e1);
		}

		try {
			pedidoService.inserirItemPedido(pedidoRevenda.getId(), itemRevenda2);
		} catch (BusinessException e1) {
			printMensagens(e1);
		}

		try {
			pedidoService.enviarPedido(pedidoRevenda.getId(), new byte[] {});
		} catch (BusinessException e1) {
			printMensagens(e1);
		}

		assertEquals("Todos os itens desse pedido existem no estoque e deve estar pronto para o empacotamento",
				SituacaoPedido.REVENDA_AGUARDANDO_EMPACOTAMENTO, pedidoRevenda.getSituacaoPedido());
	}

	@Test
	public void testReservaPedidoComUmDosItemNaoExistenteEstoque() {
		Pedido pedido = gerarPedido(TipoPedido.REVENDA);
		Material material = gerarMaterial(pedido.getRepresentada().getId());

		ItemPedido item1 = eBuilder.buildItemPedido();
		item1.setPedido(pedido);
		item1.setMaterial(material);

		ItemPedido item2 = eBuilder.buildItemPedidoPeca();
		item2.setPedido(pedido);
		item2.setMaterial(material);

		try {
			final Integer idItemPedido = pedidoService.inserirItemPedido(pedido.getId(), item1);
			pedidoService.inserirItemPedido(pedido.getId(), item2);
			// Inserindo apenas um dos itens para fazermos os testes de pendencia
			estoqueService.inserirItemPedido(idItemPedido);
		} catch (BusinessException e1) {
			printMensagens(e1);
		}

		boolean existe = false;
		try {
			existe = estoqueService.reservarItemPedido(pedido.getId());
		} catch (BusinessException e) {
			printMensagens(e);
		}
		assertFalse("O pedido nao contem todos os itens no estoque", existe);
	}

	@Test
	public void testReservaPedidoRepresentadaInvalido() {
		Pedido pedido = gerarPedido(TipoPedido.REPRESENTACAO);
		ItemPedido item1 = eBuilder.buildItemPedido();
		item1.setMaterial(gerarMaterial(pedido.getRepresentada().getId()));

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
		// Fabricando um segundo item para facilitar.
		ItemPedido i2 = eBuilder.buildItemPedidoPeca();
		i2.setPedido(i1.getPedido());
		i2.setMaterial(i1.getMaterial());

		try {
			pedidoService.inserirItemPedido(i2.getPedido().getId(), i2);
		} catch (BusinessException e2) {
			printMensagens(e2);
		}

		try {
			// Nesse ponto vamos popular o estoque com o pedido de compras
			pedidoService.enviarPedido(i1.getPedido().getId(), new byte[] {});
		} catch (BusinessException e1) {
			printMensagens(e1);
		}

		try {
			// Inserindo no estque o item do pedido de compras
			estoqueService.inserirItemPedido(i1.getId());
		} catch (BusinessException e) {
			printMensagens(e);
		}
		assertEquals(SituacaoPedido.COMPRA_AGUARDANDO_RECEBIMENTO, i1.getPedido().getSituacaoPedido());

		try {
			estoqueService.inserirItemPedido(i2.getId());
		} catch (BusinessException e) {
			printMensagens(e);
		}
		assertEquals(SituacaoPedido.COMPRA_RECEBIDA, i2.getPedido().getSituacaoPedido());

	}

	private void verificarQuantidadeTotalItemEstoque(Integer quantidadeItemPedido, Integer idItemEstoque) {
		Integer quantidadeItemEstoque = pesquisarQuantidadeTotalItemEstoque(idItemEstoque);
		assertEquals("As quantidades dos itens devem ser as mesmas apos inclusao no estoque", quantidadeItemPedido,
				quantidadeItemEstoque);

	}

}
