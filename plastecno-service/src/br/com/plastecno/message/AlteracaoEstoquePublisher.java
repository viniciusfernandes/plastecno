package br.com.plastecno.message;

import javax.ejb.Local;

@Local
public interface AlteracaoEstoquePublisher {
	public void publicar();
}
