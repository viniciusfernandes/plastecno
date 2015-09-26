package br.plastecno.listener;

import javax.ejb.ActivationConfigProperty;
import javax.ejb.EJB;
import javax.ejb.MessageDriven;
import javax.jms.Message;
import javax.jms.MessageListener;

import br.com.plastecno.service.ItemAguardandoEmpacotamentoMonitor;

@MessageDriven(name = "AlteracaoEstoqueListener", activationConfig = {
		@ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue"),
		@ActivationConfigProperty(propertyName = "destination", propertyValue = "java:/queue/vendas/alteracaoestoque"),
		@ActivationConfigProperty(propertyName = "acknowledgeMode", propertyValue = "Auto-acknowledge") })
public class AlteracaoEstoqueListener implements MessageListener {

	@EJB
	private ItemAguardandoEmpacotamentoMonitor pedidoAguardandoMaterialMonitor;

	@Override
	public void onMessage(Message mensagem) {
		pedidoAguardandoMaterialMonitor.monitorarItemPedidoAguardandoMaterial();
	}

}
