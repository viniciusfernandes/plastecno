package br.com.svr.service.test;

import java.io.FileNotFoundException;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;

import br.com.svr.service.exception.BusinessException;
import br.com.svr.service.test.builder.EntidadeBuilder;
import br.com.svr.service.test.builder.ServiceBuilder;

public abstract class AbstractTest {
	protected static EntityManager em;
	protected static EntityManagerFactory emf;

	//@AfterClass
	public static void close() {
		if (em != null) {
			em.clear();
			em.close();
		}
		if (emf != null) {
			emf.close();
		}
	}

	@BeforeClass
	public static void init() throws FileNotFoundException {
		try {
			emf = Persistence.createEntityManagerFactory("svr");

		} catch (Throwable e) {
			e.printStackTrace();
		}
		em = emf.createEntityManager();
		ServiceBuilder.config(em);
	}

	protected EntidadeBuilder eBuilder = EntidadeBuilder.getInstance();

	@Before
	public void clear() {
		em.clear();
	}

	protected void printMensagens(BusinessException exception) {
		Assert.fail("Falha em alguma regra de negocio. As mensagens sao: " + exception.getMensagemConcatenada());
	}

}
