package br.com.plastecno.service.dao.impl;

import java.util.List;

import javax.persistence.EntityManager;

import br.com.plastecno.service.dao.ClienteDAO;
import br.com.plastecno.service.entity.LogradouroCliente;

public class ClienteDAOImpl extends GenericDAOImpl implements ClienteDAO {

	public ClienteDAOImpl(EntityManager entityManager) {
		super(entityManager);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<LogradouroCliente> pesquisarLogradouro(Integer idCliente) {
		StringBuilder select = new StringBuilder().append("select l from Cliente c ")
				.append("inner join c.listaLogradouro l where c.id = :idCliente ").append(" and l.cancelado = false ");

		return this.entityManager.createQuery(select.toString()).setParameter("idCliente", idCliente).getResultList();
	}

}
