package br.com.plastecno.service.test.builder;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import mockit.Mock;
import mockit.MockUp;
import br.com.plastecno.service.dao.ItemReservadoDAO;
import br.com.plastecno.service.entity.ItemReservado;

public class ItemReservadoDAOBuilder extends DAOBuilder<ItemReservadoDAO> {

	@Override
	public ItemReservadoDAO build() {

		new MockUp<ItemReservadoDAO>() {
			@Mock
			public List<ItemReservado> pesquisarItemReservadoByIdItemPedido(Integer idItemPedido) {
				if (idItemPedido == null) {
					return new ArrayList<ItemReservado>();
				}
				List<ItemReservado> l = REPOSITORY.pesquisarTodos(ItemReservado.class);
				return l.stream().filter(i -> idItemPedido.equals(i.getItemPedido().getId()))
						.collect(Collectors.toList());
			}

			@Mock
			public Integer[] pesquisarQuantidadeEstoqueByIdItemPedido(Integer idItemPedido) {
				if (idItemPedido == null) {
					return new Integer[] {};
				}
				List<ItemReservado> l = REPOSITORY.pesquisarTodos(ItemReservado.class);

				for (ItemReservado i : l) {
					if (idItemPedido.equals(i.getItemPedido().getId())) {
						return new Integer[] { i.getItemEstoque().getId(), i.getItemEstoque().getQuantidade() };
					}
				}
				return new Integer[] {};
			}

			@Mock
			public void removerByIdItemPedido(Integer idItemPedido) {
				if (idItemPedido == null) {
					return;
				}
				List<ItemReservado> l = new ArrayList<ItemReservado>();
				l.addAll(REPOSITORY.pesquisarTodos(ItemReservado.class));
				for (ItemReservado i : l) {
					if (idItemPedido.equals(i.getItemPedido().getId())) {
						REPOSITORY.removerEntidade(ItemReservado.class, i.getId());
					}
				}

			}
		};

		return new ItemReservadoDAO(null);
	}

}
