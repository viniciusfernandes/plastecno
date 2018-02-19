package br.com.svr.service.test.builder;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import mockit.Mock;
import mockit.MockUp;
import br.com.svr.service.constante.SituacaoPedido;
import br.com.svr.service.constante.TipoLogradouro;
import br.com.svr.service.dao.ItemPedidoDAO;
import br.com.svr.service.dao.PedidoDAO;
import br.com.svr.service.entity.Cliente;
import br.com.svr.service.entity.ItemPedido;
import br.com.svr.service.entity.LogradouroEndereco;
import br.com.svr.service.entity.LogradouroPedido;
import br.com.svr.service.entity.Pedido;
import br.com.svr.service.entity.Representada;
import br.com.svr.service.entity.Transportadora;

public class PedidoDAOBuilder extends DAOBuilder<PedidoDAO> {
	private static final EntidadeRepository REPOSITORY = EntidadeRepository.getInstance();

	public PedidoDAO build() {

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
				return REPOSITORY.contar(ItemPedido.class,
						i -> !i.isEncomendado() && i.getPedido().getId().equals(idPedido));
			};
		};

		new MockUp<PedidoDAO>() {
			@Mock
			public void alterarIdOrcamentoByIdPedido(Integer idPedido, Integer idOrcamento) {
				Pedido p = REPOSITORY.pesquisarEntidadeById(Pedido.class, idPedido);
				p.setIdOrcamento(idOrcamento);
			}

			@Mock
			public void alterarSituacaoPedidoById(Integer idPedido, SituacaoPedido situacaoPedido) {
				REPOSITORY.alterarEntidadeAtributoById(Pedido.class, idPedido, "situacaoPedido", situacaoPedido);
			}

			@Mock
			public void alterarValorPedido(Integer idPedido, Double valorPedido, Double valorPedidoIPI) {
				Pedido p = REPOSITORY.pesquisarEntidadeById(Pedido.class, idPedido);
				if (p == null) {
					return;
				}
				p.setValorPedido(valorPedido);
				p.setValorPedidoIPI(valorPedidoIPI);
			}

			@Mock
			Pedido inserir(Pedido t) {
				t.setId(ENTIDADE_BUILDER.gerarId());
				REPOSITORY.inserirEntidade(t);
				return t;
			}

			@Mock
			public boolean isPedidoExistente(Integer idPedido) {
				Pedido p = REPOSITORY.pesquisarEntidadeById(Pedido.class, idPedido);
				return p != null;
			}

			@Mock
			Pedido pesquisarById(Integer idPedido, boolean isCompra) {
				Pedido pedido = REPOSITORY.pesquisarEntidadeById(Pedido.class, idPedido);
				return pedido != null && pedido.isCompra() == isCompra ? pedido : null;
			}

			@Mock
			public Cliente pesquisarClienteByIdPedido(Integer idPedido) {
				Pedido p = REPOSITORY.pesquisarEntidadeById(Pedido.class, idPedido);
				return p.getCliente();
			}

			@Mock
			public Cliente pesquisarClienteResumidoByIdPedido(Integer idPedido) {
				Pedido p = this.pesquisarById(idPedido, false);
				return p == null ? null : p.getCliente();
			}

			@Mock
			public Double pesquisarComissaoRepresentadaByIdPedido(Integer idPedido) {
				Representada r = REPOSITORY.pesquisarEntidadeAtributoById(Pedido.class, idPedido, "representada",
						Representada.class);
				return r == null ? 0 : r.getComissao();
			}

			@Mock
			public boolean pesquisarComissaoSimplesVendedor(Integer idPedido) {
				Pedido p = REPOSITORY.pesquisarEntidadeById(Pedido.class, idPedido);
				if (p == null || p.getProprietario() == null) {
					return false;
				}
				return p.getProprietario().isComissionadoSimples();
			}

			@Mock
			public Date pesquisarDataEnvioById(Integer idPedido) {
				Pedido pedido = REPOSITORY.pesquisarEntidadeById(Pedido.class, idPedido);
				return pedido != null ? pedido.getDataEntrega() : null;
			}

			@Mock
			public String pesquisarFormaPagamentoByIdPedido(Integer idPedido) {
				Pedido p = REPOSITORY.pesquisarEntidadeById(Pedido.class, idPedido);
				return p == null ? null : p.getFormaPagamento();
			}

			@Mock
			public Integer pesquisarIdClienteByIdPedido(Integer idPedido) {
				Pedido p = REPOSITORY.pesquisarEntidadeById(Pedido.class, idPedido);
				return p != null && p.getCliente() != null ? p.getCliente().getId() : null;
			}

			@Mock
			public Object[] pesquisarIdNomeClienteNomeContatoValor(Integer idPedido) {
				Pedido p = REPOSITORY.pesquisarEntidadeById(Pedido.class, idPedido);
				if (p == null) {
					return null;
				}
				return new Object[] { p.getCliente().getId(), p.getCliente().getNomeFantasia(),
						p.getContato().getDdd(), p.getContato().getNome(), p.getContato().getTelefone(),
						p.getValorPedido() };
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
				if (situacaoPedido == null) {
					return new ArrayList<Integer>();
				}
				List<Pedido> l = REPOSITORY.pesquisarTodos(Pedido.class);
				return l.stream().filter(p -> situacaoPedido.equals(p.getSituacaoPedido())).map(i -> i.getId())
						.collect(Collectors.toList());
			}

			@Mock
			Integer pesquisarIdRepresentadaByIdPedido(Integer idPedido) {
				Representada r = REPOSITORY.pesquisarEntidadeAtributoById(Pedido.class, idPedido, "representada",
						Representada.class);
				return r == null ? null : r.getId();
			}

			@Mock
			public Integer pesquisarIdVendedorByIdPedido(Integer idPedido) {
				if (idPedido == null) {
					return null;
				}
				Pedido p = REPOSITORY.pesquisarEntidadeById(Pedido.class, idPedido);
				return p == null ? null : p.getVendedor().getId();
			}

			@Mock
			public ItemPedido pesquisarItemPedidoById(Integer idItemPedido) {
				return REPOSITORY.pesquisarEntidadeById(ItemPedido.class, idItemPedido);
			}

			@Mock
			List<ItemPedido> pesquisarItemPedidoByIdPedido(Integer idPedido) {
				List<ItemPedido> l = REPOSITORY.pesquisarTodos(ItemPedido.class);
				return l.stream().filter(i -> i.getPedido() != null && i.getPedido().getId().equals(idPedido))
						.collect(Collectors.toList());
			}

			@Mock
			List<LogradouroEndereco> pesquisarLogradouro(Integer idPedido) {
				List<LogradouroEndereco> lista = new ArrayList<LogradouroEndereco>();
				lista.add(ENTIDADE_BUILDER.buildLogradouroEndereco(TipoLogradouro.COBRANCA));
				lista.add(ENTIDADE_BUILDER.buildLogradouroEndereco(TipoLogradouro.ENTREGA));
				lista.add(ENTIDADE_BUILDER.buildLogradouroEndereco(TipoLogradouro.FATURAMENTO));

				return lista;
			}

			@Mock
			public Integer pesquisarMaxSequenciaItemPedido(Integer idPedido) {
				if (idPedido == null) {
					return null;
				}

				List<ItemPedido> l = REPOSITORY.pesquisarTodos(ItemPedido.class);
				if (l == null || l.isEmpty()) {
					return null;
				}

				Optional<Integer> o = l.stream().filter(i -> idPedido.equals(i.getPedido().getId()))
						.map(i -> i.getSequencial()).max((a, b) -> a == null || b == null ? -1 : Integer.compare(a, b));
				return o.isPresent() ? o.get() : null;
			}

			@Mock
			public Pedido pesquisarPedidoResumidoCalculoComissao(Integer idPedido) {
				// Aqui temos que retornar o pedido inteiro senao podemos
				// quebrar o relacionamento entre os objetos itemPedido e pedido
				// pois o item realiza varios item.set(Pedido)
				Pedido p = REPOSITORY.pesquisarEntidadeById(Pedido.class, idPedido);
				// Aqui estamos efetuando alguns sets que serao usados no
				// calculo da comissao na inclusao dos itens do pedidos e esses
				// atributos foram criados para evitar mais consultas ao banco
				// de dados
				p.setAliquotaComissaoRepresentada(p.getRepresentada().getComissao());
				p.setIdVendedor(p.getVendedor().getId());
				return p;
			}

			@Mock
			public Pedido pesquisarPedidoResumidoFinalidadeByIdItemPedido(Integer idItemPedido) {
				if (idItemPedido == null) {
					return null;
				}
				List<ItemPedido> l = REPOSITORY.pesquisarTodos(ItemPedido.class);
				for (ItemPedido i : l) {
					if (idItemPedido.equals(i.getId())) {
						return i.getPedido();
					}
				}
				return null;
			}

			@Mock
			public Representada pesquisarRepresentadaResumidaByIdPedido(Integer idPedido) {
				Pedido p = REPOSITORY.pesquisarEntidadeById(Pedido.class, idPedido);
				Representada r = new Representada();
				r.setId(p.getRepresentada().getId());
				r.setNomeFantasia(p.getRepresentada().getNomeFantasia());
				return r;
			}

			@Mock
			public SituacaoPedido pesquisarSituacaoPedidoByIdItemPedido(Integer idItemPedido) {
				if (idItemPedido == null) {
					return null;
				}
				ItemPedido i = REPOSITORY.pesquisarEntidadeById(ItemPedido.class, idItemPedido);
				List<Pedido> lP = REPOSITORY.pesquisarTodos(Pedido.class);
				List<ItemPedido> lIt = REPOSITORY.pesquisarTodos(ItemPedido.class);
				for (Pedido p : lP) {
					System.out.println("Pedido no: " + p.getId() + " tipo ped: " + p.getTipoPedido() + " situacao: "
							+ p.getSituacaoPedido());
					for (ItemPedido it : lIt) {
						if (it.getPedido().getId().equals(p.getId())) {
							System.out.println("\t Item: " + it.getId() + " no: " + it.getSequencial() + " qtde: "
									+ it.getQuantidade());
						}
					}
				}
				return i == null ? null : i.getPedido().getSituacaoPedido();
			}

			@Mock
			public Object[] pesquisarTelefoneContatoByIdPedido(Integer idPedido) {

				Pedido p = pesquisarById(idPedido, false);
				return p == null || p.getContato() == null ? new Object[] {} : new Object[] { p.getContato().getDdd(),
						p.getContato().getTelefone() };
			}

			@Mock
			Long pesquisarTotalItemPedido(Integer idPedido) {
				return pesquisarTotalItemPedido(idPedido, false);
			}

			@Mock
			Long pesquisarTotalItemPedido(Integer idPedido, boolean apenasNaoRecebido) {
				return REPOSITORY.contar(ItemPedido.class, i -> !apenasNaoRecebido || !i.isItemRecebido());
			}

			@Mock
			public long pesquisarTotalItemPedidoByIdItem(Integer idItem) {
				if (idItem == null) {
					return 0L;
				}
				List<ItemPedido> l = REPOSITORY.pesquisarTodos(ItemPedido.class);
				int count = 0;
				Integer idPed = null;
				for (ItemPedido i : l) {
					if (i.getPedido() != null && idItem.equals(i.getId())) {
						idPed = i.getPedido().getId();
						break;
					}
				}
				if (idPed == null) {
					return 0l;
				}
				for (ItemPedido i : l) {
					if (i.getPedido() != null && idPed.equals(i.getPedido().getId())) {
						count++;
					}
				}
				return count;
			}

			@Mock
			public Transportadora pesquisarTransportadoraByIdPedido(Integer idPedido) {
				Pedido p = REPOSITORY.pesquisarEntidadeById(Pedido.class, idPedido);
				return p == null ? null : p.getTransportadora();
			}

			@Mock
			public double[] pesquisarValoresPedido(Integer idPedido) {
				if (idPedido == null) {
					return new double[] {};
				}

				List<ItemPedido> lItem = REPOSITORY.pesquisarTodos(ItemPedido.class);
				if (lItem == null || lItem.isEmpty()) {
					return new double[] {};
				}
				double valTotIPI = 0d;
				double valTot = 0d;
				for (ItemPedido i : lItem) {
					if (idPedido.equals(i.getPedido().getId())) {
						valTot += i.calcularPrecoItem();
						valTotIPI += i.calcularPrecoTotalIPI();
					}
				}

				return new double[] { valTot, valTotIPI };
			}

			@Mock
			public double pesquisarValorFreteByIdItem(Integer idItem) {
				ItemPedido i = REPOSITORY.pesquisarEntidadeById(ItemPedido.class, idItem);
				return i != null && i.getPedido() != null && i.getPedido().getValorFrete() != null ? i.getPedido()
						.getValorFrete() : 0d;
			}

			@Mock
			public Double pesquisarValorFreteByIdPedido(Integer idPedido) {
				Pedido p = REPOSITORY.pesquisarEntidadeById(Pedido.class, idPedido);
				if (p == null) {
					return 0d;
				}
				return p.getValorFrete() == null ? 0d : p.getValorFrete();
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

			@Mock
			public void removerLogradouroPedido(Integer idPedido) {
				Pedido p = REPOSITORY.pesquisarEntidadeById(Pedido.class, idPedido);
				if (p == null) {
					return;
				}
				List<LogradouroPedido> lLog = p.getListaLogradouro();
				if (lLog == null) {
					return;
				}
				for (LogradouroPedido l : lLog) {
					// Removendo do banco
					REPOSITORY.removerEntidade(LogradouroPedido.class, l.getId());
				}
				// Simulando a remocado do logradouro do pedidod
				p.setListaLogradouro(null);
			}

		};

		return new PedidoDAO(null);

	}
}
