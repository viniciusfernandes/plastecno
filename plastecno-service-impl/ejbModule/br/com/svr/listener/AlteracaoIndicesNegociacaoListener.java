package br.com.svr.listener;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.ActivationConfigProperty;
import javax.ejb.EJB;
import javax.ejb.MessageDriven;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;

import br.com.svr.service.NegociacaoService;

@MessageDriven(name = "AlteracaoIndicesNegociacaoListener", activationConfig = {
		@ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue"),
		@ActivationConfigProperty(propertyName = "destination", propertyValue = "java:/queue/vendas/AlteracaoIndicesNegociacao"),
		@ActivationConfigProperty(propertyName = "acknowledgeMode", propertyValue = "Auto-acknowledge") })
public class AlteracaoIndicesNegociacaoListener implements MessageListener {

	private Logger log = Logger.getLogger(this.getClass().getName());

	@EJB
	private NegociacaoService negociacaoService;

	@Override
	public void onMessage(Message mensagem) {
		Double indiceValor = null;
		Integer idCliente = null;
		Double indiceQuantidade = null;
		try {
			idCliente = (Integer) mensagem.getObjectProperty("idCliente");
		} catch (JMSException e) {
			log.log(Level.SEVERE, "Falha na recuperacao do id do cliente para alterar os indices das negociacoes", e);
		}
		try {
			indiceValor = (Double) mensagem.getObjectProperty("indiceValor");
		} catch (JMSException e) {
			log.log(Level.SEVERE, "Falha na recuperacao do indice de valor para alterar os indices das negociacoes", e);
		}
		try {
			indiceQuantidade = (Double) mensagem.getObjectProperty("indiceQuantidade");
		} catch (JMSException e) {
			log.log(Level.SEVERE,
					"Falha na recuperacao do indice de quantidade para alterar os indices das negociacoes", e);
		}

		if (idCliente != null && indiceQuantidade != null && indiceValor != null) {
			negociacaoService.alterarNegociacaoAbertaIndiceConversaoValorByIdCliente(idCliente, indiceQuantidade,
					indiceValor);
		} else {
			log.warning("A alteracao dos indices das negociacoes nao foi possivel. Os parametros sao: idCliente="
					+ idCliente + "; indiceQuantidade=" + indiceQuantidade + "; indiceValor=" + indiceValor);
		}
	}

}
