package br.com.plastecno.service.dao;

import javax.persistence.EntityManager;

import br.com.plastecno.service.entity.ItemPedido;

public class ItemPedidoDAO extends GenericDAO<ItemPedido> {

	public ItemPedidoDAO(EntityManager entityManager) {
		super(entityManager);
	}

}
