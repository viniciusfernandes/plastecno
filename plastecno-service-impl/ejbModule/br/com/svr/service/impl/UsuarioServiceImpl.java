package br.com.svr.service.impl;

import static br.com.svr.service.constante.TipoAcesso.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import br.com.svr.service.AutenticacaoService;
import br.com.svr.service.ContatoService;
import br.com.svr.service.LogradouroService;
import br.com.svr.service.UsuarioService;
import br.com.svr.service.constante.TipoAcesso;
import br.com.svr.service.dao.UsuarioDAO;
import br.com.svr.service.entity.ContatoUsuario;
import br.com.svr.service.entity.LogradouroUsuario;
import br.com.svr.service.entity.PerfilAcesso;
import br.com.svr.service.entity.Usuario;
import br.com.svr.service.exception.BusinessException;
import br.com.svr.service.exception.CriptografiaException;
import br.com.svr.service.impl.util.QueryUtil;
import br.com.svr.service.validacao.ValidadorInformacao;
import br.com.svr.service.wrapper.PaginacaoWrapper;
import br.com.svr.util.StringUtils;

@Stateless
@TransactionManagement(TransactionManagementType.CONTAINER)
public class UsuarioServiceImpl implements UsuarioService {
	@EJB
	private AutenticacaoService autenticacaoService;

	@EJB
	private ContatoService contatoService;

	@PersistenceContext(unitName = "svr")
	private EntityManager entityManager;

	@EJB
	private LogradouroService logradouroService;

	private UsuarioDAO usuarioDAO;

	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public void alterarComissaoSimples(Integer iUsuario, boolean isComissaoSimples) {
		usuarioDAO.alterarComissaoSimples(iUsuario, isComissaoSimples);
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public void associarCliente(Integer idVendedor, Integer idCliente) throws BusinessException {
		if (idVendedor == null || idCliente == null) {
			return;
		}

		List<Integer> ids = new ArrayList<>();
		ids.add(idCliente);
		associarCliente(idVendedor, ids);
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public void associarCliente(Integer idVendedor, List<Integer> listaIdClienteAssociado) throws BusinessException {
		verificarPerfilVendedor(idVendedor);
		entityManager
				.createQuery(
						"update Cliente c set c.vendedor.id = :idVendedor where c.id in (:listaIdClienteAssociado)")
				.setParameter("idVendedor", idVendedor)
				.setParameter("listaIdClienteAssociado", listaIdClienteAssociado).executeUpdate();
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public void associarCliente(Integer idVendedor, List<Integer> listaIdClienteAssociado,
			List<Integer> listaIdClienteDesassociado) throws BusinessException {
		if (idVendedor == null) {
			throw new BusinessException("Vendedor é obrigatório para associar aos clientes");
		}

		if (listaIdClienteDesassociado != null) {
			desassociarCliente(idVendedor, listaIdClienteDesassociado);
		}

		associarCliente(idVendedor, listaIdClienteAssociado);
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
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
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public void desassociarCliente(Integer idVendedor, List<Integer> listaIdClienteDesassociado)
			throws BusinessException {
		entityManager
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
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
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

		usuario.setLogradouro(logradouroService.inserir(usuario.getLogradouro()));
		ValidadorInformacao.validar(usuario);
		if (isAlteracaoSenha) {
			try {
				usuario.setSenha(this.autenticacaoService.criptografar(usuario.getSenha()));
			} catch (CriptografiaException e) {
				throw new BusinessException("Não foi possível criptografar a senha do usuário " + usuario.getEmail());
			}
		}

		return usuario.getId() == null ? usuarioDAO.inserir(usuario).getId() : usuarioDAO.alterar(usuario).getId();
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public boolean isAcessoPermitido(Integer idUsuario, TipoAcesso... tipos) {
		List<PerfilAcesso> l = pesquisarPerfisAssociados(idUsuario);
		TipoAcesso tp = null;
		for (PerfilAcesso perfilAcesso : l) {
			tp = TipoAcesso.valueOf(perfilAcesso.getDescricao());
			for (TipoAcesso t : tipos) {
				if (t.equals((tp))) {
					return true;
				}
			}

		}
		return false;

	}

	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public boolean isClienteAssociadoVendedor(Integer idCliente, Integer idVendedor) {
		return usuarioDAO.pesquisarIdVendedorByIdCliente(idCliente, idVendedor) != null;
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public boolean isComissionadoSimples(Integer idUsuario) {
		return usuarioDAO.pesquisarComissionadoSimples(idUsuario);
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public boolean isCompraPermitida(Integer idUsuario) {
		return isAcessoPermitido(idUsuario, ADMINISTRACAO, CADASTRO_PEDIDO_COMPRA, RECEPCAO_COMPRA);
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public boolean isCPF(Integer id, String cpf) {
		return usuarioDAO.isEntidadeExistente(Usuario.class, id, "cpf", cpf);
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public boolean isEmailExistente(Integer id, String email) {
		return usuarioDAO.isEntidadeExistente(Usuario.class, id, "email", email);
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public boolean isVendaPermitida(Integer idCliente, Integer idVendedor) {
		return isAcessoPermitido(idVendedor, ADMINISTRACAO, GERENCIA_VENDAS)
				|| isClienteAssociadoVendedor(idCliente, idVendedor);
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public boolean isVendedorAtivo(Integer idVendedor) {
		return usuarioDAO.pesquisarVendedorAtivo(idVendedor);
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public PaginacaoWrapper<Usuario> paginarUsuario(Usuario filtro, boolean isVendedor, Boolean apenasAtivos,
			Integer indiceRegistroInicial, Integer numeroMaximoRegistros) {
		return new PaginacaoWrapper<Usuario>(this.pesquisarTotalRegistros(filtro, apenasAtivos, isVendedor),
				this.pesquisar(filtro, isVendedor, apenasAtivos, indiceRegistroInicial, numeroMaximoRegistros));

	}

	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public PaginacaoWrapper<Usuario> paginarVendedor(Usuario filtro, Boolean apenasAtivos,
			Integer indiceRegistroInicial, Integer numeroMaximoRegistros) {
		return new PaginacaoWrapper<Usuario>(this.pesquisarTotalRegistros(filtro, apenasAtivos, true), this.pesquisar(
				filtro, true, apenasAtivos, indiceRegistroInicial, numeroMaximoRegistros));

	}

	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
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
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public List<Usuario> pesquisarBy(Usuario filtro) {
		return this.pesquisar(filtro, false, false, null, null);
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public List<Usuario> pesquisarBy(Usuario filtro, Boolean apenasAtivos, Integer indiceRegistroInicial,
			Integer numeroMaximoRegistros) {
		return this.pesquisar(filtro, false, apenasAtivos, indiceRegistroInicial, numeroMaximoRegistros);
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public Usuario pesquisarById(Integer id) {
		return usuarioDAO.pesquisarById(id);
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public List<Usuario> pesquisarByNome(String nome) {
		return this.pesquisarUsuarioResumidoByNome(nome, false);
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
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public List<PerfilAcesso> pesquisarPerfisAssociados(Integer id) {
		return usuarioDAO.pesquisarPerfisAssociadosByIdUsuario(id);
	}

	@SuppressWarnings("unchecked")
	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
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
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public String pesquisarSenhaByEmail(String email) {
		return QueryUtil.gerarRegistroUnico(
				this.entityManager.createQuery("select u.senha from Usuario u where u.email = :email ").setParameter(
						"email", email), String.class, null);
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public Long pesquisarTotalRegistros(Usuario filtro, Boolean apenasAtivos, boolean isVendedor) {
		if (filtro == null) {
			return 0L;
		}

		final StringBuilder select = new StringBuilder("SELECT count(u.id) FROM Usuario u ");
		gerarRestricaoPesquisa(filtro, apenasAtivos, isVendedor, select);
		Query query = gerarQueryPesquisa(filtro, isVendedor, select);

		return QueryUtil.gerarRegistroUnico(query, Long.class, null);
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public Usuario pesquisarUsuarioResumidoById(Integer idUsuario) {
		return usuarioDAO.pesquisarUsuarioResumidoById(idUsuario);
	}

	@SuppressWarnings({ "unchecked" })
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	private List<Usuario> pesquisarUsuarioResumidoByNome(String nome, boolean isVendedor) {
		StringBuilder select = new StringBuilder("select new Usuario(u.id, u.nome, u.sobrenome) from Usuario u ");
		if (isVendedor) {
			select.append("inner join u.listaPerfilAcesso p where p.id = :idPerfilAcesso and u.nome like :nome ");
		} else {
			select.append("where u.nome like :nome ");
		}

		Query query = entityManager.createQuery(select.toString()).setParameter("nome", "%" + nome + "%");
		if (isVendedor) {
			query.setParameter("idPerfilAcesso", CADASTRO_PEDIDO_VENDAS.indexOf());
		}
		return query.getResultList();
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
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
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public Usuario pesquisarVendedorByIdCliente(Integer idCliente) {
		return usuarioDAO.pesquisarVendedorByIdCliente(idCliente);
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public List<Usuario> pesquisarVendedorByNome(String nome) {
		return this.pesquisarUsuarioResumidoByNome(nome, true);
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public List<Usuario> pesquisarVendedores(Usuario filtro, Boolean apenasAtivos, Integer indiceRegistroInicial,
			Integer numeroMaximoRegistros) {
		return this.pesquisar(filtro, true, apenasAtivos, indiceRegistroInicial, numeroMaximoRegistros);
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public Usuario pesquisarVendedorResumidoByIdCliente(Integer idCliente) {
		return usuarioDAO.pesquisarVendedorByIdCliente(idCliente);
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public void removerLogradouro(Integer idLogradouro) {
		if (idLogradouro == null) {
			return;
		}

		usuarioDAO.removerLogradouro(idLogradouro);
		logradouroService.removerLogradouro(new LogradouroUsuario(idLogradouro));
	}

	private void verificarPerfilVendedor(Integer idVendedor) throws BusinessException {
		if (!isVendedorAtivo(idVendedor)) {
			throw new BusinessException("O usuário enviado não existe ou não é tem um perfil de vendedor.");
		}
	}
}
