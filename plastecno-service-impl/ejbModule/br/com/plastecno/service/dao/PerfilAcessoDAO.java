package br.com.plastecno.service.dao;

import java.util.List;

import javax.persistence.EntityManager;

import br.com.plastecno.service.entity.PerfilAcesso;

public class PerfilAcessoDAO extends GenericDAO<PerfilAcesso> {

	public PerfilAcessoDAO(EntityManager entityManager) {
		super(entityManager);
	}

	@SuppressWarnings("unchecked")
	public List<PerfilAcesso> pesquisarTodos() {
		return this.entityManager.createQuery("SELECT p FROM PerfilAcesso p order by p.descricao asc ").getResultList();
	}
}
