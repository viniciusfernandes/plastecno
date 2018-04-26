package br.com.svr.service;

import javax.ejb.Local;

import br.com.svr.service.exception.BusinessException;

@Local
public interface QueryNativaService {
    String executar(String query) throws BusinessException;
}
