package br.com.plastecno.service.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import br.com.plastecno.service.entity.Cliente;
import br.com.plastecno.service.entity.LogradouroCliente;
import br.com.plastecno.service.impl.util.QueryUtil;

public class ClienteDAO extends GenericDAO<Cliente> {

	public ClienteDAO(EntityManager entityManager) {
		super(entityManager);
	}

	public boolean isEmailExistente(Integer idCliente, String email) {
		return true;
	}

	public Cliente pesquisarById(Integer id) {
		return QueryUtil.gerarRegistroUnico(this.entityManager.createQuery("select c from Cliente c where c.id = :id")
				.setParameter("id", id), Cliente.class, null);
	}

	@SuppressWarnings("unchecked")
	public List<Cliente> pesquisarByNomeFantasia(String nomeFantasia) {
		StringBuilder select = new StringBuilder().append("select c from Cliente c ").append(
				"where c.nomeFantasia like :nomeFantasia");
		return this.entityManager.createQuery(select.toString()).setParameter("nomeFantasia", nomeFantasia).getResultList();

	}

	@SuppressWarnings("unchecked")
	public List<LogradouroCliente> pesquisarLogradouroById(Integer idCliente) {
		StringBuilder select = new StringBuilder().append("select l from Cliente c ")
				.append("inner join c.listaLogradouro l where c.id = :idCliente ").append(" and l.cancelado = false ");

		return this.entityManager.createQuery(select.toString()).setParameter("idCliente", idCliente).getResultList();
	}

	public String pesquisarNomeFantasia(Integer idCliente) {
		return super.pesquisarCampoById(Cliente.class, idCliente, "nomeFantasia", String.class);
	}

}
