package br.com.svr.service.dao.crm;

import java.util.List;

import javax.persistence.EntityManager;

import br.com.svr.service.constante.crm.CategoriaNegociacao;
import br.com.svr.service.constante.crm.SituacaoNegociacao;
import br.com.svr.service.constante.crm.TipoNaoFechamento;
import br.com.svr.service.dao.GenericDAO;
import br.com.svr.service.entity.crm.Negociacao;
import br.com.svr.service.impl.util.QueryUtil;

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

	public void alterarSituacaoNegociacao(Integer idNegociacao, SituacaoNegociacao situacaoNegociacao) {
		entityManager
				.createQuery(
						"update Negociacao n set n.situacaoNegociacao =:situacaoNegociacao where n.id=:idNegociacao")
				.setParameter("situacaoNegociacao", situacaoNegociacao).setParameter("idNegociacao", idNegociacao)
				.executeUpdate();
	}

	public void alterarTipoNaoFechamento(Integer idNegociacao, TipoNaoFechamento tipoNaoFechamento) {
		entityManager
				.createQuery("update Negociacao n set n.tipoNaoFechamento =:tipoNaoFechamento where n.id=:idNegociacao")
				.setParameter("tipoNaoFechamento", tipoNaoFechamento).setParameter("idNegociacao", idNegociacao)
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

	public Integer pesquisarIdPedidoByIdNegociacao(Integer idNegociacao) {
		return QueryUtil.gerarRegistroUnico(
				entityManager.createQuery("select n.idOrcamento from Negociacao n where n.id =:idNegociacao")
						.setParameter("idNegociacao", idNegociacao), Integer.class, null);
	}

	public double pesquisarIndiceConversaoValorByIdCliente(Integer idCliente) {
		return QueryUtil.gerarRegistroUnico(
				entityManager.createQuery("select i.indiceValor from IndiceConversao i where i.idCliente =:idCliente")
						.setParameter("idCliente", idCliente), Double.class, 0d);
	}

	public List<Negociacao> pesquisarNegociacaoAbertaByIdVendedor(Integer idVendedor) {
		return entityManager
				.createQuery(
						"select n from Negociacao n where n.idVendedor = :idVendedor and n.situacaoNegociacao =:situacaoNegociacao",
						Negociacao.class).setParameter("idVendedor", idVendedor)
				.setParameter("situacaoNegociacao", SituacaoNegociacao.ABERTO).getResultList();
	}
}
