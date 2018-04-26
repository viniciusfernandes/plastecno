package br.com.svr.service.message.impl;

import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.Resource;
import javax.ejb.Stateless;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.ObjectMessage;
import javax.jms.Queue;
import javax.jms.Session;

@Stateless
public class AbstractPublisher {
	@Resource(mappedName = "java:/ConnectionFactory")
	private ConnectionFactory connectionFactory;

	public void publicar(Queue queue) {
		this.publicar(queue, null);
	}

	public void publicar(Queue queue, Map<String, Object> mapaParametros) {
		Connection connection = null;
		try {

			connection = connectionFactory.createConnection();
			Session sessao = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

			ObjectMessage mensagem = sessao.createObjectMessage();
			if (mapaParametros != null && mapaParametros.size() > 0) {
				for (Entry<String, Object> e : mapaParametros.entrySet()) {
					// Recuperando o nome e o valor do parametro.
					mensagem.setObjectProperty(e.getKey(), e.getValue());
				}
			}

			MessageProducer messageProducer = sessao.createProducer(queue);
			messageProducer.send(mensagem);

		} catch (JMSException e) {
			try {
				throw new IllegalStateException(
						"Nao foi possivel publicar a mensagem no servico de mensageria. Nome da fila eh: "
								+ queue.getQueueName(), e);
			} catch (JMSException e1) {
				throw new IllegalStateException(
						"Falha ao tentar recuperar o nome da fila em que a mensagem foi publicada", e);
			}
		} finally {
			try {
				if (connection != null) {
					connection.close();
				}

			} catch (JMSException e) {
				try {
					throw new IllegalStateException(
							"Falha ao fechar a conexao JMS da mensagem publicada no servico de mensageria. Nome da fila eh: "
									+ queue.getQueueName(), e);
				} catch (JMSException e1) {
					throw new IllegalStateException(
							"Falha ao tentar recuperar o nome da fila no fechamento da conexao em que a mensagem foi publicada",
							e);
				}
			}
		}
	}
}
