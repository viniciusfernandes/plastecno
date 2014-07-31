package br.com.plastecno.service.impl;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import br.com.plastecno.service.ConfiguracaoSistemaService;
import br.com.plastecno.service.constante.ParametroConfiguracaoSistema;
import br.com.plastecno.service.impl.util.QueryUtil;

@Stateless
public class ConfiguracaoSistemaServiceImpl implements
		ConfiguracaoSistemaService {

	@PersistenceContext(name="plastecno")
	private EntityManager entityManager;
	
	@Override
	public String pesquisar(ParametroConfiguracaoSistema parametro) {
		return QueryUtil.gerarRegistroUnico(
				this.entityManager.createNativeQuery("select valor from vendas.tb_configuracao_sistema where parametro  = :parametro ")
				.setParameter("parametro", parametro.toString()), String.class, null);
	}

}
