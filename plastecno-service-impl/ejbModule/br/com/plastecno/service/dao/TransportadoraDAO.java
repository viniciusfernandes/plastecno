package br.com.plastecno.service.dao;

import javax.persistence.EntityManager;

import br.com.plastecno.service.entity.Transportadora;
import br.com.plastecno.service.impl.util.QueryUtil;

public class TransportadoraDAO extends GenericDAO<Transportadora> {

	public TransportadoraDAO(EntityManager entityManager) {
		super(entityManager);
	}

	public Transportadora pesquisarByCNPJ(String cnpj) {
		return QueryUtil.gerarRegistroUnico(
				entityManager
						.createQuery("select t from Transportadora t join fetch t.logradouro where t.cnpj = :cnpj")
						.setParameter("cnpj", cnpj), Transportadora.class, null);
	}
}
