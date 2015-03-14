package br.com.plastecno.service.impl.calculo.exception;

import br.com.plastecno.service.exception.BusinessException;

public class RecorteInvalidoException extends BusinessException{
	/**
	 * 
	 */
	private static final long serialVersionUID = 6210744726329525585L;

	public RecorteInvalidoException(String mensagem) {
		super(mensagem);
	}
}
