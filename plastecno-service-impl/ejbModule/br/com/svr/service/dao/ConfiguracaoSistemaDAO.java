package br.com.svr.service.dao;

import java.util.List;

import javax.persistence.EntityManager;

import br.com.svr.service.constante.ParametroConfiguracaoSistema;
import br.com.svr.service.impl.util.QueryUtil;

public class ConfiguracaoSistemaDAO {
	private EntityManager entityManager;

	public ConfiguracaoSistemaDAO(EntityManager entityManager) {
		this.entityManager = entityManager;
	}

	public String pesquisar(ParametroConfiguracaoSistema parametro) {
		return QueryUtil.gerarRegistroUnico(
				entityManager.createQuery(
						"select c.valor from ConfiguracaoSistema c where c.parametro  = :parametro ").setParameter(
						"parametro", parametro.toString()), String.class, null);
	}

	public List<Object[]> pesquisarCFOP() {
		return entityManager.createNativeQuery("select codigo, descricao from vendas.tb_cfop").getResultList();
	}
}
