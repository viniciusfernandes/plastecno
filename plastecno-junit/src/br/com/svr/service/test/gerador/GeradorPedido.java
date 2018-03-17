package br.com.svr.service.test.gerador;

import static org.junit.Assert.assertEquals;

import java.util.List;

import javax.persistence.EntityManager;

import org.junit.Assert;

import br.com.svr.service.ClienteService;
import br.com.svr.service.ComissaoService;
import br.com.svr.service.MaterialService;
import br.com.svr.service.PedidoService;
import br.com.svr.service.PerfilAcessoService;
import br.com.svr.service.RamoAtividadeService;
import br.com.svr.service.RepresentadaService;
import br.com.svr.service.UsuarioService;
import br.com.svr.service.constante.SituacaoPedido;
import br.com.svr.service.constante.TipoAcesso;
import br.com.svr.service.constante.TipoCliente;
import br.com.svr.service.constante.TipoEntrega;
import br.com.svr.service.constante.TipoFinalidadePedido;
import br.com.svr.service.constante.TipoLogradouro;
import br.com.svr.service.constante.TipoPedido;
import br.com.svr.service.constante.TipoRelacionamento;
import br.com.svr.service.entity.Cliente;
import br.com.svr.service.entity.Contato;
import br.com.svr.service.entity.ContatoCliente;
import br.com.svr.service.entity.ContatoRepresentada;
import br.com.svr.service.entity.ItemPedido;
import br.com.svr.service.entity.Material;
import br.com.svr.service.entity.Pedido;
import br.com.svr.service.entity.PerfilAcesso;
import br.com.svr.service.entity.RamoAtividade;
import br.com.svr.service.entity.Representada;
import br.com.svr.service.entity.Transportadora;
import br.com.svr.service.entity.Usuario;
import br.com.svr.service.exception.BusinessException;
import br.com.svr.service.mensagem.email.AnexoEmail;
import br.com.svr.service.test.TestUtils;
import br.com.svr.service.test.builder.EntidadeBuilder;
import br.com.svr.service.test.builder.ServiceBuilder;

public class GeradorPedido {

	private static EntityManager em;

	private static GeradorPedido gerador;

	public static GeradorPedido getInstance() {
		if (GeradorPedido.gerador == null) {
			gerador = new GeradorPedido();
		}
		return gerador;
	}

	public static GeradorPedido getInstance(EntityManager em) {
		GeradorPedido.em = em;
		return getInstance();
	}

	private ClienteService clienteService;

	private ComissaoService comissaoService;

	private EntidadeBuilder eBuilder;

	private GeradorRepresentada gRepresentada = GeradorRepresentada.getInstance();

	private GeradorTransportadora gTransportadora = GeradorTransportadora.getInstance();

	private MaterialService materialService;

	private PedidoService pedidoService;

	private PerfilAcessoService perfilAcessoService;

	private RamoAtividadeService ramoAtividadeService;

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
		ramoAtividadeService = ServiceBuilder.buildService(RamoAtividadeService.class);
		perfilAcessoService = ServiceBuilder.buildService(PerfilAcessoService.class);
	}

	private Cliente gerarCliente(TipoCliente tipoCliente) {

		Cliente cli = null;
		if (TipoCliente.REVENDEDOR.equals(tipoCliente)) {
			cli = eBuilder.buildClienteRevendedor();
		} else {
			throw new IllegalStateException("O tipo de cliente nao existe.");
		}
		cli.setRamoAtividade(gerarRamoAtividade());
		cli.addContato(gerarContato(ContatoCliente.class));
		try {
			return clienteService.inserir(cli);
		} catch (BusinessException e) {
			printMensagens(e);
		}
		return null;
	}

	public Cliente gerarCliente(Usuario vendedor) {
		RamoAtividade ramo = gerarRamoAtividade();
		ContatoCliente ct = gerarContato(ContatoCliente.class);

		Cliente c = eBuilder.buildCliente();
		c.setRamoAtividade(ramo);
		c.setListaLogradouro(null);
		c.setVendedor(vendedor);
		c.addContato(ct);

		c.addLogradouro(eBuilder.buildLogradouroCliente(TipoLogradouro.FATURAMENTO));
		c.addLogradouro(eBuilder.buildLogradouroCliente(TipoLogradouro.ENTREGA));
		c.addLogradouro(eBuilder.buildLogradouroCliente(TipoLogradouro.COBRANCA));

		try {
			return clienteService.inserir(c);
		} catch (BusinessException e) {
			printMensagens(e);
		}

		return c;
	}

	public Cliente gerarClienteComVendedor() {
		return gerarCliente(gerarVendedor());
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

	@Deprecated
	public ItemPedido gerarItemPedido() {
		List<Representada> listaRepresentada = representadaService.pesquisarRepresentadaEFornecedor();
		Representada representada = null;
		if (listaRepresentada.isEmpty()) {
			representada = eBuilder.buildRepresentada();
			try {
				Integer idRepres = representadaService.inserir(representada);
				representada = representadaService.pesquisarById(idRepres);
			} catch (BusinessException e) {
				printMensagens(e);
			}
		} else {
			representada = listaRepresentada.get(0);
		}

		ItemPedido itemPedido = eBuilder.buildItemPedido();
		itemPedido.setMaterial(gerarMaterial(representada));
		itemPedido.setAliquotaIPI(null);
		itemPedido.setNcm("36.39.90.90");
		return itemPedido;
	}

	public ItemPedido gerarItemPedido(Integer idPedido) throws BusinessException {
		Representada repres = pedidoService.pesquisarRepresentadaByIdPedido(idPedido);
		ItemPedido itemPedido = eBuilder.buildItemPedido();
		itemPedido.setMaterial(gerarMaterial(repres));
		itemPedido.setAliquotaIPI(null);
		itemPedido.setNcm("36.39.90.90");
		Integer idItem = pedidoService.inserirItemPedido(idPedido, itemPedido);
		return pedidoService.pesquisarItemPedidoById(idItem);
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

	public Material gerarMaterial(Representada representada) {
		Material mat = materialService.pesquisarBySiglaIdentica(eBuilder.buildMaterial().getSigla());
		if (mat != null) {
			return mat;
		}

		mat = eBuilder.buildMaterial();
		mat.addRepresentada(representada);
		Integer idMat = null;
		try {
			idMat = materialService.inserir(mat);
			return materialService.pesquisarById(idMat);
		} catch (BusinessException e) {
			printMensagens(e);
		}
		return null;
	}

	public Pedido gerarOrcamento() {
		return gerarPedidoRevenda(SituacaoPedido.ORCAMENTO_DIGITACAO);
	}

	public Pedido gerarOrcamentoComItem() throws BusinessException {
		Pedido o = gerarOrcamento();
		gerarItemPedido(o.getId());
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

		vendedor.addPerfilAcesso(gerarPerfilAcesso(TipoAcesso.CADASTRO_PEDIDO_VENDAS));
		Integer idVend = null;
		try {
			idVend = usuarioService.inserir(vendedor, true);
		} catch (BusinessException e2) {
			printMensagens(e2);
		}
		vendedor = recarregarEntidade(Usuario.class, idVend);
		Transportadora transp = gTransportadora.gerarTransportadora();

		// Temos que gerar um revendedor pois eh ele que efetuara as comprar
		// para abastecer o estoque.
		Cliente cliente = TipoPedido.COMPRA.equals(tipoPedido) ? gerarRevendedor() : gerarCliente(vendedor);

		Pedido pedido = eBuilder.buildPedido();
		pedido.setCliente(cliente);
		pedido.setTransportadora(transp);
		pedido.setVendedor(vendedor);
		pedido.setTipoPedido(tipoPedido);
		pedido.setSituacaoPedido(situacaoPedido);

		try {
			comissaoService.inserirComissaoVendedor(vendedor.getId(), 0.6, 0.1);
		} catch (BusinessException e3) {
			printMensagens(e3);
		}

		Representada representada = gRepresentada.gerarRepresentada(tipoRelacionamentoRepresentada);
		if (TipoRelacionamento.REVENDA.equals(tipoRelacionamentoRepresentada)) {
			representada.addContato(gerarContato(ContatoRepresentada.class));
		}
		pedido.setRepresentada(representada);
		try {
			pedido = pedidoService.inserirPedido(pedido);
		} catch (BusinessException e1) {
			printMensagens(e1);
		}

		return pedido;
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
		Cliente revendedor = gerarRevendedor();
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

	public PerfilAcesso gerarPerfilAcesso(TipoAcesso tipoAcesso) {
		if (tipoAcesso == null) {
			return null;
		}
		List<PerfilAcesso> l = perfilAcessoService.pesquisar();
		if (l.isEmpty()) {
			throw new IllegalStateException(
					"A lista de perfil de acesso deve ser inicilizada antes de todos os testes serem executados. Veja em AbstractTest");
		}

		l = perfilAcessoService.pesquisar();
		for (PerfilAcesso p : l) {
			if (tipoAcesso.toString().equals(p.getDescricao())) {
				return p;
			}
		}
		throw new IllegalStateException(
				"Nao foi possivel encontrar o perfil de acesso cadastrado no sistema para o tipo " + tipoAcesso);
	}

	public RamoAtividade gerarRamoAtividade() {
		RamoAtividade r = pesquisarPrimeiroRegistro(RamoAtividade.class);
		if (r != null) {
			return r;
		}
		r = eBuilder.buildRamoAtividade();
		try {
			return ramoAtividadeService.inserir(r);
		} catch (BusinessException e) {
			printMensagens(e);
			throw new IllegalStateException("Falha na geracao de ramo de atividade", e);
		}
	}

	public Cliente gerarRevendedor() {
		return gerarCliente(TipoCliente.REVENDEDOR);
	}

	public Usuario gerarVendedor() {
		Usuario vend = eBuilder.buildVendedor();

		// Inserindo os perfis no sistema
		List<PerfilAcesso> lPerf = eBuilder.buildListaPerfilAcesso();
		Integer idPerf = null;
		for (PerfilAcesso p : lPerf) {
			idPerf = perfilAcessoService.inserir(p);
			p.setId(idPerf);
			vend.addPerfilAcesso(p);
		}

		Integer id = null;
		try {
			id = usuarioService.inserir(vend, true);
		} catch (BusinessException e) {
			printMensagens(e);
			return null;
		}
		vend.setId(id);
		return vend;
	}

	private <T> T pesquisarPrimeiroRegistro(Class<T> classe) {
		List<T> l = em.createQuery("from " + classe.getSimpleName(), classe).getResultList();
		return l.size() >= 1 ? l.get(0) : null;
	}

	public void printMensagens(BusinessException exception) {
		Assert.fail("Falha em alguma regra de negocio. As mensagens sao: " + exception.getMensagemConcatenada());
	}

	public <T> T recarregarEntidade(Class<T> classe, Integer id) {
		T e = em.createQuery("select e from " + classe.getSimpleName() + " e where e.id =:id", classe)
				.setParameter("id", id).getSingleResult();
		em.refresh(e);
		return e;
	}
}
