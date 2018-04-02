package br.com.svr.service.message;

import javax.ejb.Local;

@Local
public interface AlteracaoEstoquePublisher {
	public void publicar();
}
