package br.com.plastecno.service.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Test;

import br.com.plastecno.service.ClienteService;
import br.com.plastecno.service.ComissaoService;
import br.com.plastecno.service.EstoqueService;
import br.com.plastecno.service.MaterialService;
import br.com.plastecno.service.PedidoService;
import br.com.plastecno.service.RepresentadaService;
import br.com.plastecno.service.UsuarioService;
import br.com.plastecno.service.constante.FormaMaterial;
import br.com.plastecno.service.constante.SituacaoPedido;
import br.com.plastecno.service.constante.SituacaoReservaEstoque;
import br.com.plastecno.service.constante.TipoApresentacaoIPI;
import br.com.plastecno.service.constante.TipoCliente;
import br.com.plastecno.service.constante.TipoPedido;
import br.com.plastecno.service.constante.TipoRelacionamento;
import br.com.plastecno.service.constante.TipoVenda;
import br.com.plastecno.service.entity.Cliente;
import br.com.plastecno.service.entity.ItemEstoque;
import br.com.plastecno.service.entity.ItemPedido;
import br.com.plastecno.service.entity.LimiteMinimoEstoque;
import br.com.plastecno.service.entity.Material;
import br.com.plastecno.service.entity.Pedido;
import br.com.plastecno.service.entity.Representada;
import br.com.plastecno.service.entity.Usuario;
import br.com.plastecno.service.exception.BusinessException;
import br.com.plastecno.service.impl.anotation.TODO;
import br.com.plastecno.service.test.builder.ServiceBuilder;

public class EstoqueServiceTest extends AbstractTest {
	private ClienteService clienteService;
	private ComissaoService comissaoService;
	private EstoqueService estoqueService;
	private MaterialService materialService;
	private PedidoService pedidoService;
	private RepresentadaService representadaService;
	private UsuarioService usuarioService;

	public EstoqueServiceTest() {
		estoqueService = ServiceBuilder.buildService(EstoqueService.class);
		pedidoService = ServiceBuilder.buildService(PedidoService.class);
		usuarioService = ServiceBuilder.buildService(UsuarioService.class);
		clienteService = ServiceBuilder.buildService(ClienteService.class);
		materialService = ServiceBuilder.buildService(MaterialService.class);
		representadaService = ServiceBuilder.buildService(RepresentadaService.class);
		comissaoService = ServiceBuilder.buildService(ComissaoService.class);
	}

	private ItemPedido enviarItemPedido(Integer quantidade, TipoPedido tipoPedido) {
		Pedido pedido = gerarPedido(tipoPedido);

		ItemPedido itemPedido = eBuilder.buildItemPedido();
		if (quantidade != null) {
			itemPedido.setQuantidade(quantidade);
		}
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

	private ItemPedido enviarItemPedido(TipoPedido tipoPedido) {
		return enviarItemPedido(null, tipoPedido);
	}

	private ItemPedido enviarItemPedidoCompra() {
		return enviarItemPedido(TipoPedido.COMPRA);
	}

	private ItemPedido enviarItemPedidoRevenda() {
		return enviarItemPedido(TipoPedido.REVENDA);
	}

	private ItemPedido enviarItemPedidoRevenda(Integer quantidade) {
		return enviarItemPedido(quantidade, TipoPedido.REVENDA);
	}

	private ItemEstoque gerarItemEstoque() {
		ItemEstoque itemEstoque = eBuilder.buildItemEstoque();
		itemEstoque.setMaterial(gerarMaterial());

		try {
			estoqueService.inserirItemEstoque(itemEstoque);
		} catch (BusinessException e) {
			printMensagens(e);
		}
		return itemEstoque;
	}

	private ItemPedido gerarItemPedidoClone(Integer quantidade, ItemPedido item1) {
		// Garantindo que o material eh o mesmo para manter a consistencia dos
		// dados
		// entre item pedido e item estoque.
		ItemPedido item2 = item1.clone();
		if (quantidade != null) {
			item2.setQuantidade(quantidade);
		}
		try {
			pedidoService.inserirItemPedido(item1.getPedido().getId(), item2);
		} catch (BusinessException e) {
			printMensagens(e);
		}
		return item2;
	}

	private ItemPedido gerarItemPedidoClone(ItemPedido item1) {
		return gerarItemPedidoClone(null, item1);
	}

	private ItemEstoque gerarItemPedidoNoEstoque() {
		ItemPedido i = enviarItemPedidoCompra();
		Integer idItemEstoque = null;
		try {
			idItemEstoque = estoqueService.inserirItemPedido(i.getId());
		} catch (BusinessException e) {
			printMensagens(e);
		}

		return estoqueService.pesquisarItemEstoqueById(idItemEstoque);
	}

	private LimiteMinimoEstoque gerarLimiteMinimoEstoque() {
		LimiteMinimoEstoque l = eBuilder.buildLimiteMinimoEstoque();
		l.setMaterial(gerarMaterial());
		return l;
	}

	private LimiteMinimoEstoque gerarLimiteMinimoEstoqueParaItemEstoque(ItemEstoque item) {
		LimiteMinimoEstoque l = eBuilder.buildLimiteMinimoEstoque();
		l.setFormaMaterial(item.getFormaMaterial());
		l.setMaterial(item.getMaterial());
		l.setMedidaExterna(item.getMedidaExterna());
		l.setMedidaInterna(item.getMedidaInterna());
		l.setComprimento(item.getComprimento());
		return l;
	}

	private List<ItemPedido> gerarListaItemPedido(TipoPedido tipoPedido) {
		List<ItemPedido> listaItem = new ArrayList<ItemPedido>();
		Pedido pedido = gerarPedido(tipoPedido);
		Material material = gerarMaterial(pedido.getRepresentada().getId());

		ItemPedido itemTB1 = eBuilder.buildItemPedido();
		itemTB1.setMaterial(material);
		itemTB1.setQuantidade(1);
		itemTB1.setPrecoVenda(1d);
		itemTB1.setTipoVenda(TipoVenda.PECA);

		ItemPedido itemTB2 = eBuilder.buildItemPedido();
		itemTB2.setMaterial(material);
		itemTB2.setQuantidade(1);
		itemTB2.setPrecoVenda(1d);
		itemTB2.setTipoVenda(TipoVenda.PECA);

		ItemPedido itemBQ = eBuilder.buildItemPedido();
		itemBQ.setFormaMaterial(FormaMaterial.BQ);
		itemBQ.setMaterial(material);
		itemBQ.setQuantidade(1);
		itemBQ.setPrecoVenda(1d);
		itemBQ.setTipoVenda(TipoVenda.PECA);

		listaItem.add(itemTB1);
		listaItem.add(itemTB2);
		listaItem.add(itemBQ);

		try {
			for (ItemPedido itemPedido : listaItem) {
				pedidoService.inserirItemPedido(pedido.getId(), itemPedido);
			}
		} catch (BusinessException e1) {
			printMensagens(e1);
		}

		try {
			pedidoService.enviarPedido(pedido.getId(), new byte[] {});
		} catch (BusinessException e1) {
			printMensagens(e1);
		}
		return listaItem;
	}

	private Material gerarMaterial() {
		return gerarMaterial(null);
	}

	private Material gerarMaterial(Integer idRepresentada) {
		Material material = eBuilder.buildMaterial();
		List<Material> lista = materialService.pesquisarBySigla(material.getSigla());
		if (lista.isEmpty()) {

			Representada representada = representadaService.pesquisarById(idRepresentada);
			if (representada == null) {
				representada = gerarRepresentada();
			}
			material.addRepresentada(representada);
			try {
				material.setId(materialService.inserir(material));
			} catch (BusinessException e) {
				printMensagens(e);
			}
		} else {
			material = lista.get(0);
		}
		return material;
	}

	private Pedido gerarPedido(TipoPedido tipoPedido) {
		Usuario vendedor = gerarVendedor();

		Pedido pedido = eBuilder.buildPedido();
		pedido.setTipoPedido(tipoPedido);
		pedido.setVendedor(vendedor);

		Cliente cliente = pedido.getCliente();
		cliente.setVendedor(vendedor);
		try {
			clienteService.inserir(cliente);
		} catch (BusinessException e) {
			printMensagens(e);
		}

		pedido.setRepresentada(gerarRepresentada());
		try {
			pedido = pedidoService.inserir(pedido);
		} catch (BusinessException e1) {
			printMensagens(e1);
		}

		return pedido;
	}

	private Representada gerarRepresentada() {
		Representada representada = eBuilder.buildRepresentada();
		representada.setTipoApresentacaoIPI(TipoApresentacaoIPI.SEMPRE);
		try {
			representadaService.inserir(representada);
		} catch (BusinessException e3) {
			printMensagens(e3);
		}
		return representada;
	}

	private Usuario gerarVendedor() {
		Usuario vendedor = eBuilder.buildVendedor();
		try {
			usuarioService.inserir(vendedor, true);
		} catch (BusinessException e2) {
			printMensagens(e2);
		}

		try {
			comissaoService.inserirComissaoVendedor(vendedor.getId(), 0.9);
		} catch (BusinessException e) {
			printMensagens(e);
		}
		return vendedor;
	}

	private Integer pesquisarQuantidadeTotalItemEstoque(Integer idItemEstoque) {
		ItemEstoque i = estoqueService.pesquisarItemEstoqueById(idItemEstoque);
		return i != null ? i.getQuantidade() : 0;

	}

	@Test
	public void testAlteracaoItemPedidoNoEstoque() {
		ItemPedido item1 = enviarItemPedidoCompra();
		ItemPedido item2 = gerarItemPedidoClone(item1.getQuantidade() + 100, item1);

		Integer idItemEstoque = null;
		try {
			idItemEstoque = estoqueService.inserirItemPedido(item1.getId());
		} catch (BusinessException e) {
			printMensagens(e);
		}

		verificarQuantidadeTotalItemEstoque(item1.getQuantidade(), idItemEstoque);
		Integer quantidadeAntes = pesquisarQuantidadeTotalItemEstoque(idItemEstoque);

		try {
			idItemEstoque = estoqueService.inserirItemPedido(item2.getId());
		} catch (BusinessException e) {
			printMensagens(e);
		}
		quantidadeAntes += item2.getQuantidade();
		verificarQuantidadeTotalItemEstoque(quantidadeAntes, idItemEstoque);
	}

	@Test
	public void testAlteracaoMedidaLimiteMinimoEstoque() {
		LimiteMinimoEstoque limite = eBuilder.buildLimiteMinimoEstoque();
		limite.setMaterial(gerarMaterial());
		Integer idLimite = null;
		Integer idLimiteAlterado = null;

		try {
			idLimite = estoqueService.associarLimiteMinimoEstoque(limite);
		} catch (BusinessException e) {
			printMensagens(e);
		}

		LimiteMinimoEstoque limiteAlterado = limite.clone();

		// ALTERANDO O COMPRIMENTO
		limiteAlterado.setComprimento(limite.getComprimento() + 100);

		try {
			idLimiteAlterado = estoqueService.associarLimiteMinimoEstoque(limiteAlterado);
		} catch (BusinessException e) {
			printMensagens(e);
		}

		assertTrue("O limite minimo com medida alterada deve ter um novo ID no sistema", idLimite != idLimiteAlterado);

		limiteAlterado = limite.clone();

		// ALTERANDO A MEDIDA EXTERNA
		limiteAlterado.setMedidaExterna(limite.getMedidaExterna() + 100);

		try {
			idLimiteAlterado = estoqueService.associarLimiteMinimoEstoque(limiteAlterado);
		} catch (BusinessException e) {
			printMensagens(e);
		}

		assertTrue("O limite minimo com medida alterada deve ter um novo ID no sistema", idLimite != idLimiteAlterado);

		limiteAlterado = limite.clone();

		// ALTERANDO A MEDIDA INTERNA
		limiteAlterado.setMedidaInterna(limite.getMedidaInterna() - 10);

		try {
			idLimiteAlterado = estoqueService.associarLimiteMinimoEstoque(limiteAlterado);
		} catch (BusinessException e) {
			printMensagens(e);
		}

		assertTrue("O limite minimo com medida alterada deve ter um novo ID no sistema", idLimite != idLimiteAlterado);

	}

	@Test
	public void testAlteracaoQuantidadeLimiteMinimoEstoque() {
		LimiteMinimoEstoque limite = eBuilder.buildLimiteMinimoEstoque();
		limite.setMaterial(gerarMaterial());
		Integer idLimite = null;
		Integer idLimiteAlterado = null;

		try {
			idLimite = estoqueService.associarLimiteMinimoEstoque(limite);
		} catch (BusinessException e) {
			printMensagens(e);
		}

		LimiteMinimoEstoque limiteAlterado = limite.clone();

		limiteAlterado.setQuantidadeMinima(limite.getQuantidadeMinima() + 10);

		try {
			idLimiteAlterado = estoqueService.associarLimiteMinimoEstoque(limiteAlterado);
		} catch (BusinessException e) {
			printMensagens(e);
		}

		assertEquals("Os IDs dos limites devem ser os mesmos apos a alteracao da quantidade minima", idLimite,
				idLimiteAlterado);
	}

	@Test
	public void testAssociacaoLimiteMinimoEstoque() {
		LimiteMinimoEstoque l1 = gerarLimiteMinimoEstoque();

		// Apenas o primeiro elemento sera associado ao limite. Como todos eles
		// ja existem no repositorio, vamos apenas alterar suas medidas para que
		// o algoritmo de escassez encontre-os no estoque.
		ItemEstoque i1 = gerarItemPedidoNoEstoque();
		i1.setQuantidade(l1.getQuantidadeMinima() - 1);
		i1.setMedidaExterna(l1.getMedidaExterna());
		i1.setMedidaInterna(l1.getMedidaInterna());
		i1.setComprimento(l1.getComprimento());

		ItemEstoque i2 = gerarItemPedidoNoEstoque();
		i2.setQuantidade(l1.getQuantidadeMinima() - 2);
		i2.setMedidaExterna(l1.getMedidaExterna() + 10);
		i2.setMedidaInterna(l1.getMedidaInterna());
		i2.setComprimento(l1.getComprimento());

		ItemEstoque i3 = gerarItemPedidoNoEstoque();
		i3.setQuantidade(l1.getQuantidadeMinima() - 2);
		i3.setMedidaExterna(l1.getMedidaExterna() + 20);
		i3.setMedidaInterna(l1.getMedidaInterna());
		i3.setComprimento(l1.getComprimento());

		try {
			// Aqui vamos associar os 2 itens criados anteriomente.
			estoqueService.associarLimiteMinimoEstoque(l1);
		} catch (BusinessException e) {
			printMensagens(e);
		}

		List<ItemEstoque> listaItemEscasso = estoqueService.pesquisarItemEstoqueEscasso();
		assertEquals(
				"Foram incluidos 3 itens no estoque com quantidades abaixo do limite mas apenas o primeiro foi associado ao limite",
				1, listaItemEscasso.size());

		LimiteMinimoEstoque l2 = gerarLimiteMinimoEstoque();
		// Criando uma nova medida
		l2.setComprimento(l2.getComprimento() + 100);

		ItemEstoque i4 = gerarItemPedidoNoEstoque();
		i4.setQuantidade(l2.getQuantidadeMinima() - 1);
		i4.setMedidaExterna(l2.getMedidaExterna());
		i4.setMedidaInterna(l2.getMedidaInterna());
		i4.setComprimento(l2.getComprimento());

		try {
			// Aqui vamos associar um segundo limite minimo
			estoqueService.associarLimiteMinimoEstoque(l2);
		} catch (BusinessException e) {
			printMensagens(e);
		}

		listaItemEscasso = estoqueService.pesquisarItemEstoqueEscasso();
		assertEquals(
				"Foi incluido mais um item no estoque com quantidades abaixo do limite agora devemos ter apenas 2 item escassos",
				2, listaItemEscasso.size());
	}

	@Test
	public void testCalculoPrecoVendaSugerido() {
		ItemEstoque itemEstoque = gerarItemEstoque();
		Integer idItem = itemEstoque.getId();

		LimiteMinimoEstoque limite = gerarLimiteMinimoEstoqueParaItemEstoque(itemEstoque);
		try {
			estoqueService.associarLimiteMinimoEstoque(limite);
		} catch (BusinessException e) {
			printMensagens(e);
		}

		itemEstoque = estoqueService.pesquisarItemEstoqueById(idItem);
		assertEquals("Os IDs do itens devem ser os mesmo pois estamos pesquisando um item que ja existe no sistema",
				idItem, itemEstoque.getId());

		assertNotNull("Deve existir um limite minimo para o item pois ja temos um limite cadastrado no sistema para ele",
				itemEstoque.getLimiteMinimoEstoque());

		Double precoSugerido = itemEstoque.getPrecoMedio() * (1 + itemEstoque.getFormaMaterial().getIpi())
				* (1 + limite.getTaxaMinima());

		Double precoCalculado = null;
		try {
			precoCalculado = estoqueService.calcularPrecoSugeridoItemEstoque(itemEstoque);

		} catch (BusinessException e) {
			printMensagens(e);
		}
		assertEquals("Os algoritmos de calculo do preco de venda sugerido possuem diferencas, isso esta errado",
				precoSugerido, precoCalculado);
	}

	@Test
	public void testCalculoPrecoVendaSugeridoSemTaxa() {
		ItemEstoque itemEstoque = gerarItemEstoque();
		Integer idItem = itemEstoque.getId();

		LimiteMinimoEstoque limite = gerarLimiteMinimoEstoqueParaItemEstoque(itemEstoque);
		// Anulando a taxa do limite minimo
		limite.setTaxaMinima(null);

		try {
			estoqueService.associarLimiteMinimoEstoque(limite);
		} catch (BusinessException e) {
			printMensagens(e);
		}

		itemEstoque = estoqueService.pesquisarItemEstoqueById(idItem);
		assertEquals("Os IDs do itens devem ser os mesmo pois estamos pesquisando um item que ja existe no sistema",
				idItem, itemEstoque.getId());

		assertNotNull("Deve existir um limite minimo para o item pois ja temos um limite cadastrado no sistema para ele",
				itemEstoque.getLimiteMinimoEstoque());

		Double precoSugerido = itemEstoque.getPrecoMedio() * (1 + itemEstoque.getFormaMaterial().getIpi());

		Double precoCalculado = null;
		try {
			precoCalculado = estoqueService.calcularPrecoSugeridoItemEstoque(itemEstoque);

		} catch (BusinessException e) {
			printMensagens(e);
		}
		assertEquals("Os algoritmos de calculo do preco de venda sugerido possuem diferencas, isso esta errado",
				precoSugerido, precoCalculado);
	}

	@Test
	public void testInclusaoItemInexistenteEstoque() {
		ItemEstoque itemEstoque = gerarItemPedidoNoEstoque();
		itemEstoque.setId(null);
		try {
			estoqueService.inserirItemEstoque(itemEstoque);
		} catch (BusinessException e) {
			printMensagens(e);
		}
	}

	@Test
	public void testInclusaoItemInvalidoComDescricao() {
		ItemEstoque itemEstoque = gerarItemPedidoNoEstoque();
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
		ItemPedido i = enviarItemPedidoCompra();

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
	public void testInclusaoLimiteMinimoEstoque() {
		LimiteMinimoEstoque limite = eBuilder.buildLimiteMinimoEstoque();
		limite.setMaterial(gerarMaterial());
		try {
			estoqueService.associarLimiteMinimoEstoque(limite);
		} catch (BusinessException e) {
			printMensagens(e);
		}
	}

	@Test
	public void testInclusaoLimiteMinimoEstoqueSemMaterial() {
		LimiteMinimoEstoque limite = eBuilder.buildLimiteMinimoEstoque();
		limite.setMaterial(null);

		boolean throwed = false;
		try {
			estoqueService.associarLimiteMinimoEstoque(limite);
		} catch (BusinessException e) {
			throwed = true;
		}
		assertTrue("O material nao pode ser nulo e deve ser validada", throwed);

		limite.setMaterial(gerarMaterial());
		limite.setFormaMaterial(null);
		throwed = false;
		try {
			estoqueService.associarLimiteMinimoEstoque(limite);
		} catch (BusinessException e) {
			throwed = true;
		}
		assertTrue("A forma de material nao pode ser nula e deve ser validada", throwed);
	}

	@Test
	public void testInclusaoLimiteMinimoEstoqueSemMedidas() {
		LimiteMinimoEstoque limite = eBuilder.buildLimiteMinimoEstoque();
		limite.setMaterial(gerarMaterial());
		limite.setComprimento(null);
		limite.setMedidaExterna(null);
		limite.setMedidaInterna(null);

		boolean throwed = false;
		try {
			estoqueService.associarLimiteMinimoEstoque(limite);
		} catch (BusinessException e) {
			throwed = true;
		}
		assertTrue("O limite minimo de estoque nao contem medidas preenchidas e deve ser validado", throwed);
	}

	@Test
	public void testInclusaoLimiteMinimoEstoqueSemQuantidade() {
		LimiteMinimoEstoque limite = eBuilder.buildLimiteMinimoEstoque();
		limite.setMaterial(gerarMaterial());
		limite.setQuantidadeMinima(null);

		boolean throwed = false;
		try {
			estoqueService.associarLimiteMinimoEstoque(limite);
		} catch (BusinessException e) {
			throwed = true;
		}
		assertTrue("A quantidade minina nao pode ser nula e deve ser validada", throwed);

		limite.setQuantidadeMinima(0);
		throwed = false;
		try {
			estoqueService.associarLimiteMinimoEstoque(limite);
		} catch (BusinessException e) {
			throwed = true;
		}
		assertTrue("A quantidade minina nao pode ser nula e deve ser validada", throwed);

		limite.setQuantidadeMinima(-1);
		throwed = false;
		try {
			estoqueService.associarLimiteMinimoEstoque(limite);
		} catch (BusinessException e) {
			throwed = true;
		}
		assertTrue("A quantidade minina nao pode ser negativa e deve ser validada", throwed);
	}

	@Test
	public void testInclusaoPecaExistenteEstoque() {
		ItemEstoque itemEstoque = gerarItemPedidoNoEstoque();
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
	public void testPesquisarItemEstoqueCadastrado() {
		ItemEstoque itemEstoque = gerarItemPedidoNoEstoque();
		itemEstoque.setId(null);
		Integer idItemEstoque = null;
		try {
			idItemEstoque = estoqueService.inserirItemEstoque(itemEstoque);
		} catch (BusinessException e) {
			printMensagens(e);
		}
		ItemEstoque itemClone = itemEstoque.clone();
		ItemEstoque itemCadastrado = estoqueService.pesquisarItemEstoque(itemClone);

		assertNotNull(
				"O item pesquisado eh identico ao cadastrado, portanto deve existir no sistema. Possivel falha na inclusao",
				itemCadastrado);
		assertEquals("O item pesquisado eh identico ao cadastrado, portanto deve existir no sistema com o mesmo ID",
				idItemEstoque, itemCadastrado.getId());

		final double tolerancia = 0.02d;
		// Alterando o comprimento em 1mm para testar a tolerancia.
		itemClone.setComprimento(itemEstoque.getComprimento() + tolerancia);

		itemCadastrado = estoqueService.pesquisarItemEstoque(itemClone);
		assertNull(
				"Foi adicionado o valor de tolerancia ao item de estoque para verificar que ele nao existe no estoque e deve ser nulo",
				itemCadastrado);
		// Alterando o comprimento em 1mm para testar a tolerancia.
		itemClone.setComprimento(itemEstoque.getComprimento() - tolerancia);

		itemCadastrado = estoqueService.pesquisarItemEstoque(itemClone);
		assertNull(
				"Foi subtraido o valor de tolerancia ao item de estoque para verificar que ele nao existe no estoque e deve ser nulo",
				itemCadastrado);
	}

	@Test
	public void testRecepcionarItemPedidoCompra() {
		ItemPedido i = enviarItemPedidoCompra();
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
		ItemPedido i = enviarItemPedidoCompra();
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
		ItemEstoque itemEstoque = gerarItemPedidoNoEstoque();
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
		ItemEstoque itemEstoque = gerarItemPedidoNoEstoque();
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
		ItemEstoque itemEstoque = gerarItemPedidoNoEstoque();
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
		ItemEstoque itemEstoque = gerarItemPedidoNoEstoque();
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
		ItemEstoque itemEstoque = gerarItemPedidoNoEstoque();
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
		ItemEstoque itemEstoque = gerarItemPedidoNoEstoque();
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
		ItemEstoque itemEstoque = gerarItemPedidoNoEstoque();
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
		ItemEstoque itemEstoque = gerarItemPedidoNoEstoque();
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
		ItemPedido i = enviarItemPedidoCompra();
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
		ItemPedido i = enviarItemPedidoCompra();
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
		ItemPedido i = enviarItemPedidoCompra();
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
		ItemPedido i = enviarItemPedidoCompra();
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
		ItemPedido i = enviarItemPedidoCompra();
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
		ItemPedido i = enviarItemPedidoCompra();
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
		ItemPedido i = enviarItemPedidoCompra();
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
		ItemPedido i = enviarItemPedidoCompra();
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
		ItemPedido i = enviarItemPedidoCompra();
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
		ItemPedido i = enviarItemPedidoCompra();
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
		ItemPedido i = enviarItemPedidoCompra();
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
		ItemPedido i = enviarItemPedidoCompra();
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
		ItemPedido i = enviarItemPedidoCompra();
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
	public void testRemocaoLimiteMinimoEstoque() {
		LimiteMinimoEstoque limite = eBuilder.buildLimiteMinimoEstoque();
		limite.setMaterial(gerarMaterial());
		Integer idLimite = null;
		try {
			idLimite = estoqueService.associarLimiteMinimoEstoque(limite);
		} catch (BusinessException e) {
			printMensagens(e);
		}

		// Esse eh a condicao para remocao do limite minimo
		limite.setQuantidadeMinima(0);
		try {
			estoqueService.associarLimiteMinimoEstoque(limite);
		} catch (BusinessException e) {
			printMensagens(e);
		}
		limite = estoqueService.pesquisarLimiteMinimoEstoqueById(idLimite);
		assertNull("O limite minimo foi removido do sistema e nao pode ser retornado na pesquisa", limite);
	}

	@Test
	public void testReservaItemEstoqueNaoExistente() {
		gerarItemPedidoNoEstoque();

		ItemPedido item1 = enviarItemPedidoRevenda();
		ItemPedido item2 = gerarItemPedidoClone(item1);
		SituacaoReservaEstoque situacaoReservaEstoque = null;

		try {
			situacaoReservaEstoque = estoqueService.reservarItemPedido(item2);
		} catch (BusinessException e) {
			printMensagens(e);
		}
		assertEquals("Um item inexistente no estoque nao pode ser reservado", SituacaoReservaEstoque.NAO_CONTEM_ESTOQUE,
				situacaoReservaEstoque);
	}

	@Test
	public void testReservaItemEstoqueQuantidadeIgualAoItemPedido() {
		ItemEstoque itemEstoque = gerarItemPedidoNoEstoque();
		itemEstoque.setQuantidade(itemEstoque.getQuantidade() + 10);
		try {
			estoqueService.redefinirItemEstoque(itemEstoque);
		} catch (BusinessException e1) {
			printMensagens(e1);
		}

		ItemPedido item1 = enviarItemPedidoRevenda();
		// Garantindo que o material eh o mesmo para manter a consistencia dos
		// dados
		// entre item pedido e item estoque.

		ItemPedido item2 = gerarItemPedidoClone(item1);
		item2.setQuantidade(itemEstoque.getQuantidade());

		try {
			pedidoService.inserirItemPedido(item1.getPedido().getId(), item2);
		} catch (BusinessException e1) {
			printMensagens(e1);
		}

		SituacaoReservaEstoque situacaoReservaEstoque = null;
		try {
			situacaoReservaEstoque = estoqueService.reservarItemPedido(item2);
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
		ItemEstoque itemEstoque = gerarItemPedidoNoEstoque();
		ItemPedido item1 = enviarItemPedidoRevenda();

		itemEstoque.setQuantidade(10);
		try {
			estoqueService.redefinirItemEstoque(itemEstoque);
		} catch (BusinessException e1) {
			printMensagens(e1);
		}

		ItemPedido item2 = gerarItemPedidoClone(item1);
		item2.setQuantidade(itemEstoque.getQuantidade() + 1);

		try {
			pedidoService.inserirItemPedido(item1.getPedido().getId(), item2);
		} catch (BusinessException e1) {
			printMensagens(e1);
		}

		SituacaoReservaEstoque situacaoReservaEstoque = null;
		try {
			situacaoReservaEstoque = estoqueService.reservarItemPedido(item2);
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

	@Test
	public void testReservaItemEstoqueQuantidadeSuperiorAoItemPedido() {
		ItemEstoque itemEstoque = gerarItemPedidoNoEstoque();
		itemEstoque.setQuantidade(itemEstoque.getQuantidade() + 10);

		try {
			estoqueService.inserirItemEstoque(itemEstoque);
		} catch (BusinessException e1) {
			printMensagens(e1);
		}

		// Nesse ponto ocorrera integracao com o estoque e o item de estque tera
		// sua
		// quantidade alterada
		ItemPedido item1 = enviarItemPedidoRevenda(2);
		ItemPedido item2 = gerarItemPedidoClone(itemEstoque.getQuantidade() - 1, item1);

		try {
			pedidoService.inserirItemPedido(item1.getPedido().getId(), item2);
		} catch (BusinessException e1) {
			printMensagens(e1);
		}

		SituacaoReservaEstoque situacaoReservaEstoque = null;
		try {
			situacaoReservaEstoque = estoqueService.reservarItemPedido(item2);
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

			// Inserindo apenas um dos itens para fazermos os testes de
			// pendencia
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
			pedidoService.inserirItemPedido(pedido.getId(), item1);
		} catch (BusinessException e2) {
			printMensagens(e2);
		}

		try {
			pedidoService.inserirItemPedido(pedido.getId(), item2);
		} catch (BusinessException e1) {
			printMensagens(e1);
		}

		try {
			pedidoService.enviarPedido(pedido.getId(), new byte[] {});
		} catch (BusinessException e2) {
			printMensagens(e2);
		}

		Representada fornecedor = pedido.getRepresentada();
		fornecedor.setTipoRelacionamento(TipoRelacionamento.FORNECIMENTO);

		Cliente revendedor = pedido.getCliente();
		revendedor.setTipoCliente(TipoCliente.REVENDEDOR);

		Set<Integer> ids = new HashSet<Integer>();
		ids.add(item1.getId());
		try {
			pedidoService.comprarItemPedido(pedido.getProprietario().getId(), fornecedor.getId(), ids);
		} catch (BusinessException e2) {
			printMensagens(e2);
		}

		assertEquals(SituacaoPedido.ITEM_AGUARDANDO_COMPRA, pedidoService.pesquisarSituacaoPedidoById(pedido.getId()));
	}

	@Test
	public void testReservaPedidoRepresentadaInvalido() {
		Pedido pedido = gerarPedido(TipoPedido.REPRESENTACAO);
		ItemPedido item1 = eBuilder.buildItemPedido();
		item1.setMaterial(gerarMaterial(pedido.getRepresentada().getId()));

		boolean throwed = false;

		try {
			final Integer idItemPedido = pedidoService.inserirItemPedido(pedido.getId(), item1);
			estoqueService.inserirItemPedido(idItemPedido);
		} catch (BusinessException e1) {
			throwed = true;
		}

		assertTrue("Pedidos de representacao nao pode fazer incluir item no estoque", throwed);

		throwed = false;
		try {
			estoqueService.reservarItemPedido(pedido.getId());
		} catch (BusinessException e) {
			throwed = true;
		}
		assertTrue("Pedidos de representacao nao pode fazer reserva de estoque", throwed);
	}

	@Test
	public void testSituacaoPedidoAposInclusaoVariosItensNoEstoque() {
		ItemPedido i1 = enviarItemPedidoCompra();
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

	@Test
	public void testValorEstoqueFormaMaterial() {
		List<ItemPedido> listaItemComprado = gerarListaItemPedido(TipoPedido.COMPRA);
		for (ItemPedido itemPedido : listaItemComprado) {
			try {
				estoqueService.inserirItemPedido(itemPedido.getId());
			} catch (BusinessException e) {
				printMensagens(e);
			}
		}
		Double totalEstoque = 1d;
		assertEquals(totalEstoque, estoqueService.calcularValorEstoque(null, FormaMaterial.BQ));

		totalEstoque = 2d;
		assertEquals(totalEstoque, estoqueService.calcularValorEstoque(null, FormaMaterial.TB));
	}

	@Test
	public void testValorTotalEstoque() {
		List<ItemPedido> listaItemComprado = gerarListaItemPedido(TipoPedido.COMPRA);
		for (ItemPedido itemPedido : listaItemComprado) {
			try {
				estoqueService.inserirItemPedido(itemPedido.getId());
			} catch (BusinessException e) {
				printMensagens(e);
			}
		}
		Double totalEstoque = 3d;
		assertEquals(totalEstoque, estoqueService.calcularValorEstoque(null, null));
	}

	private void verificarQuantidadeTotalItemEstoque(Integer quantidadeItemPedido, Integer idItemEstoque) {
		Integer quantidadeItemEstoque = pesquisarQuantidadeTotalItemEstoque(idItemEstoque);
		assertEquals("As quantidades dos itens devem ser as mesmas apos inclusao no estoque", quantidadeItemPedido,
				quantidadeItemEstoque);

	}
}
