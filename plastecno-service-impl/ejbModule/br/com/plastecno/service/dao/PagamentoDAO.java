package br.com.plastecno.service.dao;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import br.com.plastecno.service.constante.TipoPagamento;
import br.com.plastecno.service.entity.Pagamento;

public class PagamentoDAO extends GenericDAO<Pagamento> {

	public PagamentoDAO(EntityManager entityManager) {
		super(entityManager);
	}

	public void liquidarPagamento(Integer idPagamento) {
		liquidarPagamento(idPagamento, true);
	}

	private void liquidarPagamento(Integer idPagamento, boolean liquidado) {
		entityManager.createQuery("update Pagamento p set p.liquidado = :liquidado where p.id = :idPagamento")
				.setParameter("idPagamento", idPagamento).setParameter("liquidado", liquidado).executeUpdate();
	}

	public void liquidarPagamentoNFParcelada(Integer numeroNF, Integer idFornecedor, Integer parcela) {
		liquidarPagamentoNFParcelada(numeroNF, idFornecedor, parcela, true);
	}

	private void liquidarPagamentoNFParcelada(Integer numeroNF, Integer idFornecedor, Integer parcela, boolean liquidado) {
		entityManager
				.createQuery(
						"update Pagamento p set p.liquidado = :liquidado where p.numeroNF = :numeroNF and p.idFornecedor = :idFornecedor and p.parcela = :parcela ")
				.setParameter("numeroNF", numeroNF).setParameter("idFornecedor", idFornecedor)
				.setParameter("liquidado", liquidado).setParameter("parcela", parcela).executeUpdate();
	}

	public Pagamento pesquisarById(Integer idPagamento) {
		return super.pesquisarById(Pagamento.class, idPagamento);
	}

	public List<Pagamento> pesquisarByIdPedido(Integer idPedido) {
		return entityManager.createQuery("select p from Pagamento p where p.idPedido =:idPedido", Pagamento.class)
				.setParameter("idPedido", idPedido).getResultList();
	}

	public List<Pagamento> pesquisarPagamentoByIdFornecedor(Integer idFornecedor, Date dataInicial, Date dataFinal) {
		return entityManager
				.createQuery(
						"select p from Pagamento p where p.idFornecedor = :idFornecedor and p.dataVencimento >=:dataInicial and p.dataVencimento <=:dataFinal order by p.dataVencimento asc ",
						Pagamento.class).setParameter("idFornecedor", idFornecedor)
				.setParameter("dataInicial", dataInicial).setParameter("dataFinal", dataFinal).getResultList();
	}

	public List<Pagamento> pesquisarPagamentoByIdPedido(Integer idPedido) {
		return entityManager
				.createQuery("select p from Pagamento p where p.idPedido = :idPedido order by p.dataVencimento asc ",
						Pagamento.class).setParameter("idPedido", idPedido).getResultList();
	}

	public List<Pagamento> pesquisarPagamentoByNF(Integer numeroNF) {
		return entityManager
				.createQuery("select p from Pagamento p where p.numeroNF = :numeroNF order by p.dataVencimento asc ",
						Pagamento.class).setParameter("numeroNF", numeroNF).getResultList();
	}

	public List<Pagamento> pesquisarPagamentoByPeriodo(Date dataInicio, Date dataFim, List<TipoPagamento> listaTipo) {
		StringBuilder s = new StringBuilder();
		s.append("select p from Pagamento p where p.dataVencimento >=:dataInicio and p.dataVencimento <=:dataFim ");
		if (listaTipo != null && listaTipo.size() > 0) {
			s.append("and p.tipoPagamento in (:listaTipo) ");
		}
		s.append("order by p.dataVencimento asc");

		TypedQuery<Pagamento> q = entityManager.createQuery(s.toString(), Pagamento.class)
				.setParameter("dataInicio", dataInicio).setParameter("dataFim", dataFim);
		if (listaTipo != null && listaTipo.size() > 0) {
			q.setParameter("listaTipo", listaTipo);
		}
		return q.getResultList();
	}

	public List<Integer[]> pesquisarQuantidadePagaByIdItem(List<Integer> listaIdItem) {
		List<Object[]> l = entityManager
				.createQuery(
						"select p.idItemPedido, p.sequencialItem, p.idPedido, SUM(p.quantidadeItem), p.quantidadeTotal from Pagamento p where p.idItemPedido in (:listaIdItem) group by p.idItemPedido, p.idPedido, p.quantidadeTotal, p.sequencialItem",
						Object[].class).setParameter("listaIdItem", listaIdItem).getResultList();
		if (l.isEmpty()) {
			return new ArrayList<>();
		}
		List<Integer[]> lTotal = new ArrayList<>(l.size());
		for (Object[] o : l) {
			lTotal.add(new Integer[] { (Integer) o[0], (Integer) o[1], (Integer) o[2], ((Long) o[3]).intValue(),
					(Integer) o[4] });
		}
		return lTotal;
	}

	public void removerPagamentoPaceladoItemPedido(Integer idItemPedido) {
		entityManager.createQuery("delete from Pagamento p where p.idItemPedido = :idItemPedido ")
				.setParameter("idItemPedido", idItemPedido).executeUpdate();
	}

	public void retornarLiquidacaoPagamento(Integer idPagamento) {
		liquidarPagamento(idPagamento, false);
	}

	public void retornarLiquidacaoPagamentoNFParcelada(Integer numeroNF, Integer idFornecedor, Integer parcela) {
		liquidarPagamentoNFParcelada(numeroNF, idFornecedor, parcela, false);
	}
}
