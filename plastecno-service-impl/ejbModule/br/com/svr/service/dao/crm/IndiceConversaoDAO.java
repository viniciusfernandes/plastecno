package br.com.svr.service.dao.crm;

import javax.persistence.EntityManager;

import br.com.svr.service.dao.GenericDAO;
import br.com.svr.service.entity.crm.IndiceConversao;

public class IndiceConversaoDAO extends GenericDAO<IndiceConversao> {

	public IndiceConversaoDAO(EntityManager entityManager) {
		super(entityManager);
	}

}
