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
			public double pesquisarAliquotaIPIRepresentadaByIdItemPedido(Integer idItemPedido) {
				ItemPedido itemPedido = REPOSITORY.pesquisarEntidadeById(ItemPedido.class, idItemPedido);
				if (itemPedido == null) {
					return 0;
				}
				return itemPedido.getPedido().getRepresentada().getAliquotaIPI();
			}
		};

		return new ItemPedidoDAO(null);
	}

}
