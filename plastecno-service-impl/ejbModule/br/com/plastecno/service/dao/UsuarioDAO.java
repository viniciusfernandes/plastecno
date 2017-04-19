package br.com.plastecno.service.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import br.com.plastecno.service.constante.TipoAcesso;
import br.com.plastecno.service.entity.PerfilAcesso;
import br.com.plastecno.service.entity.Usuario;
import br.com.plastecno.service.impl.util.QueryUtil;

public class UsuarioDAO extends GenericDAO<Usuario> {
	public UsuarioDAO(EntityManager entityManager) {
		super(entityManager);
	}

	public Usuario pesquisarByEmailSenha(String email, String senha) {
		Query query = entityManager
				.createQuery(
						"select u from Usuario u left join fetch u.listaPerfilAcesso where u.email = :email and u.senha = :senha")
				.setParameter("email", email).setParameter("senha", senha);
		return QueryUtil.gerarRegistroUnico(query, Usuario.class, null);
	}

	public Usuario pesquisarById(Integer id) {
		return super.pesquisarById(Usuario.class, id);
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

	public String pesquisarSenha(Integer idUsuario) {
		Query query = this.entityManager.createQuery("select u.senha from Usuario u where u.id = :id");
		query.setParameter("id", idUsuario);
		return QueryUtil.gerarRegistroUnico(query, String.class, null);
	}

	public Usuario pesquisarUsuarioResumidoById(Integer id) {
		return QueryUtil.gerarRegistroUnico(
				this.entityManager.createQuery(
						"select new Usuario(c.id, c.nome, c.sobrenome, c.email) from Usuario c where c.id = :id")
						.setParameter("id", id), Usuario.class, null);
	}

	public boolean pesquisarVendedorAtivo(Integer idVendedor) {
		return QueryUtil
				.gerarRegistroUnico(
						this.entityManager
								.createQuery(
										"select u.ativo from Usuario u inner join u.listaPerfilAcesso p where u.id =:idVendedor and p.id = :idPerfilAcesso")
								.setParameter("idVendedor", idVendedor)
								.setParameter("idPerfilAcesso", TipoAcesso.CADASTRO_PEDIDO_VENDAS.indexOf()),
						Boolean.class, false);
	}

	public Usuario pesquisarVendedorByIdCliente(Integer idCliente) {
		Query query = this.entityManager.createQuery("select v from Cliente c inner join c.vendedor v where c.id =:id");
		query.setParameter("id", idCliente);
		return QueryUtil.gerarRegistroUnico(query, Usuario.class, null);
	}
}
