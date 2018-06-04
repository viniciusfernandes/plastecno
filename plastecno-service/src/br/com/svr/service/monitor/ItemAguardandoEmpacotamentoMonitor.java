package br.com.svr.service.monitor;

import java.util.Collection;

import javax.ejb.Local;

@Local
public interface ItemAguardandoEmpacotamentoMonitor {
	Collection<Integer> monitorarItemPedidoAguardandoMaterial();

	Collection<Integer> monitorarItemPedidoAguardandoCompra();
}
