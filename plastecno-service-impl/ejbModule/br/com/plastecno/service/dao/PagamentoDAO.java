package br.com.plastecno.service.dao;

import java.util.List;

import javax.persistence.EntityManager;

import br.com.plastecno.service.entity.Pagamento;

public class PagamentoDAO extends GenericDAO<Pagamento> {

	public PagamentoDAO(EntityManager entityManager) {
		super(entityManager);
	}

	public List<Pagamento> pesquisarByIdPedido(Integer idPedido) {
		return entityManager.createQuery("select p from Pagamento p where p.idPedido =:idPedido", Pagamento.class)
				.setParameter("idPedido", idPedido).getResultList();
	}

}
