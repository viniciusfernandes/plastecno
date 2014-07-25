package br.com.plastecno.service.exception;

public class AutenticacaoException extends BusinessException {
	/**
	 * 
	 */
	private static final long serialVersionUID = -215183006793982658L;

	public AutenticacaoException(String mensagem, Throwable causa) {
		super(mensagem, causa);
	}
}
