package br.com.svr.service.impl;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import br.com.svr.service.ServiceUtils;

//Essa classe foi criada para a utilizacao nos testes de integracao por isso ela nao eh de fato um  EJB.
public class ServiceUtilsImpl implements ServiceUtils {
	@PersistenceContext(unitName = "svr")
	private EntityManager entityManager;

	@Override
	public <T> T recarregarEntidade(Class<T> classe, Integer id) {
		T e = entityManager.createQuery("select e from " + classe.getSimpleName() + " e where e.id =:id", classe)
				.setParameter("id", id).getSingleResult();
		entityManager.refresh(e);
		return e;
	}
}
