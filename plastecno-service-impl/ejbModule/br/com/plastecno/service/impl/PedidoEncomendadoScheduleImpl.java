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
	@Resource
	private SessionContext sessionContext;

	@EJB
	private PedidoService pedidoService;

	@EJB
	private EstoqueService estoqueService;

	@Resource
	private TimerService timerService;
	private Logger logger = Logger.getLogger(PedidoEncomendadoScheduleImpl.class.getName());

	@Timeout
	public void init(Timer timer) {
		logger.info("Inicializando o monitoramento dos itens de pedidos encomendados aguardando reserva");
	}

	@Schedule(second = "*/300", minute = "*", hour = "*")
	public void reservarItemPedidoEncomendadoEstoque() throws BusinessException {
		List<Integer> listaItem = pedidoService.pesquisarIdPedidoRevendaPendenteEncomenda();
		boolean reservado = false;
		for (Integer idPedido : listaItem) {
			reservado = estoqueService.reservarItemPedido(idPedido);
			logger.info("Monitor de itens de pedido encomendados disparou a reserva dos itens do pedido No. " + idPedido
					+ ". Resultado: " + (reservado ? "PRONTO PARA EMPACOTAR" : "ALGUM ITEM NAO EXISTE NO ESTOQUE"));
		}
	}
}
