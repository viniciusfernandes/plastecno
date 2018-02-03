package br.com.svr.service.impl.calculo.exception;

import br.com.svr.service.calculo.exception.AlgoritmoCalculoException;

public class VolumeInvalidoException extends AlgoritmoCalculoException {
	/**
	 * 
	 */
	private static final long serialVersionUID = -4559066844378476634L;
	
	public VolumeInvalidoException(String mensagem) {
		super(mensagem);
	}
}
