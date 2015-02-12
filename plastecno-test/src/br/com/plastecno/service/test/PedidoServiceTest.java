package br.com.plastecno.service.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Date;
import java.util.List;

import mockit.Mock;
import mockit.MockUp;

import org.junit.Test;

import br.com.plastecno.service.ClienteService;
import br.com.plastecno.service.MaterialService;
import br.com.plastecno.service.PedidoService;
import br.com.plastecno.service.RepresentadaService;
import br.com.plastecno.service.constante.FormaMaterial;
import br.com.plastecno.service.constante.SituacaoPedido;
import br.com.plastecno.service.constante.TipoApresentacaoIPI;
import br.com.plastecno.service.constante.TipoEntrega;
import br.com.plastecno.service.constante.TipoLogradouro;
import br.com.plastecno.service.constante.TipoVenda;
import br.com.plastecno.service.dao.ClienteDAO;
import br.com.plastecno.service.dao.PedidoDAO;
import br.com.plastecno.service.dao.UsuarioDAO;
import br.com.plastecno.service.entity.Cliente;
import br.com.plastecno.service.entity.Contato;
import br.com.plastecno.service.entity.ItemPedido;
import br.com.plastecno.service.entity.Material;
import br.com.plastecno.service.entity.Pedido;
import br.com.plastecno.service.entity.Representada;
import br.com.plastecno.service.entity.Usuario;
import br.com.plastecno.service.exception.BusinessException;

public class PedidoServiceTest extends AbstractTest {

	private ClienteService clienteService;
	private PedidoService pedidoService;
	private RepresentadaService representadaService;
	private MaterialService materialService;

	private void associarVendedor(Cliente cliente) {
		cliente.setVendedor(eBuilder.buildVendedor());
		try {
			clienteService.inserir(cliente);
		} catch (BusinessException e) {
			printMensagens(e);
		}
	}

	private ItemPedido gerarItemPedido() {
		List<Representada> listaRepresentada = representadaService.pesquisar();
		Representada representada = null;
		if (listaRepresentada.isEmpty()) {
			representada = eBuilder.buildRepresentada();
			try {
				representadaService.inserir(representada);
			} catch (BusinessException e) {
				printMensagens(e);
			}
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

	private Pedido gerarPedidoClienteProspectado() {
		Pedido pedido = eBuilder.buildPedido();
		Cliente cliente = pedido.getCliente();
		cliente.setProspeccaoFinalizada(true);
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

	public void init() {
		pedidoService = ServiceBuilder.buildService(PedidoService.class);
		clienteService = ServiceBuilder.buildService(ClienteService.class);
		representadaService = ServiceBuilder.buildService(RepresentadaService.class);
		materialService = ServiceBuilder.buildService(MaterialService.class);
	}

	private void initTestEnvioEmailPedidoCancelado() {
		new MockUp<PedidoDAO>() {
			@Mock
			Pedido pesquisarById(Integer idPedido) {
				Pedido pedido = eBuilder.buildPedido();
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
				Pedido pedido = eBuilder.buildPedido();
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
				Pedido pedido = eBuilder.buildPedido();
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
				Pedido pedido = eBuilder.buildPedido();
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
				Pedido pedido = eBuilder.buildPedido();
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
				Pedido pedido = eBuilder.buildPedido();
				pedido.setId(idPedido);
				// Estamos supondo que o cliente ja foi prospectado
				pedido.getCliente().setProspeccaoFinalizada(true);
				pedido.setSituacaoPedido(SituacaoPedido.DIGITACAO);
				pedido.setFormaPagamento("A VISTA");
				pedido.setTipoEntrega(TipoEntrega.FOB);
				pedido.setDataEntrega(TestUtils.gerarDataPosterior());

				pedido.addLogradouro(eBuilder.buildLogradouro(TipoLogradouro.ENTREGA));
				pedido.addLogradouro(eBuilder.buildLogradouro(TipoLogradouro.FATURAMENTO));
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
				Pedido pedido = eBuilder.buildPedido();
				pedido.setId(idPedido);
				// Estamos supondo que o cliente ja foi prospectado
				pedido.getCliente().setProspeccaoFinalizada(true);
				pedido.setSituacaoPedido(SituacaoPedido.DIGITACAO);
				pedido.setFormaPagamento("A VISTA");
				pedido.setTipoEntrega(TipoEntrega.FOB);
				pedido.setDataEntrega(TestUtils.gerarDataPosterior());

				pedido.addLogradouro(eBuilder.buildLogradouro(TipoLogradouro.COBRANCA));
				pedido.addLogradouro(eBuilder.buildLogradouro(TipoLogradouro.FATURAMENTO));
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
				Pedido pedido = eBuilder.buildPedido();
				pedido.setId(idPedido);
				// Estamos supondo que o cliente ja foi prospectado
				pedido.getCliente().setProspeccaoFinalizada(true);
				pedido.setSituacaoPedido(SituacaoPedido.DIGITACAO);
				pedido.setFormaPagamento("A VISTA");
				pedido.setTipoEntrega(TipoEntrega.FOB);
				pedido.setDataEntrega(TestUtils.gerarDataPosterior());

				pedido.addLogradouro(eBuilder.buildLogradouro(TipoLogradouro.COBRANCA));
				pedido.addLogradouro(eBuilder.buildLogradouro(TipoLogradouro.ENTREGA));
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
				Pedido pedido = eBuilder.buildPedido();
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
		Pedido pedido = eBuilder.buildPedido();
		associarVendedor(pedido.getCliente());

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
	public void testInclusaoItemPedidoIPINuloRepresentadaComIPIObrigatorio() {
		Pedido pedido = eBuilder.buildPedido();
		associarVendedor(pedido.getCliente());

		try {
			pedido = pedidoService.inserir(pedido);
		} catch (BusinessException e) {
			printMensagens(e);
		}

		Representada representada = pedido.getRepresentada();
		representada.setTipoApresentacaoIPI(TipoApresentacaoIPI.SEMPRE);
		try {
			representadaService.inserir(representada);
		} catch (BusinessException e2) {
			printMensagens(e2);
		}

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
	public void testInclusaoItemPedidoIPIZeradoRepresentadaComIPIObrigatorio() {
		Pedido pedido = eBuilder.buildPedido();
		associarVendedor(pedido.getCliente());
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
	public void testInclusaoItemPedidoPeca() {
		Pedido pedido = eBuilder.buildPedido();
		associarVendedor(pedido.getCliente());

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
		try {
			pedidoService.inserirItemPedido(idPedido, itemPedido);
		} catch (BusinessException e) {
			printMensagens(e);
		}
	}

	@Test
	public void testInclusaoItemPedidoPecaDescricaoNula() {
		Pedido pedido = eBuilder.buildPedido();
		associarVendedor(pedido.getCliente());

		try {
			pedido = pedidoService.inserir(pedido);
		} catch (BusinessException e) {
			printMensagens(e);
		}

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
		Pedido pedido = eBuilder.buildPedido();
		associarVendedor(pedido.getCliente());

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
		assertTrue("Nao eh possivel vender uma peca por kilo. A venda deve ser feita por peca", throwed);
	}

	@Test
	public void testInclusaoItemPedidoPecaSemDescricao() {
		Pedido pedido = eBuilder.buildPedido();
		associarVendedor(pedido.getCliente());

		try {
			pedido = pedidoService.inserir(pedido);
		} catch (BusinessException e) {
			printMensagens(e);
		}

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
	public void testInclusaoItemPedidoRepresentadaSemIPI() {
		Pedido pedido = eBuilder.buildPedido();
		associarVendedor(pedido.getCliente());
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
	public void testInclusaoPedidoContatoNulo() {
		Pedido pedido = eBuilder.buildPedido();
		associarVendedor(pedido.getCliente());
		pedido.setContato(null);

		boolean throwed = false;
		try {
			pedido = pedidoService.inserir(pedido);
		} catch (BusinessException e) {
			throwed = true;
		}
		assertTrue("O pedido deve conter um contato", throwed);
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
		Pedido pedido = eBuilder.buildPedido();
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

	@Test
	public void testInclusaoPedidoDigitadoSemVendedorAssociado() {
		initTestInclusaoPedidoDigitadoSemVendedorAssociado();
		Pedido pedido = eBuilder.buildPedido();
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
		initTestInclusaoPedidoOrcamento();

		Pedido pedido = eBuilder.buildPedido();
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
	public void testInclusaoPedidoTipoDeEntregaSemRedespacho() {
		Pedido pedido = eBuilder.buildPedido();
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
		Pedido pedido = eBuilder.buildPedido();
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
	public void testRefazerPedidoComIPI() {
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

	@Test
	public void testRefazerPedidoRepresentadaSemIPI() {
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

		pedido = repositorio.pesquisarEntidadeById(Pedido.class, idPedido);
		assertEquals("O pedido " + idPedido + " foi refeito e deve estar na situacao " + SituacaoPedido.CANCELADO,
				SituacaoPedido.CANCELADO, pedido.getSituacaoPedido());

	}
}
