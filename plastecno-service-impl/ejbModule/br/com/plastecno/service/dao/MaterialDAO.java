package br.com.plastecno.service.dao;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import br.com.plastecno.service.entity.Material;
import br.com.plastecno.service.impl.util.QueryUtil;

public class MaterialDAO extends GenericDAO<Material> {

	public MaterialDAO(EntityManager entityManager) {
		super(entityManager);
	}

	public boolean isMaterialImportado(Integer idMaterial) {
		return QueryUtil.gerarRegistroUnico(
				this.entityManager.createQuery("select m.importado from Material m where m.id = :idMaterial").setParameter(
						"idMaterial", idMaterial), Boolean.class, false);
	}

	public void desativar(Integer id) {
		Query query = this.entityManager.createQuery("update Material r set r.ativo = false where r.id = :id");
		query.setParameter("id", id);
		query.executeUpdate();
	}

	public Material pesquisarById(Integer id) {
		return super.pesquisarById(Material.class, id);
	}
}
