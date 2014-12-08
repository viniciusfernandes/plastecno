package br.com.plastecno.service.dao;

import javax.persistence.EntityManager;

import br.com.plastecno.service.entity.Logradouro;

public class LogradouroDAO extends GenericDAO<Logradouro> {

	public LogradouroDAO(EntityManager entityManager) {
		super(entityManager);
	}

}
