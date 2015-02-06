package br.com.plastecno.service.test;

import org.junit.Assert;
import org.junit.Before;

import br.com.plastecno.service.exception.BusinessException;

public abstract class AbstractTest {

	protected EntidadeRepository repositorio = EntidadeRepository.getInstance();
	protected EntidadeBuilder gerador = EntidadeBuilder.getInstance();

	@Before
	public void clean() {
		repositorio.init();
		repositorio.clear();
		init();
	}

	public abstract void init();

	void printMensagens(BusinessException exception) {
		Assert.fail("Falha em alguma regra de negocio. As mensagens sao: " + exception.getMensagemConcatenada());
	}

}
