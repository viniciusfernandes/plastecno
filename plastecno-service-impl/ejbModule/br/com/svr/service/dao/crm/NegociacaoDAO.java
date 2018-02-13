package br.com.svr.service.dao.crm;

import java.util.List;

import javax.persistence.EntityManager;

import br.com.svr.service.constante.crm.CategoriaNegociacao;
import br.com.svr.service.constante.crm.SituacaoNegociacao;
import br.com.svr.service.dao.GenericDAO;
import br.com.svr.service.entity.crm.Negociacao;

public class NegociacaoDAO extends GenericDAO<Negociacao> {
	public NegociacaoDAO(EntityManager entityManager) {
		super(entityManager);
	}

	public void alterarCategoria(Integer idNegociacao, CategoriaNegociacao categoriaNegociacao) {
		entityManager
				.createQuery(
						"update Negociacao n set n.categoriaNegociacao =:categoriaNegociacao where n.id=:idNegociacao")
				.setParameter("categoriaNegociacao", categoriaNegociacao).setParameter("idNegociacao", idNegociacao)
				.executeUpdate();
	}

	public double calcularValorCategoriaNegociacaoAberta(Integer idVendedor, CategoriaNegociacao categoria) {
		Object v = entityManager
				.createQuery(
						"select sum(n.valor) from Negociacao n where n.idVendedor = :idVendedor and n.categoriaNegociacao =:categoria and n.situacaoNegociacao =:situacaoNegociacao")
				.setParameter("idVendedor", idVendedor).setParameter("categoria", categoria)
				.setParameter("situacaoNegociacao", SituacaoNegociacao.ABERTO).getSingleResult();
		return v == null ? 0d : (double) v;
	}

	public Negociacao pesquisarById(Integer idNegociacao) {
		return super.pesquisarById(Negociacao.class, idNegociacao);
	}

	public List<Negociacao> pesquisarNegociacaoByIdVendedor(Integer idVendedor) {
		return entityManager
				.createQuery("select n from Negociacao n where n.idVendedor = :idVendedor", Negociacao.class)
				.setParameter("idVendedor", idVendedor).getResultList();
	}
}
