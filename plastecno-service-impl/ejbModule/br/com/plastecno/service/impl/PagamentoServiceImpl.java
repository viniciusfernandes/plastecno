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
		ValidadorInformacao.validar(pagamento);
		return pagamentoDAO.inserir(pagamento).getId();
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
}
