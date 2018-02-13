package br.com.svr.service;

import java.util.List;

import br.com.svr.service.constante.crm.CategoriaNegociacao;
import br.com.svr.service.entity.crm.Negociacao;
import br.com.svr.service.exception.BusinessException;
import br.com.svr.service.wrapper.RelatorioWrapper;

public interface NegociacaoService {

	void alterarCategoria(Integer idNegociacao, CategoriaNegociacao categoriaNegociacao) throws BusinessException;

	double calcularValorCategoriaNegociacao(Integer idVendedor, CategoriaNegociacao categoria);

	void gerarNegociacaoInicial() throws BusinessException;

	RelatorioWrapper<CategoriaNegociacao, Negociacao> gerarRelatorioNegociacao(Integer idVendedor);

	Integer inserirNegociacao(Integer idOrcamento, Integer idVendedor) throws BusinessException;

	Negociacao pesquisarById(Integer idNegociacao);

	List<Negociacao> pesquisarNegociacaoByIdVendedor(Integer idVendedor);

}
