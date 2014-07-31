package br.com.plastecno.service.impl;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import br.com.plastecno.service.AutenticacaoService;
import br.com.plastecno.service.entity.Usuario;
import br.com.plastecno.service.exception.AutenticacaoException;
import br.com.plastecno.service.exception.CriptografiaException;
import br.com.plastecno.service.impl.util.QueryUtil;

@Stateless
public class AutenticacaoServiceImpl implements AutenticacaoService {

	@PersistenceContext(name="plastecno")
	private EntityManager entityManager;
			
	@Override
	public Usuario autenticar(String email, String senha) throws AutenticacaoException {
		try {
			senha = this.criptografar(senha);
		} catch (CriptografiaException e) {
			throw new AutenticacaoException("Falha na autenticacao do usuario com login "+email+". Veja o log para mais detalhes", e); 
		}
		Query query = this.entityManager.createQuery(
				"select u from Usuario u left join fetch u.listaPerfilAcesso where u.email = :email and u.senha = :senha")
				.setParameter("email", email)
				.setParameter("senha", senha);
		return QueryUtil.gerarRegistroUnico(query, Usuario.class, null);
	}
	
	@Override
	public String criptografar(String parametro) throws CriptografiaException  {
		
		if (parametro == null) {
			return null;
		}
		
		char[] c = parametro.toCharArray();
		int x = 0;
		for (int i = 0; i < c.length ; i++) {
			x = c[i]+2;
			c[i] = (char)x;
		}
		
		return String.valueOf(c);
	}
	
	@Override
	public String decriptografar(String parametro) throws CriptografiaException  {
		char[] c = parametro.toCharArray();
		int x = 0;
		for (int i = 0; i < c.length ; i++) {
			x = c[i]-2;
			c[i] = (char)x;
		}
		
		return String.valueOf(c);
        
	}
}
