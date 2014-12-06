package br.com.plastecno.service.dao;

import javax.persistence.EntityManager;

import br.com.plastecno.service.entity.RamoAtividade;

public class RamoAtividadeDAO extends GenericDAO<RamoAtividade> {

	public RamoAtividadeDAO(EntityManager entityManager) {
		super(entityManager);
	}

}
