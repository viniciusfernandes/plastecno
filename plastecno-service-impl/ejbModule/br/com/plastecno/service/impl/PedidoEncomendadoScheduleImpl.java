package br.com.plastecno.service.impl;

import java.util.List;
import java.util.logging.Logger;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.Schedule;
import javax.ejb.SessionContext;
import javax.ejb.Singleton;
import javax.ejb.Timeout;
import javax.ejb.Timer;
import javax.ejb.TimerService;

import br.com.plastecno.service.EstoqueService;
import br.com.plastecno.service.PedidoEncomendadoSchedule;
import br.com.plastecno.service.PedidoService;
import br.com.plastecno.service.exception.BusinessException;

@Singleton
public class PedidoEncomendadoScheduleImpl implements PedidoEncomendadoSchedule {
	@EJB
	private EstoqueService estoqueService;

	private Logger logger = Logger.getLogger(PedidoEncomendadoScheduleImpl.class.getName());

	@EJB
	private PedidoService pedidoService;

	@Resource
	private SessionContext sessionContext;
	@Resource
	private TimerService timerService;

	@Timeout
	public void init(Timer timer) {
		logger.info("Inicializando o monitoramento dos itens de pedidos encomendados aguardando reserva");
	}

	@Schedule(second = "*/60", minute = "*", hour = "*")
	public void reservarItemPedidoEncomendadoEstoque() throws BusinessException {
		List<Integer> listaItem = pedidoService.pesquisarIdPedidoRevendaEncomendada();
		boolean todosItensReservados = false;
		for (Integer idPedido : listaItem) {
			todosItensReservados = estoqueService.reservarItemPedido(idPedido);
			// encomendado mesmo apos o processamento do agendamento, caso contrario
			// teremos inconsistencia no estado do pedido podendo retornar ao fluxo de
			// Aqui estamos garantindo que mesmo que o pedido permaneca como
			// revenda pendente de encomenda, mas ele ja passou por essa etapa.
			if (!todosItensReservados) {
				pedidoService.alterarSituacaoPedidoEncomendadoByIdPedido(idPedido);
			}
			logger.info("Monitor de itens de pedido encomendados disparou a reserva dos itens do pedido No. " + idPedido
					+ ". Resultado: " + (todosItensReservados ? "PRONTO PARA EMPACOTAR" : "ALGUM ITEM NAO EXISTE NO ESTOQUE"));
		}
	}
}
