package br.com.plastecno.service.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;

import mockit.Mock;
import mockit.MockUp;

import org.junit.Before;
import org.junit.Test;

import br.com.plastecno.service.ClienteService;
import br.com.plastecno.service.EmailService;
import br.com.plastecno.service.LogradouroService;
import br.com.plastecno.service.MaterialService;
import br.com.plastecno.service.PedidoService;
import br.com.plastecno.service.RepresentadaService;
import br.com.plastecno.service.UsuarioService;
import br.com.plastecno.service.constante.FinalidadePedido;
import br.com.plastecno.service.constante.FormaMaterial;
import br.com.plastecno.service.constante.SituacaoPedido;
import br.com.plastecno.service.constante.TipoEntrega;
import br.com.plastecno.service.constante.TipoLogradouro;
import br.com.plastecno.service.constante.TipoPedido;
import br.com.plastecno.service.constante.TipoVenda;
import br.com.plastecno.service.dao.ClienteDAO;
import br.com.plastecno.service.dao.GenericDAO;
import br.com.plastecno.service.dao.ItemPedidoDAO;
import br.com.plastecno.service.dao.MaterialDAO;
import br.com.plastecno.service.dao.PedidoDAO;
import br.com.plastecno.service.dao.RepresentadaDAO;
import br.com.plastecno.service.dao.UsuarioDAO;
import br.com.plastecno.service.entity.Bairro;
import br.com.plastecno.service.entity.Cidade;
import br.com.plastecno.service.entity.Cliente;
import br.com.plastecno.service.entity.Contato;
import br.com.plastecno.service.entity.Endereco;
import br.com.plastecno.service.entity.ItemPedido;
import br.com.plastecno.service.entity.Logradouro;
import br.com.plastecno.service.entity.LogradouroCliente;
import br.com.plastecno.service.entity.Material;
import br.com.plastecno.service.entity.Pais;
import br.com.plastecno.service.entity.Pedido;
import br.com.plastecno.service.entity.Representada;
import br.com.plastecno.service.entity.Usuario;
import br.com.plastecno.service.exception.BusinessException;
import br.com.plastecno.service.exception.NotificacaoException;
import br.com.plastecno.service.impl.ClienteServiceImpl;
import br.com.plastecno.service.impl.EmailServiceImpl;
import br.com.plastecno.service.impl.LogradouroServiceImpl;
import br.com.plastecno.service.impl.MaterialServiceImpl;
import br.com.plastecno.service.impl.PedidoServiceImpl;
import br.com.plastecno.service.impl.RepresentadaServiceImpl;
import br.com.plastecno.service.impl.UsuarioServiceImpl;
import br.com.plastecno.service.mensagem.email.MensagemEmail;

public class PedidoServiceTest extends GenericTest {

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

	private EmailService gerarEmailService() {
		EmailServiceImpl emailService = new EmailServiceImpl();
		new MockUp<EmailServiceImpl>() {
			@Mock
			void enviar(MensagemEmail mensagemEmail) throws NotificacaoException {
			}
		};

		return emailService;
	}

	private Endereco gerarEndereco() {
		Bairro bairro = new Bairro();
		bairro.setDescricao("Centro");
		bairro.setId(1);

		Cidade cidade = new Cidade();
		cidade.setDescricao("Sao Paulo");
		cidade.setId(1);

		Pais pais = new Pais();
		pais.setId(1);
		pais.setDescricao("Brasil");

		Endereco endereco = new Endereco(bairro, cidade, pais);
		return endereco;
	}

	private ItemPedido gerarItemPedido() {
		ItemPedido itemPedido = new ItemPedido();
		itemPedido.setAliquotaIPI(11.1d);
		itemPedido.setMaterial(gerarMaterial());
		itemPedido.setFormaMaterial(FormaMaterial.TB);
		itemPedido.setQuantidade(2);
		itemPedido.setMedidaExterna(120d);
		itemPedido.setMedidaInterna(100d);
		itemPedido.setComprimento(1000d);
		itemPedido.setTipoVenda(TipoVenda.KILO);
		itemPedido.setPrecoVenda(60d);
		return itemPedido;
	}

	private Logradouro gerarLogradouro(TipoLogradouro tipoLogradouro) {
		Logradouro logradouro = new Logradouro(gerarEndereco());
		logradouro.setTipoLogradouro(tipoLogradouro);
		return logradouro;
	}

	private LogradouroService gerarLogradouroService() {
		return new LogradouroServiceImpl();
	}

	private Material gerarMaterial() {
		Material material = new Material(1, "PLAST", "PLASTICO DURO");
		material.setPesoEspecifico(0.33);
		return material;
	}

	private MaterialService gerarMaterialService() {
		MaterialServiceImpl materialService = new MaterialServiceImpl();
		new MockUp<MaterialDAO>() {
			@Mock
			Material pesquisarById(Integer id) {
				return gerarMaterial();
			}
		};
		inject(materialService, new MaterialDAO(null), "materialDAO");
		inject(materialService, gerarRepresentadaService(), "representadaService");
		return materialService;
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
		pedido.setTipoPedido(TipoPedido.REPRESENTACAO);
		return pedido;
	}

	private PedidoService gerarPedidoService() {
		PedidoServiceImpl pedidoService = new PedidoServiceImpl();
		new MockUp<PedidoDAO>() {

			@Mock
			void cancelar(Integer IdPedido) {
				Pedido pedido = pesquisarEntidadeById(Pedido.class, IdPedido);
				if (pedido != null) {
					pedido.setSituacaoPedido(SituacaoPedido.CANCELADO);
				}
			}

			@Mock
			Pedido pesquisarById(Integer idPedido) {
				Pedido pedido = gerarPedido();
				pedido.setId(idPedido);
				// Estamos supondo que o cliente ja foi prospectado
				pedido.getCliente().setProspeccaoFinalizada(true);
				pedido.setSituacaoPedido(SituacaoPedido.DIGITACAO);
				pedido.setFormaPagamento("A VISTA");
				pedido.setTipoEntrega(TipoEntrega.FOB);
				pedido.setDataEntrega(TestUtils.gerarDataPosterior());

				pedido.addLogradouro(gerarLogradouro(TipoLogradouro.COBRANCA));
				pedido.addLogradouro(gerarLogradouro(TipoLogradouro.ENTREGA));
				pedido.addLogradouro(gerarLogradouro(TipoLogradouro.FATURAMENTO));
				return pedido;
			}

			@Mock
			Integer pesquisarIdRepresentadaByIdPedido(Integer idPedido) {
				return pesquisarEntidadeById(Pedido.class, idPedido).getRepresentada().getId();
			}

			@Mock
			List<Logradouro> pesquisarLogradouro(Integer idPedido) {
				List<Logradouro> lista = new ArrayList<Logradouro>();
				lista.add(gerarLogradouro(TipoLogradouro.COBRANCA));
				lista.add(gerarLogradouro(TipoLogradouro.ENTREGA));
				lista.add(gerarLogradouro(TipoLogradouro.FATURAMENTO));

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

		initGenericDAO();
		inject(pedidoService, new PedidoDAO(null), "pedidoDAO");
		inject(pedidoService, new ItemPedidoDAO(null), "itemPedidoDAO");
		inject(pedidoService, gerarUsuarioService(), "usuarioService");
		inject(pedidoService, gerarClienteService(), "clienteService");
		inject(pedidoService, gerarLogradouroService(), "logradouroService");
		inject(pedidoService, gerarEmailService(), "emailService");
		inject(pedidoService, gerarMaterialService(), "materialService");

		return pedidoService;
	}

	private Representada gerarRepresentada() {
		Representada representada = new Representada(1, "COBEX");
		return representada;
	}

	private RepresentadaService gerarRepresentadaService() {
		RepresentadaService representadaService = new RepresentadaServiceImpl();

		new MockUp<RepresentadaDAO>() {
			@Mock
			Representada pesquisarById(Integer id) {
				return gerarRepresentada();
			}
		};

		inject(representadaService, new RepresentadaDAO(null), "representadaDAO");
		return representadaService;
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
				inserirEntidade(t);
				return t;
			}

			@Mock
			Object inserir(Object t) {
				try {
					Method m = t.getClass().getMethod("setId", Integer.class);
					m.invoke(t, gerarId());
				} catch (Exception e) {
					throw new IllegalArgumentException(e);
				}
				inserirEntidade(t);
				return t;
			}
		};

	}

	private void initTestEnvioEmailPedidoCancelado() {
		new MockUp<PedidoDAO>() {
			@Mock
			Pedido pesquisarById(Integer idPedido) {
				Pedido pedido = gerarPedido();
				pedido.setId(idPedido);
				// Estamos supondo que o cliente ja foi prospectado
				pedido.getCliente().setProspeccaoFinalizada(true);
				pedido.setSituacaoPedido(SituacaoPedido.CANCELADO);
				return pedido;
			}

			@Mock
			Long pesquisarTotalItemPedido(Integer idPedido) {
				return 12L;
			}
		};
	}

	private void initTestEnvioEmailPedidoClienteNaoPropspectado() {
		new MockUp<PedidoDAO>() {
			@Mock
			Pedido pesquisarById(Integer idPedido) {
				Pedido pedido = gerarPedido();
				pedido.setId(idPedido);
				pedido.getCliente().setProspeccaoFinalizada(false);
				return pedido;
			}

			@Mock
			Long pesquisarTotalItemPedido(Integer idPedido) {
				return 12L;
			}
		};
	}

	private void initTestEnvioEmailPedidoJaEnviado() {
		new MockUp<PedidoDAO>() {
			@Mock
			Pedido pesquisarById(Integer idPedido) {
				Pedido pedido = gerarPedido();
				pedido.setId(idPedido);
				// Estamos supondo que o cliente ja foi prospectado
				pedido.getCliente().setProspeccaoFinalizada(true);
				pedido.setSituacaoPedido(SituacaoPedido.ENVIADO);
				return pedido;
			}

			@Mock
			Long pesquisarTotalItemPedido(Integer idPedido) {
				return 12L;
			}
		};
	}

	private void initTestEnvioEmailPedidoOrcamento() {
		new MockUp<PedidoDAO>() {
			@Mock
			Pedido pesquisarById(Integer idPedido) {
				Pedido pedido = gerarPedido();
				pedido.setId(idPedido);
				// Estamos supondo que o cliente ja foi prospectado
				pedido.getCliente().setProspeccaoFinalizada(true);
				pedido.setSituacaoPedido(SituacaoPedido.DIGITACAO);
				pedido.setFormaPagamento("A VISTA");
				pedido.setTipoEntrega(TipoEntrega.FOB);
				pedido.setDataEntrega(TestUtils.gerarDataPosterior());
				pedido.setSituacaoPedido(SituacaoPedido.ORCAMENTO);
				pedido.getContato().setEmail("vinicius@hotmail.com");
				return pedido;
			}

			@Mock
			Long pesquisarTotalItemPedido(Integer idPedido) {
				return 12L;
			}
		};
	}

	private void initTestEnvioEmailPedidoOrcamentoSemEmailContato() {
		new MockUp<PedidoDAO>() {
			@Mock
			Pedido pesquisarById(Integer idPedido) {
				Pedido pedido = gerarPedido();
				pedido.setId(idPedido);
				// Estamos supondo que o cliente ja foi prospectado
				pedido.getCliente().setProspeccaoFinalizada(true);
				pedido.setSituacaoPedido(SituacaoPedido.DIGITACAO);
				pedido.setFormaPagamento("A VISTA");
				pedido.setTipoEntrega(TipoEntrega.FOB);
				pedido.setDataEntrega(TestUtils.gerarDataPosterior());
				pedido.setSituacaoPedido(SituacaoPedido.ORCAMENTO);
				return pedido;
			}

			@Mock
			Long pesquisarTotalItemPedido(Integer idPedido) {
				return 12L;
			}
		};
	}

	private void initTestEnvioEmailPedidoSemEnderecoCobranca() {
		new MockUp<PedidoDAO>() {
			@Mock
			Pedido pesquisarById(Integer idPedido) {
				Pedido pedido = gerarPedido();
				pedido.setId(idPedido);
				// Estamos supondo que o cliente ja foi prospectado
				pedido.getCliente().setProspeccaoFinalizada(true);
				pedido.setSituacaoPedido(SituacaoPedido.DIGITACAO);
				pedido.setFormaPagamento("A VISTA");
				pedido.setTipoEntrega(TipoEntrega.FOB);
				pedido.setDataEntrega(TestUtils.gerarDataPosterior());

				pedido.addLogradouro(gerarLogradouro(TipoLogradouro.ENTREGA));
				pedido.addLogradouro(gerarLogradouro(TipoLogradouro.FATURAMENTO));
				return pedido;
			}

			@Mock
			Long pesquisarTotalItemPedido(Integer idPedido) {
				return 12L;
			}
		};
	}

	private void initTestEnvioEmailPedidoSemEnderecoEntrega() {
		new MockUp<PedidoDAO>() {
			@Mock
			Pedido pesquisarById(Integer idPedido) {
				Pedido pedido = gerarPedido();
				pedido.setId(idPedido);
				// Estamos supondo que o cliente ja foi prospectado
				pedido.getCliente().setProspeccaoFinalizada(true);
				pedido.setSituacaoPedido(SituacaoPedido.DIGITACAO);
				pedido.setFormaPagamento("A VISTA");
				pedido.setTipoEntrega(TipoEntrega.FOB);
				pedido.setDataEntrega(TestUtils.gerarDataPosterior());

				pedido.addLogradouro(gerarLogradouro(TipoLogradouro.COBRANCA));
				pedido.addLogradouro(gerarLogradouro(TipoLogradouro.FATURAMENTO));
				return pedido;
			}

			@Mock
			Long pesquisarTotalItemPedido(Integer idPedido) {
				return 12L;
			}
		};
	}

	private void initTestEnvioEmailPedidoSemEnderecoFaturamento() {
		new MockUp<PedidoDAO>() {
			@Mock
			Pedido pesquisarById(Integer idPedido) {
				Pedido pedido = gerarPedido();
				pedido.setId(idPedido);
				// Estamos supondo que o cliente ja foi prospectado
				pedido.getCliente().setProspeccaoFinalizada(true);
				pedido.setSituacaoPedido(SituacaoPedido.DIGITACAO);
				pedido.setFormaPagamento("A VISTA");
				pedido.setTipoEntrega(TipoEntrega.FOB);
				pedido.setDataEntrega(TestUtils.gerarDataPosterior());

				pedido.addLogradouro(gerarLogradouro(TipoLogradouro.COBRANCA));
				pedido.addLogradouro(gerarLogradouro(TipoLogradouro.ENTREGA));
				return pedido;
			}

			@Mock
			Long pesquisarTotalItemPedido(Integer idPedido) {
				return 12L;
			}
		};
	}

	private void initTestEnvioEmailPedidoSemItens() {
		new MockUp<PedidoDAO>() {
			@Mock
			Pedido pesquisarById(Integer idPedido) {
				Pedido pedido = gerarPedido();
				pedido.setId(idPedido);
				// Estamos supondo que o cliente ja foi prospectado
				pedido.getCliente().setProspeccaoFinalizada(true);
				pedido.setSituacaoPedido(SituacaoPedido.DIGITACAO);
				return pedido;
			}

			@Mock
			Long pesquisarTotalItemPedido(Integer idPedido) {
				return 0L;
			}
		};
	}

	private void initTestInclusaoPedidoDigitadoSemVendedorAssociado() {
		new MockUp<UsuarioDAO>() {
			@Mock
			Integer pesquisarIdVendedorByIdCliente(Integer idCliente, Integer idVendedor) {
				return null;
			}
		};

		new MockUp<ClienteDAO>() {
			@Mock
			Cliente pesquisarById(Integer id) {
				Cliente cliente = new Cliente(id, "vinicius");
				return cliente;
			}
		};

		new MockUp<UsuarioDAO>() {
			@Mock
			Usuario pesquisarById(Integer id) {
				Usuario usuario = new Usuario(11, "vinicius", "fernandes");
				return usuario;
			}
		};
	}

	private void initTestInclusaoPedidoOrcamento() {
		new MockUp<PedidoDAO>() {
			@Mock
			Pedido inserir(Pedido t) {
				t.setId(1);
				inserirEntidade(t);
				return t;
			}

			@Mock
			Pedido pesquisarById(Integer idPedido) {
				return pesquisarEntidadeById(Pedido.class, idPedido);
			}

			@Mock
			Date pesquisarDataEnvioById(Integer idPedido) {
				return new Date();
			}

			@Mock
			Date pesquisarDataInclusaoById(Integer idPedido) {
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

	private void initTestRefazerPedido() {
		new MockUp<PedidoDAO>() {

			@Mock
			Pedido pesquisarById(Integer idPedido) {
				return pesquisarEntidadeById(Pedido.class, idPedido);
			}

			@Mock
			Integer pesquisarIdRepresentadaByIdPedido(Integer idPedido) {
				return 1;
			}

			@Mock
			List<ItemPedido> pesquisarItemPedidoByIdPedido(Integer idPedido) {
				List<ItemPedido> listaItem = new ArrayList<ItemPedido>();
				listaItem.add(gerarItemPedido());
				return listaItem;
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
	public void testEnvioEmailPedido() {
		try {
			pedidoService.enviar(1, new byte[] {});
		} catch (BusinessException e) {
			printMensagens(e);
		}
	}

	@Test
	public void testEnvioEmailPedidoCancelado() {
		initTestEnvioEmailPedidoCancelado();
		boolean throwed = false;
		try {
			pedidoService.enviar(1, new byte[] {});
		} catch (BusinessException e) {
			throwed = true;
		}
		assertTrue("Pedido cancelado nao pode ser enviado por email", throwed);
	}

	@Test
	public void testEnvioEmailPedidoClienteNaoPropspectado() {
		initTestEnvioEmailPedidoClienteNaoPropspectado();
		boolean throwed = false;
		try {
			pedidoService.enviar(1, new byte[] {});
		} catch (BusinessException e) {
			throwed = true;
		}
		assertTrue("Pedido com cliente nao propspectado nao pode ser enviado por email", throwed);
	}

	@Test
	public void testEnvioEmailPedidoJaEnviado() {
		initTestEnvioEmailPedidoJaEnviado();
		boolean throwed = false;
		try {
			pedidoService.enviar(1, new byte[] {});
		} catch (BusinessException e) {
			throwed = true;
		}
		assertTrue("Pedido ja enviado nao pode ser enviado por email", throwed);
	}

	@Test
	public void testEnvioEmailPedidoOrcamento() {
		initTestEnvioEmailPedidoOrcamento();
		try {
			pedidoService.enviar(1, new byte[] {});
		} catch (BusinessException e) {
			printMensagens(e);
		}
	}

	@Test
	public void testEnvioEmailPedidoOrcamentoSemEmailContato() {
		initTestEnvioEmailPedidoOrcamentoSemEmailContato();
		boolean throwed = false;
		try {
			pedidoService.enviar(1, new byte[] {});
		} catch (BusinessException e) {
			throwed = true;
		}
		assertTrue("O email do contato eh obrigatorio para o envio do orcamento", throwed);
	}

	@Test
	public void testEnvioEmailPedidoSemEnderecoCobranca() {
		initTestEnvioEmailPedidoSemEnderecoCobranca();
		boolean throwed = false;
		try {
			pedidoService.enviar(1, new byte[] {});
		} catch (BusinessException e) {
			throwed = true;
		}
		assertTrue("O endereco de cobranca eh obrigatorio para o envio do pedido por email", throwed);
	}

	@Test
	public void testEnvioEmailPedidoSemEnderecoEntrega() {
		initTestEnvioEmailPedidoSemEnderecoEntrega();
		boolean throwed = false;
		try {
			pedidoService.enviar(1, new byte[] {});
		} catch (BusinessException e) {
			throwed = true;
		}
		assertTrue("O endereco de entrega eh obrigatorio para o envio do pedido por email", throwed);
	}

	@Test
	public void testEnvioEmailPedidoSemEnderecoFaturamento() {
		initTestEnvioEmailPedidoSemEnderecoFaturamento();
		boolean throwed = false;
		try {
			pedidoService.enviar(1, new byte[] {});
		} catch (BusinessException e) {
			throwed = true;
		}
		assertTrue("O endereco de faturamento eh obrigatorio para o envio do pedido por email", throwed);
	}

	@Test
	public void testEnvioEmailPedidoSemItens() {
		initTestEnvioEmailPedidoSemItens();
		boolean throwed = false;
		try {
			pedidoService.enviar(1, new byte[] {});
		} catch (BusinessException e) {
			throwed = true;
		}
		assertTrue("Pedido sem itens nao pode ser enviado por email", throwed);
	}

	@Test
	public void testInclusaoPedidoDataEntregaInvalida() {
		Pedido pedido = gerarPedido();
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
		Pedido pedido = gerarPedido();
		pedido.setId(null);

		try {
			pedido = pedidoService.inserir(pedido);
		} catch (BusinessException e) {
			printMensagens(e);
		}
		if (!SituacaoPedido.DIGITACAO.equals(pedido.getSituacaoPedido())) {
			fail("Todo pedido incluido deve ir para a digitacao");
		}
	}

	@Test
	public void testInclusaoPedidoDigitadoSemVendedorAssociado() {
		initTestInclusaoPedidoDigitadoSemVendedorAssociado();
		Pedido pedido = gerarPedido();
		boolean throwed = false;
		try {
			pedido = pedidoService.inserir(pedido);
		} catch (BusinessException e) {
			throwed = true;
		}
		assertTrue("O Pedido a ser incluido nao esta associado ao vendedor do cliente", throwed);
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
			fail("Pedido deve ser incluido no sistema antes de virar um orcamento");
		}

		pedido.setSituacaoPedido(SituacaoPedido.ORCAMENTO);
		try {
			pedido = pedidoService.inserir(pedido);
		} catch (BusinessException e) {
			printMensagens(e);
		}
		if (!SituacaoPedido.ORCAMENTO.equals(pedido.getSituacaoPedido())) {
			fail("Pedido incluido deve ir para orcamento e esta definido como: "
					+ pedido.getSituacaoPedido().getDescricao());
		}
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
			fail("O pedido foi incluido uma transportadora para redespacho.");
		}
	}

	@Test
	public void testPedidoCanceladoDataEntregaInvalida() {
		Pedido pedido = gerarPedido();
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
		if (!throwed) {
			fail("O pedido ja foi cancelado e nao pode ser alterado");
		}
	}

	@Test
	public void testRefazerPedido() {
		initTestRefazerPedido();

		Pedido pedido = gerarPedido();

		try {
			pedido = pedidoService.inserir(pedido);
		} catch (BusinessException e) {
			printMensagens(e);
		}

		Integer idPedido = pedido.getId();
		Integer idPedidoRefeito = null;
		try {
			idPedidoRefeito = pedidoService.refazerPedido(idPedido);
		} catch (BusinessException e) {
			printMensagens(e);
		}
		assertNotEquals("O pedido " + idPedido + " foi refeito e nao pode coincidir com o anterior", idPedido,
				idPedidoRefeito);

		pedido = pesquisarEntidadeById(Pedido.class, idPedido);
		assertEquals("O pedido " + idPedido + " foi refeito e deve estar na situacao " + SituacaoPedido.CANCELADO,
				SituacaoPedido.CANCELADO, pedido.getSituacaoPedido());

	}
}
