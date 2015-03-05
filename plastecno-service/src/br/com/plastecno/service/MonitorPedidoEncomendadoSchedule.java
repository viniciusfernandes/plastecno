package br.com.plastecno.service;

import javax.ejb.Local;

import br.com.plastecno.service.exception.BusinessException;

@Local
public interface MonitorPedidoEncomendadoSchedule {
	void reservarItemPedidoEncomendadoEstoque() throws BusinessException;
}
