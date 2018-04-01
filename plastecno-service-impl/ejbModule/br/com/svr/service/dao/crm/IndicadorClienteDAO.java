package br.com.svr.service.dao.crm;

import javax.persistence.EntityManager;

import br.com.svr.service.dao.GenericDAO;
import br.com.svr.service.entity.crm.IndicadorCliente;

public class IndicadorClienteDAO extends GenericDAO<IndicadorCliente> {

	public IndicadorClienteDAO(EntityManager entityManager) {
		super(entityManager);
	}

}
