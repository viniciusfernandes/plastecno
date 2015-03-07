package br.com.plastecno.service;

import javax.ejb.Local;

import br.com.plastecno.service.exception.BusinessException;

@Local
public interface PedidoEncomendadoSchedule {
	void reservarItemPedidoEncomendadoEstoque() throws BusinessException;
}
