package br.com.plastecno.service.test;

import br.com.plastecno.service.dao.ItemReservadoDAO;

public class ItemReservadoDAOBuilder extends DAOBuilder<ItemReservadoDAO> {

	@Override
	public ItemReservadoDAO build() {
		return new ItemReservadoDAO(null);
	}

}
