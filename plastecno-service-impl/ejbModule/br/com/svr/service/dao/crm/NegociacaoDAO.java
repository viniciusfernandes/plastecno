package br.com.svr.service.dao.crm;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;

import br.com.svr.service.constante.crm.CategoriaNegociacao;
import br.com.svr.service.constante.crm.SituacaoNegociacao;
import br.com.svr.service.constante.crm.TipoNaoFechamento;
import br.com.svr.service.dao.GenericDAO;
import br.com.svr.service.entity.crm.IndicadorCliente;
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

	public void alterarIndiceConversaoValorByIdCliente(Integer idCliente, Double indiceQuantidade, Double indiceValor,
			SituacaoNegociacao situacaoNegociacao) {
		entityManager
				.createQuery(
						"update Negociacao n set n.indiceConversaoQuantidade =:indiceQuantidade, n.indiceConversaoValor =:indiceValor where n.idCliente =:idCliente and n.situacaoNegociacao=:situacaoNegociacao")
				.setParameter("idCliente", idCliente).setParameter("indiceQuantidade", indiceQuantidade)
				.setParameter("indiceValor", indiceValor).setParameter("idCliente", idCliente)
				.setParameter("situacaoNegociacao", situacaoNegociacao).executeUpdate();
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
						"select sum(n.orcamento.valorPedidoIPI) from Negociacao n where n.idVendedor = :idVendedor and n.categoriaNegociacao =:categoria and n.situacaoNegociacao =:situacaoNegociacao")
				.setParameter("idVendedor", idVendedor).setParameter("categoria", categoria)
				.setParameter("situacaoNegociacao", SituacaoNegociacao.ABERTO).getSingleResult();
		return v == null ? 0d : (double) v;
	}

	public void inserirObservacao(Integer idNegociacao, String observacao) {
		entityManager.createQuery("update Negociacao n set n.observacao =:observacao where n.id=:idNegociacao")
				.setParameter("observacao", observacao).setParameter("idNegociacao", idNegociacao).executeUpdate();
	}

	public Negociacao pesquisarById(Integer idNegociacao) {
		return super.pesquisarById(Negociacao.class, idNegociacao);
	}

	public Integer pesquisarIdNegociacaoByIdOrcamento(Integer idOrcamento) {
		return QueryUtil.gerarRegistroUnico(
				entityManager.createQuery("select n.id from Negociacao n where n.orcamento.id =:idOrcamento")
						.setParameter("idOrcamento", idOrcamento), Integer.class, null);
	}

	public Integer pesquisarIdOrcamentoByIdNegociacao(Integer idNegociacao) {
		return QueryUtil.gerarRegistroUnico(
				entityManager.createQuery("select n.orcamento.id from Negociacao n where n.id =:idNegociacao")
						.setParameter("idNegociacao", idNegociacao), Integer.class, null);
	}

	public IndicadorCliente pesquisarIndicadorByIdCliente(Integer idCliente) {
		return QueryUtil.gerarRegistroUnico(
				entityManager.createQuery("select i from IndicadorCliente i where i.idCliente =:idCliente")
						.setParameter("idCliente", idCliente), IndicadorCliente.class, null);
	}

	public double[] pesquisarIndiceConversaoValorByIdCliente(Integer idCliente) {
		Object[] o = null;
		try {
			o = entityManager
					.createQuery(
							"select i.indiceConversaoQuantidade, i.indiceConversaoValor from IndicadorCliente i where i.idCliente =:idCliente",
							Object[].class).setParameter("idCliente", idCliente).getSingleResult();

		} catch (NonUniqueResultException | NoResultException e) {
			return new double[] {};
		}

		return o == null || o.length <= 0 ? new double[] {} : new double[] { (Double) o[0], (Double) o[1] };
	}

	public List<Negociacao> pesquisarNegociacaoAbertaByIdVendedor(Integer idVendedor) {
		return entityManager
				.createQuery(
						"select new Negociacao(n.categoriaNegociacao, n.id, n.orcamento.id, n.indiceConversaoQuantidade, n.indiceConversaoValor, n.nomeCliente, n.nomeContato, n.telefoneContato, n.orcamento.valorPedidoIPI) from Negociacao n where n.idVendedor = :idVendedor and n.situacaoNegociacao =:situacaoNegociacao",
						Negociacao.class).setParameter("idVendedor", idVendedor)
				.setParameter("situacaoNegociacao", SituacaoNegociacao.ABERTO).getResultList();
	}

	public Negociacao pesquisarNegociacaoByIdOrcamento(Integer idOrcamento) {
		return QueryUtil.gerarRegistroUnico(
				entityManager.createQuery("select n from Negociacao n where n.orcamento.id =:idOrcamento")
						.setParameter("idOrcamento", idOrcamento), Negociacao.class, null);
	}

	public String pesquisarObservacao(Integer idNegociacao) {
		return QueryUtil.gerarRegistroUnico(
				entityManager.createQuery("select n.observacao from Negociacao n where n.id=:idNegociacao")
						.setParameter("idNegociacao", idNegociacao), String.class, null);

	}

	public void removerNegociacaoByIdOrcamento(Integer idOrcamento) {
		entityManager.createQuery("delete Negociacao n where n.orcamento.id = :idOrcamento")
				.setParameter("idOrcamento", idOrcamento).executeUpdate();
	}

}
