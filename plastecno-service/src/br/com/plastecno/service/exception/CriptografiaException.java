package br.com.plastecno.service.exception;

public class CriptografiaException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7942880973308928897L;
	public CriptografiaException(String mensagem, Throwable causa) {
		super(mensagem, causa);
	}
}
