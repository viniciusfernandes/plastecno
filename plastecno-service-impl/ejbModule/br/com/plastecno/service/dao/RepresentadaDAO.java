package br.com.plastecno.service.dao;

import javax.persistence.EntityManager;

import br.com.plastecno.service.entity.Representada;

public class RepresentadaDAO extends GenericDAO<Representada> {

	public RepresentadaDAO(EntityManager entityManager) {
		super(entityManager);
	}

	public Representada pesquisarById(Integer idRepresentada) {
		return pesquisarById(Representada.class, idRepresentada);
	}
}
