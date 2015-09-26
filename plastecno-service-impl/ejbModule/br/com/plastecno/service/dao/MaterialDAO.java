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

	public void desativar(Integer id) {
		Query query = this.entityManager.createQuery("update Material r set r.ativo = false where r.id = :id");
		query.setParameter("id", id);
		query.executeUpdate();
	}

	public boolean isMaterialAssociadoRepresentada(Integer idMaterial, Integer idRepresentada) {
		return QueryUtil
				.gerarRegistroUnico(
						this.entityManager
								.createQuery(
										"select m.id from Material m inner join m.listaRepresentada r where  m.id = :idMaterial and r.id = :idRepresentada")
								.setParameter("idMaterial", idMaterial).setParameter("idRepresentada", idRepresentada), Integer.class,
						null) != null;
	}

	public boolean isMaterialImportado(Integer idMaterial) {
		return QueryUtil.gerarRegistroUnico(
				this.entityManager.createQuery("select m.importado from Material m where m.id = :idMaterial").setParameter(
						"idMaterial", idMaterial), Boolean.class, false);
	}

	public Material pesquisarById(Integer id) {
		return super.pesquisarById(Material.class, id);
	}

	public List<Material> pesquisarBySigla(String sigla) {
		return pesquisarBySigla(sigla, null, null);
	}

	public List<Material> pesquisarBySigla(String sigla, Integer idRepresentada) {
		return pesquisarBySigla(sigla, idRepresentada, null);
	}

	@SuppressWarnings("unchecked")
	public List<Material> pesquisarBySigla(String sigla, Integer idRepresentada, Boolean isAtivo) {
		StringBuilder select = new StringBuilder();
		select.append("select new Material(m.id, m.sigla, m.descricao) from Material m ");

		if (idRepresentada != null) {
			select.append(" inner join m.listaRepresentada r where r.id = :idRepresentada and ");
		} else {
			select.append("where ");
		}

		if (isAtivo != null) {
			select.append("m.ativo = :isAtivo and ");
		}

		select.append("m.sigla like :sigla order by m.sigla ");
		Query query = this.entityManager.createQuery(select.toString());
		query.setParameter("sigla", "%" + sigla + "%");

		if (idRepresentada != null) {
			query.setParameter("idRepresentada", idRepresentada);
		}

		if (isAtivo != null) {
			query.setParameter("isAtivo", isAtivo);
		}
		return query.getResultList();
	}
}
