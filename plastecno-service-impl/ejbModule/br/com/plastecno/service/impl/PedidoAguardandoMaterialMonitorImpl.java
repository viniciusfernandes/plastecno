package br.com.plastecno.service.impl;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.ConcurrencyManagement;
import javax.ejb.ConcurrencyManagementType;
import javax.ejb.EJB;
import javax.ejb.Schedule;
import javax.ejb.Singleton;

import br.com.plastecno.service.PedidoAguardandoMaterialMonitor;
import br.com.plastecno.service.PedidoService;
import br.com.plastecno.service.exception.BusinessException;

@Singleton
@ConcurrencyManagement(ConcurrencyManagementType.CONTAINER)
public class PedidoAguardandoMaterialMonitorImpl implements PedidoAguardandoMaterialMonitor {

	private Logger logger = Logger.getLogger(PedidoAguardandoMaterialMonitorImpl.class.getName());

	@EJB
	private PedidoService pedidoService;

	@Schedule(hour = "*/1")
	public Collection<Integer> reservarItemPedidoAguardandoMaterial() {
		List<Integer> listaIdPedido = pedidoService.pesquisarIdPedidoAguardandoMaterial();
		boolean empacotamentoOk = false;
		// Listas utilizadas para logar o que foi enviado para o empacotamento.
		Set<Integer> empacotados = new TreeSet<Integer>();
		Set<Integer> naoEmpacotados = new TreeSet<Integer>();
		for (Integer idPedido : listaIdPedido) {
			try {
				// Encomendado mesmo apos o processamento do agendamento, caso contrario
				// teremos inconsistencia no estado do pedido podendo retornar ao fluxo.
				// Aqui estamos garantindo que mesmo que o pedido permaneca como
				// revenda aguardando encomenda, mas ele ja passou por essa etapa.
				empacotamentoOk = pedidoService.empacotarItemAguardandoMaterial(idPedido);
				if (empacotamentoOk) {
					empacotados.add(idPedido);
				} else {
					naoEmpacotados.add(idPedido);
				}

			} catch (BusinessException e) {
				logger.log(Level.SEVERE, "Falha no processamento e reenvio do pedido No. " + idPedido + ". Possivel causa: "
						+ e.getMensagemConcatenada());
			}
		}
		logger.info("Monitor enviou para o empacotamento os pedidos No.: " + Arrays.deepToString(empacotados.toArray()));
		logger.info("Monitor nao encontrou itens para empacotar dos pedidos No.: "
				+ Arrays.deepToString(naoEmpacotados.toArray()));
		return empacotados;
	}
}
