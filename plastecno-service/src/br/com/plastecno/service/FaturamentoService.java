package br.com.plastecno.service;

import javax.ejb.Local;

import br.com.plastecno.service.exception.BusinessException;
import br.com.plastecno.service.wrapper.FluxoCaixa;
import br.com.plastecno.service.wrapper.Periodo;

@Local
public interface FaturamentoService {

	FluxoCaixa gerarFluxoFaixaByPeriodo(Periodo periodo) throws BusinessException;

}
