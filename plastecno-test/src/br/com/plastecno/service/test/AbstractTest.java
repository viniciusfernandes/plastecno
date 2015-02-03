package br.com.plastecno.service.test;

import org.junit.Assert;
import org.junit.Before;

import br.com.plastecno.service.exception.BusinessException;

class AbstractTest {

	RepositorioEntidade repositorio = RepositorioEntidade.getInstance();
	GeradorEntidade gerador = GeradorEntidade.getInstance();

	@Before
	public void init() {
		repositorio.init();
		repositorio.clear();
		}

	void printMensagens(BusinessException exception) {
		Assert.fail("Falha em alguma regra de negocio. As mensagens sao: " + exception.getMensagemConcatenada());
	}

}
