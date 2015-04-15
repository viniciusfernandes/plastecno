package br.com.plastecno.service.test;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mockit.Mock;
import mockit.MockUp;
import br.com.plastecno.service.AutenticacaoService;
import br.com.plastecno.service.ClienteService;
import br.com.plastecno.service.ComissaoService;
import br.com.plastecno.service.EmailService;
import br.com.plastecno.service.EnderecamentoService;
import br.com.plastecno.service.EstoqueService;
import br.com.plastecno.service.LogradouroService;
import br.com.plastecno.service.MaterialService;
import br.com.plastecno.service.PedidoService;
import br.com.plastecno.service.PerfilAcessoService;
import br.com.plastecno.service.RepresentadaService;
import br.com.plastecno.service.UsuarioService;
import br.com.plastecno.service.constante.FormaMaterial;
import br.com.plastecno.service.constante.SituacaoPedido;
import br.com.plastecno.service.constante.TipoApresentacaoIPI;
import br.com.plastecno.service.constante.TipoCliente;
import br.com.plastecno.service.constante.TipoLogradouro;
import br.com.plastecno.service.constante.TipoRelacionamento;
import br.com.plastecno.service.dao.ClienteDAO;
import br.com.plastecno.service.dao.ComissaoDAO;
import br.com.plastecno.service.dao.EnderecoDAO;
import br.com.plastecno.service.dao.ItemEstoqueDAO;
import br.com.plastecno.service.dao.ItemPedidoDAO;
import br.com.plastecno.service.dao.ItemReservadoDAO;
import br.com.plastecno.service.dao.MaterialDAO;
import br.com.plastecno.service.dao.PedidoDAO;
import br.com.plastecno.service.dao.PerfilAcessoDAO;
import br.com.plastecno.service.dao.RepresentadaDAO;
import br.com.plastecno.service.dao.UsuarioDAO;
import br.com.plastecno.service.entity.Cliente;
import br.com.plastecno.service.entity.Comissao;
import br.com.plastecno.service.entity.ItemEstoque;
import br.com.plastecno.service.entity.ItemPedido;
import br.com.plastecno.service.entity.Logradouro;
import br.com.plastecno.service.entity.LogradouroCliente;
import br.com.plastecno.service.entity.Material;
import br.com.plastecno.service.entity.Pedido;
import br.com.plastecno.service.entity.PerfilAcesso;
import br.com.plastecno.service.entity.Representada;
import br.com.plastecno.service.entity.Usuario;
import br.com.plastecno.service.exception.NotificacaoException;
import br.com.plastecno.service.impl.EmailServiceImpl;
import br.com.plastecno.service.mensagem.email.MensagemEmail;

class ServiceBuilder {
	@SuppressWarnings("unchecked")
	static <T> T buildService(Class<T> classe) {
		T service = (T) mapTemporarioServices.get(classe);
		if (service != null) {
			return service;
		}
		String serviceNameImpl = classe.getName().replace("service", "service.impl") + "Impl";
		try {
			/*
			 * ESSE ATRIBUTO FOI CRIADO PARA CONTORNAR O PROBLEMA DE REFERENCIAS
			 * CICLICAS ENTRE OS SERVICOS, POR EXEMPLO, PEDIDOSERVICE E ESTOQUE
			 * SERVICE. QUANDO VAMOS EFETUAR O BUILD DO PEDIDOSERVICE, TEMOS QUE
			 * EFETUAR O BUILD DO ESTOQUESERVICE, ENTAO, PARA CONTORNAR UM DEADLOCK
			 * ENTRE OS BUILDS, JOGAMOS O OBJETO PEDIDOSERVICEIMPL EM MEMORIA, E ASSIM
			 * QUE O ESTOQUESERVICE FOR EFFETUAR O BUILD DO PEDIDOSSERVICE,
			 * VERIFICAMOS QUE ELE JA ESTA EM MEMORIA E RETORNAMOS ESSE OBJETO. SENDO
			 * QUE MANTEMOS TEMPORARIAMENTE ESSES OBJETOS EM MEMORIA POIS O MECANISMO
			 * DO MOCKIT DEVE SER EXECUTADO PARA CADA TESTE UNITARIO, POIS ESSE EH O
			 * CICLO DE VIDA DAS IMPLEMENTACOES MOCKADAS DOS METODOS. ELAS VALEM
			 * APENAS EM CADA TESTE UNITARIO.
			 */
			mapTemporarioServices.put(classe, Class.forName(serviceNameImpl).newInstance());
		} catch (Exception e1) {
			throw new IllegalStateException("Nao foi possivel instanciar a implementacao do servico \"" + serviceNameImpl
					+ "\"");
		}

		String metodoName = "build" + classe.getSimpleName();
		Method method = null;
		try {
			method = ServiceBuilder.class.getDeclaredMethod(metodoName);
			try {
				method.setAccessible(true);
				service = (T) method.invoke(SERVICE_BUILDER, (Object[]) null);
				/*
				 * REMOVENDO OS OBJETOS EM MEMORIA POIS O MECANISMO DO MOCKIT DEVE SER
				 * EXECUTADO PARA CADA TESTE UNITARIO, POIS ESSE EH O CICLO DE VIDA DAS
				 * IMPLEMENTACOES MOCKADAS DOS METODOS. ELAS VALEM APENAS EM CADA TESTE
				 * UNITARIO.
				 */
				mapTemporarioServices.remove(classe);
				return service;
			} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
				throw new TestUtilException("Falha na execucao do metodo de inicializacao do servico \"" + "build"
						+ classe.getSimpleName() + "\"", e);

			}
		} catch (SecurityException | NoSuchMethodException e) {
			throw new TestUtilException("Falha na localizacao do metodo de inicializacao do servico \"" + "build"
					+ classe.getSimpleName() + "\"", e);
		} finally {
			if (method != null) {
				method.setAccessible(false);
			}
		}
	}

	private static final EntidadeBuilder ENTIDADE_BUILDER = EntidadeBuilder.getInstance();

	/*
	 * ESSE ATRIBUTO FOI CRIADO PARA CONTORNAR O PROBLEMA DE REFERENCIAS CICLICAS
	 * ENTRE OS SERVICOS, POR EXEMPLO, PEDIDOSERVICE E ESTOQUE SERVICE. QUANDO
	 * VAMOS EFETUAR O BUILD DO PEDIDOSERVICE, TEMOS QUE EFETUAR O BUILD DO
	 * ESTOQUESERVICE, ENTAO, PARA CONTORNAR UM DEADLOCK ENTRE OS BUILDS, JOGAMOS
	 * O OBJETO PEDIDOSERVICEIMPL EM MEMORIA, E ASSIM QUE O ESTOQUESERVICE FOR
	 * EFFETUAR O BUILD DO PEDIDOSSERVICE, VERIFICAMOS QUE ELE JA ESTA EM MEMORIA
	 * E RETORNAMOS ESSE OBJETO. SENDO QUE MANTEMOS TEMPORARIAMENTE ESSES OBJETOS
	 * EM MEMORIA POIS O MECANISMO DO MOCKIT DEVE SER EXECUTADO PARA CADA TESTE
	 * UNITARIO, POIS ESSE EH O CICLO DE VIDA DAS IMPLEMENTACOES MOCKADAS DOS
	 * METODOS. ELAS VALEM APENAS EM CADA TESTE UNITARIO.
	 */
	private final static Map<Class<?>, Object> mapTemporarioServices = new HashMap<Class<?>, Object>();
	private static final EntidadeRepository REPOSITORY = EntidadeRepository.getInstance();

	private static final ServiceBuilder SERVICE_BUILDER = new ServiceBuilder();

	ServiceBuilder() {
	}

	@SuppressWarnings("unused")
	private AutenticacaoService buildAutenticacaoService() {
		AutenticacaoService autenticacaoService = getServiceImpl(AutenticacaoService.class);
		inject(autenticacaoService, new UsuarioDAO(null), "usuarioDAO");
		return autenticacaoService;
	}

	@SuppressWarnings("unused")
	private ClienteService buildClienteService() {
		ClienteService clienteService = getServiceImpl(ClienteService.class);

		new MockUp<ClienteDAO>() {
			@Mock
			public boolean isEmailExistente(Integer idCliente, String email) {
				return REPOSITORY.contemEntidade(Cliente.class, "email", email, idCliente);
			}

			@Mock
			public List<LogradouroCliente> pesquisarLogradouroById(Integer idCliente) {
				Cliente cliente = REPOSITORY.pesquisarEntidadeById(Cliente.class, idCliente);
				return cliente == null ? new ArrayList<LogradouroCliente>() : cliente.getListaLogradouro();
			};

			@Mock
			Cliente pesquisarRevendedor() {
				List<Cliente> l = REPOSITORY.pesquisarEntidadeByRelacionamento(Cliente.class, "tipoCliente",
						TipoCliente.REVENDEDOR);
				return !l.isEmpty() ? l.get(0) : null;
			}
		};
		ClienteDAO d = new ClienteDAO(null);
		d.isEmailExistente(1, "");

		inject(clienteService, new ClienteDAO(null), "clienteDAO");
		inject(clienteService, buildService(LogradouroService.class), "logradouroService");
		inject(clienteService, buildService(EnderecamentoService.class), "enderecamentoService");
		clienteService.isEmailExistente(1, "");
		return clienteService;
	}

	@SuppressWarnings("unused")
	private ComissaoService buildComissaoService() {
		ComissaoService comissaoService = getServiceImpl(ComissaoService.class);
		inject(comissaoService, new ComissaoDAO(null), "comissaoDAO");
		inject(comissaoService, buildService(UsuarioService.class), "usuarioService");
		inject(comissaoService, buildService(MaterialService.class), "materialService");

		new MockUp<ComissaoDAO>() {
			@Mock
			public Comissao pesquisarComissaoVigenteProduto(Integer idMaterial, Integer idFormaMaterial) {
				List<Comissao> lista = REPOSITORY.pesquisarTodos(Comissao.class);
				boolean ok = false;
				for (Comissao comissao : lista) {
					ok = false;
					if (comissao.getDataFim() != null) {
						continue;
					}

					if (idFormaMaterial != null) {
						ok = idFormaMaterial.equals(comissao.getIdFormaMaterial());
					}

					if (idMaterial != null) {
						ok |= idMaterial.equals(comissao.getIdMaterial());
					}

					if (ok) {
						return comissao;
					}
				}
				return null;
			}

			@Mock
			public Comissao pesquisarComissaoVigenteVendedor(Integer idVendedor) {
				List<Comissao> lista = REPOSITORY.pesquisarTodos(Comissao.class);
				for (Comissao comissao : lista) {
					if (comissao.getDataFim() != null) {
						continue;
					}

					if (idVendedor.equals(comissao.getIdVendedor())) {
						return comissao;
					}
				}
				return null;
			}
		};
		return comissaoService;
	}

	@SuppressWarnings("unused")
	private EmailService buildEmailService() {
		EmailService emailService = getServiceImpl(EmailService.class);
		new MockUp<EmailServiceImpl>() {
			@Mock
			void enviar(MensagemEmail mensagemEmail) throws NotificacaoException {
			}
		};

		return emailService;
	}

	@SuppressWarnings("unused")
	private EnderecamentoService buildEnderecamentoService() {
		EnderecamentoService enderecamentoService = getServiceImpl(EnderecamentoService.class);
		inject(enderecamentoService, new EnderecoDAO(null), "enderecoDAO");

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
		return enderecamentoService;
	}

	@SuppressWarnings("unused")
	private EstoqueService buildEstoqueService() {
		EstoqueService estoqueService = getServiceImpl(EstoqueService.class);
		inject(estoqueService, new ItemEstoqueDAO(null), "itemEstoqueDAO");
		inject(estoqueService, new ItemReservadoDAO(null), "itemReservadoDAO");
		inject(estoqueService, buildService(PedidoService.class), "pedidoService");

		new MockUp<ItemEstoqueDAO>() {

			@Mock
			public List<ItemEstoque> pesquisarItemEstoque(Integer idMaterial, FormaMaterial formaMaterial,
					String descricaoPeca) {
				List<ItemEstoque> lista = REPOSITORY.pesquisarEntidadeByRelacionamento(ItemEstoque.class, "formaMaterial",
						formaMaterial);
				List<ItemEstoque> itens = new ArrayList<ItemEstoque>();
				boolean isMaterialSelecionado = false;
				boolean isPecaSelecionada = false;
				for (ItemEstoque item : lista) {
					// A primeira condicao indica que se deseja todas as formas de
					// materiais.
					isMaterialSelecionado = idMaterial == null
							|| (item.getMaterial() != null && idMaterial.equals(item.getMaterial().getId()));
					if (!item.isPeca() && isMaterialSelecionado) {
						itens.add(item);
						continue;
					}

					isPecaSelecionada = item.isPeca() && descricaoPeca != null && descricaoPeca.equals(item.getDescricaoPeca());
					if (isMaterialSelecionado && isPecaSelecionada) {
						itens.add(item);
						continue;
					}
				}
				return itens;
			}

			@Mock
			Double pesquisarValorEQuantidadeItemEstoque(Integer idMaterial, FormaMaterial formaMaterial) {
				List<ItemEstoque> l = REPOSITORY.pesquisarTodos(ItemEstoque.class);
				List<Double[]> listaValores = new ArrayList<Double[]>();
				boolean isAmbosNulos = false;
				boolean isMaterialIgual = false;
				boolean isFormaIgual = false;
				for (ItemEstoque i : l) {
					isAmbosNulos = idMaterial == null && formaMaterial == null;
					isMaterialIgual = idMaterial != null && idMaterial.equals(i.getMaterial().getId());
					isFormaIgual = formaMaterial != null && formaMaterial.equals(i.getFormaMaterial());
					if (isAmbosNulos || isMaterialIgual || isFormaIgual) {
						listaValores.add(new Double[] { i.getPrecoUnidade(), (double) i.getQuantidade() });
					}
				}

				double total = 0d;
				double val = 0;
				double quant = 0;
				for (Double[] valor : listaValores) {
					val = (Double) valor[0];
					quant = (Double) valor[1];
					total += val * quant;
				}
				return total;
			}

		};

		return estoqueService;
	}

	@SuppressWarnings("unused")
	private LogradouroService buildLogradouroService() {
		LogradouroService logradouroService = getServiceImpl(LogradouroService.class);
		inject(logradouroService, buildService(EnderecamentoService.class), "enderecamentoService");
		return logradouroService;
	}

	@SuppressWarnings("unused")
	private MaterialService buildMaterialService() {
		MaterialService materialService = getServiceImpl(MaterialService.class);
		inject(materialService, new MaterialDAO(null), "materialDAO");
		inject(materialService, buildService(RepresentadaService.class), "representadaService");

		new MockUp<MaterialDAO>() {
			@Mock
			boolean isMaterialAssociadoRepresentada(Integer idMaterial, Integer idRepresentada) {
				Material m = pesquisarById(idMaterial);
				if (m == null) {
					return false;
				}
				for (Representada r : m.getListaRepresentada()) {
					if (idRepresentada != null && idRepresentada.equals(r.getId())) {
						return true;
					}
				}
				return false;
			}

			@Mock
			boolean isMaterialImportado(Integer idMaterial) {
				Material material = REPOSITORY.pesquisarEntidadeById(Material.class, idMaterial);
				return material != null ? material.isImportado() : false;
			}

			@Mock
			Material pesquisarById(Integer id) {
				return REPOSITORY.pesquisarEntidadeById(Material.class, id);
			}

			@Mock
			public List<Material> pesquisarBySigla(String sigla) {
				List<Material> l = REPOSITORY.pesquisarTodos(Material.class);
				List<Material> lista = new ArrayList<Material>();
				for (Material material : l) {
					if (material.getSigla().contains(sigla)) {
						lista.add(material);
					}
				}
				return lista;
			}
		};

		return materialService;
	}

	@SuppressWarnings("unused")
	private PedidoService buildPedidoService() {
		PedidoService pedidoService = getServiceImpl(PedidoService.class);
		inject(pedidoService, new PedidoDAO(null), "pedidoDAO");
		inject(pedidoService, new ItemPedidoDAO(null), "itemPedidoDAO");
		inject(pedidoService, buildService(UsuarioService.class), "usuarioService");
		inject(pedidoService, buildService(ClienteService.class), "clienteService");
		inject(pedidoService, buildService(LogradouroService.class), "logradouroService");
		inject(pedidoService, buildService(EmailService.class), "emailService");
		inject(pedidoService, buildService(MaterialService.class), "materialService");
		inject(pedidoService, buildService(RepresentadaService.class), "representadaService");
		inject(pedidoService, buildService(EstoqueService.class), "estoqueService");
		inject(pedidoService, buildService(ComissaoService.class), "comissaoService");

		new MockUp<ItemPedidoDAO>() {

			@Mock
			public void alterarQuantidadeRecepcionada(Integer idItemPedido, Integer quantidadeRecepcionada) {
				REPOSITORY.alterarEntidadeAtributoById(ItemPedido.class, idItemPedido, "quantidadeRecepcionada",
						quantidadeRecepcionada);
			}

			@Mock
			public Integer pesquisarIdMeterialByIdItemPedido(Integer idItemPedido) {
				ItemPedido i = REPOSITORY.pesquisarEntidadeById(ItemPedido.class, idItemPedido);
				if (i == null) {
					return null;
				}
				return i.getMaterial().getId();
			}

			@Mock
			public Long pesquisarTotalItemRevendaNaoEncomendado(Integer idPedido) {
				List<ItemPedido> l = REPOSITORY.pesquisarTodos(ItemPedido.class);
				long count = 0;
				for (ItemPedido itemPedido : l) {
					if (!itemPedido.isEncomendado() && itemPedido.getPedido().getId().equals(idPedido)) {
						count++;
					}
				}
				return count;
			};
		};

		new MockUp<PedidoDAO>() {
			@Mock
			void alterarSituacaoPedidoById(Integer idPedido, SituacaoPedido situacaoPedido) {
				REPOSITORY.alterarEntidadeAtributoById(Pedido.class, idPedido, "situacaoPedido", situacaoPedido);
			}

			@Mock
			void cancelar(Integer IdPedido) {
				Pedido pedido = REPOSITORY.pesquisarEntidadeById(Pedido.class, IdPedido);
				if (pedido != null) {
					pedido.setSituacaoPedido(SituacaoPedido.CANCELADO);
				}
			}

			@Mock
			Pedido inserir(Pedido t) {
				t.setId(ENTIDADE_BUILDER.gerarId());
				REPOSITORY.inserirEntidade(t);
				return t;
			}

			@Mock
			Pedido pesquisarById(Integer idPedido, boolean isCompra) {
				Pedido pedido = REPOSITORY.pesquisarEntidadeById(Pedido.class, idPedido);
				return pedido != null && pedido.isCompra() == isCompra ? pedido : null;
			}

			@Mock
			public Double pesquisarComissaoRepresentadaByIdPedido(Integer idPedido) {
				Representada r = REPOSITORY.pesquisarEntidadeAtributoById(Pedido.class, idPedido, "representada",
						Representada.class);
				return r == null ? 0 : r.getComissao();
			}

			@Mock
			Date pesquisarDataEnvioById(Integer idPedido) {
				Pedido pedido = REPOSITORY.pesquisarEntidadeById(Pedido.class, idPedido);
				return pedido != null ? pedido.getDataEntrega() : null;
			}

			@Mock
			Date pesquisarDataInclusaoById(Integer idPedido) {
				Pedido pedido = REPOSITORY.pesquisarEntidadeById(Pedido.class, idPedido);
				return pedido != null ? pedido.getDataInclusao() : null;
			}

			@Mock
			public Integer pesquisarIdPedidoByIdItemPedido(Integer idItemPedido) {
				ItemPedido i = REPOSITORY.pesquisarEntidadeById(ItemPedido.class, idItemPedido);
				if (i == null) {
					return null;
				}
				Pedido p = i.getPedido();
				return p != null ? p.getId() : null;
			}

			@Mock
			List<Integer> pesquisarIdPedidoBySituacaoPedido(SituacaoPedido situacaoPedido) {
				List<Pedido> l = REPOSITORY.pesquisarEntidadeByRelacionamento(Pedido.class, "situacaoPedido", situacaoPedido);
				List<Integer> id = new ArrayList<Integer>();
				for (Pedido pedido : l) {
					id.add(pedido.getId());
				}
				return id;
			}

			@Mock
			Integer pesquisarIdRepresentadaByIdPedido(Integer idPedido) {
				Representada r = REPOSITORY.pesquisarEntidadeAtributoById(Pedido.class, idPedido, "representada",
						Representada.class);
				return r == null ? null : r.getId();
			}

			@Mock
			public ItemPedido pesquisarItemPedido(Integer idItemPedido) {
				return REPOSITORY.pesquisarEntidadeById(ItemPedido.class, idItemPedido);
			}

			@Mock
			List<ItemPedido> pesquisarItemPedidoByIdPedido(Integer idPedido) {
				// Pedido pedido = this.pesquisarById(idPedido, false);
				// return REPOSITORY.pesquisarEntidadeByRelacionamento(ItemPedido.class,
				// "pedido", pedido);
				List<ItemPedido> lista = REPOSITORY.pesquisarTodos(ItemPedido.class);
				List<ItemPedido> itens = new ArrayList<ItemPedido>();
				for (ItemPedido itemPedido : lista) {
					if (itemPedido.getPedido() != null && itemPedido.getPedido().getId().equals(idPedido)) {
						itens.add(itemPedido);
					}
				}
				return itens;
			}

			@Mock
			List<Logradouro> pesquisarLogradouro(Integer idPedido) {
				List<Logradouro> lista = new ArrayList<Logradouro>();
				lista.add(ENTIDADE_BUILDER.buildLogradouro(TipoLogradouro.COBRANCA));
				lista.add(ENTIDADE_BUILDER.buildLogradouro(TipoLogradouro.ENTREGA));
				lista.add(ENTIDADE_BUILDER.buildLogradouro(TipoLogradouro.FATURAMENTO));

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
			public SituacaoPedido pesquisarSituacaoPedidoByIdItemPedido(Integer idItemPedido) {
				ItemPedido i = pesquisarItemPedido(idItemPedido);
				return i == null ? null : i.getPedido().getSituacaoPedido();
			}

			@Mock
			Long pesquisarTotalItemPedido(Integer idPedido) {
				return pesquisarTotalItemPedido(idPedido, false);
			}

			@Mock
			Long pesquisarTotalItemPedido(Integer idPedido, Boolean recebido) {
				List<ItemPedido> lista = REPOSITORY.pesquisarTodos(ItemPedido.class);
				long count = 0;
				for (ItemPedido itemPedido : lista) {
					if (recebido == null && itemPedido.getPedido() != null && itemPedido.getPedido().getId().equals(idPedido)) {
						count++;
					} else if (recebido != null && recebido == itemPedido.isRecebido() && itemPedido.getPedido() != null
							&& itemPedido.getPedido().getId().equals(idPedido)) {
						count++;
					}
				}
				return count;
			}

			@Mock
			Double pesquisarValorPedido(Integer idPedido) {
				Pedido pedido = REPOSITORY.pesquisarEntidadeById(Pedido.class, idPedido);
				return pedido != null ? pedido.getValorPedido() : null;
			}

			@Mock
			Double pesquisarValorPedidoIPI(Integer idPedido) {
				Pedido pedido = REPOSITORY.pesquisarEntidadeById(Pedido.class, idPedido);
				return pedido != null ? pedido.getValorPedidoIPI() : null;
			}

		};

		return pedidoService;
	}

	@SuppressWarnings("unused")
	private PerfilAcessoService buildPerfilAcessoService() {
		PerfilAcessoService perfilAcessoService = getServiceImpl(PerfilAcessoService.class);
		inject(perfilAcessoService, new PerfilAcessoDAO(null), "perfilAcessoDAO");
		new MockUp<PerfilAcessoDAO>() {
			@Mock
			public List<PerfilAcesso> pesquisarTodos() {
				return REPOSITORY.pesquisarTodos(PerfilAcesso.class);
			}
		};
		return perfilAcessoService;
	}

	@SuppressWarnings("unused")
	private RepresentadaService buildRepresentadaService() {
		RepresentadaService representadaService = getServiceImpl(RepresentadaService.class);
		inject(representadaService, new RepresentadaDAO(null), "representadaDAO");
		inject(representadaService, buildService(LogradouroService.class), "logradouroService");

		new MockUp<RepresentadaDAO>() {
			@Mock
			Representada pesquisarById(Integer id) {
				return REPOSITORY.pesquisarEntidadeById(Representada.class, id);
			}

			@Mock
			String pesquisarNomeFantasiaById(Integer idRepresentada) {
				Representada r = REPOSITORY.pesquisarEntidadeById(Representada.class, idRepresentada);
				return r != null ? r.getNomeFantasia() : null;
			}

			@Mock
			public List<Representada> pesquisarRepresentadaByTipoRelacionamento(boolean ativo, TipoRelacionamento... tipos) {
				List<Representada> l = REPOSITORY.pesquisarTodos(Representada.class);

				if (tipos == null || tipos.length <= 0) {
					return l;
				}

				List<Representada> lista = new ArrayList<Representada>();
				for (Representada representada : l) {
					for (int i = 0; i < tipos.length; i++) {
						if (tipos[i].equals(representada.getTipoRelacionamento())) {
							lista.add(representada);
							break;
						}
					}
				}
				return lista;
			}

			@Mock
			List<Representada> pesquisarRepresentadaExcluindoRelacionamento(Boolean ativo,
					TipoRelacionamento tipoRelacionamento) {
				List<Representada> lista = REPOSITORY.pesquisarEntidadeByRelacionamento(Representada.class, "ativo", true);
				List<Representada> representadas = new ArrayList<Representada>();
				for (Representada representada : lista) {
					if (tipoRelacionamento != null && !tipoRelacionamento.equals(representada.getTipoRelacionamento())) {
						representadas.add(representada);
					}
				}
				return representadas;
			}

			@Mock
			TipoApresentacaoIPI pesquisarTipoApresentacaoIPI(Integer idRepresentada) {
				Representada representada = REPOSITORY.pesquisarEntidadeById(Representada.class, idRepresentada);
				return representada == null ? null : representada.getTipoApresentacaoIPI();
			}

			@Mock
			TipoRelacionamento pesquisarTipoRelacionamento(Integer idRepresentada) {
				return REPOSITORY.pesquisarEntidadeAtributoById(Representada.class, idRepresentada, "tipoRelacionamento",
						TipoRelacionamento.class);
			}
		};

		return representadaService;
	}

	@SuppressWarnings("unused")
	private UsuarioService buildUsuarioService() {
		UsuarioService usuarioService = getServiceImpl(UsuarioService.class);
		inject(usuarioService, new UsuarioDAO(null), "usuarioDAO");
		inject(usuarioService, buildService(LogradouroService.class), "logradouroService");
		inject(usuarioService, buildService(AutenticacaoService.class), "autenticacaoService");

		new MockUp<UsuarioDAO>() {
			@Mock
			public Usuario pesquisarByEmailSenha(String email, String senha) {
				List<Usuario> lista = REPOSITORY.pesquisarEntidadeByRelacionamento(Usuario.class, "email", email);
				for (Usuario u : lista) {
					if (u != null && u.getEmail() != null && u.getSenha().equals(senha)) {
						return u;
					}
				}
				return null;
			}

			@Mock
			public Integer pesquisarIdVendedorByIdCliente(Integer idCliente, Integer idVendedor) {
				return idVendedor;
			}

			@Mock
			public List<PerfilAcesso> pesquisarPerfisAssociados(Integer id) {
				return new ArrayList<PerfilAcesso>();
			}

			@Mock
			public String pesquisarSenha(Integer idUsuario) {
				return REPOSITORY.pesquisarEntidadeAtributoById(Usuario.class, idUsuario, "senha", String.class);
			}

			@Mock
			Usuario pesquisarUsuarioResumidoById(Integer idUsuario) {
				return REPOSITORY.pesquisarEntidadeById(Usuario.class, idUsuario);
			}

			@Mock
			public boolean pesquisarVendedorAtivo(Integer idVendedor) {
				List<Usuario> listaUsuario = REPOSITORY.pesquisarTodos(Usuario.class);
				for (Usuario usuario : listaUsuario) {
					if (usuario.isVendedor()) {
						return usuario.isAtivo();
					}
				}
				return false;
			}

			@Mock
			public Usuario pesquisarVendedorByIdCliente(Integer idCliente) {
				Cliente c = REPOSITORY.pesquisarEntidadeById(Cliente.class, idCliente);
				return c != null ? c.getVendedor() : null;
			}

		};

		return usuarioService;
	}

	@SuppressWarnings("unchecked")
	private <T> T getServiceImpl(Class<T> classe) {
		return (T) mapTemporarioServices.get(classe);
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
