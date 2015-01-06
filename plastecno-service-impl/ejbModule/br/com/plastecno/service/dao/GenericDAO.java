package br.com.plastecno.service.dao;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import br.com.plastecno.service.impl.util.QueryUtil;

public class GenericDAO<T> {
	final EntityManager entityManager;

	public GenericDAO(EntityManager entityManager) {
		this.entityManager = entityManager;
	}

	public T alterar(T t) {
		return entityManager.merge(t);
	}

	public T inserir(T t) {
		entityManager.persist(t);
		return t;
	}

	public boolean isEntidadeExistente(Class<T> classe, Object idEntidade, String nomeAtributo, Object valorAtributo) {
		return this.isEntidadeExistente(classe, nomeAtributo, valorAtributo, "id", idEntidade);
	}

	public boolean isEntidadeExistente(Class<T> classe, String nomeAtributo, Object valorAtributo) {
		return this.isEntidadeExistente(classe, nomeAtributo, valorAtributo, null, null);
	}

	public boolean isEntidadeExistente(Class<T> classe, String nomeAtributo, Object valorAtributo, Object nomeIdEntidade,
			Object valorIdEntidade) {

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

	T pesquisarById(Class<T> classe, Integer id) {
		StringBuilder select = new StringBuilder();
		select.append("select e from ").append(classe.getSimpleName());
		select.append(" e where e.id = :id");
		return QueryUtil.gerarRegistroUnico(entityManager.createQuery(select.toString()).setParameter("id", id), classe,
				null);
	}
}
