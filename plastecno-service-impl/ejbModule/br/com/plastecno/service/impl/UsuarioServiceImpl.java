package br.com.plastecno.service.impl;

import java.util.Collections;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import br.com.plastecno.service.AutenticacaoService;
import br.com.plastecno.service.ContatoService;
import br.com.plastecno.service.LogradouroService;
import br.com.plastecno.service.UsuarioService;
import br.com.plastecno.service.dao.GenericDAO;
import br.com.plastecno.service.dao.UsuarioDAO;
import br.com.plastecno.service.entity.ContatoUsuario;
import br.com.plastecno.service.entity.Logradouro;
import br.com.plastecno.service.entity.PerfilAcesso;
import br.com.plastecno.service.entity.Remuneracao;
import br.com.plastecno.service.entity.Usuario;
import br.com.plastecno.service.exception.BusinessException;
import br.com.plastecno.service.exception.CriptografiaException;
import br.com.plastecno.service.impl.util.QueryUtil;
import br.com.plastecno.service.wrapper.PaginacaoWrapper;
import br.com.plastecno.util.StringUtils;
import br.com.plastecno.validacao.ValidadorInformacao;

@Stateless
@TransactionManagement(TransactionManagementType.CONTAINER)
public class UsuarioServiceImpl implements UsuarioService {

	@PersistenceContext(unitName = "plastecno")
	private EntityManager entityManager;

	@EJB
	private LogradouroService logradouroService;

	@EJB
	private ContatoService contatoService;

	@EJB
	private AutenticacaoService autenticacaoService;

	private GenericDAO genericDAO;

	private UsuarioDAO usuarioDAO;

	@Override
	public void associarCliente(Integer idVendedor, List<Integer> listaIdClienteAssociado) throws BusinessException {
		this.verificarVendedorAtivo(idVendedor);

		this.entityManager
				.createQuery(
						"update Cliente c set c.vendedor.id = :idVendedor where c.id in (:listaIdClienteAssociado)")
				.setParameter("idVendedor", idVendedor)
				.setParameter("listaIdClienteAssociado", listaIdClienteAssociado).executeUpdate();
	}

	@Override
	public void associarCliente(Integer idVendedor, List<Integer> listaIdClienteAssociado,
			List<Integer> listaIdClienteDesassociado) throws BusinessException {
		if (idVendedor == null) {
			throw new BusinessException("Vendedor é obrigatório para associar aos clientes");
		}

		if (listaIdClienteDesassociado != null) {
			this.desassociarCliente(idVendedor, listaIdClienteDesassociado);
		}

		this.associarCliente(idVendedor, listaIdClienteAssociado);
	}

	@Override
	public int desabilitar(Integer id) throws BusinessException {
		Query query = this.entityManager.createQuery("update Usuario u set u.ativo = false where u.id =:id ");
		query.setParameter("id", id);
		int i = query.executeUpdate();
		if (1 != i) {
			throw new BusinessException("Falha na desativacao do usuario de codigo " + id);
		}
		return i;
	}

	@Override
	public void desassociarCliente(Integer idVendedor, List<Integer> listaIdClienteDesassociado)
			throws BusinessException {
		this.verificarVendedorAtivo(idVendedor);

		this.entityManager
				.createQuery(
						"update Cliente c set c.vendedor = null where c.vendedor.id = :idVendedor and c.id in (:listaIdClienteDesassociado)")
				.setParameter("idVendedor", idVendedor)
				.setParameter("listaIdClienteDesassociado", listaIdClienteDesassociado).executeUpdate();
	}

	private Query gerarQueryPesquisa(Usuario filtro, StringBuilder select) {
		Query query = this.entityManager.createQuery(select.toString());
		if (StringUtils.isNotEmpty(filtro.getNome())) {
			query.setParameter("nome", "%" + filtro.getNome() + "%");
		}

		if (StringUtils.isNotEmpty(filtro.getSobrenome())) {
			query.setParameter("sobrenome", "%" + filtro.getSobrenome() + "%");
		}

		if (StringUtils.isNotEmpty(filtro.getEmail())) {
			query.setParameter("email", "%" + filtro.getEmail() + "%");
		}

		if (StringUtils.isNotEmpty(filtro.getCpf())) {
			query.setParameter("cpf", "%" + filtro.getCpf() + "%");
		}

		return query;
	}

	private void gerarRestricaoPesquisa(Usuario filtro, Boolean apenasAtivos, boolean isVendedor, StringBuilder select) {
		StringBuilder restricao = new StringBuilder();
		if (isVendedor) {
			restricao.append(" u.vendedorAtivo = true AND ");
		}

		if (StringUtils.isNotEmpty(filtro.getNome())) {
			restricao.append("u.nome LIKE :nome AND ");
		}

		if (StringUtils.isNotEmpty(filtro.getSobrenome())) {
			restricao.append("u.sobrenome LIKE :sobrenome AND ");
		}

		if (StringUtils.isNotEmpty(filtro.getEmail())) {
			restricao.append("u.email LIKE :email AND ");
		}

		if (StringUtils.isNotEmpty(filtro.getCpf())) {
			restricao.append("u.cpf LIKE :cpf AND ");
		}

		if (restricao.length() > 0) {

			select.append(" WHERE ").append(restricao);
			int indice = select.lastIndexOf("AND");
			if (indice > 0) {
				select.delete(indice, select.length() - 1);
			}
		}
	}

	@PostConstruct
	public void init() {
		usuarioDAO = new UsuarioDAO(entityManager);
	}

	@Override
	public Integer inserir(Usuario usuario, boolean isAlteracaoSenha) throws BusinessException {

		if (!isAlteracaoSenha && usuario.getId() != null) {
			Query query = this.entityManager.createQuery("select u.senha from Usuario u where u.id = :id");
			query.setParameter("id", usuario.getId());
			usuario.setSenha(QueryUtil.gerarRegistroUnico(query, String.class, null));
		}

		if (this.isEmailExistente(usuario.getId(), usuario.getEmail())) {
			throw new BusinessException("Email enviado ja foi cadastrado para outro usuario");
		}

		if (this.isCPF(usuario.getId(), usuario.getCpf())) {
			throw new BusinessException("CPF enviado ja foi cadastrado para outro usuario");
		}

		usuario.setLogradouro(this.logradouroService.inserir(usuario.getLogradouro()));
		ValidadorInformacao.validar(usuario);
		if (isAlteracaoSenha) {
			try {
				usuario.setSenha(this.autenticacaoService.criptografar(usuario.getSenha()));
			} catch (CriptografiaException e) {
				throw new BusinessException("Não foi possível criptografar a senha do usuário " + usuario.getEmail());
			}
		}
		return this.entityManager.merge(usuario).getId();
	}

	@Override
	public boolean isClienteAssociadoVendedor(Integer idCliente, Integer idVendedor) {
		return usuarioDAO.pesquisarIdVendedorByIdCliente(idCliente, idVendedor) != null;
	}

	@Override
	public boolean isCPF(Integer id, String cpf) {
		return this.genericDAO.isEntidadeExistente(Usuario.class, id, "cpf", cpf);
	}

	@Override
	public boolean isEmailExistente(Integer id, String email) {
		return this.genericDAO.isEntidadeExistente(Usuario.class, id, "email", email);
	}

	@Override
	public boolean isVendedorAtivo(Integer idVendedor) {
		return QueryUtil.gerarRegistroUnico(
				this.entityManager.createQuery(
						"select v.ativo from Usuario v where v.id =:idVendedor and v.vendedorAtivo = true")
						.setParameter("idVendedor", idVendedor), Boolean.class, false);
	}

	@Override
	public PaginacaoWrapper<Usuario> paginarUsuario(Usuario filtro, boolean isVendedor, Boolean apenasAtivos,
			Integer indiceRegistroInicial, Integer numeroMaximoRegistros) {
		return new PaginacaoWrapper<Usuario>(this.pesquisarTotalRegistros(filtro, apenasAtivos, isVendedor),
				this.pesquisar(filtro, isVendedor, apenasAtivos, indiceRegistroInicial, numeroMaximoRegistros));

	}

	@Override
	public PaginacaoWrapper<Usuario> paginarVendedor(Usuario filtro, Boolean apenasAtivos,
			Integer indiceRegistroInicial, Integer numeroMaximoRegistros) {
		return new PaginacaoWrapper<Usuario>(this.pesquisarTotalRegistros(filtro, apenasAtivos, true), this.pesquisar(
				filtro, true, apenasAtivos, indiceRegistroInicial, numeroMaximoRegistros));

	}

	private List<Usuario> pesquisar(Usuario filtro, boolean isVendedor, Boolean apenasAtivos,
			Integer indiceRegistroInicial, Integer numeroMaximoRegistros) {

		if (filtro == null) {
			return Collections.emptyList();
		}

		StringBuilder select = new StringBuilder("SELECT u FROM Usuario u ");
		this.gerarRestricaoPesquisa(filtro, apenasAtivos, isVendedor, select);
		select.append(" order by u.nome ");

		Query query = this.gerarQueryPesquisa(filtro, select);
		return QueryUtil.paginar(query, indiceRegistroInicial, numeroMaximoRegistros);
	}

	@Override
	public List<Usuario> pesquisarBy(Usuario filtro) {
		return this.pesquisar(filtro, false, false, null, null);
	}

	@Override
	public List<Usuario> pesquisarBy(Usuario filtro, Boolean apenasAtivos, Integer indiceRegistroInicial,
			Integer numeroMaximoRegistros) {
		return this.pesquisar(filtro, false, apenasAtivos, indiceRegistroInicial, numeroMaximoRegistros);
	}

	@Override
	public Usuario pesquisarById(Integer id) {
		return QueryUtil.gerarRegistroUnico(this.entityManager.createQuery("select c from Usuario c where c.id = :id")
				.setParameter("id", id), Usuario.class, null);
	}

	@Override
	public List<Usuario> pesquisarByNome(String nome) {
		return this.pesquisarUsuarioByNome(nome, false);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<ContatoUsuario> pesquisarContatos(Integer id) {
		Query query = this.entityManager
				.createQuery("select c from Usuario u inner join u.listaContato c where u.id =:id ");
		query.setParameter("id", id);
		return query.getResultList();
	}

	@Override
	public Logradouro pesquisarLogradouro(Integer id) {
		StringBuilder select = new StringBuilder("select u.logradouro from Usuario u  ");
		select.append(" INNER JOIN u.logradouro where u.id = :id ");

		Query query = this.entityManager.createQuery(select.toString());
		query.setParameter("id", id);
		return QueryUtil.gerarRegistroUnico(query, Logradouro.class, null);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<PerfilAcesso> pesquisarPerfisAssociados(Integer id) {
		Query query = this.entityManager
				.createQuery("select p from Usuario u , IN (u.listaPerfilAcesso) p where  u.id = :id order by p.descricao asc");
		query.setParameter("id", id);
		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<PerfilAcesso> pesquisarPerfisNaoAssociados(Integer id) {
		List<PerfilAcesso> listaPerfil = this.pesquisarPerfisAssociados(id);
		Query query = null;
		if (!listaPerfil.isEmpty()) {
			query = this.entityManager
					.createQuery("select p from PerfilAcesso p where  p not in (:listaPerfil) order by p.descricao asc");
			query.setParameter("listaPerfil", listaPerfil);
		} else {
			query = this.entityManager.createQuery("select p from PerfilAcesso p ");
		}

		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Remuneracao> pesquisarRemuneracaoById(Integer id) {
		Query query = this.entityManager
				.createQuery("select r from Usuario v inner join v.listaRemuneracao r where v.id = :id");
		query.setParameter("id", id);
		return query.getResultList();
	}

	@Override
	public String pesquisarSenhaByEmail(String email) {
		return QueryUtil.gerarRegistroUnico(
				this.entityManager.createQuery("select u.senha from Usuario u where u.email = :email ").setParameter(
						"email", email), String.class, null);
	}

	@Override
	public Long pesquisarTotalRegistros(Usuario filtro, Boolean apenasAtivos, boolean isVendedor) {
		if (filtro == null) {
			return 0L;
		}

		final StringBuilder select = new StringBuilder("SELECT count(u.id) FROM Usuario u ");
		this.gerarRestricaoPesquisa(filtro, apenasAtivos, isVendedor, select);
		Query query = this.gerarQueryPesquisa(filtro, select);

		return QueryUtil.gerarRegistroUnico(query, Long.class, null);
	}

	@SuppressWarnings({ "unchecked" })
	private List<Usuario> pesquisarUsuarioByNome(String nome, boolean isVendedor) {
		StringBuilder select = new StringBuilder("select new Usuario(u.id, u.nome, u.sobrenome) from Usuario u where ");
		if (isVendedor) {
			select.append("vendedorAtivo=true and ");
		}

		select.append("u.nome like :nome ");
		return this.entityManager.createQuery(select.toString()).setParameter("nome", "%" + nome + "%").getResultList();
	}

	@Override
	public Usuario pesquisarUsuarioResumidoById(Integer idUsuario) {
		return QueryUtil.gerarRegistroUnico(
				this.entityManager.createQuery(
						"select new Usuario(u.id, u.nome, u.sobrenome) from Usuario u where u.id = :idUsuario ")
						.setParameter("idUsuario", idUsuario), Usuario.class, null);
	}

	@Override
	public Usuario pesquisarVendedorById(Integer idVendedor) {

		return QueryUtil.gerarRegistroUnico(
				this.entityManager.createQuery(
						"select c from Usuario c where c.id = :idVendedor and c.vendedorAtivo = true").setParameter(
						"idVendedor", idVendedor), Usuario.class, null);
	}

	@Override
	public Usuario pesquisarVendedorByIdCliente(Integer idCliente) {
		Query query = this.entityManager.createQuery("select v from Cliente c inner join c.vendedor v where c.id =:id");
		query.setParameter("id", idCliente);
		return QueryUtil.gerarRegistroUnico(query, Usuario.class, null);
	}

	@Override
	public List<Usuario> pesquisarVendedorByNome(String nome) {
		return this.pesquisarUsuarioByNome(nome, true);
	}

	@Override
	public List<Usuario> pesquisarVendedores(Usuario filtro, Boolean apenasAtivos, Integer indiceRegistroInicial,
			Integer numeroMaximoRegistros) {
		return this.pesquisar(filtro, true, apenasAtivos, indiceRegistroInicial, numeroMaximoRegistros);
	}

	private void verificarVendedorAtivo(Integer idVendedor) throws BusinessException {

		if (!this.isVendedorAtivo(idVendedor)) {
			throw new BusinessException("O usuário enviado não é um vendedor ou não é um vendedor ativo");
		}

	}
}
