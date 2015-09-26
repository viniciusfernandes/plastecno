package br.com.plastecno.message.impl;

import javax.annotation.Resource;
import javax.ejb.Stateless;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.ObjectMessage;
import javax.jms.Queue;
import javax.jms.Session;

import br.com.plastecno.message.AlteracaoEstoquePublisher;

@Stateless
public class AlteracaoEstoquePublisherImpl implements AlteracaoEstoquePublisher {

	@Resource(mappedName = "java:/queue/vendas/alteracaoestoque")
	private Queue alteracaoEstoqueQueue;

	@Resource(mappedName = "java:/ConnectionFactory")
	private ConnectionFactory connectionFactory;

	@Override
	public void publicar() {
		Connection connection = null;
		try {
			connection = connectionFactory.createConnection();
			Session sessao = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

			ObjectMessage mensagem = sessao.createObjectMessage();
			MessageProducer messageProducer = sessao.createProducer(alteracaoEstoqueQueue);
			messageProducer.send(mensagem);

		} catch (JMSException e) {
			throw new IllegalStateException(
					"Nao foi possivel publicar a mensagem de alteracao de estoque para o servico de mensagens", e);
		} finally {
			try {
				if (connection != null) {
					connection.close();
				}

			} catch (JMSException e) {
				throw new IllegalStateException("Falha ao fechar a conexao JMS da mensagem de alteracao de estoque", e);
			}
		}
	}

}
