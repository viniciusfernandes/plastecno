package br.com.plastecno.service.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

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
import br.com.plastecno.service.constante.TipoApresentacaoIPI;
import br.com.plastecno.service.constante.TipoCliente;
import br.com.plastecno.service.constante.TipoEntrega;
import br.com.plastecno.service.constante.TipoLogradouro;
import br.com.plastecno.service.constante.TipoPedido;
import br.com.plastecno.service.constante.TipoRelacionamento;
import br.com.plastecno.service.constante.TipoVenda;
import br.com.plastecno.service.entity.Cliente;
import br.com.plastecno.service.entity.Contato;
import br.com.plastecno.service.entity.ItemPedido;
import br.com.plastecno.service.entity.Material;
import br.com.plastecno.service.entity.Pedido;
import br.com.plastecno.service.entity.Representada;
import br.com.plastecno.service.entity.Usuario;
import br.com.plastecno.service.exception.BusinessException;
import br.com.plastecno.service.test.builder.ServiceBuilder;

public class PedidoServiceTest extends AbstractTest {

	private class PedidoRevendaECompra {
		private final Pedido pedidoCompra;
		private final Pedido pedidoRevenda;

		public PedidoRevendaECompra(Pedido pedidoCompra, Pedido pedidoRevenda) {
			this.pedidoCompra = pedidoCompra;
			this.pedidoRevenda = pedidoRevenda;
		}

		public Pedido getPedidoCompra() {
			return pedidoCompra;
		}

		public Pedido getPedidoRevenda() {
			return pedidoRevenda;
		}

	}

	private ClienteService clienteService;

	private ComissaoService comissaoService;

	private EstoqueService estoqueService;

	private MaterialService materialService;

	private PedidoService pedidoService;

	private RepresentadaService representadaService;

	private UsuarioService usuarioService;

	public PedidoServiceTest() {
		pedidoService = ServiceBuilder.buildService(PedidoService.class);
		clienteService = ServiceBuilder.buildService(ClienteService.class);
		representadaService = ServiceBuilder.buildService(RepresentadaService.class);
		materialService = ServiceBuilder.buildService(MaterialService.class);
		usuarioService = ServiceBuilder.buildService(UsuarioService.class);
		estoqueService = ServiceBuilder.buildService(EstoqueService.class);
		comissaoService = ServiceBuilder.buildService(ComissaoService.class);
	}

	private void associarVendedor(Cliente cliente) {
		cliente.setVendedor(eBuilder.buildVendedor());
		cliente.setEmail(cliente.getEmail() + Math.random());
		try {
			clienteService.inserir(cliente);
		} catch (BusinessException e) {
			printMensagens(e);
		}
	}

	private ItemPedido gerarItemPedido() {
		List<Representada> listaRepresentada = representadaService.pesquisarRepresentadaEFornecedor();
		Representada representada = null;
		if (listaRepresentada.isEmpty()) {
			representada = eBuilder.buildRepresentada();
			try {
				representadaService.inserir(representada);
			} catch (BusinessException e) {
				printMensagens(e);
			}
		} else {
			representada = listaRepresentada.get(0);
		}

		Material material = eBuilder.buildMaterial();
		material.addRepresentada(representada);
		try {
			materialService.inserir(material);
		} catch (BusinessException e) {
			printMensagens(e);
		}

		ItemPedido itemPedido = eBuilder.buildItemPedido();
		itemPedido.setMaterial(material);
		itemPedido.setAliquotaIPI(null);
		return itemPedido;
	}

	private ItemPedido gerarItemPedidoCompra() {
		Pedido pedido = gerarPedidoCompra();
		Integer idPedido = pedido.getId();
		ItemPedido itemPedido = gerarItemPedido();
		try {
			pedidoService.inserirItemPedido(idPedido, itemPedido);
		} catch (BusinessException e) {
			printMensagens(e);
		}

		try {
			pedidoService.enviarPedido(idPedido, new byte[] {});
		} catch (BusinessException e) {
			printMensagens(e);
		}

		pedido = pedidoService.pesquisarCompraById(idPedido);
		assertEquals(SituacaoPedido.COMPRA_AGUARDANDO_RECEBIMENTO, pedido.getSituacaoPedido());
		return itemPedido;
	}

	private Material gerarMaterial(Representada representada) {
		Material material = eBuilder.buildMaterial();
		material.setImportado(true);
		material.addRepresentada(representada);
		try {
			materialService.inserir(material);
		} catch (BusinessException e1) {
			printMensagens(e1);
		}
		return material;
	}

	private Pedido gerarPedido(TipoPedido tipoPedido) {
		Usuario vendedor = eBuilder.buildVendedor();
		try {
			usuarioService.inserir(vendedor, true);
		} catch (BusinessException e2) {
			printMensagens(e2);
		}

		Pedido pedido = eBuilder.buildPedido();
		pedido.setVendedor(vendedor);
		pedido.setTipoPedido(tipoPedido);

		try {
			comissaoService.inserirComissaoRevendaVendedor(vendedor.getId(), 0.6);
		} catch (BusinessException e3) {
			printMensagens(e3);
		}

		Cliente cliente = pedido.getCliente();
		cliente.setVendedor(vendedor);
		try {
			clienteService.inserir(cliente);
		} catch (BusinessException e) {
			printMensagens(e);
		}

		Representada representada = gerarRepresentada();
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

	private Pedido gerarPedidoClienteProspectado() {
		Pedido pedido = eBuilder.buildPedido();

		Usuario vendedor = pedido.getVendedor();
		try {
			usuarioService.inserir(vendedor, true);
		} catch (BusinessException e1) {
			printMensagens(e1);
		}

		try {
			comissaoService.inserirComissaoVendedor(vendedor.getId(), 0.05, 0.1d);
		} catch (BusinessException e1) {
			printMensagens(e1);
		}

		Cliente cliente = pedido.getCliente();
		try {
			clienteService.inserir(cliente);
		} catch (BusinessException e) {
			printMensagens(e);
		}

		try {
			representadaService.inserir(pedido.getRepresentada());
		} catch (BusinessException e) {
			printMensagens(e);
		}
		return pedido;
	}

	public Pedido gerarPedidoComItem(TipoPedido tipoPedido) {
		Pedido pedido = gerarPedido(tipoPedido);
		ItemPedido item1 = gerarItemPedido();

		Integer idPedido = pedido.getId();
		try {
			pedidoService.inserirItemPedido(idPedido, item1);
		} catch (BusinessException e1) {
			printMensagens(e1);
		}
		return pedido;
	}

	private Pedido gerarPedidoCompra() {
		return gerarPedido(TipoPedido.COMPRA);
	}

	private Pedido gerarPedidoOrcamento() {
		Pedido pedido = gerarPedidoRevenda();
		pedido.setFormaPagamento("A VISTA");
		pedido.setTipoEntrega(TipoEntrega.FOB);
		pedido.setDataEntrega(TestUtils.gerarDataPosterior());
		pedido.setSituacaoPedido(SituacaoPedido.ORCAMENTO);
		pedido.getContato().setEmail("vinicius@hotmail.com");
		return pedido;
	}

	private Pedido gerarPedidoRepresentacao() {
		return gerarPedido(TipoPedido.REPRESENTACAO);
	}

	private Pedido gerarPedidoRepresentacaoComItem() {
		return gerarPedidoComItem(TipoPedido.REPRESENTACAO);
	}

	private Pedido gerarPedidoRevenda() {
		Cliente revendedor = eBuilder.buildClienteRevendedor();
		try {
			clienteService.inserir(revendedor);
		} catch (BusinessException e) {
			printMensagens(e);
		}
		return gerarPedido(TipoPedido.REVENDA);
	}

	private Pedido gerarPedidoRevendaComItem() {
		Cliente revendedor = eBuilder.buildClienteRevendedor();
		try {
			clienteService.inserir(revendedor);
		} catch (BusinessException e) {
			printMensagens(e);
		}
		return gerarPedidoComItem(TipoPedido.REVENDA);
	}

	private Pedido gerarPedidoSimples() {
		Representada representada = eBuilder.buildRepresentadaRevendedora();
		try {
			representadaService.inserir(representada);
		} catch (BusinessException e1) {
			printMensagens(e1);
		}

		Pedido pedido = eBuilder.buildPedido();

		pedido.setRepresentada(representada);
		Usuario vendedor = eBuilder.buildVendedor();
		vendedor.setId(null);

		try {
			usuarioService.inserir(vendedor, false);
		} catch (BusinessException e1) {
			printMensagens(e1);
		}

		Cliente cliente = eBuilder.buildCliente();
		cliente.setVendedor(vendedor);
		try {
			clienteService.inserir(cliente);
		} catch (BusinessException e2) {
			printMensagens(e2);
		}

		pedido.setCliente(cliente);
		pedido.setVendedor(vendedor);
		pedido.getCliente().setVendedor(vendedor);
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

	private PedidoRevendaECompra gerarRevendaEncomendada() {
		Pedido pedidoRevenda = gerarPedidoRevenda();
		ItemPedido item1 = gerarItemPedido();
		ItemPedido item2 = eBuilder.buildItemPedidoPeca();
		item2.setMaterial(item1.getMaterial());

		Integer idPedidoRevenda = pedidoRevenda.getId();
		try {
			pedidoService.inserirItemPedido(idPedidoRevenda, item1);
		} catch (BusinessException e1) {
			printMensagens(e1);
		}

		try {
			pedidoService.inserirItemPedido(idPedidoRevenda, item2);
		} catch (BusinessException e1) {
			printMensagens(e1);
		}

		try {
			pedidoService.enviarPedido(idPedidoRevenda, new byte[] {});
		} catch (BusinessException e) {
			printMensagens(e);
		}

		SituacaoPedido situacaoPedido = pedidoService.pesquisarSituacaoPedidoById(idPedidoRevenda);
		assertEquals(
				"O pedido nao contem itens no estoque e deve aguardar o setor de comprar encomendar os itens de um fornecedor",
				SituacaoPedido.ITEM_AGUARDANDO_COMPRA, situacaoPedido);

		Representada fornecedor = pedidoRevenda.getRepresentada();
		fornecedor.setTipoRelacionamento(TipoRelacionamento.REPRESENTACAO_FORNECIMENTO);

		Cliente revendedor = pedidoRevenda.getCliente();
		revendedor.setTipoCliente(TipoCliente.REVENDEDOR);

		Set<Integer> listaId = new HashSet<Integer>();
		listaId.add(item1.getId());
		listaId.add(item2.getId());
		Integer idPedidoCompra = null;
		try {
			idPedidoCompra = pedidoService.comprarItemPedido(pedidoRevenda.getComprador().getId(), fornecedor.getId(),
					listaId);
		} catch (BusinessException e) {
			printMensagens(e);
		}

		situacaoPedido = pedidoService.pesquisarSituacaoPedidoById(idPedidoRevenda);
		assertEquals("Os itens do pedido nao contem itens mas ja foram encomendados pelo setor de compras",
				SituacaoPedido.ITEM_AGUARDANDO_MATERIAL, situacaoPedido);

		Pedido pedidoCompra = pedidoService.pesquisarCompraById(idPedidoCompra);
		pedidoCompra.setFormaPagamento("A VISTA");
		pedidoCompra.setDataEntrega(TestUtils.gerarDataPosterior());
		try {
			pedidoService.enviarPedido(idPedidoCompra, new byte[] {});
		} catch (BusinessException e) {
			printMensagens(e);
		}

		return new PedidoRevendaECompra(pedidoCompra, pedidoRevenda);
	}

	@Test
	public void testAlteracaoQuantidadeRecepcionada() {
		ItemPedido itemPedido = gerarItemPedidoCompra();

		try {
			pedidoService.alterarQuantidadeRecepcionada(itemPedido.getId(), itemPedido.getQuantidade());
		} catch (BusinessException e) {
			printMensagens(e);
		}

		assertEquals(SituacaoPedido.COMPRA_AGUARDANDO_RECEBIMENTO,
				pedidoService.pesquisarSituacaoPedidoById(itemPedido.getPedido().getId()));
	}

	@Test
	public void testAlteracaoQuantidadeRecepcionadaInferiorQuantidadeComprada() {
		ItemPedido itemPedido = gerarItemPedidoCompra();

		Integer quantidadeRecepcionada = itemPedido.getQuantidade() - 1;
		try {
			pedidoService.alterarQuantidadeRecepcionada(itemPedido.getId(), quantidadeRecepcionada);
		} catch (BusinessException e) {
			printMensagens(e);
		}

		assertEquals(SituacaoPedido.COMPRA_AGUARDANDO_RECEBIMENTO,
				pedidoService.pesquisarSituacaoPedidoById(itemPedido.getPedido().getId()));
	}

	@Test
	public void testAlteracaoQuantidadeRecepcionadaSuperiorQuantidadeComprada() {
		ItemPedido itemPedido = gerarItemPedidoCompra();
		Integer quantidadeRecepcionada = itemPedido.getQuantidade() + 1;
		boolean throwed = false;
		try {
			pedidoService.alterarQuantidadeRecepcionada(itemPedido.getId(), quantidadeRecepcionada);
		} catch (BusinessException e) {
			throwed = true;
		}

		assertTrue("A quantidade recepcionada e superior a quantidade comprada e deve ser validada", throwed);
	}

	@Test
	public void testEfetuarEncomendaItemPedido() {
		Pedido pedido = gerarPedidoRepresentacao();
		Integer idPedido = pedido.getId();
		ItemPedido itemPedido = gerarItemPedido();
		try {
			pedidoService.inserirItemPedido(idPedido, itemPedido);
		} catch (BusinessException e) {
			printMensagens(e);
		}
	}

	@Test
	public void testEnvioEmailPedido() {
		Pedido pedido = gerarPedidoClienteProspectado();
		pedido.setDataEntrega(TestUtils.gerarDataPosterior());
		pedido.setFormaPagamento("30 dias");
		pedido.setTipoEntrega(TipoEntrega.FOB);
		Integer idPedido = null;
		try {
			pedido = pedidoService.inserir(pedido);
			idPedido = pedido.getId();
		} catch (BusinessException e1) {
			printMensagens(e1);
		}

		ItemPedido itemPedido = gerarItemPedido();
		try {
			pedidoService.inserirItemPedido(idPedido, itemPedido);
		} catch (BusinessException e) {
			printMensagens(e);
		}

		try {
			pedidoService.enviarPedido(idPedido, new byte[] {});
		} catch (BusinessException e) {
			printMensagens(e);
		}
	}

	@Test
	public void testEnvioEmailPedidoCancelado() {
		Pedido pedido = gerarPedidoRevenda();
		pedido.setSituacaoPedido(SituacaoPedido.CANCELADO);

		Integer idPedido = pedido.getId();
		boolean throwed = false;
		try {
			pedidoService.enviarPedido(idPedido, new byte[] {});
		} catch (BusinessException e) {
			throwed = true;
		}
		assertTrue("Pedido cancelado nao pode ser enviado por email", throwed);
	}

	@Test
	public void testEnvioEmailPedidoClienteNaoPropspectado() {
		Pedido pedido = gerarPedidoRepresentacao();
		boolean throwed = false;
		try {
			pedidoService.enviarPedido(pedido.getId(), new byte[] {});
		} catch (BusinessException e) {
			throwed = true;
		}
		assertTrue("Pedido com cliente nao propspectado nao pode ser enviado por email", throwed);
	}

	@Test
	public void testEnvioEmailPedidoJaEnviado() {
		Pedido pedido = gerarPedidoRevenda();
		pedido.setSituacaoPedido(SituacaoPedido.ENVIADO);
		boolean throwed = false;
		try {
			pedidoService.enviarPedido(pedido.getId(), new byte[] {});
		} catch (BusinessException e) {
			throwed = true;
		}
		assertTrue("Pedido ja enviado nao pode ser enviado por email", throwed);
	}

	@Test
	public void testEnvioEmailPedidoOrcamento() {
		Pedido pedido = gerarPedidoOrcamento();
		ItemPedido itemPedido = gerarItemPedido();
		Integer idPedido = pedido.getId();

		try {
			pedidoService.inserirItemPedido(idPedido, itemPedido);
		} catch (BusinessException e1) {
			printMensagens(e1);
		}

		try {
			pedidoService.enviarPedido(idPedido, new byte[] {});
		} catch (BusinessException e) {
			printMensagens(e);
		}
	}

	@Test
	public void testEnvioEmailPedidoOrcamentoSemEmailContato() {
		Pedido pedido = gerarPedidoOrcamento();
		pedido.getContato().setEmail(null);

		Integer idPedido = pedido.getId();
		ItemPedido itemPedido = gerarItemPedido();

		try {
			pedidoService.inserirItemPedido(idPedido, itemPedido);
		} catch (BusinessException e1) {
			printMensagens(e1);
		}

		boolean throwed = false;
		try {
			pedidoService.enviarPedido(idPedido, new byte[] {});
		} catch (BusinessException e) {
			throwed = true;
		}
		assertTrue("O email do contato eh obrigatorio para o envio do orcamento", throwed);
	}

	@Test
	public void testEnvioEmailPedidoReservaInvalida() {
		Pedido pedido = gerarPedidoClienteProspectado();
		pedido.setDataEntrega(TestUtils.gerarDataPosterior());
		pedido.setFormaPagamento("30 dias");
		pedido.setTipoEntrega(TipoEntrega.FOB);
		Integer idPedido = null;
		try {
			pedido = pedidoService.inserir(pedido);
			idPedido = pedido.getId();
		} catch (BusinessException e1) {
			printMensagens(e1);
		}

		ItemPedido itemPedido = gerarItemPedido();
		try {
			pedidoService.inserirItemPedido(idPedido, itemPedido);
		} catch (BusinessException e) {
			printMensagens(e);
		}

		try {
			pedidoService.enviarPedido(idPedido, new byte[] {});
		} catch (BusinessException e) {
			printMensagens(e);
		}
	}

	@Test
	public void testEnvioEmailPedidoSemEnderecoCobranca() {
		Pedido pedido = gerarPedidoRevenda();
		Cliente cliente = pedido.getCliente();
		cliente.setListaLogradouro(null);
		cliente.addLogradouro(eBuilder.buildLogradouroCliente(TipoLogradouro.ENTREGA));
		cliente.addLogradouro(eBuilder.buildLogradouroCliente(TipoLogradouro.FATURAMENTO));

		ItemPedido itemPedido = gerarItemPedido();
		Integer idPedido = pedido.getId();

		try {
			pedidoService.inserirItemPedido(idPedido, itemPedido);
		} catch (BusinessException e1) {
			printMensagens(e1);
		}

		boolean throwed = false;
		try {
			pedidoService.enviarPedido(idPedido, new byte[] {});
		} catch (BusinessException e) {
			throwed = true;
		}
		assertTrue("O endereco de cobranca eh obrigatorio para o envio do pedido por email", throwed);
	}

	@Test
	public void testEnvioEmailPedidoSemEnderecoEntrega() {
		Pedido pedido = gerarPedidoRevenda();
		Cliente cliente = pedido.getCliente();
		cliente.setListaLogradouro(null);
		cliente.addLogradouro(eBuilder.buildLogradouroCliente(TipoLogradouro.COBRANCA));
		cliente.addLogradouro(eBuilder.buildLogradouroCliente(TipoLogradouro.FATURAMENTO));

		ItemPedido itemPedido = gerarItemPedido();
		Integer idPedido = pedido.getId();

		try {
			pedidoService.inserirItemPedido(idPedido, itemPedido);
		} catch (BusinessException e1) {
			printMensagens(e1);
		}

		boolean throwed = false;
		try {
			pedidoService.enviarPedido(idPedido, new byte[] {});
		} catch (BusinessException e) {
			throwed = true;
		}
		assertTrue("O endereco de entrega eh obrigatorio para o envio do pedido por email", throwed);
	}

	@Test
	public void testEnvioEmailPedidoSemEnderecoFaturamento() {
		Pedido pedido = gerarPedidoRevenda();
		Cliente cliente = pedido.getCliente();
		cliente.setListaLogradouro(null);
		cliente.addLogradouro(eBuilder.buildLogradouroCliente(TipoLogradouro.COBRANCA));
		cliente.addLogradouro(eBuilder.buildLogradouroCliente(TipoLogradouro.ENTREGA));

		ItemPedido itemPedido = gerarItemPedido();
		Integer idPedido = pedido.getId();

		try {
			pedidoService.inserirItemPedido(idPedido, itemPedido);
		} catch (BusinessException e1) {
			printMensagens(e1);
		}

		boolean throwed = false;
		try {
			pedidoService.enviarPedido(idPedido, new byte[] {});
		} catch (BusinessException e) {
			throwed = true;
		}
		assertTrue("O endereco de faturamento eh obrigatorio para o envio do pedido por email", throwed);
	}

	@Test
	public void testEnvioEmailPedidoSemItens() {
		Pedido pedido = gerarPedidoRevenda();
		boolean throwed = false;
		try {
			pedidoService.enviarPedido(pedido.getId(), new byte[] {});
		} catch (BusinessException e) {
			throwed = true;
		}
		assertTrue("Pedido sem itens nao pode ser enviado por email", throwed);
	}

	@Test
	public void testEnvioPedidoCompra() {
		Pedido pedido = gerarPedidoClienteProspectado();
		pedido.setDataEntrega(TestUtils.gerarDataPosterior());
		pedido.setFormaPagamento("30 dias a vista");
		pedido.setTipoEntrega(TipoEntrega.CIF);
		pedido.setTipoPedido(TipoPedido.COMPRA);

		try {
			pedido = pedidoService.inserir(pedido);
		} catch (BusinessException e) {
			printMensagens(e);
		}

		ItemPedido itemPedido = gerarItemPedido();
		try {
			pedidoService.inserirItemPedido(pedido.getId(), itemPedido);
		} catch (BusinessException e) {
			printMensagens(e);
		}

		assertEquals("Antes do envio do pedido de compra ele deve estar como em digitacao", SituacaoPedido.DIGITACAO,
				pedido.getSituacaoPedido());

		try {
			pedidoService.enviarPedido(pedido.getId(), new byte[] {});
		} catch (BusinessException e) {
			printMensagens(e);
		}
		assertEquals("Apos o envio do pedido de compra, seu estado deve ser como aguardando recebimento",
				SituacaoPedido.COMPRA_AGUARDANDO_RECEBIMENTO, pedido.getSituacaoPedido());

	}

	@Test
	public void testEnvioPedidoRepresentacaoSemComissaoRepresentacao() {
		Pedido pedido = gerarPedidoRepresentacaoComItem();
		Integer idPedido = pedido.getId();
		Integer idVendedor = pedido.getVendedor().getId();
		boolean throwed = false;

		try {
			comissaoService.inserirComissaoVendedor(idVendedor, null, null);
		} catch (BusinessException e) {
			printMensagens(e);
		}

		try {
			pedidoService.enviarPedido(idPedido, new byte[] {});
		} catch (BusinessException e) {
			throwed = true;
		}
		assertTrue("O vendedor ainda nao possui comissao de representacao e deve ser validado no sistema", throwed);

		try {
			comissaoService.inserirComissaoVendedor(idVendedor, null, 0.1);
		} catch (BusinessException e) {
			printMensagens(e);
		}

		try {
			pedidoService.enviarPedido(idPedido, new byte[] {});
		} catch (BusinessException e) {
			printMensagens(e);
		}
	}

	@Test
	public void testEnvioPedidoRevendaSemComissaoRevenda() {
		Pedido pedido = gerarPedidoRevendaComItem();
		Integer idPedido = pedido.getId();
		Integer idVendedor = pedido.getVendedor().getId();
		boolean throwed = false;
		try {
			comissaoService.inserirComissaoVendedor(idVendedor, null, null);
		} catch (BusinessException e) {
			printMensagens(e);
		}

		try {
			pedidoService.enviarPedido(idPedido, new byte[] {});
		} catch (BusinessException e) {
			throwed = true;
		}

		assertTrue("O vendedor ainda nao possui comissao de revenda e deve ser validado no sistema", throwed);

		try {
			comissaoService.inserirComissaoVendedor(idVendedor, 0.1, null);
		} catch (BusinessException e) {
			printMensagens(e);
		}

		try {
			pedidoService.enviarPedido(idPedido, new byte[] {});
		} catch (BusinessException e) {
			printMensagens(e);
		}
	}

	@Test
	public void testEnvioPedidoRevendaComissaoItemPedido() {
		Pedido pedido = gerarPedidoRevendaComItem();

		Integer idPedido = pedido.getId();

		List<ItemPedido> listaItem = pedidoService.pesquisarItemPedidoByIdPedido(idPedido);
		ItemPedido itemPedido = listaItem.get(0);
		itemPedido.setAliquotaComissao(0.5d);

		final Double valorComissionado = itemPedido.calcularPrecoItem() * itemPedido.getAliquotaComissao();
		final Integer idItemPedido = itemPedido.getId();

		try {
			// Estamos inserindo a inclusao de uma aliquota de comissao do item para
			// testar o algoritmo de calculo de comissoes disparado no envio do
			// pedido. Essa informacao pode ser inputada pelo usuario e deve ter
			// prioridade no calculo.
			pedidoService.inserirItemPedido(itemPedido);
		} catch (BusinessException e1) {
			printMensagens(e1);
		}

		Integer idVendedor = pedido.getVendedor().getId();
		try {
			comissaoService.inserirComissaoVendedor(idVendedor, 0.1, null);
		} catch (BusinessException e) {
			printMensagens(e);
		}

		try {
			pedidoService.enviarPedido(idPedido, new byte[] {});
		} catch (BusinessException e) {
			printMensagens(e);
		}

		itemPedido = pedidoService.pesquisarItemPedido(idItemPedido);

		assertEquals(valorComissionado, itemPedido.getValorComissionado());
	}

	@Test
	public void testEnvioRevendaAguardandoMaterialEmpacotamentoInvalido() {
		PedidoRevendaECompra pedidoRevendaECompra = gerarRevendaEncomendada();
		Integer idPedidoCompra = pedidoRevendaECompra.getPedidoCompra().getId();
		Integer idPedidoRevenda = pedidoRevendaECompra.getPedidoRevenda().getId();

		List<ItemPedido> listaItemComprado = pedidoService.pesquisarItemPedidoByIdPedido(idPedidoCompra);
		// Vamos inserir apenas 1 item do pedido para manter a revenda como
		// encomendada.
		ItemPedido itemComprado = listaItemComprado.get(0);
		try {
			// Recepcionando os itens comprados para preencher o estoque.
			estoqueService.recepcionarItemCompra(itemComprado.getId(), itemComprado.getQuantidade());
		} catch (BusinessException e) {
			printMensagens(e);
		}

		try {
			pedidoService.empacotarItemAguardandoMaterial(idPedidoRevenda);
		} catch (BusinessException e) {
			printMensagens(e);
		}
		SituacaoPedido situacaoPedido = pedidoService.pesquisarSituacaoPedidoById(idPedidoRevenda);
		assertEquals(SituacaoPedido.ITEM_AGUARDANDO_MATERIAL, situacaoPedido);
	}

	@Test
	public void testEnvioRevendaEncomendadaEmpacotamento() {
		PedidoRevendaECompra pedidoRevendaECompra = gerarRevendaEncomendada();
		Integer idPedidoCompra = pedidoRevendaECompra.getPedidoCompra().getId();
		Integer idPedidoRevenda = pedidoRevendaECompra.getPedidoRevenda().getId();

		List<ItemPedido> listaItemComprado = pedidoService.pesquisarItemPedidoByIdPedido(idPedidoCompra);
		for (ItemPedido itemComprado : listaItemComprado) {
			// Recepcionando os itens comprados para preencher o estoque.
			try {
				estoqueService.recepcionarItemCompra(itemComprado.getId(), itemComprado.getQuantidade());
			} catch (BusinessException e) {
				printMensagens(e);
			}
		}

		try {
			pedidoService.empacotarItemAguardandoMaterial(idPedidoRevenda);
		} catch (BusinessException e) {
			printMensagens(e);
		}
		SituacaoPedido situacaoPedido = pedidoService.pesquisarSituacaoPedidoById(idPedidoRevenda);
		assertEquals(SituacaoPedido.REVENDA_AGUARDANDO_EMPACOTAMENTO, situacaoPedido);
	}

	public void testInclusaoItemPedido() {
		Pedido pedido = eBuilder.buildPedido();
		associarVendedor(pedido.getCliente());

		try {
			pedido = pedidoService.inserir(pedido);
		} catch (BusinessException e) {
			printMensagens(e);
		}

		Integer idPedido = pedido.getId();
		ItemPedido itemPedido = gerarItemPedido();
		try {
			pedidoService.inserirItemPedido(idPedido, itemPedido);
		} catch (BusinessException e) {
			printMensagens(e);
		}
	}

	@Test
	public void testInclusaoItemPedidoComIPIRepresentadaSemIPI() {
		Pedido pedido = gerarPedidoClienteProspectado();
		pedido.getRepresentada().setTipoApresentacaoIPI(TipoApresentacaoIPI.NUNCA);
		try {
			pedido = pedidoService.inserir(pedido);
		} catch (BusinessException e) {
			printMensagens(e);
		}
		ItemPedido itemPedido = eBuilder.buildItemPedido();
		itemPedido.setAliquotaIPI(0.02);
		boolean throwed = false;
		try {
			pedidoService.inserirItemPedido(pedido.getId(), itemPedido);
		} catch (BusinessException e) {
			throwed = true;
		}
		assertTrue("A representada definida no pedido nao permite valores de IPI", throwed);
	}

	@Test
	public void testInclusaoItemPedidoFormaQuadradaMedidaInternaIgualExterna() {
		Pedido pedido = gerarPedidoRepresentacao();

		try {
			pedido = pedidoService.inserir(pedido);
		} catch (BusinessException e) {
			printMensagens(e);
		}

		Integer idPedido = pedido.getId();
		ItemPedido itemPedido = gerarItemPedido();
		itemPedido.setFormaMaterial(FormaMaterial.BQ);
		try {
			pedidoService.inserirItemPedido(idPedido, itemPedido);
		} catch (BusinessException e) {
			printMensagens(e);
		}
		assertEquals(
				"Para a forma de material qaudrada as medidas interna e externa devem ser identicas apos a inclusao do item",
				itemPedido.getMedidaExterna(), itemPedido.getMedidaInterna());
	}

	@Test
	public void testInclusaoItemPedidoIPINuloMaterialImportadoRepresentadaIPIOcasional() {
		Pedido pedido = gerarPedidoClienteProspectado();
		pedido.getRepresentada().setTipoApresentacaoIPI(TipoApresentacaoIPI.OCASIONAL);
		try {
			pedido = pedidoService.inserir(pedido);
		} catch (BusinessException e) {
			printMensagens(e);
		}

		Material material = gerarMaterial(pedido.getRepresentada());
		ItemPedido itemPedido = eBuilder.buildItemPedido();
		itemPedido.setMaterial(material);
		itemPedido.setAliquotaIPI(null);

		try {
			pedidoService.inserirItemPedido(pedido.getId(), itemPedido);
		} catch (BusinessException e) {
			printMensagens(e);
		}
		Double ipi = itemPedido.getFormaMaterial().getIpi();
		assertEquals("O IPI nao foi enviado e deve ser o valor default apos a inclusao", ipi, itemPedido.getAliquotaIPI());
	}

	@Test
	public void testInclusaoItemPedidoIPINuloRepresentadaComIPIObrigatorio() {
		Pedido pedido = gerarPedidoRepresentacao();

		Representada representada = pedido.getRepresentada();
		representada.setTipoApresentacaoIPI(TipoApresentacaoIPI.SEMPRE);

		Integer idPedido = pedido.getId();
		ItemPedido itemPedido = gerarItemPedido();
		try {
			pedidoService.inserirItemPedido(idPedido, itemPedido);
		} catch (BusinessException e) {
			printMensagens(e);
		}

		try {
			pedido = pedidoService.inserir(pedido);
		} catch (BusinessException e1) {
			printMensagens(e1);
		}

		try {
			pedidoService.inserirItemPedido(pedido.getId(), itemPedido);
		} catch (BusinessException e) {
			printMensagens(e);
		}
		assertTrue("O IPI do item nao confere com o IPI da forma de material escolhida",
				FormaMaterial.TB.getIpi() == itemPedido.getAliquotaIPI().doubleValue());
	}

	@Test
	public void testInclusaoItemPedidoIPIZeradoMaterialImportadoRepresentadaIPIOcasional() {
		Pedido pedido = gerarPedidoClienteProspectado();
		pedido.getRepresentada().setTipoApresentacaoIPI(TipoApresentacaoIPI.OCASIONAL);
		try {
			pedido = pedidoService.inserir(pedido);
		} catch (BusinessException e) {
			printMensagens(e);
		}

		Material material = gerarMaterial(pedido.getRepresentada());
		ItemPedido itemPedido = eBuilder.buildItemPedido();
		itemPedido.setMaterial(material);
		itemPedido.setAliquotaIPI(0d);

		try {
			pedidoService.inserirItemPedido(pedido.getId(), itemPedido);
		} catch (BusinessException e) {
			printMensagens(e);
		}
		Double ipi = 0d;
		assertEquals("O IPI foi enviado zerado e deve ser o mesmo apos a inclusao", ipi, itemPedido.getAliquotaIPI());
	}

	@Test
	public void testInclusaoItemPedidoIPIZeradoRepresentadaComIPIObrigatorio() {
		Pedido pedido = gerarPedidoRepresentacao();
		pedido.getRepresentada().setTipoApresentacaoIPI(TipoApresentacaoIPI.SEMPRE);

		try {
			pedido = pedidoService.inserir(pedido);
		} catch (BusinessException e) {
			printMensagens(e);
		}

		ItemPedido itemPedido = gerarItemPedido();
		itemPedido.setAliquotaIPI(0d);

		try {
			pedidoService.inserirItemPedido(pedido.getId(), itemPedido);
		} catch (BusinessException e) {
			printMensagens(e);
		}
		assertTrue("O IPI do item nao confere com o IPI da forma de material escolhida",
				FormaMaterial.TB.getIpi() == itemPedido.getAliquotaIPI().doubleValue());
	}

	@Test
	public void testInclusaoItemPedidoMaterialImportadoRepresentadaIPIOcasional() {
		Pedido pedido = gerarPedidoClienteProspectado();
		pedido.getRepresentada().setTipoApresentacaoIPI(TipoApresentacaoIPI.OCASIONAL);
		try {
			pedido = pedidoService.inserir(pedido);
		} catch (BusinessException e) {
			printMensagens(e);
		}

		Material material = gerarMaterial(pedido.getRepresentada());
		final Double ipi = 0.02d;
		ItemPedido itemPedido = eBuilder.buildItemPedido();
		itemPedido.setMaterial(material);
		itemPedido.setAliquotaIPI(ipi);

		try {
			pedidoService.inserirItemPedido(pedido.getId(), itemPedido);
		} catch (BusinessException e) {
			printMensagens(e);
		}
		assertEquals("O IPI foi enviado e deve ser o mesmo apos a inclusao do item do pedido", ipi,
				itemPedido.getAliquotaIPI());
	}

	@Test
	public void testInclusaoItemPedidoMaterialImportadoRepresentadaSemIPI() {
		Pedido pedido = gerarPedidoClienteProspectado();
		pedido.getRepresentada().setTipoApresentacaoIPI(TipoApresentacaoIPI.NUNCA);
		try {
			pedido = pedidoService.inserir(pedido);
		} catch (BusinessException e) {
			printMensagens(e);
		}

		Material material = gerarMaterial(pedido.getRepresentada());

		ItemPedido itemPedido = eBuilder.buildItemPedido();
		itemPedido.setMaterial(material);
		itemPedido.setAliquotaIPI(0.02);
		boolean throwed = false;
		try {
			pedidoService.inserirItemPedido(pedido.getId(), itemPedido);
		} catch (BusinessException e) {
			throwed = true;
		}
		assertTrue("A representada definida no pedido nao permite valores de IPI", throwed);
	}

	@Test
	public void testInclusaoItemPedidoMaterialNacionalRepresentadaIPIOcasional() {
		Pedido pedido = gerarPedidoClienteProspectado();
		pedido.getRepresentada().setTipoApresentacaoIPI(TipoApresentacaoIPI.OCASIONAL);
		try {
			pedido = pedidoService.inserir(pedido);
		} catch (BusinessException e) {
			printMensagens(e);
		}

		Material material = gerarMaterial(pedido.getRepresentada());
		material.setImportado(false);

		final Double ipi = 0.02d;
		ItemPedido itemPedido = eBuilder.buildItemPedido();
		itemPedido.setMaterial(material);
		itemPedido.setAliquotaIPI(ipi);

		try {
			pedidoService.inserirItemPedido(pedido.getId(), itemPedido);
		} catch (BusinessException e) {
			printMensagens(e);
		}
		assertEquals("O IPI foi enviado e deve ser o mesmo apos a inclusao do item do pedido", ipi,
				itemPedido.getAliquotaIPI());
	}

	@Test
	public void testInclusaoItemPedidoPecaDescricaoNula() {
		Pedido pedido = gerarPedidoRepresentacao();
		Integer idPedido = pedido.getId();
		ItemPedido itemPedido = gerarItemPedido();
		itemPedido.setTipoVenda(TipoVenda.PECA);
		itemPedido.setFormaMaterial(FormaMaterial.PC);
		itemPedido.setDescricaoPeca(null);
		boolean throwed = false;
		try {
			pedidoService.inserirItemPedido(idPedido, itemPedido);
		} catch (BusinessException e) {
			throwed = true;
		}
		assertTrue("A venda de peca deve conter uma descricao", throwed);
	}

	@Test
	public void testInclusaoItemPedidoPecaPorKilo() {
		Pedido pedido = gerarPedidoRepresentacao();

		Integer idPedido = pedido.getId();
		ItemPedido itemPedido = gerarItemPedido();
		itemPedido.setTipoVenda(TipoVenda.KILO);
		itemPedido.setFormaMaterial(FormaMaterial.PC);
		itemPedido.setDescricaoPeca("engrenagem de plastico");
		boolean throwed = false;
		try {
			pedidoService.inserirItemPedido(idPedido, itemPedido);
		} catch (BusinessException e) {
			throwed = true;
		}
		assertTrue("Nao eh possivel vender uma peca por kilo. A venda deve ser feita por peca", throwed);
	}

	@Test
	public void testInclusaoItemPedidoPecaSemDescricao() {
		Pedido pedido = gerarPedidoRepresentacao();

		Integer idPedido = pedido.getId();
		ItemPedido itemPedido = gerarItemPedido();
		itemPedido.setTipoVenda(TipoVenda.PECA);
		itemPedido.setFormaMaterial(FormaMaterial.PC);
		itemPedido.setDescricaoPeca("");
		boolean throwed = false;
		try {
			pedidoService.inserirItemPedido(idPedido, itemPedido);
		} catch (BusinessException e) {
			throwed = true;
		}
		assertTrue("A venda de peca deve conter uma descricao", throwed);
	}

	@Test
	public void testInclusaoItemPedidoPecaVendidoPorKilo() {
		Pedido pedido = gerarPedidoRepresentacao();

		try {
			pedido = pedidoService.inserir(pedido);
		} catch (BusinessException e) {
			printMensagens(e);
		}

		Integer idPedido = pedido.getId();
		ItemPedido itemPedido = gerarItemPedido();
		itemPedido.setTipoVenda(TipoVenda.KILO);
		itemPedido.setFormaMaterial(FormaMaterial.PC);
		itemPedido.setDescricaoPeca("engrenagem de plastico");
		boolean throwed = false;
		try {
			pedidoService.inserirItemPedido(idPedido, itemPedido);
		} catch (BusinessException e) {
			throwed = true;
		}
		assertTrue("Uma peca nunca pode ser vendida a kilo", throwed);
	}

	@Test
	public void testInclusaoItemPedidoPecaVendidoPorPeca() {
		Pedido pedido = gerarPedidoRepresentacao();

		try {
			pedido = pedidoService.inserir(pedido);
		} catch (BusinessException e) {
			printMensagens(e);
		}

		Integer idPedido = pedido.getId();
		ItemPedido itemPedido = gerarItemPedido();
		itemPedido.setTipoVenda(TipoVenda.PECA);
		itemPedido.setFormaMaterial(FormaMaterial.PC);
		itemPedido.setDescricaoPeca("engrenagem de plastico");
		itemPedido.setMedidaExterna(null);
		itemPedido.setMedidaInterna(null);
		itemPedido.setComprimento(null);
		try {
			pedidoService.inserirItemPedido(idPedido, itemPedido);
		} catch (BusinessException e) {
			printMensagens(e);
		}
	}

	@Test
	public void testInclusaoItemPedidoRepresentadaSemIPI() {
		Pedido pedido = gerarPedidoRepresentacao();
		pedido.getRepresentada().setTipoApresentacaoIPI(null);

		try {
			pedido = pedidoService.inserir(pedido);
		} catch (BusinessException e) {
			printMensagens(e);
		}
		ItemPedido itemPedido = gerarItemPedido();
		itemPedido.setAliquotaIPI(null);
		try {
			pedidoService.inserirItemPedido(pedido.getId(), itemPedido);
		} catch (BusinessException e) {
			printMensagens(e);
		}
		assertNull("O IPI nao foi configurado e deve ser nulo", itemPedido.getAliquotaIPI());
	}

	@Test
	public void testInclusaoItemPedidoSemIPIMaterialNacionalRepresentadaIPIOcasional() {
		Pedido pedido = gerarPedidoClienteProspectado();
		pedido.getRepresentada().setTipoApresentacaoIPI(TipoApresentacaoIPI.OCASIONAL);
		try {
			pedido = pedidoService.inserir(pedido);
		} catch (BusinessException e) {
			printMensagens(e);
		}

		Material material = gerarMaterial(pedido.getRepresentada());
		material.setImportado(false);

		ItemPedido itemPedido = eBuilder.buildItemPedido();
		itemPedido.setMaterial(material);
		itemPedido.setAliquotaIPI(null);

		try {
			pedidoService.inserirItemPedido(pedido.getId(), itemPedido);
		} catch (BusinessException e) {
			printMensagens(e);
		}
		assertNull("O IPI foi nao foi enviado, portanto, nao deve existir apos a inclusao do item do pedido",
				itemPedido.getAliquotaIPI());
	}

	@Test
	public void testInclusaoItemSemIPIPedidoMaterialImportadoRepresentadaIPIOcasional() {
		Pedido pedido = gerarPedidoClienteProspectado();
		pedido.getRepresentada().setTipoApresentacaoIPI(TipoApresentacaoIPI.OCASIONAL);
		try {
			pedido = pedidoService.inserir(pedido);
		} catch (BusinessException e) {
			printMensagens(e);
		}

		Material material = gerarMaterial(pedido.getRepresentada());
		ItemPedido itemPedido = eBuilder.buildItemPedido();
		itemPedido.setMaterial(material);
		itemPedido.setAliquotaIPI(null);
		itemPedido.setFormaMaterial(FormaMaterial.CH);
		try {
			pedidoService.inserirItemPedido(pedido.getId(), itemPedido);
		} catch (BusinessException e) {
			printMensagens(e);
		}
		Double padrao = FormaMaterial.CH.getIpi();
		assertEquals(
				"O IPI de material importado nao foi enviado portanto deve ser utilizado o default apos a inclusao do item do pedido",
				padrao, itemPedido.getAliquotaIPI());
	}

	@Test
	public void testInclusaoPedidoComContatoEmBranco() {
		Pedido pedido = eBuilder.buildPedido();
		associarVendedor(pedido.getCliente());
		pedido.setContato(new Contato());

		boolean throwed = false;
		try {
			pedido = pedidoService.inserir(pedido);
		} catch (BusinessException e) {
			throwed = true;
		}
		assertTrue("O pedido deve conter um contato com informacoes preenchidas", throwed);
	}

	@Test
	public void testInclusaoPedidoCompra() {
		Pedido pedido = gerarPedidoCompra();
		pedido.setId(null);
		try {
			pedido = pedidoService.inserir(pedido);
		} catch (BusinessException e) {
			printMensagens(e);
		}

		assertEquals("Todo pedido incluido deve ir para a digitacao", SituacaoPedido.DIGITACAO, pedido.getSituacaoPedido());
		assertEquals("O tipo do pedido deve ser de compra apos a inclusao", TipoPedido.COMPRA, pedido.getTipoPedido());
	}

	@Test
	public void testInclusaoPedidoDataEntregaInvalida() {
		Pedido pedido = eBuilder.buildPedido();
		pedido.setDataEntrega(TestUtils.gerarDataAnterior());
		boolean throwed = false;
		try {
			pedido = pedidoService.inserir(pedido);
		} catch (BusinessException e) {
			throwed = true;
		}
		assertTrue("A data de entrega nao pode ser anterior a data atual na inclusao de pedidos", throwed);
	}

	@Test
	public void testInclusaoPedidoDigitado() {
		Pedido pedido = gerarPedidoSimples();
		try {
			pedido = pedidoService.inserir(pedido);
		} catch (BusinessException e) {
			printMensagens(e);
		}
		assertEquals("Todo pedido incluido deve ir para a digitacao", SituacaoPedido.DIGITACAO, pedido.getSituacaoPedido());
	}

	@Test
	public void testInclusaoPedidoDigitadoSemVendedorAssociado() {
		Pedido pedido = gerarPedidoRepresentacao();
		pedido.setVendedor(new Usuario());
		boolean throwed = false;
		try {
			pedido = pedidoService.inserir(pedido);
		} catch (BusinessException e) {
			throwed = true;
		}
		assertTrue("O Pedido a ser incluido nao esta associado ao vendedor do cliente", throwed);
	}

	@Test
	public void testInclusaoPedidoNomeContatoObrigatorio() {
		Pedido pedido = eBuilder.buildPedido();
		associarVendedor(pedido.getCliente());
		Contato contato = new Contato();
		contato.setNome("");
		pedido.setContato(contato);

		boolean throwed = false;
		try {
			pedido = pedidoService.inserir(pedido);
		} catch (BusinessException e) {
			throwed = true;
		}
		assertTrue("O nome do contato eh obrigatorio para a inclusao do pedido", throwed);
	}

	@Test
	public void testInclusaoPedidoOrcamento() {
		Pedido pedido = gerarPedidoRepresentacao();
		pedido.getCliente().setId(null);

		// Incluindo o pedido no sistema para, posteriormente, inclui-lo como
		// orcamento.
		try {
			pedido = pedidoService.inserirOrcamento(pedido);
		} catch (BusinessException e) {
			printMensagens(e);
		}

		assertNotEquals("Pedido deve ser incluido no sistema antes de virar um orcamento", null, pedido.getId());

		assertEquals("Pedido incluido deve ir para orcamento e esta definido como: "
				+ pedido.getSituacaoPedido().getDescricao(), SituacaoPedido.ORCAMENTO, pedido.getSituacaoPedido());

	}

	@Test
	public void testInclusaoPedidoRepresentacao() {
		Pedido pedido = gerarPedidoRepresentacao();
		try {
			pedido = pedidoService.inserir(pedido);
		} catch (BusinessException e) {
			printMensagens(e);
		}
		assertEquals(
				"Apos a inclusao de um pedido por representacao o tipo de pedido tem que ser configurado como representaacao",
				TipoPedido.REPRESENTACAO, pedido.getTipoPedido());
	}

	@Test
	public void testInclusaoPedidoRevenda() {
		Pedido pedido = gerarPedidoSimples();
		try {
			pedido = pedidoService.inserir(pedido);
		} catch (BusinessException e) {
			printMensagens(e);
		}
		assertEquals("Apos a inclusao de um pedido por revenda o tipo de pedido tem que ser configurado como revenda",
				TipoPedido.REVENDA, pedido.getTipoPedido());
	}

	@Test
	public void testInclusaoPedidoTipoDeEntregaSemRedespacho() {
		Pedido pedido = eBuilder.buildPedido();
		pedido.setTipoEntrega(TipoEntrega.CIF_TRANS);
		boolean throwed = false;
		try {
			pedidoService.inserir(pedido);
		} catch (BusinessException e) {
			throwed = true;
		}
		assertTrue("O pedido foi incluido uma transportadora para redespacho.", throwed);
	}

	@Test
	public void testItemPedidoAguardandoCompra() {
		Pedido pedido = gerarPedidoRevenda();
		ItemPedido item1 = gerarItemPedido();

		Integer idPedido = pedido.getId();
		try {
			pedidoService.inserirItemPedido(idPedido, item1);
		} catch (BusinessException e1) {
			printMensagens(e1);
		}

		try {
			pedidoService.enviarPedido(idPedido, new byte[] {});
		} catch (BusinessException e) {
			printMensagens(e);
		}

		SituacaoPedido situacaoPedido = pedidoService.pesquisarSituacaoPedidoById(idPedido);
		assertEquals(SituacaoPedido.ITEM_AGUARDANDO_COMPRA, situacaoPedido);

		Set<Integer> ids = new TreeSet<Integer>();
		ids.add(item1.getId());
		try {

			// Estamos alterando o tipo de relacionamento da representada para
			// podermos efetuar a encomenda dos itens para o fornecedor.
			Representada representada = pedido.getRepresentada();
			representada.setTipoRelacionamento(TipoRelacionamento.REPRESENTACAO_FORNECIMENTO);
			pedidoService.comprarItemPedido(pedido.getVendedor().getId(), pedido.getRepresentada().getId(), ids);

		} catch (BusinessException e) {
			printMensagens(e);
		}

		situacaoPedido = pedidoService.pesquisarSituacaoPedidoById(idPedido);
		assertEquals(SituacaoPedido.ITEM_AGUARDANDO_MATERIAL, situacaoPedido);
	}

	@Test
	public void testPedidoCanceladoDataEntregaInvalida() {
		Pedido pedido = gerarPedidoRepresentacao();

		try {
			// Inserindo o pedido no sistema
			pedido = pedidoService.inserir(pedido);
			// Cancelando o pedido que sera recuperado adiante para o teste
			// unitario
			pedido.setSituacaoPedido(SituacaoPedido.CANCELADO);
		} catch (BusinessException e1) {
			printMensagens(e1);
		}
		boolean throwed = false;
		try {
			// Alterando o pedido que ja foi cancelado no sistema existente no
			// sistema
			pedidoService.inserir(pedido);
		} catch (BusinessException e) {
			throwed = true;
		}
		assertTrue("O pedido ja foi cancelado e nao pode ser alterado", throwed);
	}

	@Test
	public void testReencomendaItemPedido() {
		PedidoRevendaECompra pedidoRevendaECompra = gerarRevendaEncomendada();
		Pedido pedidoRevenda = pedidoRevendaECompra.getPedidoRevenda();
		List<ItemPedido> listaItem = pedidoService.pesquisarItemPedidoByIdPedido(pedidoRevenda.getId());
		Integer idItemPedido = listaItem.get(0).getId();
		try {
			pedidoService.reencomendarItemPedido(idItemPedido);
		} catch (BusinessException e) {
			printMensagens(e);
		}
		ItemPedido itemPedido = pedidoService.pesquisarItemPedido(idItemPedido);
		assertEquals("Apos a reencomenda a quantidade reservada deve ser zero", new Integer("0"),
				itemPedido.getQuantidadeReservada());
		assertFalse("Apos a reencomenda a o item nao pode estar encomendado", itemPedido.isEncomendado());

		pedidoRevenda = pedidoService.pesquisarPedidoById(pedidoRevenda.getId());
		assertEquals("O pedido deve aguardar nova encomenda dos itens apos a reencomenda no empacotamento",
				SituacaoPedido.ITEM_AGUARDANDO_COMPRA, pedidoRevenda.getSituacaoPedido());
	}

	@Test
	public void testRefazerPedidoComIPI() {
		Pedido pedido = gerarPedidoRepresentacao();
		try {
			pedido = pedidoService.inserir(pedido);
		} catch (BusinessException e) {
			printMensagens(e);
		}

		Integer idPedido = pedido.getId();
		ItemPedido itemPedido = gerarItemPedido();
		try {
			pedidoService.inserirItemPedido(idPedido, itemPedido);
		} catch (BusinessException e) {
			printMensagens(e);
		}

		Integer idPedidoRefeito = null;
		try {
			idPedidoRefeito = pedidoService.refazerPedido(idPedido);
		} catch (BusinessException e) {
			printMensagens(e);
		}
		assertNotEquals("O pedido " + idPedido + " foi refeito e nao pode coincidir com o anterior", idPedido,
				idPedidoRefeito);

		pedido = pedidoService.pesquisarPedidoById(idPedido);
		assertEquals("O pedido " + idPedido + " foi refeito e deve estar na situacao " + SituacaoPedido.CANCELADO,
				SituacaoPedido.CANCELADO, pedido.getSituacaoPedido());

	}

	@Test
	public void testRefazerPedidoRepresentadaSemIPI() {
		Pedido pedido = gerarPedidoRepresentacao();

		try {
			pedido = pedidoService.inserir(pedido);
		} catch (BusinessException e) {
			printMensagens(e);
		}

		Integer idPedido = pedido.getId();
		ItemPedido itemPedido = gerarItemPedido();
		try {
			pedidoService.inserirItemPedido(idPedido, itemPedido);
		} catch (BusinessException e1) {
			printMensagens(e1);
		}

		Integer idPedidoRefeito = null;
		try {
			idPedidoRefeito = pedidoService.refazerPedido(idPedido);
		} catch (BusinessException e) {
			printMensagens(e);
		}

		assertNotEquals("O pedido " + idPedido + " foi refeito e nao pode coincidir com o anterior", idPedido,
				idPedidoRefeito);

		pedido = pedidoService.pesquisarPedidoById(idPedido);
		assertEquals("O pedido " + idPedido + " foi refeito e deve estar na situacao " + SituacaoPedido.CANCELADO,
				SituacaoPedido.CANCELADO, pedido.getSituacaoPedido());

	}

	@Test
	public void testRevendaComApenasUmItemEncomendado() {
		Pedido pedido = gerarPedidoRevenda();
		ItemPedido item1 = gerarItemPedido();
		ItemPedido item2 = eBuilder.buildItemPedidoPeca();
		item2.setMaterial(item1.getMaterial());

		Integer idPedido = pedido.getId();
		try {
			pedidoService.inserirItemPedido(idPedido, item1);
		} catch (BusinessException e1) {
			printMensagens(e1);
		}
		try {
			pedidoService.inserirItemPedido(idPedido, item2);
		} catch (BusinessException e1) {
			printMensagens(e1);
		}

		try {
			pedidoService.enviarPedido(idPedido, new byte[] {});
		} catch (BusinessException e) {
			printMensagens(e);
		}

		SituacaoPedido situacaoPedido = pedidoService.pesquisarSituacaoPedidoById(idPedido);
		assertEquals(
				"O pedido nao contem itens no estoque e deve aguardar o setor de comprar encomendar os itens de um fornecedor",
				SituacaoPedido.ITEM_AGUARDANDO_COMPRA, situacaoPedido);

		Representada fornecedor = pedido.getRepresentada();
		fornecedor.setTipoRelacionamento(TipoRelacionamento.REPRESENTACAO_FORNECIMENTO);

		Cliente revendedor = pedido.getCliente();
		revendedor.setTipoCliente(TipoCliente.REVENDEDOR);

		Set<Integer> listaId = new HashSet<Integer>();
		listaId.add(item1.getId());
		try {
			pedidoService.comprarItemPedido(pedido.getComprador().getId(), fornecedor.getId(), listaId);
		} catch (BusinessException e) {
			printMensagens(e);
		}

		situacaoPedido = pedidoService.pesquisarSituacaoPedidoById(idPedido);
		assertEquals("O segundo item do pedido nao foi encomendado pelo setor de compras",
				SituacaoPedido.ITEM_AGUARDANDO_COMPRA, situacaoPedido);
	}

	@Test
	public void testRevendaComTodosItensEncomendados() {
		Pedido pedido = gerarPedidoRevenda();
		ItemPedido item1 = gerarItemPedido();
		ItemPedido item2 = eBuilder.buildItemPedidoPeca();
		item2.setMaterial(item1.getMaterial());

		Integer idPedido = pedido.getId();
		try {
			pedidoService.inserirItemPedido(idPedido, item1);
		} catch (BusinessException e1) {
			printMensagens(e1);
		}
		try {
			pedidoService.inserirItemPedido(idPedido, item2);
		} catch (BusinessException e1) {
			printMensagens(e1);
		}

		try {
			pedidoService.enviarPedido(idPedido, new byte[] {});
		} catch (BusinessException e) {
			printMensagens(e);
		}

		SituacaoPedido situacaoPedido = pedidoService.pesquisarSituacaoPedidoById(idPedido);
		assertEquals(
				"O pedido nao contem itens no estoque e deve aguardar o setor de comprar encomendar os itens de um fornecedor",
				SituacaoPedido.ITEM_AGUARDANDO_COMPRA, situacaoPedido);

		Representada fornecedor = pedido.getRepresentada();
		fornecedor.setTipoRelacionamento(TipoRelacionamento.REPRESENTACAO_FORNECIMENTO);

		Cliente revendedor = pedido.getCliente();
		revendedor.setTipoCliente(TipoCliente.REVENDEDOR);

		Set<Integer> listaId = new HashSet<Integer>();
		listaId.add(item1.getId());
		listaId.add(item2.getId());
		Integer idPedidoCompra = null;
		try {
			idPedidoCompra = pedidoService.comprarItemPedido(pedido.getComprador().getId(), fornecedor.getId(), listaId);
		} catch (BusinessException e) {
			printMensagens(e);
		}

		situacaoPedido = pedidoService.pesquisarSituacaoPedidoById(idPedido);
		assertEquals("O segundo item do pedido nao foi encomendado pelo setor de compras",
				SituacaoPedido.ITEM_AGUARDANDO_MATERIAL, situacaoPedido);

		Pedido pedidoCompra = pedidoService.pesquisarCompraById(idPedidoCompra);

		assertEquals("Apos compra dos itens do pedido a encomenda foi efetuada", SituacaoPedido.DIGITACAO,
				pedidoCompra.getSituacaoPedido());

		try {
			pedidoCompra.setFormaPagamento("A VISTA");
			pedidoCompra.setDataEntrega(TestUtils.gerarDataPosterior());
			pedidoService.enviarPedido(idPedidoCompra, new byte[] {});
		} catch (BusinessException e) {
			printMensagens(e);
		}

		assertEquals("Apos o envio da compra dos itens do pedido a compra deve aguardar o recebimento",
				SituacaoPedido.COMPRA_AGUARDANDO_RECEBIMENTO, pedidoCompra.getSituacaoPedido());

		long totalItemRevenda = pedidoService.pesquisarTotalItemPedido(idPedido);
		long totalItemComprado = pedidoService.pesquisarTotalItemPedido(idPedidoCompra);
		assertEquals(totalItemRevenda, totalItemComprado);
	}

	@Test
	public void testRevendaEncomendada() {
		gerarRevendaEncomendada();
	}

	@Test
	public void testRevendaEncomendadaFornecedorInexistente() {
		Pedido pedido = gerarPedidoRevenda();
		ItemPedido item1 = gerarItemPedido();

		Integer idPedido = pedido.getId();
		try {
			pedidoService.inserirItemPedido(idPedido, item1);
		} catch (BusinessException e1) {
			printMensagens(e1);
		}

		try {
			pedidoService.enviarPedido(idPedido, new byte[] {});
		} catch (BusinessException e) {
			printMensagens(e);
		}

		SituacaoPedido situacaoPedido = pedidoService.pesquisarSituacaoPedidoById(idPedido);
		assertEquals(
				"O pedido nao contem itens no estoque e deve aguardar o setor de comprar encomendar os itens de um fornecedor",
				SituacaoPedido.ITEM_AGUARDANDO_COMPRA, situacaoPedido);

		Representada fornecedor = pedido.getRepresentada();
		fornecedor.setTipoRelacionamento(TipoRelacionamento.REPRESENTACAO_FORNECIMENTO);

		Cliente revendedor = pedido.getCliente();
		revendedor.setTipoCliente(TipoCliente.REVENDEDOR);

		Set<Integer> listaId = new HashSet<Integer>();
		listaId.add(item1.getId());
		boolean throwed = false;
		try {
			pedidoService.comprarItemPedido(pedido.getComprador().getId(), -1, listaId);
		} catch (BusinessException e) {
			throwed = true;
		}
		assertTrue("O fornecedor para a compra nao existe no sistema", throwed);
	}

	@Test
	public void testRevendaEncomendadaFornecedorInvalido() {
		Pedido pedido = gerarPedidoRevenda();
		ItemPedido item1 = gerarItemPedido();

		Integer idPedido = pedido.getId();
		try {
			pedidoService.inserirItemPedido(idPedido, item1);
		} catch (BusinessException e1) {
			printMensagens(e1);
		}

		try {
			pedidoService.enviarPedido(idPedido, new byte[] {});
		} catch (BusinessException e) {
			printMensagens(e);
		}

		SituacaoPedido situacaoPedido = pedidoService.pesquisarSituacaoPedidoById(idPedido);
		assertEquals(
				"O pedido nao contem itens no estoque e deve aguardar o setor de comprar encomendar os itens de um fornecedor",
				SituacaoPedido.ITEM_AGUARDANDO_COMPRA, situacaoPedido);

		Representada fornecedor = pedido.getRepresentada();
		fornecedor.setTipoRelacionamento(TipoRelacionamento.REPRESENTACAO);

		Cliente revendedor = pedido.getCliente();
		revendedor.setTipoCliente(TipoCliente.REVENDEDOR);

		Set<Integer> listaId = new HashSet<Integer>();
		listaId.add(item1.getId());
		boolean throwed = false;
		try {
			pedidoService.comprarItemPedido(pedido.getComprador().getId(), fornecedor.getId(), listaId);
		} catch (BusinessException e) {
			throwed = true;
		}
		assertTrue("O fornecedor eh invalido eh nao pode enfetuar encomenda de itens para pedido de compras", throwed);
	}

	@Test
	public void testRevendaEncomendadaItemPedidoInexistente() {
		Pedido pedido = gerarPedidoRevenda();
		ItemPedido item1 = gerarItemPedido();

		Integer idPedido = pedido.getId();
		try {
			pedidoService.inserirItemPedido(idPedido, item1);
		} catch (BusinessException e1) {
			printMensagens(e1);
		}

		try {
			pedidoService.enviarPedido(idPedido, new byte[] {});
		} catch (BusinessException e) {
			printMensagens(e);
		}

		SituacaoPedido situacaoPedido = pedidoService.pesquisarSituacaoPedidoById(idPedido);
		assertEquals(
				"O pedido nao contem itens no estoque e deve aguardar o setor de comprar encomendar os itens de um fornecedor",
				SituacaoPedido.ITEM_AGUARDANDO_COMPRA, situacaoPedido);

		Representada fornecedor = pedido.getRepresentada();
		fornecedor.setTipoRelacionamento(TipoRelacionamento.FORNECIMENTO);

		Cliente revendedor = pedido.getCliente();
		revendedor.setTipoCliente(TipoCliente.REVENDEDOR);

		Set<Integer> listaId = new HashSet<Integer>();
		// Inncluindo um item inexistente
		listaId.add(-1);
		boolean throwed = false;
		try {
			pedidoService.comprarItemPedido(pedido.getComprador().getId(), fornecedor.getId(), listaId);
		} catch (BusinessException e) {
			throwed = true;
		}

		assertTrue("Um item inexsitente nao pode ser encomendado, e portanto deve ser validado", throwed);
		situacaoPedido = pedidoService.pesquisarSituacaoPedidoById(idPedido);
		assertEquals(
				"O item encomendado nao existe no estoque, entao devemos cria-lo antes de encomendar, portanto o pedido deve estar na mesma situacao",
				SituacaoPedido.ITEM_AGUARDANDO_COMPRA, situacaoPedido);
	}

	@Test
	public void testRevendaEncomendadaListaIdItensNulos() {
		Pedido pedido = gerarPedidoRevenda();
		ItemPedido item1 = gerarItemPedido();
		ItemPedido item2 = eBuilder.buildItemPedidoPeca();
		item2.setMaterial(item1.getMaterial());

		Integer idPedido = pedido.getId();
		try {
			pedidoService.inserirItemPedido(idPedido, item1);
		} catch (BusinessException e1) {
			printMensagens(e1);
		}
		try {
			pedidoService.inserirItemPedido(idPedido, item2);
		} catch (BusinessException e1) {
			printMensagens(e1);
		}

		try {
			pedidoService.enviarPedido(idPedido, new byte[] {});
		} catch (BusinessException e) {
			printMensagens(e);
		}

		SituacaoPedido situacaoPedido = pedidoService.pesquisarSituacaoPedidoById(idPedido);
		assertEquals(
				"O pedido nao contem itens no estoque e deve aguardar o setor de comprar encomendar os itens de um fornecedor",
				SituacaoPedido.ITEM_AGUARDANDO_COMPRA, situacaoPedido);

		Representada fornecedor = pedido.getRepresentada();
		fornecedor.setTipoRelacionamento(TipoRelacionamento.REPRESENTACAO_FORNECIMENTO);

		Cliente revendedor = pedido.getCliente();
		revendedor.setTipoCliente(TipoCliente.REVENDEDOR);

		Set<Integer> listaId = new HashSet<Integer>();
		boolean throwed = false;
		try {
			pedidoService.comprarItemPedido(pedido.getComprador().getId(), fornecedor.getId(), listaId);
		} catch (BusinessException e) {
			throwed = true;
		}
		assertTrue("A lista de ids dos itens nao pode ser nula", throwed);
	}

	@Test
	public void testRevendaEncomendadaRevendedorInexistente() {
		Pedido pedido = gerarPedidoRevenda();
		ItemPedido item1 = gerarItemPedido();

		Integer idPedido = pedido.getId();
		try {
			pedidoService.inserirItemPedido(idPedido, item1);
		} catch (BusinessException e1) {
			printMensagens(e1);
		}

		try {
			pedidoService.enviarPedido(idPedido, new byte[] {});
		} catch (BusinessException e) {
			printMensagens(e);
		}

		SituacaoPedido situacaoPedido = pedidoService.pesquisarSituacaoPedidoById(idPedido);
		assertEquals(
				"O pedido nao contem itens no estoque e deve aguardar o setor de comprar encomendar os itens de um fornecedor",
				SituacaoPedido.ITEM_AGUARDANDO_COMPRA, situacaoPedido);

		Representada fornecedor = pedido.getRepresentada();
		fornecedor.setTipoRelacionamento(TipoRelacionamento.REPRESENTACAO);

		Set<Integer> listaId = new HashSet<Integer>();
		listaId.add(item1.getId());
		boolean throwed = false;
		try {
			pedidoService.comprarItemPedido(pedido.getComprador().getId(), fornecedor.getId(), listaId);
		} catch (BusinessException e) {
			throwed = true;
		}
		assertTrue("O revendedor nao existe no sistemea para efetuar a encomenda de itens para pedido de compras", throwed);
	}
}
