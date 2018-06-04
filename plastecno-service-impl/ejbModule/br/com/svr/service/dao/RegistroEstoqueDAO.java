package br.com.svr.service.dao;

import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;

import br.com.svr.service.entity.RegistroEstoque;
import br.com.svr.service.impl.util.QueryUtil;

public class RegistroEstoqueDAO extends GenericDAO<RegistroEstoque> {
	public RegistroEstoqueDAO(EntityManager entityManager) {
		super(entityManager);
	}

	public void removerRegistroByDataLimite(Date dataLimite) {
		entityManager.createQuery("delete from RegistroEstoque r where r.dataOperacao <= :dataLimite")
				.setParameter("dataLimite", dataLimite).executeUpdate();
	}

	public List<RegistroEstoque> pesquisarRegistroByIdItemEstoque(Integer idItemEstoque) {
		return pesquisarRegistroByIdItemEstoque(idItemEstoque, null, null);
	}

	public List<RegistroEstoque> pesquisarRegistroByIdItemEstoque(Integer idItemEstoque, Integer indiceInicial,
			Integer numeroMaxRegistros) {
		if (indiceInicial == null || numeroMaxRegistros == null) {
			return entityManager
					.createQuery("select r from RegistroEstoque r where r.idItemEstoque = :idItemEstoque",
							RegistroEstoque.class).setParameter("idItemEstoque", idItemEstoque).getResultList();
		}
		return QueryUtil.paginar(
				entityManager.createQuery("select r from RegistroEstoque r where r.idItemEstoque = :idItemEstoque",
						RegistroEstoque.class).setParameter("idItemEstoque", idItemEstoque), indiceInicial,
				numeroMaxRegistros);
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

	public Long pesquisarTotalRegistroByItemEstoque(Integer idItemEstoque) {
		return QueryUtil.gerarRegistroUnico(
				entityManager.createQuery(
						"select count(r.id) from RegistroEstoque r where r.idItemEstoque=:idItemEstoque").setParameter(
						"idItemEstoque", idItemEstoque), Long.class, 0L);
	}

}
