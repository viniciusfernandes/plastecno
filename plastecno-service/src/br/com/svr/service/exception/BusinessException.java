package br.com.svr.service.exception;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.ApplicationException;

@ApplicationException(rollback = true, inherited = true)
public class BusinessException extends Exception {

	private static final long serialVersionUID = 4002460058473505938L;

	private List<String> listaMensagem;

	public BusinessException() {
		listaMensagem = new ArrayList<String>();
	}

	public BusinessException(final List<String> listaMensagem) {
		this.listaMensagem = listaMensagem;
	}

	public BusinessException(String mensagem) {
		super(mensagem);
		this.listaMensagem = new ArrayList<String>();
		this.listaMensagem.add(mensagem);
	}

	public BusinessException(String mensagem, Throwable causa) {
		super(mensagem, causa);
		this.listaMensagem = new ArrayList<String>();
		this.listaMensagem.add(mensagem);
	}

	public BusinessException addMensagem(List<String> listaMensagem) {
		this.listaMensagem.addAll(listaMensagem);
		return this;
	}

	public BusinessException addMensagem(String mensagem) {
		this.listaMensagem.add(mensagem);
		return this;
	}

	public boolean contemExceptionPropagada() {
		return getCause() != null;
	}

	public boolean contemMensagem() {
		return !this.listaMensagem.isEmpty();
	}

	private String gerarMensagemString(String separador) {
		final StringBuilder mensagem = new StringBuilder();
		for (String mesagem : this.listaMensagem) {
			mensagem.append(mesagem).append(separador);
		}
		return mensagem.toString();
	}

	public List<String> getListaMensagem() {
		return this.listaMensagem;
	}

	public String getMensagemConcatenada() {
		return gerarMensagemString(". ");
	}

	public String getMensagemEmpilhada() {
		return gerarMensagemString("\n");
	}
}
