package br.com.plastecno.service;

import javax.ejb.Local;

import br.com.plastecno.service.constante.ParametroConfiguracaoSistema;

@Local
public interface ConfiguracaoSistemaService {
    String pesquisar(ParametroConfiguracaoSistema parametro);
}
