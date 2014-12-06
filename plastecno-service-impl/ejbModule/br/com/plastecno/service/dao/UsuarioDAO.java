package br.com.plastecno.service.dao;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import br.com.plastecno.service.entity.Usuario;
import br.com.plastecno.service.impl.util.QueryUtil;

public class UsuarioDAO extends GenericDAO<Usuario> {

	public UsuarioDAO(EntityManager entityManager) {
		super(entityManager);
	}

	public Usuario pesquisarById(Integer id) {
		return QueryUtil.gerarRegistroUnico(this.entityManager.createQuery("select c from Usuario c where c.id = :id")
				.setParameter("id", id), Usuario.class, null);
	}

	public Integer pesquisarIdVendedorByIdCliente(Integer idCliente, Integer idVendedor) {
		Query query = this.entityManager
				.createQuery("select c.id from Cliente c where c.id =:id and c.vendedor.id = :idVendedor ");
		query.setParameter("id", idCliente).setParameter("idVendedor", idVendedor);
		return QueryUtil.gerarRegistroUnico(query, Integer.class, null);
	}

}
