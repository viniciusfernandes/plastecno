package br.com.plastecno.service.test;

import java.lang.reflect.Method;

import mockit.Mock;
import mockit.MockUp;

import org.junit.Assert;
import org.junit.Before;

import br.com.plastecno.service.dao.GenericDAO;
import br.com.plastecno.service.exception.BusinessException;

class AbstractTest {

	RepositorioEntidade repositorio = RepositorioEntidade.getInstance();
	GeradorEntidade gerador = GeradorEntidade.getInstance();

	private Integer gerarId() {
		return (int) (9999 * Math.random());
	}

	@Before
	public void init() {
		initGenericDAO();
		repositorio.clear();

	}

	void initGenericDAO() {
		new MockUp<GenericDAO<Object>>() {

			@Mock
			Object alterar(Object t) {
				repositorio.inserirEntidade(t);
				return t;
			}

			@Mock
			Object inserir(Object t) {
				try {
					Method m = t.getClass().getMethod("setId", Integer.class);
					m.invoke(t, gerarId());
				} catch (Exception e) {
					throw new IllegalArgumentException(e);
				}
				repositorio.inserirEntidade(t);
				return t;
			}

			@Mock
			<T> boolean isEntidadeExistente(Class<T> classe, String nomeAtributo, Object valorAtributo,
					Object nomeIdEntidade, Object valorIdEntidade) {
				return repositorio.contemEntidade(classe, nomeAtributo, valorAtributo, valorIdEntidade);
			}
		};

	}

	void printMensagens(BusinessException exception) {
		Assert.fail("Falha em alguma regra de negocio. As mensagens sao: " + exception.getMensagemConcatenada());
	}

}
