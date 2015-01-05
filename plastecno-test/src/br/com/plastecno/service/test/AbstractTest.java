package br.com.plastecno.service.test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Assert;

import br.com.plastecno.service.exception.BusinessException;

public class AbstractTest {
	private final Map<Class<?>, List<Object>> entidades = new HashMap<Class<?>, List<Object>>();

	void inserirEntidade(Object entidade) {
		if (!entidades.containsKey(entidade.getClass())) {
			entidades.put(entidade.getClass(), new ArrayList<Object>());
		}
		entidades.get(entidade.getClass()).add(entidade);
	}

	@SuppressWarnings("unchecked")
	<T> T pesquisarEntidadeById(Class<T> classe, Integer Id) {
		if (!entidades.containsKey(classe)) {
			return null;
		}
		Integer idObj = null;
		for (Object o : entidades.get(classe)) {
			try {
				idObj = (Integer) o.getClass().getMethod("getId", (Class[]) null).invoke(o, (Object[]) null);
			} catch (Exception e) {
				throw new IllegalStateException(e);
			}
			if (idObj != null) {
				return (T) o;
			}
		}
		return null;
	}

	void printMensagens(BusinessException exception) {
		Assert.fail("Falha em alguma regra de negocio. As mensagens sao: " + exception.getMensagemConcatenada());
	}

}
