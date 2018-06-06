package br.com.svr.service.test;

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

import br.com.svr.service.EstoqueService;
import br.com.svr.service.MaterialService;
import br.com.svr.service.PedidoService;
import br.com.svr.service.RegistroEstoqueService;
import br.com.svr.service.RepresentadaService;
import br.com.svr.service.constante.FormaMaterial;
import br.com.svr.service.constante.SituacaoPedido;
import br.com.svr.service.constante.SituacaoReservaEstoque;
import br.com.svr.service.constante.TipoApresentacaoIPI;
import br.com.svr.service.constante.TipoOperacaoEstoque;
import br.com.svr.service.constante.TipoPedido;
import br.com.svr.service.constante.TipoVenda;
import br.com.svr.service.entity.ItemEstoque;
import br.com.svr.service.entity.ItemPedido;
import br.com.svr.service.entity.ItemReservado;
import br.com.svr.service.entity.Material;
import br.com.svr.service.entity.Pedido;
import br.com.svr.service.entity.RegistroEstoque;
import br.com.svr.service.entity.Representada;
import br.com.svr.service.exception.BusinessException;
import br.com.svr.service.mensagem.email.AnexoEmail;
import br.com.svr.service.monitor.ItemAguardandoEmpacotamentoMonitor;
import br.com.svr.service.test.builder.ServiceBuilder;
import br.com.svr.util.NumeroUtils;

public class EstoqueServiceTest extends AbstractTest {
	private EstoqueService estoqueService;
	private ItemAguardandoEmpacotamentoMonitor itemAguardandoEmpacotamentoMonitor;
	private MaterialService materialService;
	private PedidoService pedidoService;
	private RegistroEstoqueService registroEstoqueService;

	private RepresentadaService representadaService;

	public EstoqueServiceTest() {
		estoqueService = ServiceBuilder.buildService(EstoqueService.class);
		materialService = ServiceBuilder.buildService(MaterialService.class);
		pedidoService = ServiceBuilder.buildService(PedidoService.class);
		registroEstoqueService = ServiceBuilder.buildService(RegistroEstoqueService.class);
		representadaService = ServiceBuilder.buildService(RepresentadaService.class);
		itemAguardandoEmpacotamentoMonitor = ServiceBuilder.buildService(ItemAguardandoEmpacotamentoMonitor.class);
	}

	private ItemPedido enviarItemPedido(Integer quantidade, TipoPedido tipoPedido) {
		Pedido pedido = null;
		if (TipoPedido.COMPRA.equals(tipoPedido)) {
			pedido = gPedido.gerarPedidoCompra();
		} else if (TipoPedido.REVENDA.equals(tipoPedido)) {
			pedido = gPedido.gerarPedidoRevenda();
		} else {
			pedido = gPedido.gerarPedidoRepresentacao();
		}
		Representada r = recarregarEntidade(Representada.class, pedido.getRepresentada().getId());
		pedido.setRepresentada(r);

		ItemPedido item = eBuilder.buildItemPedido();
		if (quantidade != null) {
			item.setQuantidade(quantidade);
		}
		item.setMaterial(gerarMaterial(pedido.getRepresentada().getId()));

		try {
			final Integer id = pedidoService.inserirItemPedido(pedido.getId(), item);
			item.setId(id);
		} catch (BusinessException e1) {
			printMensagens(e1);
		}

		try {
			pedidoService.enviarPedido(pedido.getId(), new AnexoEmail(new byte[] {}));
		} catch (BusinessException e) {
			printMensagens(e);
		}
		return item;
	}

	private ItemPedido enviarItemPedido(TipoPedido tipoPedido) {
		return enviarItemPedido(null, tipoPedido);
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
		Representada fornecedor = eBuilder.buildFornecedor();
		fornecedor.setTipoApresentacaoIPI(TipoApresentacaoIPI.SEMPRE);

		try {
			representadaService.inserir(fornecedor);
		} catch (BusinessException e3) {
			printMensagens(e3);
		}
		return fornecedor;
	}

	private ItemPedido gerarItemCompraEnviado() {
		return enviarItemPedido(TipoPedido.COMPRA);
	}

	private ItemEstoque gerarItemEstoque() {
		ItemPedido iCompra = gerarItemCompraEnviado();
		Integer idItemEstoque = null;
		try {
			idItemEstoque = estoqueService.adicionarQuantidadeRecepcionadaItemCompra(iCompra.getId(),
					iCompra.getQuantidade(), "99.11.22.33");
		} catch (BusinessException e) {
			printMensagens(e);
		}

		return estoqueService.pesquisarItemEstoqueById(idItemEstoque);
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

	public ItemPedido gerarItemPedidoNoEstoque() {
		ItemPedido i = gerarItemCompraEnviado();
		try {
			estoqueService.adicionarQuantidadeRecepcionadaItemCompra(i.getId(), i.getQuantidade(), "99.99.99.99");
		} catch (BusinessException e) {
			printMensagens(e);
		}

		return i;
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
			Representada fornecedor = gerarFornecedor();
			material.addRepresentada(representada);
			material.addRepresentada(fornecedor);

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
		if (TipoPedido.COMPRA.equals(tipoPedido)) {
			return gPedido.gerarPedidoCompra();
		}
		if (TipoPedido.REPRESENTACAO.equals(tipoPedido)) {
			return gPedido.gerarPedidoRepresentacao();
		}
		if (TipoPedido.REVENDA.equals(tipoPedido)) {
			return gPedido.gerarPedidoRevenda();
		}
		return null;
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

	private Integer pesquisarQuantidadeTotalItemEstoque(Integer idItemEstoque) {
		ItemEstoque i = estoqueService.pesquisarItemEstoqueById(idItemEstoque);
		return i != null ? i.getQuantidade() : 0;

	}

	private Integer recepcionarItemCompra() {
		ItemPedido i = gerarItemCompraEnviado();
		Integer idItemEstoque = null;
		try {
			idItemEstoque = estoqueService.adicionarQuantidadeRecepcionadaItemCompra(i.getId(), i.getQuantidade());
		} catch (BusinessException e) {
			printMensagens(e);
		}
		return idItemEstoque;
	}

	@Test
	public void testAlteracaoItemPedidoNoEstoque() {
		ItemPedido item1 = gerarItemCompraEnviado();
		ItemPedido item2 = gerarItemPedidoClone(item1.getQuantidade() + 100, item1);

		Integer idItemEstoque = null;
		try {
			idItemEstoque = estoqueService.adicionarQuantidadeRecepcionadaItemCompra(item1.getId(),
					item1.getQuantidade());
		} catch (BusinessException e) {
			printMensagens(e);
		}

		verificarQuantidadeTotalItemEstoque(item1.getQuantidade(), idItemEstoque);
		Integer quantidadeAntes = pesquisarQuantidadeTotalItemEstoque(idItemEstoque);

		try {
			idItemEstoque = estoqueService.adicionarQuantidadeRecepcionadaItemCompra(item2.getId(),
					item2.getQuantidade());
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

		item1.setQuantidadeMinima(11);

		try {
			estoqueService.inserirItemEstoque(item1);
			item1 = estoqueService.pesquisarItemEstoqueById(idItemEstoque);
		} catch (BusinessException e1) {
			printMensagens(e1);
		}

		ItemEstoque configuracao = item1.clone();
		configuracao.setQuantidadeMinima(item1.getQuantidadeMinima() + 3);
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

		item1 = recarregarEntidade(ItemEstoque.class, item1.getId());
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

		Double precoMinimo = 78.53;

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

		Double precoMinimo = 71.4;
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
			estoqueService.adicionarQuantidadeRecepcionadaItemCompra(itemPeca.getId(), itemPeca.getQuantidade());
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

		Integer idEst1 = recepcionarItemCompra();
		Integer idEst2 = recepcionarItemCompra();

		ItemEstoque item1 = estoqueService.pesquisarItemEstoqueById(idEst1);
		ItemEstoque item2 = estoqueService.pesquisarItemEstoqueById(idEst2);

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

		ItemEstoque config = gerarConfiguracaoEstoque();
		config.setMaterial(item1.getMaterial());
		config.setFormaMaterial(item1.getFormaMaterial());
		config.setMedidaExterna(null);
		config.setMedidaInterna(null);
		config.setComprimento(null);

		try {
			estoqueService.inserirConfiguracaoEstoque(config);
		} catch (BusinessException e) {
			printMensagens(e);
		}

		item1 = recarregarEntidade(ItemEstoque.class, idEst1);
		item2 = recarregarEntidade(ItemEstoque.class, idEst2);

		assertEquals("Os valores da margem minima de lucro devem ser as mesmas apos o cadastro do limite minimo",
				item1.getMargemMinimaLucro(), config.getMargemMinimaLucro());
		assertEquals("Os valores da quantidade minima devem ser as mesmas apos o cadastro do limite minimo",
				item1.getQuantidadeMinima(), config.getQuantidadeMinima());

		assertEquals("Os valores da margem minima de lucro devem ser as mesmas apos o cadastro do limite minimo",
				item2.getMargemMinimaLucro(), config.getMargemMinimaLucro());
		assertEquals("Os valores da quantidade minima devem ser as mesmas apos o cadastro do limite minimo",
				item2.getQuantidadeMinima(), config.getQuantidadeMinima());
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

		tubo = recarregarEntidade(ItemEstoque.class, idTubo);
		tubo2 = recarregarEntidade(ItemEstoque.class, idTubo2);
		barra = recarregarEntidade(ItemEstoque.class, idBarra);

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
		ItemEstoque item = gerarItemEstoque();
		ItemEstoque item2 = item.clone();

		Integer idItem = null;

		try {
			idItem = estoqueService.inserirItemEstoque(item2);
		} catch (BusinessException e) {
			printMensagens(e);
		}

		item2 = estoqueService.pesquisarItemEstoqueById(idItem);
		Double precoFatorICMS = 64.31;

		assertEquals(
				"Apos a inclusao de um item novo deve-se aplicar o fator ICMS no preco de custo. Verifique o algoritmo de calculo",
				precoFatorICMS, NumeroUtils.arredondarValor2Decimais(item2.getPrecoMedioFatorICMS()));
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
	public void testInclusaoItemPedidoValidoNoEstoque() {
		ItemPedido i = gerarItemCompraEnviado();

		try {
			pedidoService.enviarPedido(i.getPedido().getId(), new AnexoEmail(new byte[] {}));
		} catch (BusinessException e1) {
			printMensagens(e1);
		}

		Integer idItemEstoque = null;
		try {
			idItemEstoque = estoqueService.adicionarQuantidadeRecepcionadaItemCompra(i.getId(), i.getQuantidade());
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
	public void testInclusaoRegistroAlteracaoValorItemEstoque() {
		Pedido pCompra = gPedido.gerarPedidoCompra();
		ItemPedido iCompra = null;
		try {
			iCompra = gPedido.gerarItemPedido(pCompra.getId());
			pedidoService.inserirItemPedido(pCompra.getId(), iCompra);
		} catch (BusinessException e) {
			printMensagens(e);
		}

		try {
			pedidoService.enviarPedido(pCompra.getId(), new AnexoEmail(new byte[] {}));
		} catch (BusinessException e) {
			printMensagens(e);
		}

		try {
			estoqueService.adicionarQuantidadeRecepcionadaItemCompra(iCompra.getId(), iCompra.getQuantidade(), null);
		} catch (BusinessException e) {
			printMensagens(e);
		}

		ItemEstoque iEst = estoqueService.pesquisarItemEstoque(iCompra);
		double vlAntes = iEst.getPrecoMedio();
		double vlDepois = 75.27;

		ItemEstoque iClone = iEst.clone();
		iClone.setAliquotaReajuste(0.10);

		int idUsu = 10;
		String nome = "VINICIUS";
		try {
			estoqueService.reajustarPrecoItemEstoque(iClone, idUsu, nome);
		} catch (BusinessException e) {
			printMensagens(e);
		}
		List<RegistroEstoque> lRegistro = registroEstoqueService.pesquisarRegistroByIdItemEstoque(iEst.getId());
		assertEquals(
				"Deve haver 2 registros de estoque, o primeiro feito na inclusao do item de compra, o segundo na redefinicao do valor",
				(Integer) 2, (Integer) lRegistro.size());
		RegistroEstoque r = null;
		for (RegistroEstoque reg : lRegistro) {
			if (TipoOperacaoEstoque.ALTERACAO_MANUAL_VALOR.equals(reg.getTipoOperacao())) {
				r = reg;
			}
		}
		assertEquals("O valor anterior do registro deve ser o mesmo do valor editado",
				NumeroUtils.arredondarValor2Decimais(vlAntes),
				NumeroUtils.arredondarValor2Decimais(r.getValorAnterior()));
		assertEquals("O valor posterior do registro deve ser o mesmo do valor editado",
				NumeroUtils.arredondarValor2Decimais(vlDepois),
				NumeroUtils.arredondarValor2Decimais(r.getValorPosterior()));
	}

	@Test
	public void testInclusaoRegistroEntradaEstoqueViaDevolucaoItemVendas() {
		Pedido pCompra = gPedido.gerarPedidoCompra();
		ItemPedido iCompra = null;
		try {
			iCompra = gPedido.gerarItemPedido(pCompra.getId());
			// Alterando a quantidade dos itens comprados para poder vender.
			iCompra.setQuantidade(100);

			pedidoService.inserirItemPedido(pCompra.getId(), iCompra);
		} catch (BusinessException e) {
			printMensagens(e);
		}

		try {
			pedidoService.enviarPedido(pCompra.getId(), new AnexoEmail(new byte[] {}));
		} catch (BusinessException e) {
			printMensagens(e);
		}

		try {
			estoqueService.adicionarQuantidadeRecepcionadaItemCompra(iCompra.getId(), iCompra.getQuantidade(), null);
		} catch (BusinessException e) {
			printMensagens(e);
		}

		Pedido pVenda = gPedido.gerarPedidoRevenda();
		ItemPedido iVenda = iCompra.clone();
		iVenda.setQuantidade(iVenda.getQuantidade() + 10);

		try {
			pedidoService.inserirItemPedido(pVenda.getId(), iVenda);
			pedidoService.enviarPedido(pVenda.getId(), new AnexoEmail(new byte[] {}));
		} catch (BusinessException e) {
			printMensagens(e);
		}

		try {
			pedidoService.cancelarPedido(pVenda.getId());
		} catch (BusinessException e) {
			printMensagens(e);
		}

		List<RegistroEstoque> lRegistro = registroEstoqueService.pesquisarRegistroByIdPedido(pVenda.getId());

		int totDevol = 0;
		for (RegistroEstoque r : lRegistro) {
			if (TipoOperacaoEstoque.ENTRADA_DEVOLUCAO_VENDA.equals(r.getTipoOperacao())) {
				totDevol++;
			}
		}
		assertEquals("No cancelamento deve haver apenas uma devolucao pois o pedido continha apenas 1 item",
				(Integer) 1, (Integer) totDevol);
		Integer qtdeEst = estoqueService.pesquisarItemEstoque(iVenda).getQuantidade();
		assertEquals(
				"apos a devolucao do item de venda a quantidade no estoque deve ser a mesma que a quantidade comprada no inicio",
				(Integer) iCompra.getQuantidade(), qtdeEst);

	}

	@Test
	public void testInclusaoRegistroEntradaViaPedidoCompras() {
		Pedido compra = gPedido.gerarPedidoCompra();
		ItemPedido i = null;
		try {
			i = gPedido.gerarItemPedido(compra.getId());
		} catch (BusinessException e) {
			printMensagens(e);
		}
		try {
			pedidoService.enviarPedido(compra.getId(), new AnexoEmail(new byte[] {}));
		} catch (BusinessException e) {
			printMensagens(e);
		}

		List<RegistroEstoque> lRegistro = registroEstoqueService.pesquisarRegistroByIdItemPedido(i.getId());
		assertTrue("Ate o momento o pedido de compras nao foi enviado entao nao pode haver registro de estoque",
				lRegistro.size() == 0);

		try {
			estoqueService.adicionarQuantidadeRecepcionadaItemCompra(i.getId(), i.getQuantidade(), null);
		} catch (BusinessException e) {
			printMensagens(e);
		}

		lRegistro = registroEstoqueService.pesquisarRegistroByIdItemPedido(i.getId());
		assertTrue(
				"Foi recebido apenas um item do pedido de compras, portanto deve haver apensa um registro de estoque",
				lRegistro.size() == 1);

		RegistroEstoque r = lRegistro.get(0);

		assertNotNull("Apos a recepcao de compras deve haver registro de estoque", r);
		assertEquals("As quantidade devem ser iguais pois o item todo foi recepcionado", r.getQuantidadeItem(),
				i.getQuantidade());
		assertEquals("A quantidade anterior deve ser zero pois nao havia dados no sistema", (Integer) 0,
				r.getQuantidadeAnterior());
	}

	@Test
	public void testInclusaoRegistroSaidaEstoqueViaItemVendas() {
		Pedido pCompra = gPedido.gerarPedidoCompra();
		ItemPedido iCompra = null;
		try {
			iCompra = gPedido.gerarItemPedido(pCompra.getId());
			// Alterando a quantidade dos itens comprados para poder vender.
			iCompra.setQuantidade(100);

			pedidoService.inserirItemPedido(pCompra.getId(), iCompra);
		} catch (BusinessException e) {
			printMensagens(e);
		}

		try {
			pedidoService.enviarPedido(pCompra.getId(), new AnexoEmail(new byte[] {}));
		} catch (BusinessException e) {
			printMensagens(e);
		}
		List<RegistroEstoque> lRegistroEnt = registroEstoqueService.pesquisarRegistroByIdItemPedido(iCompra.getId());
		assertTrue("Ate o momento o pedido de compras nao foi recepcionado entao nao pode haver registro de estoque",
				lRegistroEnt.size() == 0);

		try {
			estoqueService.adicionarQuantidadeRecepcionadaItemCompra(iCompra.getId(), iCompra.getQuantidade(), null);
		} catch (BusinessException e) {
			printMensagens(e);
		}

		lRegistroEnt = registroEstoqueService.pesquisarRegistroByIdItemPedido(iCompra.getId());
		assertTrue("Deve conter 1 registro de estoque no sistema pois houve uma entrada por compras",
				lRegistroEnt.size() == 1);

		Pedido pVenda = gPedido.gerarPedidoRevenda();
		ItemPedido iVenda = null;
		try {
			iVenda = gPedido.gerarItemPedido(pVenda.getId());
			iVenda.setQuantidade(iCompra.getQuantidade() - 1);
			pedidoService.inserirItemPedido(pVenda.getId(), iVenda);
		} catch (BusinessException e) {
			printMensagens(e);
		}

		Integer qAnteriorVenda = estoqueService.pesquisarItemEstoque(iCompra).getQuantidade();

		try {
			pedidoService.enviarPedido(pVenda.getId(), new AnexoEmail(new byte[] {}));
		} catch (BusinessException e) {
			printMensagens(e);
		}

		List<RegistroEstoque> lRegistroSai = registroEstoqueService.pesquisarRegistroByIdItemPedido(iVenda.getId());

		RegistroEstoque rEnt = lRegistroEnt.get(0);
		RegistroEstoque rSai = lRegistroSai.get(0);

		assertTrue("Existiu apenas uma entrada no sistema via compras, entao as quantidades anteriores deve ser zero",
				rEnt.getQuantidadeAnterior().intValue() == 0);
		assertEquals("As quantidades de compras e registrada devem coincidir", iCompra.getQuantidade(),
				rEnt.getQuantidadeItem());

		assertEquals("As quantidades de venda e registrada devem coincidir", iVenda.getQuantidade(),
				rSai.getQuantidadeItem());

		assertEquals(
				"Existiu apenas uma compra e uma venda no sistema, entao as quantidades registradas devem ser as mesmas",
				qAnteriorVenda, rSai.getQuantidadeAnterior());
	}

	@Test
	public void testInclusaoRegistroSaidaEstoqueViaItemVendasPeloMonitoramentoDeCompras() {
		ItemPedido iCompra = gerarItemCompraEnviado();
		try {
			estoqueService.adicionarQuantidadeRecepcionadaItemCompra(iCompra.getId(), iCompra.getQuantidade(), null);
		} catch (BusinessException e) {
			printMensagens(e);
		}

		Pedido pVenda = gPedido.gerarPedidoRevenda();
		ItemPedido iVenda = null;
		Integer qVenda = iCompra.getQuantidade() + 50;
		try {
			iVenda = gPedido.gerarItemPedido(pVenda.getId());
			iVenda.setQuantidade(qVenda);

			pedidoService.inserirItemPedido(pVenda.getId(), iVenda);
		} catch (BusinessException e) {
			printMensagens(e);
		}

		Integer qAnteriorVenda = estoqueService.pesquisarItemEstoque(iCompra).getQuantidade();

		try {
			pedidoService.enviarPedido(pVenda.getId(), new AnexoEmail(new byte[] {}));
		} catch (BusinessException e) {
			printMensagens(e);
		}

		List<RegistroEstoque> lRegistroSai = registroEstoqueService.pesquisarRegistroByIdItemPedido(iVenda.getId());

		RegistroEstoque rSai = lRegistroSai.get(0);
		assertEquals("As quantidades de venda e anterior devem coincidir", qAnteriorVenda, rSai.getQuantidadeItem());

		assertEquals(
				"Existiu apenas uma compra e uma venda no sistema, entao as quantidades registradas devem ser as mesmas",
				qAnteriorVenda, rSai.getQuantidadeAnterior());

		qAnteriorVenda = estoqueService.pesquisarItemEstoque(iVenda).getQuantidade();

		// Gerando um novo pedido de comprar para dar baixa no restante de
		// vendas via monitor.
		Pedido pCompra2 = gPedido.gerarPedidoCompra();
		ItemPedido iCompra2 = null;
		try {
			iCompra2 = gPedido.gerarItemPedido(pCompra2.getId());
		} catch (BusinessException e1) {
			printMensagens(e1);
		}
		iCompra2.setQuantidade(qVenda);
		try {
			pedidoService.inserirItemPedido(pCompra2.getId(), iCompra2);
		} catch (BusinessException e) {
			printMensagens(e);
		}
		try {
			pedidoService.enviarPedido(iCompra2.getId(), new AnexoEmail(new byte[] {}));
		} catch (BusinessException e1) {
			printMensagens(e1);
		}

		try {
			// Entrando com a segunda compra no estoque
			estoqueService.adicionarQuantidadeRecepcionadaItemCompra(iCompra2.getId(), iCompra2.getQuantidade(), null);
		} catch (BusinessException e) {
			printMensagens(e);
		}

		itemAguardandoEmpacotamentoMonitor.monitorarItemPedidoAguardandoCompra();
		lRegistroSai = registroEstoqueService.pesquisarRegistroByIdItemPedido(iVenda.getId());
		assertEquals(
				"O pedido de vendas deve ter dois registros de saida. A primeira foi parcial pois faltava no estoque, a segunda foi a saida do restante das quantidades. ",
				(Integer) 2, (Integer) lRegistroSai.size());

		int qSaidaRegistrada = 0;
		for (RegistroEstoque r : lRegistroSai) {
			qSaidaRegistrada += r.getQuantidadeItem();
		}
		assertEquals(
				"A quantidade total de saide por vendas deve ser a mesma que a quantidade dos itens do pedido original",
				(Integer) qVenda, (Integer) qSaidaRegistrada);

	}

	@Test
	public void testPesquisarItemEstoqueCadastrado() {
		ItemEstoque item1 = gerarItemEstoque();
		ItemEstoque item2 = item1.clone();
		Integer idItem2 = null;
		try {
			idItem2 = estoqueService.inserirItemEstoque(item2);
		} catch (BusinessException e) {
			printMensagens(e);
		}
		ItemEstoque itemClone = item1.clone();
		ItemEstoque itemCadastrado = estoqueService.pesquisarItemEstoque(itemClone);

		assertNotNull(
				"O item pesquisado eh identico ao cadastrado, portanto deve existir no sistema. Possivel falha na inclusao",
				itemCadastrado);
		assertEquals("O item pesquisado eh identico ao cadastrado, portanto deve existir no sistema com o mesmo ID",
				idItem2, itemCadastrado.getId());

		final double tolerancia = 0.02d;
		// Alterando o comprimento em 1mm para testar a tolerancia.
		itemClone.setComprimento(item1.getComprimento() + tolerancia);

		itemCadastrado = estoqueService.pesquisarItemEstoque(itemClone);
		assertNull(
				"Foi adicionado o valor de tolerancia ao item de estoque para verificar que ele nao existe no estoque e deve ser nulo",
				itemCadastrado);
		// Alterando o comprimento em 1mm para testar a tolerancia.
		itemClone.setComprimento(item1.getComprimento() - tolerancia);

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

		itemCH = recarregarEntidade(ItemEstoque.class, idCH);
		itemCH.setAliquotaReajuste(0.1);

		try {
			estoqueService.reajustarPrecoItemEstoque(itemCH, null, null);
		} catch (BusinessException e) {
			printMensagens(e);
		}

		Double precoMedioReajustadoCH = 100d;
		Double precoMedioFatorICMSReajustadoCH = 94d;

		Double precoMedioReajustadoTB = 68.42d;
		Double precoMedioFatorICMSReajustadoTB = 64.32d;

		assertEquals(
				"O preco medio de um determinado item de estoque foi reajustado. Verifique o algoritmo de reajuste",
				precoMedioReajustadoCH, NumeroUtils.arredondarValor2Decimais(itemCH.getPrecoMedio()));

		assertEquals(
				"O preco medio com fator ICMS de um determinado item de estoque foi reajustado. Verifique o algoritmo de reajuste",
				precoMedioFatorICMSReajustadoCH, NumeroUtils.arredondarValor2Decimais(itemCH.getPrecoMedioFatorICMS()));

		assertEquals(
				"O preco medio do tubo nao pode ter sido reajustado pois estamos reajustanto uma chapa. Verifique o algoritmo de reajuste",
				precoMedioReajustadoTB, NumeroUtils.arredondarValor2Decimais(itemTB.getPrecoMedio()));

		assertEquals(
				"O preco medio com fator ICMS do tubo nao pode ter sido reajustado pois estamos reajustanto uma chapa. Verifique o algoritmo de reajuste",
				precoMedioFatorICMSReajustadoTB, NumeroUtils.arredondarValor2Decimais(itemTB.getPrecoMedioFatorICMS()));

		boolean throwed = false;
		itemCH.setId(null);
		itemCH.setFormaMaterial(null);
		try {
			estoqueService.reajustarPrecoItemEstoque(itemCH, null, null);
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
			estoqueService.reajustarPrecoItemEstoque(itemCH, null, null);
		} catch (BusinessException e) {
			throwed = true;
		}
		assertTrue("O material do item eh obrigatorio para o reajuste e essa condicao nao foi validada", throwed);

		throwed = false;
		itemCH.setId(null);
		itemCH.setFormaMaterial(FormaMaterial.CH);
		itemCH.setMaterial(null);
		try {
			estoqueService.reajustarPrecoItemEstoque(itemCH, null, null);
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
			estoqueService.reajustarPrecoItemEstoque(itemEstoque, null, null);
		} catch (BusinessException e) {
			printMensagens(e);
		}

		Double precoMedioReajustado = 75.27d;
		Double precoMedioFatorICMSReajustado = 70.75d;

		// Pesquisando o item resjustado
		itemEstoque = recarregarEntidade(ItemEstoque.class, idItemEstoque);

		assertEquals(
				"O preco medio de um determinado item de estoque foi reajustado. Verifique o algoritmo de reajuste",
				precoMedioReajustado, NumeroUtils.arredondarValor2Decimais(itemEstoque.getPrecoMedio()));

		assertEquals(
				"O preco medio com fator ICMS de um determinado item de estoque foi reajustado. Verifique o algoritmo de reajuste",
				precoMedioFatorICMSReajustado,
				NumeroUtils.arredondarValor2Decimais(itemEstoque.getPrecoMedioFatorICMS()));
	}

	@Test
	public void testReajustePrecoItemEstoqueSemAliquota() {
		Integer idItemEstoque = recepcionarItemCompra();

		ItemEstoque itemEstoque = estoqueService.pesquisarItemEstoqueById(idItemEstoque);
		itemEstoque.setAliquotaReajuste(null);
		boolean throwed = false;
		try {
			estoqueService.reajustarPrecoItemEstoque(itemEstoque, null, null);
		} catch (BusinessException e) {
			throwed = true;
		}

		assertTrue("O reajuste de preco necessita de aliquota e essa condicao nao foi validada", throwed);

		itemEstoque.setAliquotaReajuste(0d);
		throwed = false;
		try {
			estoqueService.reajustarPrecoItemEstoque(itemEstoque, null, null);
		} catch (BusinessException e) {
			throwed = true;
		}

		assertTrue("O reajuste de preco necessita de aliquota positiva e essa condicao nao foi validada", throwed);

		throwed = false;
		try {
			estoqueService.reajustarPrecoItemEstoque(null, null, null);
		} catch (BusinessException e) {
			throwed = true;
		}

		assertTrue("O reajuste de preco necessita de um item para ser efetuado e essa condicao nao foi validada",
				throwed);

	}

	@Test
	public void testRecepcaoItemPedidoCompraComAliquotaIPI() {
		ItemPedido i = gerarItemCompraEnviado();

		Integer idItemEstoque = null;
		i = recarregarEntidade(ItemPedido.class, i.getId());
		try {
			idItemEstoque = estoqueService.adicionarQuantidadeRecepcionadaItemCompra(i.getId(), i.getQuantidade());
		} catch (BusinessException e) {
			printMensagens(e);
		}

		assertEquals(SituacaoPedido.COMPRA_RECEBIDA, pedidoService.pesquisarSituacaoPedidoById(i.getPedido().getId()));

		ItemEstoque itemEstoque = estoqueService.pesquisarItemEstoqueById(idItemEstoque);
		Double precoMedioComFatorIPI = 68.42;

		assertEquals("O valor do preco medio apos a recepcao da compra deve conter o ipi. Os valores nao conferem",
				precoMedioComFatorIPI, NumeroUtils.arredondarValor2Decimais(itemEstoque.getPrecoMedio()));
	}

	@Test
	public void testRecepcaoItemPedidoCompraItemEstoqueComNCM() {
		ItemPedido i = gerarItemCompraEnviado();
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
		recarregarEntidade(ItemPedido.class, i.getId());
		try {
			idItemEstoque = estoqueService.adicionarQuantidadeRecepcionadaItemCompra(i.getId(), i.getQuantidade(), ncm);
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
		ItemPedido iCompra = gerarItemCompraEnviado();
		ItemEstoque iEstoque = new ItemEstoque();
		iEstoque.copiar(iCompra);
		iEstoque.setNcm(null);

		Integer idItemEstoque = null;
		try {
			idItemEstoque = estoqueService.inserirItemEstoque(iEstoque);
		} catch (BusinessException e1) {
			printMensagens(e1);
		}

		final String ncm = "11.11.11.11";

		try {
			iCompra = recarregarEntidade(ItemPedido.class, iCompra.getId());
			idItemEstoque = estoqueService.adicionarQuantidadeRecepcionadaItemCompra(iCompra.getId(),
					iCompra.getQuantidade(), ncm);
		} catch (BusinessException e) {
			printMensagens(e);
		}

		assertEquals(SituacaoPedido.COMPRA_RECEBIDA,
				pedidoService.pesquisarSituacaoPedidoById(iCompra.getPedido().getId()));

		iEstoque = recarregarEntidade(ItemEstoque.class, idItemEstoque);
		iCompra = recarregarEntidade(ItemPedido.class, iCompra.getId());

		assertEquals(
				"O item do estoque nao contem ncm e deve ser configurado apos a recepcao da compra. Verficique as regras de negocios.",
				ncm, iEstoque.getNcm());

		assertEquals(
				"O item do pedido teve o ncm configurado na recepcao da compra, mas as informacoes nao confere. Verficique as regras de negocios.",
				ncm, iCompra.getNcm());
	}

	@Test
	public void testRecepcaoItemPedidoCompraItemEstoqueNCMVazio() {
		ItemPedido i = gerarItemCompraEnviado();
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
			i = recarregarEntidade(ItemPedido.class, i.getId());
			idItemEstoque = estoqueService.adicionarQuantidadeRecepcionadaItemCompra(i.getId(), i.getQuantidade(), ncm);
		} catch (BusinessException e) {
			printMensagens(e);
		}

		assertEquals(SituacaoPedido.COMPRA_RECEBIDA, pedidoService.pesquisarSituacaoPedidoById(i.getPedido().getId()));

		itemEstoque = recarregarEntidade(ItemEstoque.class, idItemEstoque);

		assertEquals(
				"O item do estoque nao contem ncm e deve ser configurado apos a recepcao da compra. Verficique as regras de negocios.",
				ncm, itemEstoque.getNcm());

		assertEquals(
				"O item do pedido teve o ncm configurado na recepcao da compra, mas as informacoes nao confere. Verficique as regras de negocios.",
				ncm, i.getNcm());
	}

	@Test
	public void testRecepcaoItemPedidoCompraNCMNuloItemEstoque() {
		ItemPedido i = gerarItemCompraEnviado();

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

		recarregarEntidade(ItemPedido.class, i.getId());

		try {
			idItemEstoque = estoqueService
					.adicionarQuantidadeRecepcionadaItemCompra(i.getId(), i.getQuantidade(), null);
		} catch (BusinessException e) {
			printMensagens(e);
		}
		assertEquals(SituacaoPedido.COMPRA_RECEBIDA, pedidoService.pesquisarSituacaoPedidoById(i.getPedido().getId()));

		itemEstoque = estoqueService.pesquisarItemEstoqueById(idItemEstoque);
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
		ItemPedido iCompra = gerarItemCompraEnviado();
		final Integer idCompra = iCompra.getPedido().getId();

		ItemEstoque iEstoque = new ItemEstoque();
		iEstoque.copiar(iCompra);
		iEstoque.setNcm(null);

		Integer idItemEst = null;
		try {
			idItemEst = estoqueService.inserirItemEstoque(iEstoque);
		} catch (BusinessException e1) {
			printMensagens(e1);
		}

		try {
			idItemEst = estoqueService.adicionarQuantidadeRecepcionadaItemCompra(iCompra.getId(),
					iCompra.getQuantidade(), null);
		} catch (BusinessException e) {
			printMensagens(e);
		}
		assertEquals(SituacaoPedido.COMPRA_RECEBIDA, pedidoService.pesquisarSituacaoPedidoById(idCompra));

		iEstoque = estoqueService.pesquisarItemEstoqueById(idItemEst);
		iCompra = pedidoService.pesquisarItemPedidoById(iCompra.getId());

		assertNull("Nao exite configuracao para o ncm. Verficique as regras de negocios.", iEstoque.getNcm());
	}

	@Test
	public void testRecepcaoItemPedidoCompraNCMVazioItemEstoque() {
		ItemPedido i = gerarItemCompraEnviado();

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

		recarregarEntidade(ItemPedido.class, i.getId());

		try {
			idItemEstoque = estoqueService.adicionarQuantidadeRecepcionadaItemCompra(i.getId(), i.getQuantidade(), "");
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
		ItemPedido i = gerarItemCompraEnviado();
		Integer quantidadeRecepcionada = i.getQuantidade() - 1;

		try {
			estoqueService.adicionarQuantidadeRecepcionadaItemCompra(i.getId(), quantidadeRecepcionada);
		} catch (BusinessException e) {
			printMensagens(e);
		}

		assertEquals(SituacaoPedido.COMPRA_AGUARDANDO_RECEBIMENTO,
				pedidoService.pesquisarSituacaoPedidoById(i.getPedido().getId()));
	}

	@Test
	public void testRecepcaoItemPedidoCompraSemAliquotaIPI() {
		ItemPedido i = gerarItemCompraEnviado();
		i.setAliquotaIPI(null);

		Integer idItemEstoque = null;
		try {
			idItemEstoque = estoqueService.adicionarQuantidadeRecepcionadaItemCompra(i.getId(), i.getQuantidade());
		} catch (BusinessException e) {
			printMensagens(e);
		}

		ItemEstoque itemEstoque = estoqueService.pesquisarItemEstoqueById(idItemEstoque);
		Double precoMedioSemFatorIPI = 68.42;

		assertEquals(
				"O item nao contem ipi entao o valor do preco medio apos a recepcao da compra deve ser o mesmo. Os valores nao conferem",
				precoMedioSemFatorIPI, NumeroUtils.arredondarValor2Decimais(itemEstoque.getPrecoMedio()));
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

		Double precoFatorICMS = 158.32;

		itemEstoque.setPrecoMedio(itemEstoque.getPrecoMedio() + 100);
		try {
			estoqueService.redefinirItemEstoque(itemEstoque);
		} catch (BusinessException e) {
			printMensagens(e);
		}
		assertEquals(
				"Apos a redefinicao do item o deve-se incluir aplicar o fator ICMS no preco de custo. Verifique o algoritmo de calculo",
				precoFatorICMS, NumeroUtils.arredondarValor2Decimais(itemEstoque.getPrecoMedioFatorICMS()));
	}

	@Test
	public void testRedefinicaoEstoqueFormaMaterialNulo() {
		ItemPedido i = gerarItemCompraEnviado();
		Integer idItemEstoque = null;
		try {
			idItemEstoque = estoqueService.adicionarQuantidadeRecepcionadaItemCompra(i.getId(), i.getQuantidade());
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
		ItemPedido i = gerarItemCompraEnviado();
		Integer idItemEstoque = null;
		try {
			idItemEstoque = estoqueService.adicionarQuantidadeRecepcionadaItemCompra(i.getId(), i.getQuantidade());
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
		ItemPedido i = gerarItemCompraEnviado();
		Integer idItemEstoque = null;
		try {
			idItemEstoque = estoqueService.adicionarQuantidadeRecepcionadaItemCompra(i.getId(), i.getQuantidade());
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
		ItemPedido i = gerarItemCompraEnviado();
		Integer idItemEstoque = null;
		try {
			idItemEstoque = estoqueService.adicionarQuantidadeRecepcionadaItemCompra(i.getId(), i.getQuantidade());
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
		ItemPedido i = gerarItemCompraEnviado();
		Integer idItemEstoque = null;
		try {
			idItemEstoque = estoqueService.adicionarQuantidadeRecepcionadaItemCompra(i.getId(), i.getQuantidade());
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
		ItemPedido i = gerarItemCompraEnviado();
		Integer idItemEstoque = null;
		try {
			idItemEstoque = estoqueService.adicionarQuantidadeRecepcionadaItemCompra(i.getId(), i.getQuantidade());
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
		ItemPedido i = gerarItemCompraEnviado();
		Integer idItemEstoque = null;
		try {
			idItemEstoque = estoqueService.adicionarQuantidadeRecepcionadaItemCompra(i.getId(), i.getQuantidade());
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
		ItemEstoque itemEsto = gerarItemEstoque();

		Integer qtdeEstoque = itemEsto.getQuantidade();

		Pedido p = gerarPedido(TipoPedido.REVENDA);

		ItemPedido itemPed = eBuilder.buildItemPedido();
		itemPed.copiar(itemEsto);
		itemPed.setPedido(p);
		itemPed.setAliquotaIPI(null);

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

		itemEsto = estoqueService.pesquisarItemEstoqueById(itemEsto.getId());
		assertEquals("Apos o envio do pedido o item do estoque deve estar zerado", (Integer) 0,
				itemEsto.getQuantidade());

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

		ItemEstoque itemEsto2 = recarregarEntidade(ItemEstoque.class, itemEsto.getId());
		assertEquals(
				"Apos a remocao de um item de um pedido as quantidades reservada desse item devem ser devolvidas ao estoque",
				qtdeEstoque, itemEsto2.getQuantidade());

	}

	@Test
	public void testReservaItemEstoqueNaoExistente() {
		gerarItemEstoque();

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
		ItemEstoque itemEstoque = gerarItemEstoque();
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
		ItemEstoque itemEstoque = gerarItemEstoque();
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
		ItemEstoque itemEstoque = gerarItemEstoque();
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
		Material material = gPedido.gerarMaterial(representada);

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
			estoqueService.adicionarQuantidadeRecepcionadaItemCompra(item1.getId(), item1.getQuantidade());
			estoqueService.adicionarQuantidadeRecepcionadaItemCompra(item2.getId(), item2.getQuantidade());
		} catch (BusinessException e1) {
			printMensagens(e1);
		}

		Pedido pedidoRevenda = gPedido.gerarPedidoRevenda();
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
		Pedido pedido = gPedido.gerarPedidoRevenda();
		Material material = gerarMaterial(pedido.getRepresentada().getId());

		ItemPedido item1 = eBuilder.buildItemPedido();
		item1.setPedido(pedido);
		item1.setMaterial(material);
		item1.setAliquotaIPI(null);

		ItemPedido item2 = eBuilder.buildItemPedidoPeca();
		item2.setPedido(pedido);
		item2.setMaterial(material);
		item2.setAliquotaIPI(null);

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

		Representada fornecedor = gRepresentada.gerarFornecedor();
		gPedido.gerarRevendedor();

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
		ItemPedido i1 = gerarItemCompraEnviado();
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
			i1 = recarregarEntidade(ItemPedido.class, i1.getId());
			estoqueService.adicionarQuantidadeRecepcionadaItemCompra(i1.getId(), i1.getQuantidade());
		} catch (BusinessException e) {
			printMensagens(e);
		}

		Pedido p = recarregarEntidade(Pedido.class, i1.getPedido().getId());
		assertEquals(SituacaoPedido.COMPRA_AGUARDANDO_RECEBIMENTO, p.getSituacaoPedido());
	}

	@Test
	public void testValorEstoqueFormaMaterial() {
		List<ItemPedido> listaItemComprado = gerarListaItemPedido(TipoPedido.COMPRA);
		for (ItemPedido itemPedido : listaItemComprado) {
			try {
				estoqueService
						.adicionarQuantidadeRecepcionadaItemCompra(itemPedido.getId(), itemPedido.getQuantidade());
			} catch (BusinessException e) {
				printMensagens(e);
			}
		}
		Double totalEstoque = 1.11;
		assertEquals(totalEstoque, estoqueService.calcularValorEstoque(null, FormaMaterial.BQ));

		totalEstoque = 2.22;
		assertEquals(totalEstoque, estoqueService.calcularValorEstoque(null, FormaMaterial.TB));
	}

	@Test
	public void testValorTotalEstoque() {
		List<ItemPedido> listaItemComprado = gerarListaItemPedido(TipoPedido.COMPRA);
		for (ItemPedido itemPedido : listaItemComprado) {
			try {
				estoqueService
						.adicionarQuantidadeRecepcionadaItemCompra(itemPedido.getId(), itemPedido.getQuantidade());
			} catch (BusinessException e) {
				printMensagens(e);
			}
		}
		Double totalEstoque = 3.33d;
		assertEquals(totalEstoque, estoqueService.calcularValorEstoque(null, null));
	}

	private void verificarQuantidadeTotalItemEstoque(Integer quantidadeItemPedido, Integer idItemEstoque) {
		Integer quantidadeItemEstoque = pesquisarQuantidadeTotalItemEstoque(idItemEstoque);
		assertEquals("As quantidades dos itens devem ser as mesmas apos inclusao no estoque", quantidadeItemPedido,
				quantidadeItemEstoque);

	}
}
