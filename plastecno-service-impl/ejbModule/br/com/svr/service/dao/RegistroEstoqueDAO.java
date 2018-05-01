package br.com.svr.service.dao;

import java.util.List;

import javax.persistence.EntityManager;

import br.com.svr.service.entity.RegistroEstoque;

public class RegistroEstoqueDAO extends GenericDAO<RegistroEstoque> {
	public RegistroEstoqueDAO(EntityManager entityManager) {
		super(entityManager);
	}

	public List<RegistroEstoque> pesquisarRegistroByIdItemEstoque(Integer idItemEstoque) {
		return entityManager
				.createQuery("select r from RegistroEstoque r where r.idItemEstoque = :idItemEstoque",
						RegistroEstoque.class).setParameter("idItemEstoque", idItemEstoque).getResultList();

	}

	public List<RegistroEstoque> pesquisarRegistroByIdPedido(Integer idPedido) {
		return entityManager
				.createQuery("select r from RegistroEstoque r where r.idPedido = :idPedido", RegistroEstoque.class)
				.setParameter("idPedido", idPedido).getResultList();

	}

	public List<RegistroEstoque> pesquisarRegistroEstoqueByIdItemPedido(Integer idItemPedido) {
		return entityManager
				.createQuery("select r from RegistroEstoque r where r.idItemPedido = :idItemPedido",
						RegistroEstoque.class).setParameter("idItemPedido", idItemPedido).getResultList();
	}

}
