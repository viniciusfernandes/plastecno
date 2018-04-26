package br.com.svr.service.message;

import javax.ejb.Local;

@Local
public interface AlteracaoIndicesNegociacaoPublisher {
	public void publicar(Integer idCliente, Double indiceQuantidade, Double indiceValor);
}
