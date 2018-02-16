package br.com.svr.service;

import java.util.List;

import br.com.svr.service.constante.crm.CategoriaNegociacao;
import br.com.svr.service.constante.crm.TipoNaoFechamento;
import br.com.svr.service.entity.crm.IndiceConversao;
import br.com.svr.service.entity.crm.Negociacao;
import br.com.svr.service.exception.BusinessException;
import br.com.svr.service.wrapper.RelatorioWrapper;

public interface NegociacaoService {

	Integer aceitarNegocicacao(Integer idNegociacao) throws BusinessException;

	void alterarCategoria(Integer idNegociacao, CategoriaNegociacao categoriaNegociacao) throws BusinessException;

	double calcularValorCategoriaNegociacaoAberta(Integer idVendedor, CategoriaNegociacao categoria);

	Integer cancelarNegocicacao(Integer idNegociacao, TipoNaoFechamento tipoNaoFechamento) throws BusinessException;

	void gerarIndiceConversaoCliente() throws BusinessException;

	void gerarNegociacaoInicial() throws BusinessException;

	RelatorioWrapper<CategoriaNegociacao, Negociacao> gerarRelatorioNegociacao(Integer idVendedor);

	Integer inserirNegociacao(Integer idOrcamento, Integer idVendedor) throws BusinessException;

	Negociacao pesquisarById(Integer idNegociacao);

	IndiceConversao pesquisarIndiceConversaoByIdCliente(Integer idCliente);

	List<Negociacao> pesquisarNegociacaoAbertaByIdVendedor(Integer idVendedor);

	Negociacao pesquisarNegociacaoByIdOrcamento(Integer idOrcamento);

	void recalcularIndiceConversao(Integer idPedido, Integer idOrcamento) throws BusinessException;
}
