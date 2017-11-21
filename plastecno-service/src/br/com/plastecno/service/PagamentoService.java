package br.com.plastecno.service;

import java.util.Date;
import java.util.List;

import javax.ejb.Local;

import br.com.plastecno.service.entity.Pagamento;
import br.com.plastecno.service.exception.BusinessException;
import br.com.plastecno.service.wrapper.Periodo;
import br.com.plastecno.service.wrapper.RelatorioWrapper;

@Local
public interface PagamentoService {

	Pagamento gerarPagamentoItemCompra(Integer idItemPedido);

	RelatorioWrapper<String, Pagamento> gerarRelatorioPagamento(List<Pagamento> lPagamento, Periodo periodo);

	Integer inserirPagamento(Pagamento pagamento) throws BusinessException;

	void inserirPagamentoParceladoItemCompra(Integer numeroNF, Double valorNF, Date dataVencimento, Date dataEmissao,
			Integer modalidadeFrete, List<Integer> listaIdItem) throws BusinessException;

	void inserirPagamentoParceladoItemCompra(Pagamento pagamento) throws BusinessException;

	void liquidarPagamento(Integer idPagamento, boolean liquidado);

	void liquidarPagamentoNFParcelada(Integer numeroNF, Integer idFornecedor, Integer parcela, boolean liquidado);

	Pagamento pesquisarById(Integer idPagamento);

	List<Pagamento> pesquisarByIdPedido(Integer idPedido);

	Integer pesquisarIdItemPedidoByIdPagamento(Integer idPagamento);

	List<Pagamento> pesquisarPagamentoByIdFornecedor(Integer idFornecedor, Periodo periodo);

	List<Pagamento> pesquisarPagamentoByIdPedido(Integer idPedido);

	List<Pagamento> pesquisarPagamentoByNF(Integer numeroNF);

	List<Pagamento> pesquisarPagamentoByPeriodo(Periodo periodo);

	List<Pagamento> pesquisarPagamentoByPeriodo(Periodo periodo, boolean apenasInsumos);

	int pesquisarQuantidadeTotalPagaByIdItem(Integer idItemPedido);

	void remover(Integer idPagamento) throws BusinessException;

	void removerPagamentoPaceladoByIdPagamento(Integer idItemPedido) throws BusinessException;

	void retornarLiquidacaoPagamento(Integer idPagamento) throws BusinessException;

	void retornarLiquidacaoPagamentoNFParcelada(Integer numeroNF, Integer idFornecedor, Integer parcela)
			throws BusinessException;

}
