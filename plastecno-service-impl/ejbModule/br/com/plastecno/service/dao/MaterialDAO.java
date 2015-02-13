package br.com.plastecno.service.dao;

import java.util.List;

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

	@SuppressWarnings("unchecked")
	public List<Material> pesquisarBySigla(String sigla) {
		Query query = this.entityManager
				.createQuery("select new Material(m.id, m.sigla, m.descricao) from Material m  where m.sigla like :sigla order by m.sigla ");
		query.setParameter("sigla", "%" + sigla + "%");
		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	public List<Material> pesquisarBySigla(String sigla, Integer idRepresentada) {
		StringBuilder select = new StringBuilder();
		select
				.append("select new Material(m.id, m.sigla, m.descricao) from Material m inner join m.listaRepresentada r where ");

		if (idRepresentada != null) {
			select.append("r.id = :idRepresentada and ");
		}

		select.append("m.sigla like :sigla order by m.sigla ");
		Query query = this.entityManager.createQuery(select.toString());
		query.setParameter("sigla", "%" + sigla + "%");

		if (idRepresentada != null) {
			query.setParameter("idRepresentada", idRepresentada);
		}
		return query.getResultList();
	}
}
