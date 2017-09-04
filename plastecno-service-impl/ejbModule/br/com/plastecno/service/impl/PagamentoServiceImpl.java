package br.com.plastecno.service.impl;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import br.com.plastecno.service.PagamentoService;
import br.com.plastecno.service.dao.PagamentoDAO;
import br.com.plastecno.service.entity.Pagamento;
import br.com.plastecno.service.exception.BusinessException;
import br.com.plastecno.service.wrapper.Periodo;
import br.com.plastecno.validacao.ValidadorInformacao;

@Stateless
public class PagamentoServiceImpl implements PagamentoService {

	@PersistenceContext(name = "plastecno")
	private EntityManager entityManager;

	private PagamentoDAO pagamentoDAO;

	@PostConstruct
	public void init() {
		pagamentoDAO = new PagamentoDAO(entityManager);
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public Integer inserir(Pagamento pagamento) throws BusinessException {
		if (pagamento == null) {
			return null;
		}

		ValidadorInformacao.validar(pagamento);
		if (pagamento.getParcela() != null && pagamento.getTotalParcelas() != null
				&& pagamento.getParcela() > pagamento.getTotalParcelas()) {
			throw new BusinessException("A parcela não pode ser maior do que o total de parcelas.");
		}

		if (pagamento.getParcela() == null && pagamento.getTotalParcelas() != null) {
			throw new BusinessException("A parcela deve ser preenchida.");
		}

		if (pagamento.getParcela() != null && pagamento.getTotalParcelas() == null) {
			throw new BusinessException("O total de parcelas devem ser preenchidos.");
		}

		return pagamentoDAO.alterar(pagamento).getId();
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public void liquidarPagamento(Integer idPagamento) {
		if (idPagamento == null) {
			return;
		}
		pagamentoDAO.liquidarPagamento(idPagamento);
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public Pagamento pesquisarById(Integer idPagamento) {
		return pagamentoDAO.pesquisarById(idPagamento);
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public List<Pagamento> pesquisarByIdPedido(Integer idPedido) {
		return pagamentoDAO.pesquisarByIdPedido(idPedido);
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public List<Pagamento> pesquisarPagamentoByPeriodo(Periodo periodo) {
		return pagamentoDAO.pesquisarPagamentoByPeriodo(periodo.getInicio(), periodo.getFim());
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public void remover(Integer idPagamento) throws BusinessException {
		if (idPagamento == null) {
			return;
		}
		pagamentoDAO.remover(new Pagamento(idPagamento));
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public void retornarLiquidacaoPagamento(Integer idPagamento) {
		if (idPagamento == null) {
			return;
		}
		pagamentoDAO.alterarPropriedade(Pagamento.class, idPagamento, "liquidado", false);
	}
}
