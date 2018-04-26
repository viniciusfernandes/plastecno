package br.com.svr.service;

import java.util.List;

import javax.ejb.Local;

import br.com.svr.service.constante.ParametroConfiguracaoSistema;

@Local
public interface ConfiguracaoSistemaService {
    String pesquisar(ParametroConfiguracaoSistema parametro);

	List<Object[]> pesquisarCFOP();
}
