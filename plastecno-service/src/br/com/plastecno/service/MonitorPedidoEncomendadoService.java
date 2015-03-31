package br.com.plastecno.service;

import javax.ejb.Local;

@Local
public interface MonitorPedidoEncomendadoService {
	void reservarItemPedidoEncomendadoEstoque();
}
