package br.com.plastecno.service.test;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.junit.Test;

public class Teste {
	private EntityManagerFactory emf;
	private EntityManager em;

	public void startConnection() {
		emf = Persistence.createEntityManagerFactory("junit");
		em = emf.createEntityManager();
		em.getTransaction().begin();
	}

	@Test
	public void test() {
		startConnection();
	}
}
