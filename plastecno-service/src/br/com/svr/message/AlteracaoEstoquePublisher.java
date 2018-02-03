package br.com.svr.message;

import javax.ejb.Local;

@Local
public interface AlteracaoEstoquePublisher {
	public void publicar();
}
