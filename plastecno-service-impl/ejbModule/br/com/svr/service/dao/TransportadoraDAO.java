package br.com.svr.service.dao;

import java.util.List;

import javax.persistence.EntityManager;

import br.com.svr.service.entity.Transportadora;
import br.com.svr.service.impl.util.QueryUtil;

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

	public List<Transportadora> pesquisarByNomeFantasia(String nomeFantasia) {
		return entityManager
				.createQuery(
						"select new Transportadora(c.id, c.nomeFantasia) from Transportadora c where c.nomeFantasia like :nomeFantasia order by c.nomeFantasia asc ",
						Transportadora.class).setParameter("nomeFantasia", "%" + nomeFantasia + "%").getResultList();
	}

	public Transportadora pesquisarTransportadoraLogradouroById(Integer idTransportadora) {
		return QueryUtil.gerarRegistroUnico(
				entityManager.createQuery(
						"select t from Transportadora t left join fetch t.logradouro where t.id =:idTransportadora ")
						.setParameter("idTransportadora", idTransportadora), Transportadora.class, null);
	}
}
