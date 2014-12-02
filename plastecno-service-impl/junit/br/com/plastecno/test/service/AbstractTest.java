package br.com.plastecno.test.service;

import org.junit.Assert;

import br.com.plastecno.service.exception.BusinessException;

class AbstractTest {

	void printMensagens(BusinessException exception) {
		Assert.fail("Falha em alguma regra de negocio. As mensagens sao: "+exception.getMensagemConcatenada());
	}

}
