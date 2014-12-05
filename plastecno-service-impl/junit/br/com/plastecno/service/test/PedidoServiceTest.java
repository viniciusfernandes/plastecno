package br.com.plastecno.service.test;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;

import mockit.Mock;
import mockit.MockUp;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import br.com.plastecno.service.ClienteService;
import br.com.plastecno.service.PedidoService;
import br.com.plastecno.service.UsuarioService;
import br.com.plastecno.service.constante.FinalidadePedido;
import br.com.plastecno.service.constante.SituacaoPedido;
import br.com.plastecno.service.constante.TipoEntrega;
import br.com.plastecno.service.dao.ClienteDAO;
import br.com.plastecno.service.dao.GenericDAO;
import br.com.plastecno.service.dao.PedidoDAO;
import br.com.plastecno.service.dao.UsuarioDAO;
import br.com.plastecno.service.entity.Cliente;
import br.com.plastecno.service.entity.Contato;
import br.com.plastecno.service.entity.LogradouroCliente;
import br.com.plastecno.service.entity.Pedido;
import br.com.plastecno.service.entity.Representada;
import br.com.plastecno.service.entity.Usuario;
import br.com.plastecno.service.exception.BusinessException;
import br.com.plastecno.service.impl.ClienteServiceImpl;
import br.com.plastecno.service.impl.PedidoServiceImpl;
import br.com.plastecno.service.impl.UsuarioServiceImpl;

public class PedidoServiceTest extends AbstractTest {

	public PedidoService pedidoService;

	private ClienteService gerarClienteService() {
		ClienteServiceImpl clienteService = new ClienteServiceImpl();

		new MockUp<ClienteDAO>() {
			@Mock
			List<LogradouroCliente> pesquisarLogradouroById(Integer idCliente) {
				return new ArrayList<LogradouroCliente>();
			}
		};

		inject(clienteService, new ClienteDAO(null), "clienteDAO");
		return clienteService;
	}

	private Pedido gerarPedido() {
		Cliente cliente = new Cliente(1, "Vinicius");
		Representada representada = new Representada(1, "COBEX");
		Usuario vendedor = new Usuario(1, null, null);
		Contato contato = new Contato();
		contato.setNome("Adriano");

		Pedido pedido = new Pedido();
		pedido.setCliente(cliente);
		pedido.setRepresentada(representada);
		pedido.setVendedor(vendedor);
		pedido.setSituacaoPedido(SituacaoPedido.DIGITACAO);
		pedido.setFinalidadePedido(FinalidadePedido.CONSUMO);
		pedido.setContato(contato);
		return pedido;
	}

	private PedidoService gerarPedidoService() {
		PedidoServiceImpl pedidoService = new PedidoServiceImpl();
		new MockUp<PedidoDAO>() {

			@Mock
			Pedido pesquisarById(Integer idPedido) {
				return new Pedido();
			}
		};

		new MockUp<PedidoServiceImpl>() {
			@Mock
			public void $init() {
			}
		};
		initGenericDAO();
		pedidoService.init();
		inject(pedidoService, gerarUsuarioService(), "usuarioService");
		inject(pedidoService, gerarClienteService(), "clienteService");
		return pedidoService;
	}

	private UsuarioService gerarUsuarioService() {
		UsuarioServiceImpl usuarioService = new UsuarioServiceImpl();
		new MockUp<UsuarioDAO>() {
			@Mock
			public void $init(EntityManager entityManager) {
			}

			@Mock
			public Integer pesquisarIdVendedorByIdCliente(Integer idCliente, Integer idVendedor) {
				return idVendedor;
			}
		};

		usuarioService.init();
		return usuarioService;
	}

	@Before
	public void init() {
		pedidoService = gerarPedidoService();
	}

	private void initGenericDAO() {
		new MockUp<GenericDAO<Object>>() {

			@Mock
			Object alterar(Object t) {
				return t;
			}

			@Mock
			Object inserir(Object t) {
				return t;
			}
		};

	}

	private void inject(Object service, Object dependencia, String nomeCampo) {
		try {
			Field campo = service.getClass().getDeclaredField(nomeCampo);
			campo.setAccessible(true);
			campo.set(service, dependencia);
			campo.setAccessible(false);
		} catch (Exception e) {
			throw new IllegalArgumentException("Falha ao injetar a dependencia para o servico \""
					+ service.getClass().getName() + "\". Campo com problemas eh \"" + nomeCampo);
		}
	}

	@Test
	public void testInclusaoPedidoDataEntregaInvalido() {
		Pedido pedido = gerarPedido();
		pedido.setDataEntrega(TestUtils.gerarDataAnterior());
		boolean throwed = false;
		try {
			pedidoService.inserir(pedido);
		} catch (BusinessException e) {
			throwed = true;
		}
		if (!throwed) {
			Assert.fail("O pedido foi incluido uma data de entrega invalida");
		}
	}

	@Test
	public void testInclusaoPedidoDigitado() {
		Pedido pedido = gerarPedido();
		pedido.setId(null);

		try {
			pedido = pedidoService.inserir(pedido);
		} catch (BusinessException e) {
			printMensagens(e);
		}
		if (!SituacaoPedido.DIGITACAO.equals(pedido.getSituacaoPedido())) {
			Assert.fail("Todo pedido incluido deve ir para a digitacao");
		}
	}

	@Test
	public void testInclusaoPedidoOrcamento() {
		initTestInclusaoPedidoOrcamento();

		Pedido pedido = gerarPedido();
		pedido.setDataEntrega(TestUtils.gerarDataPosterior());
		// Incluindo o pedido no sistema para, posteriormente, inclui-lo como
		// orcamento.
		try {
			pedido = pedidoService.inserir(pedido);
		} catch (BusinessException e) {
			printMensagens(e);
		}

		if (pedido.getId() == null) {
			Assert.fail("Pedido deve ser incluido no sistema antes de virar um orcamento");
		}

		pedido.setSituacaoPedido(SituacaoPedido.ORCAMENTO);
		try {
			pedido = pedidoService.inserir(pedido);
		} catch (BusinessException e) {
			printMensagens(e);
		}
		if (!SituacaoPedido.ORCAMENTO.equals(pedido.getSituacaoPedido())) {
			Assert.fail("Pedido incluido deve ir para orcamento e esta definido como: "
					+ pedido.getSituacaoPedido().getDescricao());
		}
	}

	private void initTestInclusaoPedidoOrcamento() {
		new MockUp<PedidoDAO>() {
			@Mock
			Pedido inserir(Pedido t) {
				t.setId(1);
				return t;
			}

			@Mock
			Date pesquisarDataInclusaoById(Integer idPedido) {
				return new Date();
			}

			@Mock
			Date pesquisarDataEnvioById(Integer idPedido) {
				return new Date();
			}

			@Mock
			Double pesquisarValorPedido(Integer idPedido) {
				return 1200d;
			}

			@Mock
			Double pesquisarValorPedidoIPI(Integer idPedido) {
				return 12d;
			}
		};

	}

	@Test
	public void testInclusaoPedidoTipoDeEntregaSemRedespacho() {
		Pedido pedido = gerarPedido();
		pedido.setTipoEntrega(TipoEntrega.CIF_TRANS);
		boolean throwed = false;
		try {
			pedidoService.inserir(pedido);
		} catch (BusinessException e) {
			throwed = true;
		}
		if (!throwed) {
			Assert.fail("O pedido foi incluido uma transportadora para redespacho.");
		}
	}

}
