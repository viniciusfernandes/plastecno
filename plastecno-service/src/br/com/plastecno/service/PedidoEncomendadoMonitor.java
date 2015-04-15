package br.com.plastecno.service;

import javax.ejb.Local;

@Local
public interface PedidoEncomendadoMonitor {
	void reservarItemPedidoEncomendadoEstoque();
}
