package br.com.plastecno.service.dao;

import java.util.List;

import javax.persistence.EntityManager;

import br.com.plastecno.service.entity.LogradouroCliente;

public class ClienteDAO extends GenericDAO {

	public ClienteDAO(EntityManager entityManager) {
		super(entityManager);
	}
	@SuppressWarnings("unchecked")
	public List<LogradouroCliente> pesquisarLogradouroById (Integer idCliente) {
		StringBuilder select = new StringBuilder()
		.append("select l from Cliente c ")
		.append("inner join c.listaLogradouro l where c.id = :idCliente ")
		.append(" and l.cancelado = false ");
		
		return this.entityManager.createQuery(select.toString()).setParameter("idCliente", idCliente).getResultList();
	}
}
