package br.com.plastecno.service;

import java.util.List;

import javax.ejb.Local;

import br.com.plastecno.service.entity.Pagamento;
import br.com.plastecno.service.exception.BusinessException;
import br.com.plastecno.service.wrapper.Periodo;

@Local
public interface PagamentoService {

	Pagamento gerarPagamentoItemPedido(Integer idItemPedido);

	Integer inserir(Pagamento pagamento) throws BusinessException;

	void inserirPagamentoItemPedido(Pagamento pagamento) throws BusinessException;

	void liquidarPagamento(Integer idPagamento);

	Pagamento pesquisarById(Integer idPagamento);

	List<Pagamento> pesquisarByIdPedido(Integer idPedido);

	List<Pagamento> pesquisarPagamentoByIdFornecedor(Integer idFornecedor, Periodo periodo);

	List<Pagamento> pesquisarPagamentoByIdPedido(Integer idPedido);

	List<Pagamento> pesquisarPagamentoByNF(Integer numeroNF);

	List<Pagamento> pesquisarPagamentoByPeriodo(Periodo periodo);

	void remover(Integer idPagamento) throws BusinessException;

	void retornarLiquidacaoPagamento(Integer idPagamento);
}
