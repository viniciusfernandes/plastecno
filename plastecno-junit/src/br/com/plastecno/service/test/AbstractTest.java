package br.com.plastecno.service.test;

import org.junit.Assert;
import org.junit.Before;

import br.com.plastecno.service.exception.BusinessException;
import br.com.plastecno.service.test.builder.EntidadeBuilder;
import br.com.plastecno.service.test.builder.EntidadeRepository;

public abstract class AbstractTest {

	protected EntidadeBuilder eBuilder = EntidadeBuilder.getInstance();
	private final EntidadeRepository repositorio = EntidadeRepository.getInstance();

	@Before
	public void clear() {
		repositorio.clear();
	}

	protected void printMensagens(BusinessException exception) {
		Assert.fail("Falha em alguma regra de negocio. As mensagens sao: " + exception.getMensagemConcatenada());
	}

}
