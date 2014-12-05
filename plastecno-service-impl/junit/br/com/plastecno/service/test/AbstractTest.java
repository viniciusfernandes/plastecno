package br.com.plastecno.service.test;

import org.junit.Assert;

import br.com.plastecno.service.exception.BusinessException;

public class AbstractTest {
	void printMensagens(BusinessException exception) {
		Assert.fail("Falha em alguma regra de negocio. As mensagens sao: " + exception.getMensagemConcatenada());
	}
}
