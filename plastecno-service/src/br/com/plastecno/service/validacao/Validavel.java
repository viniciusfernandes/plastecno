package br.com.plastecno.service.validacao;

import br.com.plastecno.service.exception.BusinessException;

public interface Validavel {
	void validar() throws BusinessException;
}
