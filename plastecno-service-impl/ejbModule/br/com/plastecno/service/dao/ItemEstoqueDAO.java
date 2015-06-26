package br.com.plastecno.service.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import br.com.plastecno.service.constante.FormaMaterial;
import br.com.plastecno.service.entity.ItemEstoque;
import br.com.plastecno.service.impl.util.QueryUtil;
import br.com.plastecno.util.StringUtils;

public class ItemEstoqueDAO extends GenericDAO<ItemEstoque> {

	public ItemEstoqueDAO(EntityManager entityManager) {
		super(entityManager);
	}

	public ItemEstoque pesquisarById(Integer idItemEstoque) {
		return pesquisarById(ItemEstoque.class, idItemEstoque);
	}

	@SuppressWarnings("unchecked")
	public List<ItemEstoque> pesquisarEscassezItemEstoque(Integer idMaterial, FormaMaterial formaMaterial,
			Integer quantidadeMinima) {
		StringBuilder select = new StringBuilder();
		select.append("select i from ItemEstoque i where i.quantidade <= :quantidadeMinima ");
		if (idMaterial != null && formaMaterial != null) {
			select.append("and i.material.id = :idMaterial and i.formaMaterial = :formaMaterial ");
		}

		if (idMaterial != null && formaMaterial == null) {
			select.append("and i.material.id = :idMaterial ");
		}

		if (idMaterial == null && formaMaterial != null) {
			select.append("and i.formaMaterial = :formaMaterial ");
		}
		select.append("order by i.formaMaterial, i.material.descricao, i.descricaoPeca ");

		Query query = entityManager.createQuery(select.toString());
		query.setParameter("quantidadeMinima", quantidadeMinima);
		if (idMaterial != null) {
			query.setParameter("idMaterial", idMaterial);
		}

		if (formaMaterial != null) {
			query.setParameter("formaMaterial", formaMaterial);
		}
		return query.getResultList();
	}

	public List<ItemEstoque> pesquisarItemEstoque(Integer idMaterial, FormaMaterial formaMaterial, String descricaoPeca) {
		return pesquisarItemEstoque(idMaterial, formaMaterial, descricaoPeca, true);
	}

	@SuppressWarnings("unchecked")
	public List<ItemEstoque> pesquisarItemEstoque(Integer idMaterial, FormaMaterial formaMaterial, String descricaoPeca,
			boolean zeradosExcluidos) {
		StringBuilder select = new StringBuilder();
		select.append("select i from ItemEstoque i ");
		if (idMaterial != null || formaMaterial != null) {
			select.append("where ");
		}

		if (idMaterial != null) {
			select.append("i.material.id = :idMaterial ");
		}

		if (formaMaterial != null && idMaterial != null) {
			select.append("and i.formaMaterial = :formaMaterial ");
		} else if (formaMaterial != null) {
			select.append("i.formaMaterial = :formaMaterial ");
		}

		if (StringUtils.isNotEmpty(descricaoPeca)) {
			select.append("and i.descricaoPeca = :descricaoPeca ");
		}

		if (zeradosExcluidos) {
			select.append("and i.quantidade > 0 ");
		}

		if (FormaMaterial.CH.equals(formaMaterial) || FormaMaterial.TB.equals(formaMaterial)) {
			select
					.append("order by i.formaMaterial, i.material.sigla, i.medidaExterna asc, i.medidaInterna asc, i.comprimento asc ");

		} else {
			select
					.append("order by i.formaMaterial, i.material.sigla, i.medidaInterna asc, i.medidaExterna asc, i.comprimento asc ");
		}
		
		Query query = entityManager.createQuery(select.toString());
		if (idMaterial != null) {
			query.setParameter("idMaterial", idMaterial);
		}

		if (formaMaterial != null) {
			query.setParameter("formaMaterial", formaMaterial);
		}

		if (StringUtils.isNotEmpty(descricaoPeca)) {
			query.setParameter("descricaoPeca", descricaoPeca);
		}
		return query.getResultList();
	}

	public Double pesquisarValorEQuantidadeItemEstoque(Integer idMaterial, FormaMaterial formaMaterial) {
		StringBuilder select = new StringBuilder();
		select.append("select SUM(i.precoMedio * i.quantidade) from ItemEstoque i ");
		if (idMaterial != null && formaMaterial != null) {
			select.append("where i.material.id = :idMaterial and i.formaMaterial = :formaMaterial ");
		}

		if (idMaterial != null && formaMaterial == null) {
			select.append("where i.material.id = :idMaterial ");
		}

		if (idMaterial == null && formaMaterial != null) {
			select.append("where i.formaMaterial = :formaMaterial ");
		}

		Query query = entityManager.createQuery(select.toString());
		if (idMaterial != null) {
			query.setParameter("idMaterial", idMaterial);
		}

		if (formaMaterial != null) {
			query.setParameter("formaMaterial", formaMaterial);
		}
		return QueryUtil.gerarRegistroUnico(query, Double.class, 0d);
	}

}
