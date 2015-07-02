package br.com.plastecno.service.dao;

import javax.persistence.EntityManager;

import br.com.plastecno.service.entity.LimiteMinimoEstoque;

public class LimiteMinimoEstoqueDAO extends GenericDAO<LimiteMinimoEstoque> {

	public LimiteMinimoEstoqueDAO(EntityManager entityManager) {
		super(entityManager);
	}

}
