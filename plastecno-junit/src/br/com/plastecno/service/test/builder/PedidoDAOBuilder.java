package br.com.plastecno.service.test.builder;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
import br.com.plastecno.service.entity.Transportadora;

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
			List<Logradouro> pesquisarLogradouro(Integer idPedido) {
				List<Logradouro> lista = new ArrayList<Logradouro>();
				lista.add(ENTIDADE_BUILDER.buildLogradouro(TipoLogradouro.COBRANCA));
				lista.add(ENTIDADE_BUILDER.buildLogradouro(TipoLogradouro.ENTREGA));
				lista.add(ENTIDADE_BUILDER.buildLogradouro(TipoLogradouro.FATURAMENTO));

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
			public Representada pesquisarRepresentadaResumidaByIdPedido(Integer idPedido) {
				Pedido p = REPOSITORY.pesquisarEntidadeById(Pedido.class, idPedido);
				Representada r = new Representada();
				r.setId(p.getRepresentada().getId());
				r.setNomeFantasia(p.getRepresentada().getNomeFantasia());
				return r;
			}

			@Mock
			public SituacaoPedido pesquisarSituacaoPedidoByIdItemPedido(Integer idItemPedido) {
				ItemPedido i = REPOSITORY.pesquisarEntidadeById(ItemPedido.class, idItemPedido);
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
			public Transportadora pesquisarTransportadoraByIdPedido(Integer idPedido) {
				Pedido p = REPOSITORY.pesquisarEntidadeById(Pedido.class, idPedido);
				return p == null ? null : p.getTransportadora();
			}

			@Mock
			public Double[] pesquisarValoresPedido(Integer idPedido) {
				double vTot = 0;
				double vTotIPI = 0;

				List<ItemPedido> lItem = REPOSITORY.pesquisarTodos(ItemPedido.class);
				if (lItem == null || lItem.isEmpty()) {
					return new Double[] {};
				}
				for (ItemPedido i : lItem) {
					if (i.getPedido() == null || i.getPedido().getId() == null
							|| !i.getPedido().getId().equals(idPedido)) {
						continue;
					}
					vTot += i.getQuantidade() * i.getPrecoUnidade();
					vTotIPI += i.getQuantidade() * i.getPrecoUnidadeIPI();
				}

				return new Double[] { vTot, vTotIPI };
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
