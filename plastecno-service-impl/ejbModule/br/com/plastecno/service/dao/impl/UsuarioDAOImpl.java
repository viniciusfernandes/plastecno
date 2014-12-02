package br.com.plastecno.service.dao.impl;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import br.com.plastecno.service.dao.UsuarioDAO;
import br.com.plastecno.service.impl.util.QueryUtil;

public class UsuarioDAOImpl implements UsuarioDAO {

	private EntityManager entityManager;

	public UsuarioDAOImpl(EntityManager entityManager) {
		this.entityManager = entityManager;
	}

	@Override
	public boolean isClienteAssociadoVendedor(Integer idCliente, Integer idVendedor) {
		Query query = this.entityManager
				.createQuery("select c.id from Cliente c where c.id =:id and c.vendedor.id = :idVendedor ");
		query.setParameter("id", idCliente).setParameter("idVendedor", idVendedor);
		return QueryUtil.gerarRegistroUnico(query, Integer.class, null) != null;
	}
}
