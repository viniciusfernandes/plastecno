package br.com.plastecno.service.test;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;

import mockit.Mock;
import mockit.MockUp;
import br.com.plastecno.service.ClienteService;
import br.com.plastecno.service.EmailService;
import br.com.plastecno.service.EnderecamentoService;
import br.com.plastecno.service.LogradouroService;
import br.com.plastecno.service.MaterialService;
import br.com.plastecno.service.PedidoService;
import br.com.plastecno.service.RepresentadaService;
import br.com.plastecno.service.UsuarioService;
import br.com.plastecno.service.constante.SituacaoPedido;
import br.com.plastecno.service.constante.TipoLogradouro;
import br.com.plastecno.service.dao.ClienteDAO;
import br.com.plastecno.service.dao.EnderecoDAO;
import br.com.plastecno.service.dao.ItemPedidoDAO;
import br.com.plastecno.service.dao.MaterialDAO;
import br.com.plastecno.service.dao.PedidoDAO;
import br.com.plastecno.service.dao.RepresentadaDAO;
import br.com.plastecno.service.dao.UsuarioDAO;
import br.com.plastecno.service.entity.Cliente;
import br.com.plastecno.service.entity.Logradouro;
import br.com.plastecno.service.entity.LogradouroCliente;
import br.com.plastecno.service.entity.Material;
import br.com.plastecno.service.entity.Pedido;
import br.com.plastecno.service.entity.Representada;
import br.com.plastecno.service.exception.NotificacaoException;
import br.com.plastecno.service.impl.ClienteServiceImpl;
import br.com.plastecno.service.impl.EmailServiceImpl;
import br.com.plastecno.service.impl.EnderecamentoServiceImpl;
import br.com.plastecno.service.impl.LogradouroServiceImpl;
import br.com.plastecno.service.impl.MaterialServiceImpl;
import br.com.plastecno.service.impl.PedidoServiceImpl;
import br.com.plastecno.service.impl.RepresentadaServiceImpl;
import br.com.plastecno.service.impl.UsuarioServiceImpl;
import br.com.plastecno.service.mensagem.email.MensagemEmail;

class GeradorServico {
	@SuppressWarnings("unchecked")
	static <T> T gerarServico(Class<T> classe) {
		String metodoName = "gerar" + classe.getSimpleName();
		Method method = null;
		try {
			method = GeradorServico.class.getDeclaredMethod(metodoName);
			try {
				method.setAccessible(true);
				return (T) method.invoke(GERADOR_SERVICO, (Object[]) null);
			} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
				throw new TestUtilException("Falha na execucao do metodo de inicializacao do servico \"" + "gerar"
						+ classe.getSimpleName() + "\"", e);

			}
		} catch (SecurityException | NoSuchMethodException e) {
			throw new TestUtilException("Falha na localizacao do metodo de inicializacao do servico \"" + "gerar"
					+ classe.getSimpleName() + "\"", e);
		} finally {
			if (method != null) {
				method.setAccessible(false);
			}
		}
	}

	private static final RepositorioEntidade REPOSITORIO = RepositorioEntidade.getInstance();
	private static final GeradorEntidade GERADOR_ENTIDADE = GeradorEntidade.getInstance();
	private static final GeradorServico GERADOR_SERVICO = new GeradorServico();

	GeradorServico() {
	}

	public ClienteService gerarClienteService() {
		ClienteServiceImpl clienteService = new ClienteServiceImpl();

		new MockUp<ClienteDAO>() {
			@Mock
			boolean isEmailExistente(Integer idCliente, String email) {
				return REPOSITORIO.contemEntidade(Cliente.class, "email", email, idCliente);
			}

			@Mock
			List<LogradouroCliente> pesquisarLogradouroById(Integer idCliente) {
				Cliente cliente = REPOSITORIO.pesquisarEntidadeById(Cliente.class, idCliente);
				return cliente == null ? new ArrayList<LogradouroCliente>() : cliente.getListaLogradouro();
			}

		};

		inject(clienteService, new ClienteDAO(null), "clienteDAO");
		inject(clienteService, gerarLogradouroService(), "logradouroService");
		inject(clienteService, gerarEnderecamentoService(), "enderecamentoService");
		return clienteService;
	}

	public EmailService gerarEmailService() {
		EmailServiceImpl emailService = new EmailServiceImpl();
		new MockUp<EmailServiceImpl>() {
			@Mock
			void enviar(MensagemEmail mensagemEmail) throws NotificacaoException {
			}
		};

		return emailService;
	}

	public EnderecamentoService gerarEnderecamentoService() {
		EnderecamentoService enderecamentoService = new EnderecamentoServiceImpl();
		new MockUp<EnderecoDAO>() {
			@Mock
			boolean isUFExistente(String sigla, Integer idPais) {
				return true;
			}

			@Mock
			Integer pesquisarIdBairroByDescricao(String descricao, Integer idCidade) {
				return 1;
			}

			@Mock
			Integer pesquisarIdCidadeByDescricao(String descricao, Integer idPais) {
				return 1;
			}

			@Mock
			Integer pesquisarIdPaisByDescricao(String descricao) {
				return 1;
			}

		};
		inject(enderecamentoService, new EnderecoDAO(null), "enderecoDAO");
		return enderecamentoService;
	}

	public LogradouroService gerarLogradouroService() {
		LogradouroService logradouroService = new LogradouroServiceImpl();
		inject(logradouroService, gerarEnderecamentoService(), "enderecamentoService");
		return logradouroService;
	}

	public MaterialService gerarMaterialService() {
		MaterialServiceImpl materialService = new MaterialServiceImpl();
		new MockUp<MaterialDAO>() {
			@Mock
			Material pesquisarById(Integer id) {
				return GERADOR_ENTIDADE.gerarMaterial();
			}
		};
		inject(materialService, new MaterialDAO(null), "materialDAO");
		inject(materialService, gerarRepresentadaService(), "representadaService");
		return materialService;
	}

	public PedidoService gerarPedidoService() {
		PedidoServiceImpl pedidoService = new PedidoServiceImpl();
		new MockUp<PedidoDAO>() {
			@Mock
			void cancelar(Integer IdPedido) {
				Pedido pedido = REPOSITORIO.pesquisarEntidadeById(Pedido.class, IdPedido);
				if (pedido != null) {
					pedido.setSituacaoPedido(SituacaoPedido.CANCELADO);
				}
			}

			@Mock
			Pedido inserir(Pedido t) {
				t.setId(GERADOR_ENTIDADE.gerarId());
				REPOSITORIO.inserirEntidade(t);
				return t;
			}

			@Mock
			Pedido pesquisarById(Integer idPedido) {
				return REPOSITORIO.pesquisarEntidadeById(Pedido.class, idPedido);
			}

			@Mock
			Integer pesquisarIdRepresentadaByIdPedido(Integer idPedido) {
				return REPOSITORIO.pesquisarEntidadeById(Pedido.class, idPedido).getRepresentada().getId();
			}

			@Mock
			List<Logradouro> pesquisarLogradouro(Integer idPedido) {
				List<Logradouro> lista = new ArrayList<Logradouro>();
				lista.add(GERADOR_ENTIDADE.gerarLogradouro(TipoLogradouro.COBRANCA));
				lista.add(GERADOR_ENTIDADE.gerarLogradouro(TipoLogradouro.ENTREGA));
				lista.add(GERADOR_ENTIDADE.gerarLogradouro(TipoLogradouro.FATURAMENTO));

				return lista;
			}

			@Mock
			Integer pesquisarMaxSequenciaItemPedido(Integer idPedido) {
				return 1;
			}

			@Mock
			Double pesquisarQuantidadePrecoUnidade(Integer idPedido) {
				return 120d;
			}

			@Mock
			Double pesquisarQuantidadePrecoUnidadeIPI(Integer idPedido) {
				return 55d;
			}

			@Mock
			Long pesquisarTotalItemPedido(Integer idPedido) {
				return 12L;
			}
		};

		inject(pedidoService, new PedidoDAO(null), "pedidoDAO");
		inject(pedidoService, new ItemPedidoDAO(null), "itemPedidoDAO");
		inject(pedidoService, gerarServico(UsuarioService.class), "usuarioService");
		inject(pedidoService, gerarServico(ClienteService.class), "clienteService");
		inject(pedidoService, gerarServico(LogradouroService.class), "logradouroService");
		inject(pedidoService, gerarServico(EmailService.class), "emailService");
		inject(pedidoService, gerarServico(MaterialService.class), "materialService");
		inject(pedidoService, gerarServico(RepresentadaService.class), "representadaService");

		return pedidoService;
	}

	public RepresentadaService gerarRepresentadaService() {
		RepresentadaService representadaService = new RepresentadaServiceImpl();

		new MockUp<RepresentadaDAO>() {
			@Mock
			Representada pesquisarById(Integer id) {
				return GERADOR_ENTIDADE.gerarRepresentada();
			}
		};

		inject(representadaService, new RepresentadaDAO(null), "representadaDAO");
		return representadaService;
	}

	public UsuarioService gerarUsuarioService() {
		UsuarioServiceImpl usuarioService = new UsuarioServiceImpl();
		new MockUp<UsuarioDAO>() {

			@Mock
			public Integer pesquisarIdVendedorByIdCliente(Integer idCliente, Integer idVendedor) {
				return idVendedor;
			}

			@Mock
			public void $init(EntityManager entityManager) {
			}

		};
		inject(usuarioService, new UsuarioDAO(null), "usuarioDAO");
		return usuarioService;
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

}
