package br.com.plastecno.service.test;

import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;

import br.com.plastecno.service.exception.BusinessException;

public class AbstractTest {
	private final Map<Class<?>, Object> services = new HashMap<Class<?>, Object>();

	public void addService(Class<?> type, Object serviceObj) {
		services.put(type, serviceObj);
	}

	public Object getService(Class<?> type) {
		return services.get(type);
	}

	@Before
	public void init() {

	}

	void printMensagens(BusinessException exception) {
		Assert.fail("Falha em alguma regra de negocio. As mensagens sao: " + exception.getMensagemConcatenada());
	}

}
