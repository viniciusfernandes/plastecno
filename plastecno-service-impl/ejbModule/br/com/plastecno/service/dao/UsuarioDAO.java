package br.com.plastecno.service.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import br.com.plastecno.service.entity.PerfilAcesso;
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

	@SuppressWarnings("unchecked")
	public List<PerfilAcesso> pesquisarPerfisAssociados(Integer id) {
		Query query = this.entityManager
				.createQuery("select p from Usuario u , IN (u.listaPerfilAcesso) p where  u.id = :id order by p.descricao asc");
		query.setParameter("id", id);
		return query.getResultList();
	}

	public Usuario pesquisarVendedorByIdCliente(Integer idCliente) {
		Query query = this.entityManager.createQuery("select v from Cliente c inner join c.vendedor v where c.id =:id");
		query.setParameter("id", idCliente);
		return QueryUtil.gerarRegistroUnico(query, Usuario.class, null);
	}
}
