package br.com.plastecno.service.test;

import javax.naming.NamingException;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.junit.Before;
import org.junit.Test;

import br.com.plastecno.service.PedidoService;
import br.com.plastecno.service.entity.Pais;

public class Teste {
	private EntityManagerFactory emf;
	private EntityManager em;

	private PedidoService pedidoService;

	public void startConnection() throws NamingException {

	}

	@Before
	public void init() {
		emf = Persistence.createEntityManagerFactory("junit");
		em = emf.createEntityManager();
		em.getTransaction().begin();
		em.persist(new Pais());
		em.getTransaction().commit();
	}

	@Test
	public void test() throws NamingException {
		startConnection();
		System.out.println("Encontrou: " + pedidoService != null);
	}
}
