package br.com.plastecno.service.dao;

import javax.persistence.EntityManager;

import br.com.plastecno.service.entity.RamoAtividade;
import br.com.plastecno.service.impl.util.QueryUtil;

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
