package br.com.plastecno.service.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import br.com.plastecno.service.constante.TipoCliente;
import br.com.plastecno.service.entity.Cliente;
import br.com.plastecno.service.entity.LogradouroCliente;
import br.com.plastecno.service.impl.util.QueryUtil;

public class ClienteDAO extends GenericDAO<Cliente> {

	public ClienteDAO(EntityManager entityManager) {
		super(entityManager);
	}

	public boolean isEmailExistente(Integer idCliente, String email) {
		Query query = null;
		if (idCliente == null) {
			query = this.entityManager.createQuery("select count(r.id) from Cliente r where r.email = :email ");
			query.setParameter("email", email);
		} else {
			query = this.entityManager
					.createQuery("select count(r.id) from Cliente  r where r.id != :id AND r.email = :email ");
			query.setParameter("email", email);
			query.setParameter("id", idCliente);
		}

		return QueryUtil.gerarRegistroUnico(query, Long.class, 0L) > 1;
	}

	public boolean isRevendedorExistente(Integer id) {
		return isEntidadeExistente(Cliente.class, id, "tipoCliente", TipoCliente.REVENDEDOR);
	}

	public Cliente pesquisarById(Integer id) {
		return super.pesquisarById(Cliente.class, id);
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

	public Cliente pesquisarRevendedor() {
		return QueryUtil.gerarRegistroUnico(
				entityManager.createQuery(
						"select new Cliente(c.id, c.nomeFantasia) from Cliente c where c.tipoCliente = :tipoCliente").setParameter(
						"tipoCliente", TipoCliente.REVENDEDOR), Cliente.class, null);
	}
}
