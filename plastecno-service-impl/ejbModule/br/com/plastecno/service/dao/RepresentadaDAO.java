package br.com.plastecno.service.dao;

import java.util.Arrays;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import br.com.plastecno.service.constante.TipoApresentacaoIPI;
import br.com.plastecno.service.constante.TipoRelacionamento;
import br.com.plastecno.service.entity.LogradouroRepresentada;
import br.com.plastecno.service.entity.Representada;
import br.com.plastecno.service.impl.util.QueryUtil;

public class RepresentadaDAO extends GenericDAO<Representada> {
	public RepresentadaDAO(EntityManager entityManager) {
		super(entityManager);
	}

	public double pesquisarAliquotaICMSRevendedor() {
		Double icms = QueryUtil.gerarRegistroUnico(
				entityManager.createQuery(
						"select r.aliquotaICMS from Representada r where r.tipoRelacionamento = :tipoRelacionamento")
						.setParameter("tipoRelacionamento", TipoRelacionamento.REVENDA), Double.class, null);
		return icms == null ? 0 : icms;
	}

	public Representada pesquisarById(Integer idRepresentada) {
		return pesquisarById(Representada.class, idRepresentada);
	}

	public double pesquisarComissao(Integer idRepresentada) {
		return super.pesquisarCampoById(Representada.class, idRepresentada, "comissao", Double.class);
	}

	public LogradouroRepresentada pesquisarLogradorouro(Integer id) {
		return QueryUtil.gerarRegistroUnico(
				entityManager.createQuery(
						"select t.logradouro from Representada t INNER JOIN t.logradouro where t.id = :id")
						.setParameter("id", id), LogradouroRepresentada.class, null);
	}

	public String pesquisarNomeFantasiaById(Integer idRepresentada) {
		return QueryUtil.gerarRegistroUnico(
				entityManager.createQuery("SELECT r.nomeFantasia FROM Representada r where r.id = :idRepresentada")
						.setParameter("idRepresentada", idRepresentada), String.class, null);
	}

	public List<Representada> pesquisarRepresentadaByTipoRelacionamento(boolean ativo, TipoRelacionamento... tipos) {
		return pesquisarRepresentadaByTipoRelacionamento(null, ativo, tipos);
	}

	public List<Representada> pesquisarRepresentadaByTipoRelacionamento(String nomeFantasia, boolean ativo,
			TipoRelacionamento... tipos) {
		StringBuilder select = new StringBuilder(
				"SELECT new Representada(r.id, r.nomeFantasia) FROM Representada r where r.ativo = :ativo ");

		if (tipos != null && tipos.length > 0) {
			select.append("and r.tipoRelacionamento IN (:tipos) ");
		}

		if (nomeFantasia != null) {
			select.append("and r.nomeFantasia like :nomeFantasia ");
		}

		select.append("order by r.nomeFantasia ");

		TypedQuery<Representada> query = entityManager.createQuery(select.toString(), Representada.class).setParameter(
				"ativo", ativo);
		if (tipos != null) {
			query.setParameter("tipos", Arrays.asList(tipos));
		}

		if (nomeFantasia != null) {
			query.setParameter("nomeFantasia", "%" + nomeFantasia + "%");
		}
		return query.getResultList();
	}

	public List<Representada> pesquisarRepresentadaEFornecedor() {
		return pesquisarRepresentadaExcluindoRelacionamento(null, null);
	}

	@SuppressWarnings("unchecked")
	public List<Representada> pesquisarRepresentadaExcluindoRelacionamento(Boolean ativo,
			TipoRelacionamento tipoRelacionamento) {
		StringBuilder select = new StringBuilder("SELECT new Representada(r.id, r.nomeFantasia) FROM Representada r ");

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

	public Representada pesquisarRevendedor() {
		return QueryUtil
				.gerarRegistroUnico(
						entityManager
								.createQuery(
										"select new Representada(r.id, r.nomeFantasia) from Representada r where r.tipoRelacionamento = :tipoRelacionamento")
								.setParameter("tipoRelacionamento", TipoRelacionamento.REVENDA), Representada.class,
						null);
	}

	public TipoApresentacaoIPI pesquisarTipoApresentacaoIPI(Integer idRepresentada) {
		Query query = this.entityManager.createQuery(
				"select r.tipoApresentacaoIPI from Representada r where r.id = :idRepresentada").setParameter(
				"idRepresentada", idRepresentada);
		return QueryUtil.gerarRegistroUnico(query, TipoApresentacaoIPI.class, TipoApresentacaoIPI.NUNCA);
	}

	public TipoRelacionamento pesquisarTipoRelacionamento(Integer idRepresentada) {
		return QueryUtil.gerarRegistroUnico(
				entityManager.createQuery(
						"SELECT r.tipoRelacionamento FROM Representada r where r.id = :idRepresentada").setParameter(
						"idRepresentada", idRepresentada), TipoRelacionamento.class, null);
	}
}
