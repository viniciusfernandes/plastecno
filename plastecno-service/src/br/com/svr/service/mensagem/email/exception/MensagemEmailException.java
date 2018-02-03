package br.com.svr.service.mensagem.email.exception;

import br.com.svr.service.exception.BusinessException;

public class MensagemEmailException extends BusinessException {
	/**
	 * 
	 */
	private static final long serialVersionUID = -4359571260593668270L;

	public MensagemEmailException(final String mensagem) {
		super(mensagem);
	}
	
	public MensagemEmailException(final String mensagem, Throwable causa) {
		super(mensagem, causa);
	}
}
