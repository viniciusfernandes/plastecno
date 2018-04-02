package br.com.svr.service.message.impl;

import javax.annotation.Resource;
import javax.ejb.Stateless;
import javax.jms.Queue;

import br.com.svr.service.message.AlteracaoEstoquePublisher;

@Stateless
public class AlteracaoEstoquePublisherImpl extends AbstractPublisher implements AlteracaoEstoquePublisher {

	@Resource(mappedName = "java:/queue/vendas/AlteracaoEstoque", name = "AlteracaoEstoque")
	private Queue queue;

	@Override
	public void publicar() {
		super.publicar(queue);
	}

}
