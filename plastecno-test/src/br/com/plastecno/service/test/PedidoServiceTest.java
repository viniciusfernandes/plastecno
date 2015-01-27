package br.com.plastecno.service.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import mockit.Mock;
import mockit.MockUp;

import org.junit.Before;
import org.junit.Test;

import br.com.plastecno.service.ClienteService;
import br.com.plastecno.service.PedidoService;
import br.com.plastecno.service.constante.SituacaoPedido;
import br.com.plastecno.service.constante.TipoEntrega;
import br.com.plastecno.service.constante.TipoLogradouro;
import br.com.plastecno.service.constante.TipoPedido;
import br.com.plastecno.service.dao.ClienteDAO;
import br.com.plastecno.service.dao.PedidoDAO;
import br.com.plastecno.service.dao.UsuarioDAO;
import br.com.plastecno.service.entity.Cliente;
import br.com.plastecno.service.entity.ItemPedido;
import br.com.plastecno.service.entity.Pedido;
import br.com.plastecno.service.entity.Usuario;
import br.com.plastecno.service.exception.BusinessException;

public class PedidoServiceTest extends AbstractTest {

	private ClienteService clienteService;
	private PedidoService pedidoService;

	private Pedido gerarPedidoClienteProspectado() {
		Pedido pedido = gerador.gerarPedido();
		Cliente cliente = pedido.getCliente();
		cliente.setProspeccaoFinalizada(true);
		try {
			clienteService.inserir(cliente);
		} catch (BusinessException e) {
			printMensagens(e);
		}
		return pedido;
	}

	@Before
	public void init() {
		super.init();
		pedidoService = GeradorServico.gerarServico(PedidoService.class);
		clienteService = GeradorServico.gerarServico(ClienteService.class);
	}

	private void initTestEnvioEmailPedidoCancelado() {
		new MockUp<PedidoDAO>() {
			@Mock
			Pedido pesquisarById(Integer idPedido) {
				Pedido pedido = gerador.gerarPedido();
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
				Pedido pedido = gerador.gerarPedido();
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
				Pedido pedido = gerador.gerarPedido();
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
				Pedido pedido = gerador.gerarPedido();
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
				Pedido pedido = gerador.gerarPedido();
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
				Pedido pedido = gerador.gerarPedido();
				pedido.setId(idPedido);
				// Estamos supondo que o cliente ja foi prospectado
				pedido.getCliente().setProspeccaoFinalizada(true);
				pedido.setSituacaoPedido(SituacaoPedido.DIGITACAO);
				pedido.setFormaPagamento("A VISTA");
				pedido.setTipoEntrega(TipoEntrega.FOB);
				pedido.setDataEntrega(TestUtils.gerarDataPosterior());

				pedido.addLogradouro(gerador.gerarLogradouro(TipoLogradouro.ENTREGA));
				pedido.addLogradouro(gerador.gerarLogradouro(TipoLogradouro.FATURAMENTO));
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
				Pedido pedido = gerador.gerarPedido();
				pedido.setId(idPedido);
				// Estamos supondo que o cliente ja foi prospectado
				pedido.getCliente().setProspeccaoFinalizada(true);
				pedido.setSituacaoPedido(SituacaoPedido.DIGITACAO);
				pedido.setFormaPagamento("A VISTA");
				pedido.setTipoEntrega(TipoEntrega.FOB);
				pedido.setDataEntrega(TestUtils.gerarDataPosterior());

				pedido.addLogradouro(gerador.gerarLogradouro(TipoLogradouro.COBRANCA));
				pedido.addLogradouro(gerador.gerarLogradouro(TipoLogradouro.FATURAMENTO));
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
				Pedido pedido = gerador.gerarPedido();
				pedido.setId(idPedido);
				// Estamos supondo que o cliente ja foi prospectado
				pedido.getCliente().setProspeccaoFinalizada(true);
				pedido.setSituacaoPedido(SituacaoPedido.DIGITACAO);
				pedido.setFormaPagamento("A VISTA");
				pedido.setTipoEntrega(TipoEntrega.FOB);
				pedido.setDataEntrega(TestUtils.gerarDataPosterior());

				pedido.addLogradouro(gerador.gerarLogradouro(TipoLogradouro.COBRANCA));
				pedido.addLogradouro(gerador.gerarLogradouro(TipoLogradouro.ENTREGA));
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
				Pedido pedido = gerador.gerarPedido();
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
				return repositorio.pesquisarEntidadeById(Pedido.class, idPedido);
			}

			@Mock
			Integer pesquisarIdRepresentadaByIdPedido(Integer idPedido) {
				return 1;
			}

			@Mock
			List<ItemPedido> pesquisarItemPedidoByIdPedido(Integer idPedido) {
				List<ItemPedido> listaItem = new ArrayList<ItemPedido>();
				listaItem.add(gerador.gerarItemPedido());
				return listaItem;
			}
		};
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

		try {
			pedidoService.enviar(idPedido, new byte[] {});
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

		assertEquals("Antes do envio do pedido de compra ele deve estar como em digitacao", SituacaoPedido.DIGITACAO,
				pedido.getSituacaoPedido());

		try {
			pedidoService.enviar(pedido.getId(), new byte[] {});
		} catch (BusinessException e) {
			printMensagens(e);
		}
		assertEquals("Apos o envio do pedido de compra, seu estado deve ser como pendente",
				SituacaoPedido.COMPRA_PENDENTE_RECEBIMENTO, pedido.getSituacaoPedido());

	}

	@Test
	public void testInclusaoPedidoCompra() {
		Pedido pedido = gerador.gerarPedido();
		pedido.setId(null);
		pedido.setTipoPedido(TipoPedido.COMPRA);
		associarVendedor(pedido.getCliente());

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
		Pedido pedido = gerador.gerarPedido();
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
		Pedido pedido = gerador.gerarPedido();
		pedido.setId(null);
		associarVendedor(pedido.getCliente());

		try {
			pedido = pedidoService.inserir(pedido);
		} catch (BusinessException e) {
			printMensagens(e);
		}
		if (!SituacaoPedido.DIGITACAO.equals(pedido.getSituacaoPedido())) {
			fail("Todo pedido incluido deve ir para a digitacao");
		}
	}

	private void associarVendedor(Cliente cliente) {
		cliente.setVendedor(GeradorEntidade.getInstance().gerarVendedor());
		try {
			clienteService.inserir(cliente);
		} catch (BusinessException e) {
			printMensagens(e);
		}
	}

	@Test
	public void testInclusaoPedidoDigitadoSemVendedorAssociado() {
		initTestInclusaoPedidoDigitadoSemVendedorAssociado();
		Pedido pedido = gerador.gerarPedido();
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

		Pedido pedido = gerador.gerarPedido();
		associarVendedor(pedido.getCliente());

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
			fail("Pedido incluido deve ir para orcamento e esta definido como: " + pedido.getSituacaoPedido().getDescricao());
		}
	}

	@Test
	public void testInclusaoPedidoRepresentacao() {
		Pedido pedido = gerador.gerarPedido();
		associarVendedor(pedido.getCliente());
		
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
		Pedido pedido = gerador.gerarPedidoRevenda();
		associarVendedor(pedido.getCliente());

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
		Pedido pedido = gerador.gerarPedido();
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
		Pedido pedido = gerador.gerarPedido();
		associarVendedor(pedido.getCliente());

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

		Pedido pedido = gerador.gerarPedido();
		associarVendedor(pedido.getCliente());

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

		pedido = repositorio.pesquisarEntidadeById(Pedido.class, idPedido);
		assertEquals("O pedido " + idPedido + " foi refeito e deve estar na situacao " + SituacaoPedido.CANCELADO,
				SituacaoPedido.CANCELADO, pedido.getSituacaoPedido());

	}
}
