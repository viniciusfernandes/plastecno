package br.com.svr.service.dao;

import javax.persistence.EntityManager;

import br.com.svr.service.entity.Contato;

public class ContatoDAO extends GenericDAO<Contato> {

	public ContatoDAO(EntityManager entityManager) {
		super(entityManager);
	}
}
