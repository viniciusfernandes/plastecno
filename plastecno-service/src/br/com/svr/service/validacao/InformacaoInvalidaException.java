package br.com.svr.service.validacao;

import java.util.List;

import br.com.svr.service.exception.BusinessException;

public class InformacaoInvalidaException extends BusinessException {
	
	private static final long serialVersionUID = 6100470489247693591L;
	
	public InformacaoInvalidaException(final List<String> listaMensagem) {
		super(listaMensagem);
	}
	
	public InformacaoInvalidaException(final String mensagem) {
		super(mensagem);
	}
	
	public InformacaoInvalidaException(final String mensagem, final Throwable causa) {
		super(mensagem, causa);
	}
}
