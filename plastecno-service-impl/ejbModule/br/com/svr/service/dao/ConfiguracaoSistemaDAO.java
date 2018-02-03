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
				this.entityManager.createNativeQuery(
						"select valor from vendas.tb_configuracao_sistema where parametro  = :parametro ")
						.setParameter("parametro", parametro.toString()), String.class, null);
	}

	public List<Object[]> pesquisarCFOP() {
		return this.entityManager.createNativeQuery("select codigo, descricao from vendas.tb_cfop").getResultList();
	}
}
