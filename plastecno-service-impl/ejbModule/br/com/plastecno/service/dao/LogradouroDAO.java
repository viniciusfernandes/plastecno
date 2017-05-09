package br.com.plastecno.service.dao;

import javax.persistence.EntityManager;

import br.com.plastecno.service.entity.Logradouro;
import br.com.plastecno.service.impl.util.QueryUtil;

public class LogradouroDAO extends GenericDAO<Logradouro> {

	public LogradouroDAO(EntityManager entityManager) {
		super(entityManager);
	}

	public String pesquisarCodigoMunicipioByCep(String cep) {
		return QueryUtil.gerarRegistroUnico(
				entityManager.createQuery(
						"select c.codigoMunicipio from Endereco e inner join e.cidade c where e.cep = :cep")
						.setParameter("cep", cep), String.class, null);
	}

}
