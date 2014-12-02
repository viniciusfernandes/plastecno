package br.com.plastecno.service.dao.impl;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import br.com.plastecno.service.dao.GenericDAO;

public class GenericDAOImpl implements GenericDAO {
	final EntityManager entityManager;

	public GenericDAOImpl(EntityManager entityManager) {
		this.entityManager = entityManager;
	}

	public void alterar(Object entidade) {
		entityManager.merge(entidade);
	}

	public void inserir(Object entidade) {
		entityManager.persist(entidade);
	}

	public <T> boolean isEntidadeExistente(Class<T> classe, Object idEntidade, String nomeAtributo, Object valorAtributo) {
		return this.isEntidadeExistente(classe, nomeAtributo, valorAtributo, "id", idEntidade);
	}

	public <T> boolean isEntidadeExistente(Class<T> classe, String nomeAtributo, Object valorAtributo) {
		return this.isEntidadeExistente(classe, nomeAtributo, valorAtributo, null, null);
	}

	public <T> boolean isEntidadeExistente(Class<T> classe, String nomeAtributo, Object valorAtributo,
			Object nomeIdEntidade, Object valorIdEntidade) {

		StringBuilder select = new StringBuilder();
		select.append("select r.").append(nomeAtributo).append(" ");
		select.append("from ").append(classe.getSimpleName()).append(" r ");
		select.append(" where ").append("r.").append(nomeAtributo);
		select.append(" = :").append(nomeAtributo);
		final boolean idInclusoNoFiltro = nomeIdEntidade != null && valorIdEntidade != null;
		if (idInclusoNoFiltro) {
			select.append(" and r.").append(nomeIdEntidade).append(" != :valorIdEntidade ");
		}

		if (valorAtributo instanceof String) {
			valorAtributo = valorAtributo.toString().trim();
		}

		Query query = this.entityManager.createQuery(select.toString()).setParameter(nomeAtributo, valorAtributo);
		if (idInclusoNoFiltro) {
			query.setParameter("valorIdEntidade", valorIdEntidade);
		}
		return query.getResultList().size() > 0;
	}
}
