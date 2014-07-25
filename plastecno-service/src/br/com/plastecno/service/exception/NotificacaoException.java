package br.com.plastecno.service.exception;

public class NotificacaoException extends BusinessException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3573345415069699477L;
	
	public NotificacaoException(String mensagem) {
		super(mensagem);
	}
	
	public NotificacaoException(String mensagem, Throwable causa) {
		super(mensagem, causa);
	}
}
