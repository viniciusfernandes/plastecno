package br.com.plastecno.service.dao;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import br.com.plastecno.service.constante.TipoApresentacaoIPI;
import br.com.plastecno.service.entity.Representada;
import br.com.plastecno.service.impl.util.QueryUtil;

public class RepresentadaDAO extends GenericDAO<Representada> {

	public RepresentadaDAO(EntityManager entityManager) {
		super(entityManager);
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
