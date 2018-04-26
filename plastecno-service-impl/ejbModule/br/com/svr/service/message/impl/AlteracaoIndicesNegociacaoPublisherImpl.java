package br.com.svr.service.message.impl;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.ejb.Stateless;
import javax.jms.ConnectionFactory;
import javax.jms.Queue;

import br.com.svr.service.message.AlteracaoIndicesNegociacaoPublisher;

@Stateless
public class AlteracaoIndicesNegociacaoPublisherImpl extends AbstractPublisher implements
		AlteracaoIndicesNegociacaoPublisher {

	@Resource(mappedName = "java:/ConnectionFactory")
	private ConnectionFactory connectionFactory;

	@Resource(mappedName = "java:/queue/vendas/AlteracaoIndicesNegociacao", name = "AlteracaoIndicesNegociacao")
	private Queue queue;

	public void publicar(Integer idCliente, Double indiceQuantidade, Double indiceValor) {
		Map<String, Object> parametros = new HashMap<>();
		parametros.put("idCliente", idCliente);
		parametros.put("indiceQuantidade", indiceQuantidade);
		parametros.put("indiceValor", indiceValor);
		super.publicar(queue, parametros);
	}

}
