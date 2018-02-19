package br.com.svr.service.test.gerador;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;

import br.com.svr.service.ClienteService;
import br.com.svr.service.ComissaoService;
import br.com.svr.service.LogradouroService;
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
import br.com.svr.service.entity.ContatoCliente;
import br.com.svr.service.entity.ContatoRepresentada;
import br.com.svr.service.entity.ItemPedido;
import br.com.svr.service.entity.LogradouroCliente;
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

	private LogradouroService logradouroService;

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
		logradouroService = ServiceBuilder.buildService(LogradouroService.class);
	}

	public Cliente gerarCliente(Usuario vendedor) {
		ContatoCliente ct = gerarContato(ContatoCliente.class);

		Cliente cliente = eBuilder.buildCliente();
		cliente.addContato(ct);
		cliente.setVendedor(vendedor);

		List<LogradouroCliente> lLog = new ArrayList<>();
		for (LogradouroCliente l : cliente.getListaLogradouro()) {
			try {
				lLog.add(logradouroService.inserir(l));
			} catch (BusinessException e) {
				printMensagens(e);
			}
		}
		cliente.setListaLogradouro(lLog);
		try {
			return clienteService.inserir(cliente);
		} catch (BusinessException e2) {
			printMensagens(e2);
		}
		return null;
	}

	public Cliente gerarClienteRevendedor() {
		Cliente revend = eBuilder.buildClienteRevendedor();
		revend.addContato(gerarContato(ContatoCliente.class));
		try {
			return clienteService.inserir(revend);
		} catch (BusinessException e) {
			printMensagens(e);
		}
		return null;
	}

	public <T extends Contato> T gerarContato(Class<T> t) {
		T ct;
		try {
			ct = t.newInstance();
		} catch (InstantiationException | IllegalAccessException e) {
			throw new IllegalStateException("Falha na criacao de uma instancia do contato do tipo "
					+ t.getClass().getName());
		}
		ct.setNome("Daniel Pereira");
		ct.setDdd("11");
		ct.setDdi("55");
		ct.setDepartamento("Comercial");
		ct.setTelefone("999999999");
		ct.setEmail("daniel@gmail.com");
		return ct;
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
		Pedido o = gerarPedidoRevenda(SituacaoPedido.ORCAMENTO_DIGITACAO);
		// Inserindo frete para testar o calculo do indice de conversao com
		// frete
		// o.setValorFrete(1000d);

		try {
			pedidoService.inserirPedido(o);
		} catch (BusinessException e1) {
			printMensagens(e1);
		}

		ItemPedido i = gerarItemPedido();
		try {
			pedidoService.inserirItemPedido(o.getId(), i);
		} catch (BusinessException e) {
			printMensagens(e);
		}
		return o;
	}

	public Pedido gerarOrcamentoSemCliente() {
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
		p.setTipoPedido(TipoPedido.REVENDA);

		Contato c = new Contato();
		c.setNome("Renato");
		c.setEmail("asdf@asdf.asdf");
		c.setDdd("11");
		c.setTelefone("54325432");
		p.setContato(c);
		return p;
	}

	public Pedido gerarPedido(TipoPedido tipoPedido, SituacaoPedido situacaoPedido,
			TipoRelacionamento tipoRelacionamentoRepresentada) {
		Usuario vendedor = eBuilder.buildVendedor();
		try {
			usuarioService.inserir(vendedor, true);
		} catch (BusinessException e2) {
			printMensagens(e2);
		}

		Transportadora transp = gTransportadora.gerarTransportadora();

		// Temos que gerar um revendedor pois eh ele que efetuara as comprar
		// para abastecer o estoque.
		Cliente cliente = TipoPedido.COMPRA.equals(tipoPedido) ? gerarClienteRevendedor() : gerarCliente(vendedor);

		Pedido pCompra = eBuilder.buildPedido();
		pCompra.setCliente(cliente);
		pCompra.setTransportadora(transp);
		pCompra.setVendedor(vendedor);
		pCompra.setTipoPedido(tipoPedido);
		pCompra.setSituacaoPedido(situacaoPedido);

		try {
			comissaoService.inserirComissaoVendedor(vendedor.getId(), 0.6, 0.1);
		} catch (BusinessException e3) {
			printMensagens(e3);
		}

		Representada representada = gRepresentada.gerarRepresentada(tipoRelacionamentoRepresentada);
		if (TipoRelacionamento.REVENDA.equals(tipoRelacionamentoRepresentada)) {
			representada.addContato(gerarContato(ContatoRepresentada.class));
		}
		pCompra.setRepresentada(representada);
		try {
			pCompra = pedidoService.inserirPedido(pCompra);
		} catch (BusinessException e1) {
			printMensagens(e1);
		}

		return pCompra;
	}

	public Pedido gerarPedido(TipoPedido tipoPedido, TipoRelacionamento tipoRelacionamento) {
		return gerarPedido(tipoPedido, SituacaoPedido.DIGITACAO, tipoRelacionamento);
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
		return gerarPedido(TipoPedido.COMPRA, TipoRelacionamento.FORNECIMENTO);
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
		return gerarPedidoRevenda(SituacaoPedido.DIGITACAO);
	}

	public Pedido gerarPedidoRevenda(SituacaoPedido situacaoPedido) {
		Cliente revendedor = eBuilder.buildRevendedor();
		ContatoCliente ct = gerarContato(ContatoCliente.class);
		revendedor.addContato(ct);
		// Garantindo a existencia de um revendedor no sistema para inserir um
		// pedido de revenda.
		try {
			clienteService.inserir(revendedor);
		} catch (BusinessException e) {
			printMensagens(e);
		}
		return gerarPedido(TipoPedido.REVENDA, situacaoPedido, TipoRelacionamento.REVENDA);
	}

	public Pedido gerarPedidoRevendaComItem() {
		Cliente revendedor = eBuilder.buildRevendedor();
		ContatoCliente ct = gerarContato(ContatoCliente.class);
		revendedor.addContato(ct);

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
		pedido.setTipoPedido(TipoPedido.REPRESENTACAO);
		Usuario vendedor = eBuilder.buildVendedor();
		vendedor.setId(null);

		try {
			usuarioService.inserir(vendedor, false);
		} catch (BusinessException e1) {
			printMensagens(e1);
		}

		pedido.setCliente(gerarCliente(vendedor));
		pedido.setVendedor(vendedor);
		pedido.getCliente().setVendedor(vendedor);
		return pedido;
	}

	public void printMensagens(BusinessException exception) {
		Assert.fail("Falha em alguma regra de negocio. As mensagens sao: " + exception.getMensagemConcatenada());
	}
}
