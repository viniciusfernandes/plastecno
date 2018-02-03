package br.com.svr.service.dao;

import javax.persistence.EntityManager;

import br.com.svr.service.entity.RamoAtividade;
import br.com.svr.service.impl.util.QueryUtil;

public class RamoAtividadeDAO extends GenericDAO<RamoAtividade> {

	public RamoAtividadeDAO(EntityManager entityManager) {
		super(entityManager);
	}

	public RamoAtividade pesquisarRamoAtividadePadrao() {
		return QueryUtil.gerarRegistroUnico(
				entityManager.createQuery("select r from RamoAtividade r where r.sigla = 'NDEFINIDO'"), RamoAtividade.class,
				null);
	}
}
