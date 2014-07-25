package br.com.plastecno.service;

import javax.ejb.Local;

import br.com.plastecno.service.exception.BusinessException;

@Local
public interface QueryNativaService {
    String executar(String query) throws BusinessException;
}
