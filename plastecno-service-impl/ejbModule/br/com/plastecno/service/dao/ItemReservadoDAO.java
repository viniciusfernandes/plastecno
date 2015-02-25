package br.com.plastecno.service.dao;

import javax.persistence.EntityManager;

import br.com.plastecno.service.entity.ItemReservado;

public class ItemReservadoDAO extends GenericDAO<ItemReservado> {

	public ItemReservadoDAO(EntityManager entityManager) {
		super(entityManager);
	}

}
