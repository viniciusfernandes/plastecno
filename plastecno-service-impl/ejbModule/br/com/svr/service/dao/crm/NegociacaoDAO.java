package br.com.svr.service.dao.crm;

import javax.persistence.EntityManager;

import br.com.svr.service.dao.GenericDAO;
import br.com.svr.service.entity.crm.Negociacao;

public class NegociacaoDAO extends GenericDAO<Negociacao> {

	public NegociacaoDAO(EntityManager entityManager) {
		super(entityManager);
	}

	public Negociacao pesquisarById(Integer idNegociacao) {
		return super.pesquisarById(Negociacao.class, idNegociacao);
	}
}
