package br.com.plastecno.service.test;

import java.util.Properties;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import br.com.plastecno.service.PedidoService;
import br.com.plastecno.service.entity.Pedido;
import br.com.plastecno.service.exception.BusinessException;

public class AbstractTest {
	Context context = null;

	@Before
	public void init() throws NamingException {
		Properties p = new Properties();
		p.put(Context.INITIAL_CONTEXT_FACTORY,
				"org.apache.openejb.client.LocalInitialContextFactory");
		p.put("plastecno", "new://Resource?type=DataSource");
		p.put("plastecno.JdbcDriver", "org.hsqldb.jdbcDriver");
		p.put("plastecno.JdbcUrl", "jdbc:hsqldb:mem:moviedb");

		p.put("plastecnoUnmanaged", "new://Resource?type=DataSource");
		p.put("plastecnoUnmanaged.JdbcDriver", "org.hsqldb.jdbcDriver");
		p.put("plastecnoUnmanaged.JdbcUrl", "jdbc:hsqldb:mem:moviedb");
		p.put("plastecnoUnmanaged.JtaManaged", "false");

		context = new InitialContext(p);
	}

	@Test
	public void testInclusaoPedido() throws NamingException {
		PedidoService pedidoService = (PedidoService) context
				.lookup("PedidoServiceImplLocal");
		try {
			pedidoService.inserir(new Pedido());
		} catch (BusinessException e) {
			printBusinessException(e);
			Assert.fail();
		}
	}
	
	void printBusinessException(BusinessException e){
		System.out.println("--------- FALHA DE REGRA DE NEGOCIOS ----------");
		System.out.println(e.getMensagemEmpilhada());
		System.out.println("-----------------------------------------------");
	}
}
