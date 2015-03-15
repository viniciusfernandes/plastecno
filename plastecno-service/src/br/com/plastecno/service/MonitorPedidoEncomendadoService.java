package br.com.plastecno.service;

import javax.ejb.Local;

import br.com.plastecno.service.exception.BusinessException;

@Local
public interface MonitorPedidoEncomendadoService {
	void reservarItemPedidoEncomendadoEstoque() throws BusinessException;
}
