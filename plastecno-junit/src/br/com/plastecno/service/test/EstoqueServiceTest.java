package br.com.plastecno.service.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
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
import br.com.plastecno.service.entity.ItemReservado;
import br.com.plastecno.service.entity.Material;
import br.com.plastecno.service.entity.Pedido;
import br.com.plastecno.service.entity.Representada;
import br.com.plastecno.service.entity.Usuario;
import br.com.plastecno.service.exception.BusinessException;
import br.com.plastecno.service.impl.anotation.TODO;
import br.com.plastecno.service.mensagem.email.AnexoEmail;
import br.com.plastecno.service.test.builder.ServiceBuilder;
import br.com.plastecno.util.NumeroUtils;

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
			pedidoService.enviarPedido(pedido.getId(), new AnexoEmail(new byte[] {}));
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

	private ItemEstoque gerarConfiguracaoEstoque() {
		ItemEstoque configuracao = eBuilder.buildItemEstoque();
		configuracao.setMaterial(gerarMaterial());
		configuracao.setMargemMinimaLucro(0.1d);
		configuracao.setQuantidadeMinima(10);
		configuracao.setAliquotaIPI(0.1d);
		configuracao.setAliquotaICMS(0.06d);
		return configuracao;
	}

	private ItemEstoque gerarConfiguracaoEstoque(ItemEstoque itemEstoque) {
		ItemEstoque configuracao = itemEstoque.clone();
		configuracao.setMargemMinimaLucro(0.1d);
		configuracao.setQuantidadeMinima(10);
		return configuracao;
	}

	private ItemEstoque gerarConfiguracaoEstoquePeca() {
		ItemEstoque configuracao = gerarItemEstoquePeca();
		configuracao.setMargemMinimaLucro(0.1d);
		configuracao.setQuantidadeMinima(10);
		configuracao.setAliquotaIPI(0.1d);
		configuracao.setAliquotaICMS(0.06d);
		return configuracao;
	}

	private Representada gerarFornecedor() {
		Representada fornecedor = eBuilder.buildRepresentadaFornecedor();
		fornecedor.setTipoApresentacaoIPI(TipoApresentacaoIPI.SEMPRE);

		try {
			representadaService.inserir(fornecedor);
		} catch (BusinessException e3) {
			printMensagens(e3);
		}
		return fornecedor;
	}

	private ItemEstoque gerarItemEstoqueComMedidaInterna(FormaMaterial formaMaterial) {
		ItemEstoque item = eBuilder.buildItemEstoque();
		item.setMaterial(gerarMaterial());
		item.setFormaMaterial(formaMaterial);
		return item;
	}

	private ItemEstoque gerarItemEstoquePeca() {
		ItemEstoque configuracao = eBuilder.buildItemEstoquePeca();
		configuracao.setMaterial(gerarMaterial());
		return configuracao;
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
			idItemEstoque = estoqueService.recepcionarItemCompra(i.getId(), i.getQuantidade());
		} catch (BusinessException e) {
			printMensagens(e);
		}

		return estoqueService.pesquisarItemEstoqueById(idItemEstoque);
	}

	private ItemPedido gerarItemPedidoPeca(TipoPedido tipoPedido) {
		Pedido pedido = gerarPedido(tipoPedido);
		Material material = gerarMaterial(pedido.getRepresentada().getId());

		ItemPedido itemPedido = eBuilder.buildItemPedido();
		itemPedido.setMaterial(material);
		itemPedido.setQuantidade(1);
		itemPedido.setPrecoVenda(1d);
		itemPedido.setTipoVenda(TipoVenda.PECA);

		try {
			pedidoService.inserirItemPedido(pedido.getId(), itemPedido);
		} catch (BusinessException e1) {
			printMensagens(e1);
		}

		try {
			pedidoService.enviarPedido(pedido.getId(), new AnexoEmail(new byte[] {}));
		} catch (BusinessException e1) {
			printMensagens(e1);
		}
		return itemPedido;
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
			pedidoService.enviarPedido(pedido.getId(), new AnexoEmail(new byte[] {}));
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

		Cliente cliente = null;
		Representada representada = null;

		if (TipoPedido.COMPRA.equals(tipoPedido)) {
			cliente = eBuilder.buildClienteRevendedor();
			representada = gerarFornecedor();

		} else {
			cliente = eBuilder.buildCliente();
			representada = gerarRepresentada();
		}

		pedido.setCliente(cliente);
		pedido.setRepresentada(representada);

		cliente.setVendedor(vendedor);
		try {
			clienteService.inserir(cliente);
		} catch (BusinessException e) {
			printMensagens(e);
		}

		// No sistema sempre devemos ter o revendedor cadastrado.
		gerarRevendedor();

		try {
			pedido = pedidoService.inserirPedido(pedido);
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

	private Representada gerarRevendedor() {
		Representada revendedor = eBuilder.buildRepresentadaRevendedora();
		try {
			representadaService.inserir(revendedor);
		} catch (BusinessException e3) {
			printMensagens(e3);
		}
		return revendedor;
	}

	private Usuario gerarVendedor() {
		Usuario vendedor = eBuilder.buildVendedor();
		try {
			usuarioService.inserir(vendedor, true);
		} catch (BusinessException e2) {
			printMensagens(e2);
		}

		try {
			comissaoService.inserirComissaoRevendaVendedor(vendedor.getId(), 0.9);
		} catch (BusinessException e) {
			printMensagens(e);
		}
		return vendedor;
	}

	private Integer pesquisarQuantidadeTotalItemEstoque(Integer idItemEstoque) {
		ItemEstoque i = estoqueService.pesquisarItemEstoqueById(idItemEstoque);
		return i != null ? i.getQuantidade() : 0;

	}

	private Integer recepcionarItemCompra() {
		ItemPedido i = enviarItemPedidoCompra();
		Integer idItemEstoque = null;
		try {
			idItemEstoque = estoqueService.recepcionarItemCompra(i.getId(), i.getQuantidade());
		} catch (BusinessException e) {
			printMensagens(e);
		}
		return idItemEstoque;
	}

	@Test
	public void testAlteracaoItemPedidoNoEstoque() {
		ItemPedido item1 = enviarItemPedidoCompra();
		ItemPedido item2 = gerarItemPedidoClone(item1.getQuantidade() + 100, item1);

		Integer idItemEstoque = null;
		try {
			idItemEstoque = estoqueService.recepcionarItemCompra(item1.getId(), item1.getQuantidade());
		} catch (BusinessException e) {
			printMensagens(e);
		}

		verificarQuantidadeTotalItemEstoque(item1.getQuantidade(), idItemEstoque);
		Integer quantidadeAntes = pesquisarQuantidadeTotalItemEstoque(idItemEstoque);

		try {
			idItemEstoque = estoqueService.recepcionarItemCompra(item2.getId(), item2.getQuantidade());
		} catch (BusinessException e) {
			printMensagens(e);
		}
		quantidadeAntes += item2.getQuantidade();
		verificarQuantidadeTotalItemEstoque(quantidadeAntes, idItemEstoque);
	}

	@Test
	public void testAlteracaoMedidaConfiguracaoEstoque() {
		Integer idItemEstoque = recepcionarItemCompra();
		ItemEstoque item1 = estoqueService.pesquisarItemEstoqueById(idItemEstoque);

		ItemEstoque configuracao = item1.clone();
		configuracao.setQuantidadeMinima(10);
		configuracao.setMargemMinimaLucro(0.1d);

		try {
			estoqueService.inserirConfiguracaoEstoque(configuracao);
		} catch (BusinessException e) {
			printMensagens(e);
		}

		item1 = estoqueService.pesquisarItemEstoqueById(item1.getId());
		Integer antes = item1.getQuantidadeMinima();
		Integer depois = null;

		configuracao.setQuantidadeMinima(antes + 10);

		try {
			estoqueService.inserirConfiguracaoEstoque(configuracao);
		} catch (BusinessException e) {
			printMensagens(e);
		}

		item1 = estoqueService.pesquisarItemEstoqueById(item1.getId());
		depois = item1.getQuantidadeMinima();
		assertTrue("A quantidade minima de estoque foi alterada e por isso os valores devem ser diferentes",
				!antes.equals(depois));
	}

	@Test
	public void testCalculoPrecoMinimo() {
		Integer idItemEstoque = recepcionarItemCompra();
		ItemEstoque itemEstoque = estoqueService.pesquisarItemEstoqueById(idItemEstoque);

		ItemEstoque configuracao = gerarConfiguracaoEstoque(itemEstoque);

		try {
			estoqueService.inserirConfiguracaoEstoque(configuracao);
		} catch (BusinessException e) {
			printMensagens(e);
		}

		Double precoMinimo = 93.57;

		Double precoMinimoCalculado = null;
		try {
			precoMinimoCalculado = estoqueService.calcularPrecoMinimoItemEstoque(itemEstoque);
		} catch (BusinessException e) {
			printMensagens(e);
		}
		assertEquals(precoMinimo, precoMinimoCalculado);
	}

	@Test
	public void testCalculoPrecoMinimoSemFatorICMS() {
		Integer idItemEstoque = recepcionarItemCompra();
		ItemEstoque itemEstoque = estoqueService.pesquisarItemEstoqueById(idItemEstoque);

		try {
			estoqueService.inserirItemEstoque(itemEstoque);
		} catch (BusinessException e1) {
			printMensagens(e1);
		}

		itemEstoque = estoqueService.pesquisarItemEstoqueById(idItemEstoque);

		ItemEstoque configuracao = gerarConfiguracaoEstoque(itemEstoque);
		configuracao.setMargemMinimaLucro(null);

		try {
			estoqueService.inserirConfiguracaoEstoque(configuracao);
		} catch (BusinessException e) {
			printMensagens(e);
		}

		Double precoMinimo = 75.95;
		Double precoMinimoCalculado = null;
		try {
			itemEstoque.setPrecoMedioFatorICMS(null);
			precoMinimoCalculado = estoqueService.calcularPrecoMinimoItemEstoque(itemEstoque);
		} catch (BusinessException e) {
			printMensagens(e);
		}

		assertEquals(
				"O preco minimo de venda sem o fator icms deve ser o mesmo que o preco medio cadastrado. Verificar o algoritmo de calculo.",
				precoMinimo, precoMinimoCalculado);
	}

	@Test
	public void testCalculoPrecoMinimoSemTaxa() {
		Integer idItemEstoque = recepcionarItemCompra();

		ItemEstoque itemEstoque = estoqueService.pesquisarItemEstoqueById(idItemEstoque);

		ItemEstoque configuracao = gerarConfiguracaoEstoque(itemEstoque);
		configuracao.setMargemMinimaLucro(null);

		try {
			estoqueService.inserirConfiguracaoEstoque(configuracao);
		} catch (BusinessException e) {
			printMensagens(e);
		}

		Double precoMinimo = 85.06;
		Double precoMinimoCalculado = null;
		try {
			precoMinimoCalculado = estoqueService.calcularPrecoMinimoItemEstoque(itemEstoque);
		} catch (BusinessException e) {
			printMensagens(e);
		}
		assertEquals("O preco minimo de venda do item de estoque nao esta correto. Verificar o algoritmo de calculo.",
				precoMinimo, precoMinimoCalculado);
	}

	@Test
	public void testInclusaoConfiguracaoEstoque() {
		ItemEstoque configuracao = gerarConfiguracaoEstoque();

		try {
			estoqueService.inserirConfiguracaoEstoque(configuracao);
		} catch (BusinessException e) {
			printMensagens(e);
		}
	}

	@Test
	public void testInclusaoConfiguracaoEstoquePeca() {
		ItemPedido itemPeca = gerarItemPedidoPeca(TipoPedido.COMPRA);

		try {
			estoqueService.recepcionarItemCompra(itemPeca.getId(), itemPeca.getQuantidade());
		} catch (BusinessException e1) {
			printMensagens(e1);
		}

		ItemEstoque configuracao = gerarConfiguracaoEstoquePeca();
		try {
			estoqueService.inserirConfiguracaoEstoque(configuracao);
		} catch (BusinessException e) {
			printMensagens(e);
		}

		// Item peca = estoqueService.pesquisarItemEstoqueById(idPeca);
		// assertEquals(peca.getQuantidadeMinima(),
		// limite.getQuantidadeMinima());
	}

	@Test
	public void testInclusaoConfiguracaoEstoqueSemMaterial() {

		ItemEstoque configuracao = gerarConfiguracaoEstoque();
		configuracao.setMaterial(null);

		boolean throwed = false;
		try {
			estoqueService.inserirConfiguracaoEstoque(configuracao);
		} catch (BusinessException e) {
			throwed = true;
		}

		assertTrue("O material para inserir limite minimo nao exite, eh obrigatorio e deve ser validado", throwed);

		configuracao = gerarConfiguracaoEstoque();
		configuracao.getMaterial().setId(null);

		throwed = false;
		try {
			estoqueService.inserirConfiguracaoEstoque(configuracao);
		} catch (BusinessException e) {
			throwed = true;
		}

		assertTrue("O id do material para inserir limite minimo nao exite, eh obrigatorio e deve ser validado", throwed);
	}

	@Test
	public void testInclusaoConfiguracaoEstoqueSemMedidas() {

		Integer idItem1 = recepcionarItemCompra();
		Integer idItem2 = recepcionarItemCompra();

		ItemEstoque item1 = estoqueService.pesquisarItemEstoqueById(idItem1);
		ItemEstoque item2 = estoqueService.pesquisarItemEstoqueById(idItem2);

		// Estmos alterando o item2 pois mesmo com medida diferente ele devera
		// ter
		// seu limite configurado.
		item2.setMedidaExterna(item2.getMedidaExterna() + 20);
		item2.setComprimento(item2.getComprimento() + 20);

		try {
			estoqueService.inserirItemEstoque(item2);
		} catch (BusinessException e1) {
			printMensagens(e1);
		}

		ItemEstoque configuracao = gerarConfiguracaoEstoque();
		configuracao.setMaterial(item1.getMaterial());
		configuracao.setFormaMaterial(item1.getFormaMaterial());
		configuracao.setMedidaExterna(null);
		configuracao.setMedidaInterna(null);
		configuracao.setComprimento(null);

		try {
			estoqueService.inserirConfiguracaoEstoque(configuracao);
		} catch (BusinessException e) {
			printMensagens(e);
		}

		item1 = estoqueService.pesquisarItemEstoqueById(item1.getId());
		item2 = estoqueService.pesquisarItemEstoqueById(item2.getId());

		assertEquals("Os valores da margem minima de lucro devem ser as mesmas apos o cadastro do limite minimo",
				item1.getMargemMinimaLucro(), configuracao.getMargemMinimaLucro());
		assertEquals("Os valores da quantidade minima devem ser as mesmas apos o cadastro do limite minimo",
				item1.getQuantidadeMinima(), configuracao.getQuantidadeMinima());

		assertEquals("Os valores da margem minima de lucro devem ser as mesmas apos o cadastro do limite minimo",
				item2.getMargemMinimaLucro(), configuracao.getMargemMinimaLucro());
		assertEquals("Os valores da quantidade minima devem ser as mesmas apos o cadastro do limite minimo",
				item2.getQuantidadeMinima(), configuracao.getQuantidadeMinima());
	}

	@Test
	public void testInclusaoConfiguracaoEstoqueSemQuantidade() {
		ItemEstoque configuracao = gerarConfiguracaoEstoque();
		configuracao.setQuantidadeMinima(-1);

		try {
			estoqueService.inserirConfiguracaoEstoque(configuracao);
		} catch (BusinessException e) {
			printMensagens(e);
		}

		assertNull("A quantidade minina de estoque deve ser nula no caso de valores menores ou iguais a zero",
				configuracao.getQuantidadeMinima());

		configuracao = gerarConfiguracaoEstoque();
		configuracao.setQuantidadeMinima(0);

		try {
			estoqueService.inserirConfiguracaoEstoque(configuracao);
		} catch (BusinessException e) {
			printMensagens(e);
		}

		assertNull("A quantidade minina de estoque deve ser nula no caso de valores menores ou iguais a zero",
				configuracao.getQuantidadeMinima());
	}

	@Test
	public void testInclusaoConfiguracaoNcm() {
		ItemEstoque tubo = gerarItemEstoqueComMedidaInterna(FormaMaterial.TB);
		ItemEstoque tubo2 = gerarItemEstoqueComMedidaInterna(FormaMaterial.TB);

		ItemEstoque barra = gerarItemEstoqueComMedidaInterna(FormaMaterial.BQ);

		Integer idTubo = null;
		Integer idTubo2 = null;
		Integer idBarra = null;
		Integer idMaterial = tubo.getMaterial().getId();
		try {
			idTubo = estoqueService.inserirItemEstoque(tubo);
			idTubo2 = estoqueService.inserirItemEstoque(tubo2);
			idBarra = estoqueService.inserirItemEstoque(barra);
		} catch (BusinessException e) {
			printMensagens(e);
		}

		String ncm = "11.11.11.11";
		estoqueService.inserirConfiguracaoNcmEstoque(idMaterial, FormaMaterial.TB, ncm);

		tubo = estoqueService.pesquisarItemEstoqueById(idTubo);
		tubo2 = estoqueService.pesquisarItemEstoqueById(idTubo2);
		barra = estoqueService.pesquisarItemEstoqueById(idBarra);

		assertEquals(
				"Foi configurado um ncm para os tubos e os valores nao esta identicos. Verifique a regra de negocio.",
				ncm, tubo.getNcm());
		assertEquals(
				"Foi configurado um ncm para os tubos e os valores nao esta identicos. Verifique a regra de negocio.",
				ncm, tubo2.getNcm());
		assertNull(
				"Nao foi configurado um ncm para as barras entao os valores devem estar nulos. Verifique a regra de negocio.",
				barra.getNcm());

		boolean configurou = false;

		configurou = estoqueService.inserirConfiguracaoNcmEstoque(null, FormaMaterial.TB, ncm);
		assertFalse("O item nao contem material e nao pode ter o ncm configurado.", configurou);

		ItemEstoque itemSemForma = gerarItemEstoqueComMedidaInterna(null);
		itemSemForma.setMaterial(null);

		configurou = estoqueService.inserirConfiguracaoNcmEstoque(idMaterial, null, ncm);
		assertFalse("O item nao contem material e nao pode ter o ncm configurado.", configurou);

	}

	@Test
	public void testInclusaoInvalidaPecaComMedidaInterna() {

		ItemEstoque itemEstoque = gerarItemEstoquePeca();

		itemEstoque.setFormaMaterial(FormaMaterial.PC);
		itemEstoque.setMedidaInterna(100d);

		boolean throwed = false;
		try {
			estoqueService.inserirItemEstoque(itemEstoque);
		} catch (BusinessException e) {
			throwed = true;
		}
		assertTrue("Peca nao pode ter nenhuma medida e essa condicao nao foi validada", throwed);
	}

	@Test
	public void testInclusaoItemInexistenteEstoque() {
		ItemEstoque itemEstoque = gerarItemPedidoNoEstoque();
		itemEstoque.setId(null);

		Integer idItem = null;

		try {
			idItem = estoqueService.inserirItemEstoque(itemEstoque);
		} catch (BusinessException e) {
			printMensagens(e);
		}

		itemEstoque = estoqueService.pesquisarItemEstoqueById(idItem);
		Double precoFatorICMS = 76.63;

		assertEquals(
				"Apos a inclusao de um item novo deve-se aplicar o fator ICMS no preco de custo. Verifique o algoritmo de calculo",
				precoFatorICMS, NumeroUtils.arredondarValorMonetario(itemEstoque.getPrecoMedioFatorICMS()));
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
	public void testInclusaoItemPedidoValidoNoEstoque() {
		ItemPedido i = enviarItemPedidoCompra();

		try {
			pedidoService.enviarPedido(i.getPedido().getId(), new AnexoEmail(new byte[] {}));
		} catch (BusinessException e1) {
			printMensagens(e1);
		}

		Integer idItemEstoque = null;
		try {
			idItemEstoque = estoqueService.recepcionarItemCompra(i.getId(), i.getQuantidade());
		} catch (BusinessException e) {
			printMensagens(e);
		}

		verificarQuantidadeTotalItemEstoque(i.getQuantidade(), idItemEstoque);

		assertEquals(SituacaoPedido.COMPRA_RECEBIDA, i.getPedido().getSituacaoPedido());
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
	public void testReajustePrecoCategoriaItemEstoque() {
		Integer idItemEstoque = recepcionarItemCompra();

		ItemEstoque itemTB = estoqueService.pesquisarItemEstoqueById(idItemEstoque);

		// Criando novo item
		ItemEstoque itemCH = itemTB.clone();
		itemCH.setFormaMaterial(FormaMaterial.CH);
		itemCH.setPrecoMedio(100d);
		Integer idCH = null;

		try {
			idCH = estoqueService.inserirItemEstoque(itemCH);
		} catch (BusinessException e1) {
			printMensagens(e1);
		}

		itemCH = estoqueService.pesquisarItemEstoqueById(idCH);
		itemCH.setAliquotaReajuste(0.1);

		try {
			estoqueService.reajustarPrecoItemEstoque(itemCH);
		} catch (BusinessException e) {
			printMensagens(e);
		}

		Double precoMedioReajustadoCH = 110d;
		Double precoMedioFatorICMSReajustadoCH = 123.2d;

		Double precoMedioReajustadoTB = 68.42d;
		Double precoMedioFatorICMSReajustadoTB = 76.63d;

		assertEquals(
				"O preco medio de um determinado item de estoque foi reajustado. Verifique o algoritmo de reajuste",
				precoMedioReajustadoCH, NumeroUtils.arredondarValorMonetario(itemCH.getPrecoMedio()));

		assertEquals(
				"O preco medio com fator ICMS de um determinado item de estoque foi reajustado. Verifique o algoritmo de reajuste",
				precoMedioFatorICMSReajustadoCH, NumeroUtils.arredondarValorMonetario(itemCH.getPrecoMedioFatorICMS()));

		assertEquals(
				"O preco medio do tubo nao pode ter sido reajustado pois estamos reajustanto uma chapa. Verifique o algoritmo de reajuste",
				precoMedioReajustadoTB, NumeroUtils.arredondarValorMonetario(itemTB.getPrecoMedio()));

		assertEquals(
				"O preco medio com fator ICMS do tubo nao pode ter sido reajustado pois estamos reajustanto uma chapa. Verifique o algoritmo de reajuste",
				precoMedioFatorICMSReajustadoTB, NumeroUtils.arredondarValorMonetario(itemTB.getPrecoMedioFatorICMS()));

		boolean throwed = false;
		itemCH.setId(null);
		itemCH.setFormaMaterial(null);
		try {
			estoqueService.reajustarPrecoItemEstoque(itemCH);
		} catch (BusinessException e) {
			throwed = true;
		}
		assertTrue("A forma do material do item eh obrigatorio para o reajuste e essa condicao nao foi validada",
				throwed);

		throwed = false;
		itemCH.setId(null);
		itemCH.setFormaMaterial(FormaMaterial.CH);
		itemCH.getMaterial().setId(null);
		try {
			estoqueService.reajustarPrecoItemEstoque(itemCH);
		} catch (BusinessException e) {
			throwed = true;
		}
		assertTrue("O material do item eh obrigatorio para o reajuste e essa condicao nao foi validada", throwed);

		throwed = false;
		itemCH.setId(null);
		itemCH.setFormaMaterial(FormaMaterial.CH);
		itemCH.setMaterial(null);
		try {
			estoqueService.reajustarPrecoItemEstoque(itemCH);
		} catch (BusinessException e) {
			throwed = true;
		}
		assertTrue("O material do item eh obrigatorio para o reajuste e essa condicao nao foi validada", throwed);
	}

	@Test
	public void testReajustePrecoItemEstoque() {
		Integer idItemEstoque = recepcionarItemCompra();

		ItemEstoque itemEstoque = estoqueService.pesquisarItemEstoqueById(idItemEstoque);
		itemEstoque.setAliquotaReajuste(0.1);

		try {
			estoqueService.reajustarPrecoItemEstoque(itemEstoque);
		} catch (BusinessException e) {
			printMensagens(e);
		}

		Double precoMedioReajustado = 75.27d;
		Double precoMedioFatorICMSReajustado = 84.3d;

		// Pesquisando o item resjustado
		itemEstoque = estoqueService.pesquisarItemEstoqueById(idItemEstoque);

		assertEquals(
				"O preco medio de um determinado item de estoque foi reajustado. Verifique o algoritmo de reajuste",
				precoMedioReajustado, NumeroUtils.arredondarValorMonetario(itemEstoque.getPrecoMedio()));

		assertEquals(
				"O preco medio com fator ICMS de um determinado item de estoque foi reajustado. Verifique o algoritmo de reajuste",
				precoMedioFatorICMSReajustado,
				NumeroUtils.arredondarValorMonetario(itemEstoque.getPrecoMedioFatorICMS()));
	}

	@Test
	public void testReajustePrecoItemEstoqueSemAliquota() {
		Integer idItemEstoque = recepcionarItemCompra();

		ItemEstoque itemEstoque = estoqueService.pesquisarItemEstoqueById(idItemEstoque);
		itemEstoque.setAliquotaReajuste(null);
		boolean throwed = false;
		try {
			estoqueService.reajustarPrecoItemEstoque(itemEstoque);
		} catch (BusinessException e) {
			throwed = true;
		}

		assertTrue("O reajuste de preco necessita de aliquota e essa condicao nao foi validada", throwed);

		itemEstoque.setAliquotaReajuste(0d);
		throwed = false;
		try {
			estoqueService.reajustarPrecoItemEstoque(itemEstoque);
		} catch (BusinessException e) {
			throwed = true;
		}

		assertTrue("O reajuste de preco necessita de aliquota positiva e essa condicao nao foi validada", throwed);

		throwed = false;
		try {
			estoqueService.reajustarPrecoItemEstoque(null);
		} catch (BusinessException e) {
			throwed = true;
		}

		assertTrue("O reajuste de preco necessita de um item para ser efetuado e essa condicao nao foi validada",
				throwed);

	}

	@Test
	public void testRecepcaoItemPedidoCompraComAliquotaIPI() {
		ItemPedido i = enviarItemPedidoCompra();

		Integer idItemEstoque = null;
		try {
			idItemEstoque = estoqueService.recepcionarItemCompra(i.getId(), i.getQuantidade());
		} catch (BusinessException e) {
			printMensagens(e);
		}

		assertEquals(SituacaoPedido.COMPRA_RECEBIDA, pedidoService.pesquisarSituacaoPedidoById(i.getPedido().getId()));

		ItemEstoque itemEstoque = estoqueService.pesquisarItemEstoqueById(idItemEstoque);
		Double precoMedioComFatorIPI = 68.42;

		assertEquals("O valor do preco medio apos a recepcao da compra deve conter o ipi. Os valores nao conferem",
				precoMedioComFatorIPI, NumeroUtils.arredondarValorMonetario(itemEstoque.getPrecoMedio()));
	}

	@Test
	public void testRecepcaoItemPedidoCompraItemEstoqueComNCM() {
		ItemPedido i = enviarItemPedidoCompra();
		final String ncmAntes = "22.22.22.22";

		ItemEstoque itemEstoque = new ItemEstoque();
		itemEstoque.copiar(i);
		itemEstoque.setNcm(ncmAntes);

		Integer idItemEstoque = null;
		try {
			idItemEstoque = estoqueService.inserirItemEstoque(itemEstoque);
		} catch (BusinessException e1) {
			printMensagens(e1);
		}

		final String ncm = "11.11.11.11";

		try {
			idItemEstoque = estoqueService.recepcionarItemCompra(i.getId(), i.getQuantidade(), ncm);
		} catch (BusinessException e) {
			printMensagens(e);
		}

		assertEquals(SituacaoPedido.COMPRA_RECEBIDA, pedidoService.pesquisarSituacaoPedidoById(i.getPedido().getId()));

		itemEstoque = estoqueService.pesquisarItemEstoqueById(idItemEstoque);

		assertEquals(
				"O item do estoque ja continha ncm configurado e nao deve ser alterado apos a recepcao da compra. Verficique as regras de negocios.",
				ncmAntes, itemEstoque.getNcm());

		assertEquals(
				"O item do pedido teve o ncm configurado na recepcao da compra, mas ja existia um ncm no estoque e ambos devem ser iguais. Verficique as regras de negocios.",
				ncmAntes, i.getNcm());

	}

	@Test
	public void testRecepcaoItemPedidoCompraItemEstoqueNCMNulo() {
		ItemPedido i = enviarItemPedidoCompra();
		ItemEstoque itemEstoque = new ItemEstoque();
		itemEstoque.copiar(i);
		itemEstoque.setNcm(null);

		Integer idItemEstoque = null;
		try {
			idItemEstoque = estoqueService.inserirItemEstoque(itemEstoque);
		} catch (BusinessException e1) {
			printMensagens(e1);
		}

		final String ncm = "11.11.11.11";

		try {
			idItemEstoque = estoqueService.recepcionarItemCompra(i.getId(), i.getQuantidade(), ncm);
		} catch (BusinessException e) {
			printMensagens(e);
		}

		assertEquals(SituacaoPedido.COMPRA_RECEBIDA, pedidoService.pesquisarSituacaoPedidoById(i.getPedido().getId()));

		itemEstoque = estoqueService.pesquisarItemEstoqueById(idItemEstoque);
		i = pedidoService.pesquisarItemPedidoById(i.getId());

		assertEquals(
				"O item do estoque nao contem ncm e deve ser configurado apos a recepcao da compra. Verficique as regras de negocios.",
				ncm, itemEstoque.getNcm());

		assertEquals(
				"O item do pedido teve o ncm configurado na recepcao da compra, mas as informacoes nao confere. Verficique as regras de negocios.",
				ncm, i.getNcm());
	}

	@Test
	public void testRecepcaoItemPedidoCompraItemEstoqueNCMVazio() {
		ItemPedido i = enviarItemPedidoCompra();
		ItemEstoque itemEstoque = new ItemEstoque();
		itemEstoque.copiar(i);
		itemEstoque.setNcm("");

		Integer idItemEstoque = null;
		try {
			idItemEstoque = estoqueService.inserirItemEstoque(itemEstoque);
		} catch (BusinessException e1) {
			printMensagens(e1);
		}

		final String ncm = "11.11.11.11";

		try {
			idItemEstoque = estoqueService.recepcionarItemCompra(i.getId(), i.getQuantidade(), ncm);
		} catch (BusinessException e) {
			printMensagens(e);
		}

		assertEquals(SituacaoPedido.COMPRA_RECEBIDA, pedidoService.pesquisarSituacaoPedidoById(i.getPedido().getId()));

		itemEstoque = estoqueService.pesquisarItemEstoqueById(idItemEstoque);
		i = pedidoService.pesquisarItemPedidoById(i.getId());

		assertEquals(
				"O item do estoque nao contem ncm e deve ser configurado apos a recepcao da compra. Verficique as regras de negocios.",
				ncm, itemEstoque.getNcm());

		assertEquals(
				"O item do pedido teve o ncm configurado na recepcao da compra, mas as informacoes nao confere. Verficique as regras de negocios.",
				ncm, i.getNcm());
	}

	@Test
	public void testRecepcaoItemPedidoCompraNCMNuloItemEstoque() {
		ItemPedido i = enviarItemPedidoCompra();

		String ncm = "22.22.22.22";

		ItemEstoque itemEstoque = new ItemEstoque();
		itemEstoque.copiar(i);
		itemEstoque.setNcm(ncm);

		Integer idItemEstoque = null;
		try {
			idItemEstoque = estoqueService.inserirItemEstoque(itemEstoque);
		} catch (BusinessException e1) {
			printMensagens(e1);
		}

		try {
			idItemEstoque = estoqueService.recepcionarItemCompra(i.getId(), i.getQuantidade(), null);
		} catch (BusinessException e) {
			printMensagens(e);
		}

		assertEquals(SituacaoPedido.COMPRA_RECEBIDA, pedidoService.pesquisarSituacaoPedidoById(i.getPedido().getId()));

		itemEstoque = estoqueService.pesquisarItemEstoqueById(idItemEstoque);
		i = pedidoService.pesquisarItemPedidoById(i.getId());

		i = pedidoService.pesquisarItemPedidoById(i.getId());

		assertEquals(
				"O item do pedido nao contem ncm e deve ser configurado igual ao estoque apos a recepcao da compra. Verficique as regras de negocios.",
				itemEstoque.getNcm(), i.getNcm());

		assertEquals(
				"O item do estoque ja continha ncm configurado e nao deve ser alterado apos arecepcao da compra. Verficique as regras de negocios.",
				ncm, i.getNcm());
	}

	@Test
	public void testRecepcaoItemPedidoCompraNCMNuloItemEstoqueNulo() {
		ItemPedido i = enviarItemPedidoCompra();

		ItemEstoque itemEstoque = new ItemEstoque();
		itemEstoque.copiar(i);
		itemEstoque.setNcm(null);

		Integer idItemEstoque = null;
		try {
			idItemEstoque = estoqueService.inserirItemEstoque(itemEstoque);
		} catch (BusinessException e1) {
			printMensagens(e1);
		}

		try {
			idItemEstoque = estoqueService.recepcionarItemCompra(i.getId(), i.getQuantidade(), null);
		} catch (BusinessException e) {
			printMensagens(e);
		}

		assertEquals(SituacaoPedido.COMPRA_RECEBIDA, pedidoService.pesquisarSituacaoPedidoById(i.getPedido().getId()));

		itemEstoque = estoqueService.pesquisarItemEstoqueById(idItemEstoque);
		i = pedidoService.pesquisarItemPedidoById(i.getId());

		i = pedidoService.pesquisarItemPedidoById(i.getId());

		assertEquals("Nao exite configuracao para o ncm. Verficique as regras de negocios.", null, i.getNcm());

		assertEquals("Nao exite configuracao para o ncm. Verficique as regras de negocios.", null, i.getNcm());
	}

	@Test
	public void testRecepcaoItemPedidoCompraNCMVazioItemEstoque() {
		ItemPedido i = enviarItemPedidoCompra();

		String ncm = "22.22.22.22";

		ItemEstoque itemEstoque = new ItemEstoque();
		itemEstoque.copiar(i);
		itemEstoque.setNcm(ncm);

		Integer idItemEstoque = null;
		try {
			idItemEstoque = estoqueService.inserirItemEstoque(itemEstoque);
		} catch (BusinessException e1) {
			printMensagens(e1);
		}

		try {
			idItemEstoque = estoqueService.recepcionarItemCompra(i.getId(), i.getQuantidade(), "");
		} catch (BusinessException e) {
			printMensagens(e);
		}

		assertEquals(SituacaoPedido.COMPRA_RECEBIDA, pedidoService.pesquisarSituacaoPedidoById(i.getPedido().getId()));

		itemEstoque = estoqueService.pesquisarItemEstoqueById(idItemEstoque);
		i = pedidoService.pesquisarItemPedidoById(i.getId());

		i = pedidoService.pesquisarItemPedidoById(i.getId());

		assertEquals(
				"O item do pedido nao contem ncm e deve ser configurado igual ao estoque apos a recepcao da compra. Verficique as regras de negocios.",
				itemEstoque.getNcm(), i.getNcm());

		assertEquals(
				"O item do estoque ja continha ncm configurado e nao deve ser alterado apos arecepcao da compra. Verficique as regras de negocios.",
				ncm, i.getNcm());
	}

	@Test
	public void testRecepcaoItemPedidoCompraQuantidadeInferior() {
		ItemPedido i = enviarItemPedidoCompra();
		Integer quantidadeRecepcionada = i.getQuantidade() - 1;

		try {
			estoqueService.recepcionarItemCompra(i.getId(), quantidadeRecepcionada);
		} catch (BusinessException e) {
			printMensagens(e);
		}

		assertEquals(SituacaoPedido.COMPRA_AGUARDANDO_RECEBIMENTO,
				pedidoService.pesquisarSituacaoPedidoById(i.getPedido().getId()));
	}

	@Test
	public void testRecepcaoItemPedidoCompraSemAliquotaIPI() {
		ItemPedido i = enviarItemPedidoCompra();
		i.setAliquotaIPI(null);

		Integer idItemEstoque = null;
		try {
			idItemEstoque = estoqueService.recepcionarItemCompra(i.getId(), i.getQuantidade());
		} catch (BusinessException e) {
			printMensagens(e);
		}

		ItemEstoque itemEstoque = estoqueService.pesquisarItemEstoqueById(idItemEstoque);
		Double precoMedioSemFatorIPI = 68.42;

		assertEquals(
				"O item nao contem ipi entao o valor do preco medio apos a recepcao da compra deve ser o mesmo. Os valores nao conferem",
				precoMedioSemFatorIPI, NumeroUtils.arredondarValorMonetario(itemEstoque.getPrecoMedio()));
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
		Integer idItemEstoque = recepcionarItemCompra();

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
	public void testRedefinicaoEstoqueAlteracaoPrecoFatorICMS() {
		Integer idItemEstoque = recepcionarItemCompra();

		ItemEstoque itemEstoque = estoqueService.pesquisarItemEstoqueById(idItemEstoque);

		Double precoFatorICMS = 188.63;

		itemEstoque.setPrecoMedio(itemEstoque.getPrecoMedio() + 100);
		try {
			estoqueService.redefinirItemEstoque(itemEstoque);
		} catch (BusinessException e) {
			printMensagens(e);
		}
		assertEquals(
				"Apos a redefinicao do item o deve-se incluir aplicar o fator ICMS no preco de custo. Verifique o algoritmo de calculo",
				precoFatorICMS, NumeroUtils.arredondarValorMonetario(itemEstoque.getPrecoMedioFatorICMS()));
	}

	@Test
	public void testRedefinicaoEstoqueFormaMaterialNulo() {
		ItemPedido i = enviarItemPedidoCompra();
		Integer idItemEstoque = null;
		try {
			idItemEstoque = estoqueService.recepcionarItemCompra(i.getId(), i.getQuantidade());
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
			idItemEstoque = estoqueService.recepcionarItemCompra(i.getId(), i.getQuantidade());
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
	public void testRedefinicaoEstoqueMaterialQuantidadeNegativa() {
		ItemPedido i = enviarItemPedidoCompra();
		Integer idItemEstoque = null;
		try {
			idItemEstoque = estoqueService.recepcionarItemCompra(i.getId(), i.getQuantidade());
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
	public void testRedefinicaoEstoqueMaterialQuantidadeZerada() {
		ItemPedido i = enviarItemPedidoCompra();
		Integer idItemEstoque = null;
		try {
			idItemEstoque = estoqueService.recepcionarItemCompra(i.getId(), i.getQuantidade());
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
	public void testRedefinicaoEstoquePecaDescricaoNulo() {
		Integer idItemEstoque = recepcionarItemCompra();

		ItemEstoque itemEstoque = gerarItemEstoquePeca();

		try {
			idItemEstoque = estoqueService.inserirItemEstoque(itemEstoque);
		} catch (BusinessException e1) {
			printMensagens(e1);
		}

		itemEstoque = estoqueService.pesquisarItemEstoqueById(idItemEstoque);
		itemEstoque.setQuantidade(300);

		try {
			estoqueService.redefinirItemEstoque(itemEstoque);
		} catch (BusinessException e) {
			printMensagens(e);
		}

		assertNotNull("O item de estoque nao deve conter descricao nula apos a redefinicao da peca",
				itemEstoque.getDescricaoPeca());
	}

	@Test
	public void testRedefinicaoEstoquePrecoMedio() {
		Integer idItemEstoque = recepcionarItemCompra();

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
			idItemEstoque = estoqueService.recepcionarItemCompra(i.getId(), i.getQuantidade());
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
			idItemEstoque = estoqueService.recepcionarItemCompra(i.getId(), i.getQuantidade());
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
		ItemEstoque itemEstoque = gerarItemEstoquePeca();
		itemEstoque.setComprimento(100d);

		boolean throwed = false;
		try {
			estoqueService.redefinirItemEstoque(itemEstoque);
		} catch (BusinessException e) {
			throwed = true;
		}
		assertTrue("Pecas nao devem ter comprimento e isso nao foi validado", throwed);
	}

	@Test
	public void testRedefinicaoInvalidaPecaComMedidaExterna() {

		ItemEstoque itemEstoque = gerarItemEstoquePeca();
		itemEstoque.setMedidaExterna(100d);

		boolean throwed = false;
		try {
			estoqueService.redefinirItemEstoque(itemEstoque);
		} catch (BusinessException e) {
			throwed = true;
		}
		assertTrue("Pecas nao podem ter medida externa e isso nao foi validado", throwed);
	}

	@Test
	public void testRedefinirItemPedidoFormaQuadrada() {
		ItemPedido i = enviarItemPedidoCompra();
		Integer idItemEstoque = null;
		try {
			idItemEstoque = estoqueService.recepcionarItemCompra(i.getId(), i.getQuantidade());
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
		assertEquals("As medidas externa e interna devem ser iguais para barra quadrada",
				itemEstoque.getMedidaExterna(), itemEstoque.getMedidaInterna());
	}

	@Test
	public void testRemocaoConfiguracaoEstoque() {
		Integer idItemEstoque = recepcionarItemCompra();
		ItemEstoque item1 = estoqueService.pesquisarItemEstoqueById(idItemEstoque);

		ItemEstoque configuracao = gerarConfiguracaoEstoque();

		try {
			estoqueService.inserirConfiguracaoEstoque(configuracao);
		} catch (BusinessException e) {
			printMensagens(e);
		}

		configuracao.setQuantidadeMinima(null);
		try {
			estoqueService.inserirConfiguracaoEstoque(configuracao);
		} catch (BusinessException e) {
			printMensagens(e);
		}

		item1 = estoqueService.pesquisarItemEstoqueById(item1.getId());

		assertNull("A quantidade minima de estoque foi anulada e esse item nao pode ter limite minimo",
				item1.getQuantidadeMinima());
	}

	@Test
	public void testRemocaoItemPedido() {
		ItemEstoque itemEsto = gerarItemPedidoNoEstoque();
		Integer qtdeEstoque = itemEsto.getQuantidade();

		ItemPedido itemPed = eBuilder.buildItemPedido();
		itemPed.copiar(itemEsto);
		Pedido p = gerarPedido(TipoPedido.REVENDA);
		itemPed.setPedido(p);
		Integer idItemPedido = null;
		try {
			idItemPedido = pedidoService.inserirItemPedido(p.getId(), itemPed);
		} catch (BusinessException e) {
			printMensagens(e);
		}

		try {
			pedidoService.enviarPedido(p.getId(), new AnexoEmail(new byte[] {}));
		} catch (BusinessException e1) {
			printMensagens(e1);
		}

		List<ItemReservado> lItemReserv = estoqueService.pesquisarItemReservadoByIdItemPedido(idItemPedido);
		assertTrue("O item do pedido deveria ter um item reservado no estoque pois o estoque ja foi populado",
				lItemReserv.size() > 0);

		try {
			pedidoService.removerItemPedido(idItemPedido);
		} catch (BusinessException e) {
			printMensagens(e);
		}

		itemPed = pedidoService.pesquisarItemPedidoById(idItemPedido);
		assertTrue("O item foi removido do sistema e deveria ser nulo", itemPed == null);

		lItemReserv = estoqueService.pesquisarItemReservadoByIdItemPedido(idItemPedido);
		assertEquals("O item id " + idItemPedido
				+ " foi removido do sistema e nao deveria ter item reservado associado", (Integer) 0,
				(Integer) lItemReserv.size());

		ItemEstoque itemEsto2 = estoqueService.pesquisarItemEstoqueById(itemEsto.getId());
		assertEquals(
				"Apos a remocao de um item de um pedido as quantidades reservada desse item devem ser devolvidas ao estoque",
				qtdeEstoque, itemEsto2.getQuantidade());

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
		assertEquals("Um item inexistente no estoque nao pode ser reservado",
				SituacaoReservaEstoque.NAO_CONTEM_ESTOQUE, situacaoReservaEstoque);
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

			pedidoService.enviarPedido(pedido.getId(), new AnexoEmail(new byte[] {}));

			// Inserindo apenas um dos itens para fazermos os testes de
			// pendencia
			estoqueService.recepcionarItemCompra(item1.getId(), item1.getQuantidade());
			estoqueService.recepcionarItemCompra(item2.getId(), item2.getQuantidade());
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
			pedidoService.enviarPedido(pedidoRevenda.getId(), new AnexoEmail(new byte[] {}));
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
			pedidoService.enviarPedido(pedido.getId(), new AnexoEmail(new byte[] {}));
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
			pedidoService.enviarPedido(i1.getPedido().getId(), new AnexoEmail(new byte[] {}));
		} catch (BusinessException e1) {
			printMensagens(e1);
		}

		try {
			estoqueService.recepcionarItemCompra(i1.getId(), i1.getQuantidade());
		} catch (BusinessException e) {
			printMensagens(e);
		}
		assertEquals(SituacaoPedido.COMPRA_AGUARDANDO_RECEBIMENTO, i1.getPedido().getSituacaoPedido());
	}

	@Test
	public void testValorEstoqueFormaMaterial() {
		List<ItemPedido> listaItemComprado = gerarListaItemPedido(TipoPedido.COMPRA);
		for (ItemPedido itemPedido : listaItemComprado) {
			try {
				estoqueService.recepcionarItemCompra(itemPedido.getId(), itemPedido.getQuantidade());
			} catch (BusinessException e) {
				printMensagens(e);
			}
		}
		Double totalEstoque = 1.0;
		assertEquals(totalEstoque, estoqueService.calcularValorEstoque(null, FormaMaterial.BQ));

		totalEstoque = 2.0;
		assertEquals(totalEstoque, estoqueService.calcularValorEstoque(null, FormaMaterial.TB));
	}

	@Test
	public void testValorTotalEstoque() {
		List<ItemPedido> listaItemComprado = gerarListaItemPedido(TipoPedido.COMPRA);
		for (ItemPedido itemPedido : listaItemComprado) {
			try {
				estoqueService.recepcionarItemCompra(itemPedido.getId(), itemPedido.getQuantidade());
			} catch (BusinessException e) {
				printMensagens(e);
			}
		}
		Double totalEstoque = 3.0d;
		assertEquals(totalEstoque, estoqueService.calcularValorEstoque(null, null));
	}

	private void verificarQuantidadeTotalItemEstoque(Integer quantidadeItemPedido, Integer idItemEstoque) {
		Integer quantidadeItemEstoque = pesquisarQuantidadeTotalItemEstoque(idItemEstoque);
		assertEquals("As quantidades dos itens devem ser as mesmas apos inclusao no estoque", quantidadeItemPedido,
				quantidadeItemEstoque);

	}
}
