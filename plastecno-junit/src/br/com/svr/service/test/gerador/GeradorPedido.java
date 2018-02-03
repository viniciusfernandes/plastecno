package br.com.svr.service.test.gerador;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Assert;

import br.com.svr.service.ClienteService;
import br.com.svr.service.ComissaoService;
import br.com.svr.service.MaterialService;
import br.com.svr.service.PedidoService;
import br.com.svr.service.RepresentadaService;
import br.com.svr.service.UsuarioService;
import br.com.svr.service.constante.SituacaoPedido;
import br.com.svr.service.constante.TipoEntrega;
import br.com.svr.service.constante.TipoFinalidadePedido;
import br.com.svr.service.constante.TipoPedido;
import br.com.svr.service.constante.TipoRelacionamento;
import br.com.svr.service.entity.Cliente;
import br.com.svr.service.entity.Contato;
import br.com.svr.service.entity.ItemPedido;
import br.com.svr.service.entity.Material;
import br.com.svr.service.entity.Pedido;
import br.com.svr.service.entity.Representada;
import br.com.svr.service.entity.Transportadora;
import br.com.svr.service.entity.Usuario;
import br.com.svr.service.exception.BusinessException;
import br.com.svr.service.mensagem.email.AnexoEmail;
import br.com.svr.service.test.TestUtils;
import br.com.svr.service.test.builder.EntidadeBuilder;
import br.com.svr.service.test.builder.ServiceBuilder;

public class GeradorPedido {

	private static GeradorPedido gerador;

	public static GeradorPedido getInstance() {
		if (gerador == null) {
			gerador = new GeradorPedido();
		}
		return gerador;
	}

	private ClienteService clienteService;

	private ComissaoService comissaoService;

	private EntidadeBuilder eBuilder;

	private GeradorRepresentada gRepresentada = GeradorRepresentada.getInstance();

	private GeradorTransportadora gTransportadora = GeradorTransportadora.getInstance();

	private MaterialService materialService;

	private PedidoService pedidoService;

	private RepresentadaService representadaService;

	private UsuarioService usuarioService;

	private GeradorPedido() {
		eBuilder = EntidadeBuilder.getInstance();
		pedidoService = ServiceBuilder.buildService(PedidoService.class);
		clienteService = ServiceBuilder.buildService(ClienteService.class);
		representadaService = ServiceBuilder.buildService(RepresentadaService.class);
		materialService = ServiceBuilder.buildService(MaterialService.class);
		usuarioService = ServiceBuilder.buildService(UsuarioService.class);
		comissaoService = ServiceBuilder.buildService(ComissaoService.class);
	}

	public ItemPedido gerarItemPedido() {
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
		itemPedido.setNcm("36.39.90.90");
		return itemPedido;
	}

	public ItemPedido gerarItemPedidoCompra() {
		Pedido pedido = gerarPedidoCompra();
		Integer idPedido = pedido.getId();
		ItemPedido itemPedido = gerarItemPedido();
		try {
			pedidoService.inserirItemPedido(idPedido, itemPedido);
		} catch (BusinessException e) {
			printMensagens(e);
		}

		try {
			pedidoService.enviarPedido(idPedido, new AnexoEmail(new byte[] {}));
		} catch (BusinessException e) {
			printMensagens(e);
		}

		pedido = pedidoService.pesquisarCompraById(idPedido);
		assertEquals(SituacaoPedido.COMPRA_AGUARDANDO_RECEBIMENTO, pedido.getSituacaoPedido());
		return itemPedido;
	}

	public ItemPedido gerarItemPedidoPeca() {
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

		ItemPedido itemPedido = eBuilder.buildItemPedidoPeca();
		itemPedido.setMaterial(material);
		itemPedido.setAliquotaIPI(null);
		itemPedido.setQuantidade(44);
		return itemPedido;
	}

	public Pedido gerarOrcamento() {
		Usuario vendedor = eBuilder.buildVendedor();
		vendedor.setSenha("asdf34");
		try {
			usuarioService.inserir(vendedor, true);
		} catch (BusinessException e) {
			printMensagens(e);
		}
		Pedido p = new Pedido();
		p.setRepresentada(gRepresentada.gerarRevendedor());
		p.setVendedor(vendedor);
		p.setFinalidadePedido(TipoFinalidadePedido.CONSUMO);

		Contato c = new Contato();
		c.setNome("Renato");
		c.setEmail("asdf@asdf.asdf");
		c.setDdd("11");
		c.setTelefone("54325432");
		p.setContato(c);
		return p;
	}

	public Pedido gerarPedido(TipoPedido tipoPedido, TipoRelacionamento tipoRelacionamento) {
		Usuario vendedor = eBuilder.buildVendedor();
		try {
			usuarioService.inserir(vendedor, true);
		} catch (BusinessException e2) {
			printMensagens(e2);
		}

		Transportadora transp = gTransportadora.gerarTransportadora();

		Pedido pedido = eBuilder.buildPedido();
		pedido.setTransportadora(transp);
		pedido.setVendedor(vendedor);
		pedido.setTipoPedido(tipoPedido);

		try {
			comissaoService.inserirComissaoVendedor(vendedor.getId(), 0.6, 0.1);
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

		Representada representada = gRepresentada.gerarRepresentada(tipoRelacionamento);
		pedido.setRepresentada(representada);
		try {
			pedido = pedidoService.inserirPedido(pedido);
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

	public Pedido gerarPedidoClienteProspectado() {
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
		Pedido pedido = gerarPedido(tipoPedido, TipoPedido.REVENDA.equals(tipoPedido) ? TipoRelacionamento.REVENDA
				: TipoRelacionamento.REPRESENTACAO);
		ItemPedido item1 = gerarItemPedido();
		ItemPedido item2 = gerarItemPedidoPeca();
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
		return pedido;
	}

	public Pedido gerarPedidoCompra() {
		return gerarPedido(TipoPedido.COMPRA, TipoRelacionamento.REPRESENTACAO);
	}

	public Pedido gerarPedidoOrcamento() {
		Pedido pedido = gerarPedidoRevenda();
		pedido.setFormaPagamento("A VISTA");
		pedido.setTipoEntrega(TipoEntrega.FOB);
		pedido.setDataEntrega(TestUtils.gerarDataAmanha());
		pedido.setSituacaoPedido(SituacaoPedido.ORCAMENTO);
		pedido.getContato().setEmail("vinicius@hotmail.com");
		pedido.getContato().setDdd("11");
		pedido.getContato().setTelefone("43219999");
		return pedido;
	}

	public Pedido gerarPedidoRepresentacao() {
		return gerarPedido(TipoPedido.REPRESENTACAO, TipoRelacionamento.REPRESENTACAO);
	}

	public Pedido gerarPedidoRepresentacaoComItem() {
		return gerarPedidoComItem(TipoPedido.REPRESENTACAO);
	}

	public Pedido gerarPedidoRevenda() {
		Cliente revendedor = eBuilder.buildClienteRevendedor();
		try {
			clienteService.inserir(revendedor);
		} catch (BusinessException e) {
			printMensagens(e);
		}
		return gerarPedido(TipoPedido.REVENDA, TipoRelacionamento.REPRESENTACAO);
	}

	public Pedido gerarPedidoRevendaComItem() {
		Cliente revendedor = eBuilder.buildClienteRevendedor();
		try {
			clienteService.inserir(revendedor);
		} catch (BusinessException e) {
			printMensagens(e);
		}
		return gerarPedidoComItem(TipoPedido.REVENDA);
	}

	public Pedido gerarPedidoSimples() {
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

	public void printMensagens(BusinessException exception) {
		Assert.fail("Falha em alguma regra de negocio. As mensagens sao: " + exception.getMensagemConcatenada());
	}
}
