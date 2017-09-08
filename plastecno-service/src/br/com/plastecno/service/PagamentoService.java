package br.com.plastecno.service;

import java.util.List;

import javax.ejb.Local;

import br.com.plastecno.service.entity.Pagamento;
import br.com.plastecno.service.exception.BusinessException;
import br.com.plastecno.service.wrapper.Periodo;
import br.com.plastecno.service.wrapper.RelatorioWrapper;

@Local
public interface PagamentoService {

	Pagamento gerarPagamentoItemPedido(Integer idItemPedido);

	RelatorioWrapper<String, Pagamento> gerarRelatorioPagamento(Periodo periodo);

	Integer inserir(Pagamento pagamento) throws BusinessException;

	void inserirPagamentoItemPedido(Pagamento pagamento) throws BusinessException;

	void liquidarPagamento(Integer idPagamento);

	void liquidarPagamentoNFParcelada(Integer numeroNF, Integer idFornecedor, Integer parcela);

	Pagamento pesquisarById(Integer idPagamento);

	List<Pagamento> pesquisarByIdPedido(Integer idPedido);

	List<Pagamento> pesquisarPagamentoByIdFornecedor(Integer idFornecedor, Periodo periodo);

	List<Pagamento> pesquisarPagamentoByIdPedido(Integer idPedido);

	List<Pagamento> pesquisarPagamentoByNF(Integer numeroNF);

	List<Pagamento> pesquisarPagamentoByPeriodo(Periodo periodo);

	void remover(Integer idPagamento) throws BusinessException;

	void retornarLiquidacaoPagamento(Integer idPagamento) throws BusinessException;

	void retornarLiquidacaoPagamentoNFParcelada(Integer numeroNF, Integer idFornecedor, Integer parcela)
			throws BusinessException;
}
