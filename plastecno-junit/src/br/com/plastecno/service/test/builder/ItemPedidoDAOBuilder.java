package br.com.plastecno.service.test.builder;

import mockit.Mock;
import mockit.MockUp;
import br.com.plastecno.service.dao.ItemPedidoDAO;
import br.com.plastecno.service.entity.ItemPedido;

public class ItemPedidoDAOBuilder extends DAOBuilder<ItemPedidoDAO> {

	@Override
	public ItemPedidoDAO build() {
		new MockUp<ItemPedidoDAO>() {


			@Mock
			public double pesquisarAliquotaIPIByIdItemPedido(Integer idItemPedido) {
				ItemPedido itemPedido = REPOSITORY.pesquisarEntidadeById(ItemPedido.class, idItemPedido);
				if (itemPedido == null) {
					return 0;
				}
				return itemPedido.getAliquotaIPI();
			}

			@Mock
			public Double[] pesquisarValorPedidoByItemPedido(Integer idItemPedido) {
				ItemPedido itemPedido = REPOSITORY.pesquisarEntidadeById(ItemPedido.class, idItemPedido);
				if (itemPedido == null || itemPedido.getPedido() == null) {
					return new Double[] { 0d, 0d };
				}

				return new Double[] { itemPedido.getPedido().getValorPedido(), itemPedido.getPedido().getValorPedidoIPI() };
			}
		};

		return new ItemPedidoDAO(null);
	}

}
