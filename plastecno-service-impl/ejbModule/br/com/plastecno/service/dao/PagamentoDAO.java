package br.com.plastecno.service.dao;

import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;

import br.com.plastecno.service.entity.Pagamento;

public class PagamentoDAO extends GenericDAO<Pagamento> {

	public PagamentoDAO(EntityManager entityManager) {
		super(entityManager);
	}

	public void liquidarPagamento(Integer idPagamento) {
		entityManager.createQuery("update Pagamento p set p.liquidado = true where p.id = :idPagamento")
				.setParameter("idPagamento", idPagamento).executeUpdate();
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
						"select p from Pagamento p where p.idFornecedor = :idFornecedor and p.dataVencimento >=:dataInicial and p.dataVencimento <=:dataFinal ",
						Pagamento.class).setParameter("idFornecedor", idFornecedor)
				.setParameter("dataInicial", dataInicial).setParameter("dataFinal", dataFinal).getResultList();
	}

	public List<Pagamento> pesquisarPagamentoByPeriodo(Date dataInicio, Date dataFim) {
		return entityManager
				.createQuery(
						"select p from Pagamento p where p.dataVencimento >=:dataInicio and p.dataVencimento <=:dataFim order by p.dataVencimento asc",
						Pagamento.class).setParameter("dataInicio", dataInicio).setParameter("dataFim", dataFim)
				.getResultList();
	}
}
