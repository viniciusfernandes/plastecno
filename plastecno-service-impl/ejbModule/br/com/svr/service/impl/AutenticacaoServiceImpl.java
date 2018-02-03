package br.com.svr.service.impl;

import javax.annotation.PostConstruct;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import br.com.svr.service.AutenticacaoService;
import br.com.svr.service.dao.UsuarioDAO;
import br.com.svr.service.entity.Usuario;
import br.com.svr.service.exception.AutenticacaoException;
import br.com.svr.service.exception.CriptografiaException;

@Stateless
public class AutenticacaoServiceImpl implements AutenticacaoService {

	@PersistenceContext(name = "svr")
	private EntityManager entityManager;
	private UsuarioDAO usuarioDAO;

	@Override
	public Usuario autenticar(String email, String senha) throws AutenticacaoException {
		try {
			senha = criptografar(senha);
		} catch (CriptografiaException e) {
			throw new AutenticacaoException("Falha na autenticacao do usuario com login " + email
					+ ". Veja o log para mais detalhes", e);
		}
		return usuarioDAO.pesquisarByEmailSenha(email, senha);
	}

	@Override
	public String criptografar(String parametro) throws CriptografiaException {

		if (parametro == null) {
			return null;
		}

		char[] c = parametro.toCharArray();
		int x = 0;
		for (int i = 0; i < c.length; i++) {
			x = c[i] + 2;
			c[i] = (char) x;
		}

		return String.valueOf(c);
	}

	@Override
	public String decriptografar(String parametro) throws CriptografiaException {
		char[] c = parametro.toCharArray();
		int x = 0;
		for (int i = 0; i < c.length; i++) {
			x = c[i] - 2;
			c[i] = (char) x;
		}

		return String.valueOf(c);

	}

	@PostConstruct
	public void init() {
		usuarioDAO = new UsuarioDAO(entityManager);
	}
}
