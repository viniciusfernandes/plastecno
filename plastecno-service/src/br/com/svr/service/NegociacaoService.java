package br.com.svr.service;

import java.util.List;

import br.com.svr.service.constante.crm.CategoriaNegociacao;
import br.com.svr.service.constante.crm.TipoNaoFechamento;
import br.com.svr.service.entity.crm.IndicadorCliente;
import br.com.svr.service.entity.crm.Negociacao;
import br.com.svr.service.exception.BusinessException;
import br.com.svr.service.wrapper.RelatorioWrapper;

public interface NegociacaoService {

	Integer aceitarNegocicacaoEOrcamentoByIdNegociacao(Integer idNegociacao) throws BusinessException;

	void alterarCategoria(Integer idNegociacao, CategoriaNegociacao categoriaNegociacao) throws BusinessException;

	void alterarNegociacaoAbertaIndiceConversaoValorByIdCliente(Integer idCliente, Double indiceQuantidade,
			Double indiceValor);

	void alterarSituacaoNegociacaoAceite(Integer idNegociacao);

	double calcularValorCategoriaNegociacaoAberta(Integer idVendedor, CategoriaNegociacao categoria);

	Integer cancelarNegocicacao(Integer idNegociacao, TipoNaoFechamento tipoNaoFechamento) throws BusinessException;

	void gerarIndicadorCliente() throws BusinessException;

	void gerarNegociacaoInicial() throws BusinessException;

	RelatorioWrapper<CategoriaNegociacao, Negociacao> gerarRelatorioNegociacao(Integer idVendedor);

	Integer inserirNegociacao(Integer idOrcamento, Integer idCliente, Integer idVendedor) throws BusinessException;

	void inserirObservacao(Integer idNegociacao, String observacao) throws BusinessException;

	Negociacao pesquisarById(Integer idNegociacao);

	Integer pesquisarIdNegociacaoByIdOrcamento(Integer idOrcamento);

	IndicadorCliente pesquisarIndicadorByIdCliente(Integer idCliente);

	List<Negociacao> pesquisarNegociacaoAbertaByIdVendedor(Integer idVendedor);

	Negociacao pesquisarNegociacaoByIdOrcamento(Integer idOrcamento);

	String pesquisarObservacao(Integer idNegociacao);

	void removerNegociacaoByIdOrcamento(Integer idOrcamento);
}
