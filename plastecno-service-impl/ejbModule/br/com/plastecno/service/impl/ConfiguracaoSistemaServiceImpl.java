package br.com.plastecno.service.impl;

import java.util.List;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import br.com.plastecno.service.ConfiguracaoSistemaService;
import br.com.plastecno.service.constante.ParametroConfiguracaoSistema;
import br.com.plastecno.service.impl.util.QueryUtil;

@Stateless
public class ConfiguracaoSistemaServiceImpl implements
		ConfiguracaoSistemaService {

	@PersistenceContext(name = "plastecno")
	private EntityManager entityManager;

	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public String pesquisar(ParametroConfiguracaoSistema parametro) {
		return QueryUtil
				.gerarRegistroUnico(
						this.entityManager
								.createNativeQuery(
										"select valor from vendas.tb_configuracao_sistema where parametro  = :parametro ")
								.setParameter("parametro", parametro.toString()),
						String.class, null);
	}

	@SuppressWarnings("unchecked")
	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public List<Object[]> pesquisarCFOP() {
		return this.entityManager.createNativeQuery(
				"select codigo, descricao from vendas.tb_cfop").getResultList();
	}

}
