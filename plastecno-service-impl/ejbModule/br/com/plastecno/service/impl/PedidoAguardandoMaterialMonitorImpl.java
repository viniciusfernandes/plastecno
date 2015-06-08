package br.com.plastecno.service.impl;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.ConcurrencyManagement;
import javax.ejb.ConcurrencyManagementType;
import javax.ejb.EJB;
import javax.ejb.Schedule;
import javax.ejb.Singleton;
import javax.ejb.Timeout;
import javax.ejb.Timer;

import br.com.plastecno.service.PedidoAguardandoMaterialMonitor;
import br.com.plastecno.service.PedidoService;
import br.com.plastecno.service.exception.BusinessException;

@Singleton
@ConcurrencyManagement(ConcurrencyManagementType.CONTAINER)
public class PedidoAguardandoMaterialMonitorImpl implements PedidoAguardandoMaterialMonitor {

	private Logger logger = Logger.getLogger(PedidoAguardandoMaterialMonitorImpl.class.getName());

	@EJB
	private PedidoService pedidoService;

	@Timeout
	public void init(Timer timer) {
		logger.info("Inicializando o monitoramento dos itens de pedidos encomendados aguardando reserva");
	}

	@Schedule(hour = "*/1")
	public void reservarItemPedidoAguardandoMaterial() {
		List<Integer> listaIdPedido = pedidoService.pesquisarIdPedidoAguardandoMaterial();
		boolean empacotamentoOk = false;
		for (Integer idPedido : listaIdPedido) {
			try {
				// Encomendado mesmo apos o processamento do agendamento, caso contrario
				// teremos inconsistencia no estado do pedido podendo retornar ao fluxo.
				// Aqui estamos garantindo que mesmo que o pedido permaneca como
				// revenda aguardando encomenda, mas ele ja passou por essa etapa.
				empacotamentoOk = pedidoService.empacotarItemAguardandoMaterial(idPedido);
				logger.info("Monitor de itens aguardando material disparou a reserva dos itens do pedido No. " + idPedido
						+ ". Resultado: " + (empacotamentoOk ? "PRONTO PARA EMPACOTAR" : "ALGUM ITEM NAO EXISTE NO ESTOQUE"));
			} catch (BusinessException e) {
				logger.log(Level.SEVERE, "Falha no processamento e reenvio do pedido No. " + idPedido + ". Possivel causa: "
						+ e.getMensagemConcatenada());
			}
		}
	}
}