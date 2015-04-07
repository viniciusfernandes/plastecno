package br.com.plastecno.service.dao;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import br.com.plastecno.service.entity.Comissao;
import br.com.plastecno.service.impl.util.QueryUtil;

public class ComissaoDAO extends GenericDAO<Comissao> {

	public ComissaoDAO(EntityManager entityManager) {
		super(entityManager);
	}

	public Comissao pesquisarById(Integer idComissao) {
		return super.pesquisarById(Comissao.class, idComissao);
	}

	public Comissao pesquisarComissaoVigente(Integer idVendedor, Integer idMaterial, Integer idFormaMaterial) {
		if (idVendedor == null && idFormaMaterial == null && idMaterial == null) {
			return null;
		}
		StringBuilder select = new StringBuilder();
		select.append("select c from Comissao c where ");
		if (idVendedor != null) {
			select.append(" c.idVendedor = :idVendedor and ");
		}
		if (idFormaMaterial != null) {
			select.append(" c.idFormaMaterial = :idFormaMaterial and ");
		}

		if (idMaterial != null) {
			select.append(" c.idMaterial = :idMaterial ");
		}
		Query query = entityManager.createQuery(select.toString());
		if (idVendedor != null) {
			query.setParameter("idVendedor", idVendedor);
		}
		if (idFormaMaterial != null) {
			query.setParameter("idFormaMaterial", idFormaMaterial);
		}

		if (idMaterial != null) {
			query.setParameter("idMaterial", idMaterial);
		}
		return QueryUtil.gerarRegistroUnico(query, Comissao.class, null);
	}
}
