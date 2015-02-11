package br.com.plastecno.service.dao;

import javax.persistence.EntityManager;

import br.com.plastecno.service.entity.ItemEstoque;

public class ItemEstoqueDAO extends GenericDAO<ItemEstoque> {

	public ItemEstoqueDAO(EntityManager entityManager) {
		super(entityManager);
	}

	public ItemEstoque pesquisarById(Integer idItemEstoque) {
		return pesquisarById(ItemEstoque.class, idItemEstoque);
	}
}
