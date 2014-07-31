package br.com.plastecno.service.impl.calculo.exception;

import br.com.plastecno.service.calculo.exception.AlgoritmoCalculoException;

public class VolumeInvalidoException extends AlgoritmoCalculoException {
	/**
	 * 
	 */
	private static final long serialVersionUID = -4559066844378476634L;
	
	public VolumeInvalidoException(String mensagem) {
		super(mensagem);
	}
}
