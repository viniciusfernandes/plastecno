package br.com.svr.service;

import javax.ejb.Local;

import br.com.svr.service.exception.BusinessException;
import br.com.svr.service.wrapper.FluxoCaixa;
import br.com.svr.service.wrapper.Periodo;

@Local
public interface FaturamentoService {

	FluxoCaixa gerarFluxoFaixaByPeriodo(Periodo periodo) throws BusinessException;

}
