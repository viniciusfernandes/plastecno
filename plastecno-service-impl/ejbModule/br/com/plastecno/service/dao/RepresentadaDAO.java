package br.com.plastecno.service.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import br.com.plastecno.service.constante.TipoApresentacaoIPI;
import br.com.plastecno.service.constante.TipoRelacionamento;
import br.com.plastecno.service.entity.Representada;
import br.com.plastecno.service.impl.util.QueryUtil;

public class RepresentadaDAO extends GenericDAO<Representada> {

	public RepresentadaDAO(EntityManager entityManager) {
		super(entityManager);
	}

	public Representada pesquisarById(Integer idRepresentada) {
		return pesquisarById(Representada.class, idRepresentada);
	}

	public String pesquisarNomeFantasia(Integer idRepresentada) {
		return QueryUtil.gerarRegistroUnico(
				entityManager.createQuery("SELECT r.nomeFantasia FROM Representada r where r.id = :idRepresentada")
						.setParameter("idRepresentada", idRepresentada), String.class, null);
	}

	@SuppressWarnings("unchecked")
	public List<Representada> pesquisarRepresentadaExcluindoRelacionamento(Boolean ativo,
			TipoRelacionamento tipoRelacionamento) {
		StringBuilder select = new StringBuilder("SELECT r FROM Representada r ");

		if (ativo != null && tipoRelacionamento != null) {
			select.append("where r.ativo = :ativo and r.tipoRelacionamento != :tipoRelacionamento ");
		} else if (tipoRelacionamento != null) {
			select.append("where r.tipoRelacionamento != :tipoRelacionamento ");
		}

		select.append("order by r.nomeFantasia ");

		Query query = this.entityManager.createQuery(select.toString());
		if (ativo != null) {
			query.setParameter("ativo", ativo);
		}
		if (tipoRelacionamento != null) {
			query.setParameter("tipoRelacionamento", tipoRelacionamento);
		}
		return query.getResultList();
	}

	public TipoApresentacaoIPI pesquisarTipoApresentacaoIPI(Integer idRepresentada) {
		Query query = this.entityManager.createQuery(
				"select r.tipoApresentacaoIPI from Representada r where r.id = :idRepresentada").setParameter("idRepresentada",
				idRepresentada);
		return QueryUtil.gerarRegistroUnico(query, TipoApresentacaoIPI.class, TipoApresentacaoIPI.NUNCA);
	}

	public TipoRelacionamento pesquisarTipoRelacionamento(Integer idRepresentada) {
		return QueryUtil.gerarRegistroUnico(
				entityManager.createQuery("SELECT r.tipoRelacionamento FROM Representada r where r.id = :idRepresentada")
						.setParameter("idRepresentada", idRepresentada), TipoRelacionamento.class, null);
	}
}
