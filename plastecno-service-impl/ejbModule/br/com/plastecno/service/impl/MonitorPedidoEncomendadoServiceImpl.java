package br.com.plastecno.service.impl;

import java.util.List;
import java.util.logging.Logger;

import javax.ejb.ConcurrencyManagement;
import javax.ejb.ConcurrencyManagementType;
import javax.ejb.EJB;
import javax.ejb.Schedule;
import javax.ejb.Singleton;
import javax.ejb.Timeout;
import javax.ejb.Timer;

import br.com.plastecno.service.MonitorPedidoEncomendadoService;
import br.com.plastecno.service.PedidoService;
import br.com.plastecno.service.exception.BusinessException;

@Singleton
@ConcurrencyManagement(ConcurrencyManagementType.CONTAINER)
public class MonitorPedidoEncomendadoServiceImpl implements MonitorPedidoEncomendadoService {

	private Logger logger = Logger.getLogger(MonitorPedidoEncomendadoServiceImpl.class.getName());

	@EJB
	private PedidoService pedidoService;

	@Timeout
	public void init(Timer timer) {
		logger.info("Inicializando o monitoramento dos itens de pedidos encomendados aguardando reserva");
	}

	@Schedule(minute = "*/60", hour = "*")
	public void reservarItemPedidoEncomendadoEstoque() throws BusinessException {
		List<Integer> listaIdPedido = pedidoService.pesquisarIdPedidoRevendaEncomendada();
		logger.info("Monitor de itens de pedido encomendados disparou a reserva dos itens. Existem pedidos: "
				+ (listaIdPedido.isEmpty()));
		boolean empacotamentoOk = false;
		for (Integer idPedido : listaIdPedido) {
			// Encomendado mesmo apos o processamento do agendamento, caso contrario
			// teremos inconsistencia no estado do pedido podendo retornar ao fluxo de
			// Aqui estamos garantindo que mesmo que o pedido permaneca como
			// revenda aguardando encomenda, mas ele ja passou por essa etapa.
			empacotamentoOk = pedidoService.enviarRevendaEncomendadaEmpacotamento(idPedido);
			logger.info("Monitor de itens de pedido encomendados disparou a reserva dos itens do pedido No. " + idPedido
					+ ". Resultado: " + (empacotamentoOk ? "PRONTO PARA EMPACOTAR" : "ALGUM ITEM NAO EXISTE NO ESTOQUE"));
		}
	}
}
