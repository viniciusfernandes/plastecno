package br.com.plastecno.service.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import br.com.plastecno.service.constante.FormaMaterial;
import br.com.plastecno.service.entity.Comissao;
import br.com.plastecno.service.impl.util.QueryUtil;

public class ComissaoDAO extends GenericDAO<Comissao> {

	public ComissaoDAO(EntityManager entityManager) {
		super(entityManager);
	}

	public Comissao pesquisarById(Integer idComissao) {
		return super.pesquisarById(Comissao.class, idComissao);
	}

	@SuppressWarnings("unchecked")
	public List<Comissao> pesquisarComissaoByIdVendedor(Integer idVendedor) {
		return entityManager
				.createQuery("select c from Comissao c where c.idVendedor = :idVendedor order by c.dataInicio desc ")
				.setParameter("idVendedor", idVendedor).getResultList();
	}

	@SuppressWarnings("unchecked")
	public List<Comissao> pesquisarComissaoByProduto(FormaMaterial formaMaterial, Integer idMaterial) {
		StringBuilder select = new StringBuilder("select c from Comissao c where ");
		if (formaMaterial != null) {
			select.append("c.idFormaMaterial = :idFormaMaterial and ");
		}

		if (idMaterial != null) {
			select.append("c.idMaterial = :idMaterial and ");
		}
		select.append(" c.idVendedor = null order by c.dataInicio desc ");

		Query query = entityManager.createQuery(select.toString());
		if (formaMaterial != null) {
			query.setParameter("idFormaMaterial", formaMaterial.indexOf());
		}

		if (idMaterial != null) {
			query.setParameter("idMaterial", idMaterial);
		}
		return query.getResultList();
	}

	public Comissao pesquisarComissaoVigenteProduto(Integer idMaterial, Integer idFormaMaterial) {
		if (idFormaMaterial == null && idMaterial == null) {
			return null;
		}
		StringBuilder select = new StringBuilder();
		select.append("select new Comissao(c.id, c.idFormaMaterial, c.idMaterial, c.aliquotaRevenda, c.aliquotaRepresentacao, c.dataInicio) from Comissao c where ");
		if (idFormaMaterial != null) {
			select.append(" c.idFormaMaterial = :idFormaMaterial and ");
		}

		if (idMaterial != null) {
			select.append(" c.idMaterial = :idMaterial and ");
		}

		select.append(" c.dataFim = null ");

		Query query = entityManager.createQuery(select.toString());
		if (idFormaMaterial != null) {
			query.setParameter("idFormaMaterial", idFormaMaterial);
		}

		if (idMaterial != null) {
			query.setParameter("idMaterial", idMaterial);
		}
		return QueryUtil.gerarRegistroUnico(query, Comissao.class, null);
	}

	public Comissao pesquisarComissaoVigenteVendedor(Integer idVendedor) {
		if (idVendedor == null) {
			return null;
		}
		return QueryUtil
				.gerarRegistroUnico(
						entityManager
								.createQuery(
										"select new Comissao(c.id, c.idVendedor, c.aliquotaRevenda, c.aliquotaRepresentacao, c.dataInicio) from Comissao c where  c.idVendedor = :idVendedor and c.dataFim = null")
								.setParameter("idVendedor", idVendedor), Comissao.class, null);
	}
}
