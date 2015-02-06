package br.com.plastecno.service.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import br.com.plastecno.service.constante.TipoApresentacaoIPI;
import br.com.plastecno.service.entity.Representada;
import br.com.plastecno.service.impl.util.QueryUtil;

public class RepresentadaDAO extends GenericDAO<Representada> {

	public RepresentadaDAO(EntityManager entityManager) {
		super(entityManager);
	}
	@SuppressWarnings("unchecked")
	public List<Representada> pesquisar(Boolean ativo) {
		StringBuilder select = new StringBuilder("SELECT r FROM Representada r ");
		if (ativo != null) {
			select.append("where r.ativo = :ativo");
		}

		select.append(" order by r.nomeFantasia ");

		Query query = this.entityManager.createQuery(select.toString());

		if (ativo != null) {
			query.setParameter("ativo", ativo);
		}
		return query.getResultList();
	}


	public Representada pesquisarById(Integer idRepresentada) {
		return pesquisarById(Representada.class, idRepresentada);
	}

	public TipoApresentacaoIPI pesquisarTipoApresentacaoIPI(Integer idRepresentada) {
		Query query = this.entityManager.createQuery(
				"select r.tipoApresentacaoIPI from Representada r where r.id = :idRepresentada").setParameter("idRepresentada",
				idRepresentada);
		return QueryUtil.gerarRegistroUnico(query, TipoApresentacaoIPI.class, TipoApresentacaoIPI.NUNCA);
	}
}
