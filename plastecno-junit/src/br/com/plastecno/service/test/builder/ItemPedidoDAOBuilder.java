package br.com.plastecno.service.test.builder;

import br.com.plastecno.service.dao.ItemPedidoDAO;

public class ItemPedidoDAOBuilder extends DAOBuilder<ItemPedidoDAO> {

	@Override
	public ItemPedidoDAO build() {
		return new ItemPedidoDAO(null);
	}

}
