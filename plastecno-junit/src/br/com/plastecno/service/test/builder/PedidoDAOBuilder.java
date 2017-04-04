package br.com.plastecno.service.test.builder;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import mockit.Mock;
import mockit.MockUp;
import br.com.plastecno.service.constante.SituacaoPedido;
import br.com.plastecno.service.constante.TipoLogradouro;
import br.com.plastecno.service.dao.ItemPedidoDAO;
import br.com.plastecno.service.dao.PedidoDAO;
import br.com.plastecno.service.entity.Cliente;
import br.com.plastecno.service.entity.ItemPedido;
import br.com.plastecno.service.entity.Logradouro;
import br.com.plastecno.service.entity.Pedido;
import br.com.plastecno.service.entity.Representada;

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
			public Date pesquisarDataEnvioById(Integer idPedido) {
				Pedido pedido = REPOSITORY.pesquisarEntidadeById(Pedido.class, idPedido);
				return pedido != null ? pedido.getDataEntrega() : null;
			}

			@Mock
			public String pesquisarFormaPagamentoByIdPedido(Integer idPedido) {
				Pedido p = pesquisarById(idPedido, false);
				return p == null ? null : p.getFormaPagamento();
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
				List<Pedido> l = REPOSITORY.pesquisarEntidadeByRelacionamento(Pedido.class, "situacaoPedido",
						situacaoPedido);
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
			public ItemPedido pesquisarItemPedidoById(Integer idItemPedido) {
				return REPOSITORY.pesquisarEntidadeById(ItemPedido.class, idItemPedido);
			}

			@Mock
			List<ItemPedido> pesquisarItemPedidoByIdPedido(Integer idPedido) {
				// Pedido pedido = this.pesquisarById(idPedido, false);
				// return
				// REPOSITORY.pesquisarEntidadeByRelacionamento(ItemPedido.class,
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
			public Representada pesquisarRepresentadaResumidaByIdPedido(Integer idPedido) {
				Pedido p = REPOSITORY.pesquisarEntidadeById(Pedido.class, idPedido);
				Representada r = new Representada();
				r.setId(p.getRepresentada().getId());
				r.setNomeFantasia(p.getRepresentada().getNomeFantasia());
				return r;
			}

			@Mock
			public SituacaoPedido pesquisarSituacaoPedidoByIdItemPedido(Integer idItemPedido) {
				ItemPedido i = pesquisarItemPedidoById(idItemPedido);
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
				List<ItemPedido> lista = REPOSITORY.pesquisarTodos(ItemPedido.class);
				long count = 0;

				for (ItemPedido itemPedido : lista) {
					if (itemPedido.getPedido() == null || !itemPedido.getPedido().getId().equals(idPedido)) {
						continue;
					}

					if (!apenasNaoRecebido || !itemPedido.isItemRecebido()) {
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

		return new PedidoDAO(null);

	}
}
