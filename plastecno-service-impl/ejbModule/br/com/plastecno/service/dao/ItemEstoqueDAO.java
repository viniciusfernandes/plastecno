package br.com.plastecno.service.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import br.com.plastecno.service.constante.FormaMaterial;
import br.com.plastecno.service.entity.ItemEstoque;

public class ItemEstoqueDAO extends GenericDAO<ItemEstoque> {

	public ItemEstoqueDAO(EntityManager entityManager) {
		super(entityManager);
	}

	public ItemEstoque pesquisarById(Integer idItemEstoque) {
		return pesquisarById(ItemEstoque.class, idItemEstoque);
	}

	@SuppressWarnings("unchecked")
	public List<ItemEstoque> pesquisarItemEstoque(Integer idMaterial, FormaMaterial formaMaterial) {
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

		Query query = entityManager.createQuery(select.toString());
		if (idMaterial != null) {
			query.setParameter("idMaterial", idMaterial);
		}

		if (formaMaterial != null) {
			query.setParameter("formaMaterial", formaMaterial);
		}

		return query.getResultList();
	}
}
