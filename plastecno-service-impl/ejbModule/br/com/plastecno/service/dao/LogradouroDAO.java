package br.com.plastecno.service.dao;

import javax.persistence.EntityManager;

import br.com.plastecno.service.entity.LogradouroEndereco;
import br.com.plastecno.service.impl.util.QueryUtil;

public class LogradouroDAO extends GenericDAO<LogradouroEndereco> {

	public LogradouroDAO(EntityManager entityManager) {
		super(entityManager);
	}

	public String pesquisarCodigoIBGEByIdCidade(Integer idCidade) {
		return QueryUtil.gerarRegistroUnico(
				entityManager.createNativeQuery(
						"select c.cod_ibge from enderecamento.tb_cidade as c where c.id_cidade = :idCidade")
						.setParameter("idCidade", idCidade), String.class, "");
	}
}
