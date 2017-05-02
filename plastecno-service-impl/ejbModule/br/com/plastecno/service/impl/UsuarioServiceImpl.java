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
import br.com.plastecno.service.constante.TipoAcesso;
import br.com.plastecno.service.dao.UsuarioDAO;
import br.com.plastecno.service.entity.ContatoUsuario;
import br.com.plastecno.service.entity.LogradouroEndereco;
import br.com.plastecno.service.entity.LogradouroUsuario;
import br.com.plastecno.service.entity.PerfilAcesso;
import br.com.plastecno.service.entity.Usuario;
import br.com.plastecno.service.exception.BusinessException;
import br.com.plastecno.service.exception.CriptografiaException;
import br.com.plastecno.service.impl.anotation.TODO;
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

	private UsuarioDAO usuarioDAO;

	@Override
	public void associarCliente(Integer idVendedor, List<Integer> listaIdClienteAssociado) throws BusinessException {
		this.verificarPerfilVendedor(idVendedor);

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
			throw new BusinessException("Vendedor � obrigat�rio para associar aos clientes");
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
		this.verificarPerfilVendedor(idVendedor);

		this.entityManager
				.createQuery(
						"update Cliente c set c.vendedor = null where c.vendedor.id = :idVendedor and c.id in (:listaIdClienteDesassociado)")
				.setParameter("idVendedor", idVendedor)
				.setParameter("listaIdClienteDesassociado", listaIdClienteDesassociado).executeUpdate();
	}

	private Query gerarQueryPesquisa(Usuario filtro, boolean isVendedor, StringBuilder select) {
		Query query = this.entityManager.createQuery(select.toString());
		if (isVendedor) {
			query.setParameter("idPerfilAcesso", TipoAcesso.CADASTRO_PEDIDO_VENDAS.indexOf());
		}
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
			select.append("inner join u.listaPerfilAcesso p ");
			restricao.append("p.id = :idPerfilAcesso AND ");
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
			usuario.setSenha(usuarioDAO.pesquisarSenha(usuario.getId()));
		}

		if (this.isEmailExistente(usuario.getId(), usuario.getEmail())) {
			throw new BusinessException("Email enviado ja foi cadastrado para outro usuario");
		}

		if (this.isCPF(usuario.getId(), usuario.getCpf())) {
			throw new BusinessException("CPF enviado ja foi cadastrado para outro usuario");
		}

		usuario.setLogradouro(logradouroService.inserirBaseCep(usuario.getLogradouro()));
		ValidadorInformacao.validar(usuario);
		if (isAlteracaoSenha) {
			try {
				usuario.setSenha(this.autenticacaoService.criptografar(usuario.getSenha()));
			} catch (CriptografiaException e) {
				throw new BusinessException("N�o foi poss�vel criptografar a senha do usu�rio " + usuario.getEmail());
			}
		}

		return usuario.getId() == null ? usuarioDAO.inserir(usuario).getId() : usuarioDAO.alterar(usuario).getId();
	}

	@TODO(descricao = "Remover o hardcoded administracao")
	@Override
	public boolean isAdministrador(Integer idUsuario) {
		List<PerfilAcesso> l = pesquisarPerfisAssociados(idUsuario);
		for (PerfilAcesso perfilAcesso : l) {
			if (perfilAcesso.getDescricao().equals("ADMINISTRACAO")) {
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean isClienteAssociadoVendedor(Integer idCliente, Integer idVendedor) {
		return usuarioDAO.pesquisarIdVendedorByIdCliente(idCliente, idVendedor) != null;
	}

	@Override
	public boolean isCPF(Integer id, String cpf) {
		return usuarioDAO.isEntidadeExistente(Usuario.class, id, "cpf", cpf);
	}

	@Override
	public boolean isEmailExistente(Integer id, String email) {
		return usuarioDAO.isEntidadeExistente(Usuario.class, id, "email", email);
	}

	@Override
	public boolean isVendaPermitida(Integer idCliente, Integer idVendedor) {
		return isAdministrador(idVendedor) || isClienteAssociadoVendedor(idCliente, idVendedor);
	}

	@Override
	public boolean isVendedorAtivo(Integer idVendedor) {
		return usuarioDAO.pesquisarVendedorAtivo(idVendedor);
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

		Query query = this.gerarQueryPesquisa(filtro, isVendedor, select);
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
		return usuarioDAO.pesquisarById(id);
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
	public LogradouroUsuario pesquisarLogradouro(Integer id) {
		return QueryUtil.gerarRegistroUnico(
				entityManager
						.createQuery("select u.logradouro from Usuario u INNER JOIN u.logradouro where u.id = :id")
						.setParameter("id", id), LogradouroUsuario.class, null);
	}

	@Override
	public List<PerfilAcesso> pesquisarPerfisAssociados(Integer id) {
		return usuarioDAO.pesquisarPerfisAssociados(id);
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
		gerarRestricaoPesquisa(filtro, apenasAtivos, isVendedor, select);
		Query query = gerarQueryPesquisa(filtro, isVendedor, select);

		return QueryUtil.gerarRegistroUnico(query, Long.class, null);
	}

	@SuppressWarnings({ "unchecked" })
	private List<Usuario> pesquisarUsuarioByNome(String nome, boolean isVendedor) {
		StringBuilder select = new StringBuilder("select new Usuario(u.id, u.nome, u.sobrenome) from Usuario u ");
		if (isVendedor) {
			select.append("inner join u.listaPerfilAcesso p where p.id = :idPerfilAcesso and u.nome like :nome ");
		} else {
			select.append("where u.nome like :nome ");
		}

		Query query = this.entityManager.createQuery(select.toString()).setParameter("nome", "%" + nome + "%");
		if (isVendedor) {
			query.setParameter("idPerfilAcesso", TipoAcesso.CADASTRO_PEDIDO_VENDAS.indexOf());
		}
		return query.getResultList();
	}

	@Override
	public Usuario pesquisarUsuarioResumidoById(Integer idUsuario) {
		return usuarioDAO.pesquisarUsuarioResumidoById(idUsuario);
	}

	@Override
	public Usuario pesquisarVendedorById(Integer idVendedor) {

		return QueryUtil
				.gerarRegistroUnico(
						this.entityManager
								.createQuery(
										"select c from Usuario c inner join c.listaPerfilAcesso p where c.id = :idVendedor and p.id = :idPerfilAcesso ")
								.setParameter("idVendedor", idVendedor)
								.setParameter("idPerfilAcesso", TipoAcesso.CADASTRO_PEDIDO_VENDAS.indexOf()),
						Usuario.class, null);
	}

	@Override
	public Usuario pesquisarVendedorByIdCliente(Integer idCliente) {
		return usuarioDAO.pesquisarVendedorByIdCliente(idCliente);
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

	private void verificarPerfilVendedor(Integer idVendedor) throws BusinessException {
		if (!isVendedorAtivo(idVendedor)) {
			throw new BusinessException("O usu�rio enviado n�o existe ou n�o � tem um perfil de vendedor.");
		}
	}
}
