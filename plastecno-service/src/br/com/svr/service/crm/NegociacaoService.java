package br.com.svr.service.crm;

import br.com.svr.service.entity.crm.Negociacao;
import br.com.svr.service.exception.BusinessException;

public interface NegociacaoService {

	Integer inserirNegociacao(Integer idOrcamento, Integer idVendedor) throws BusinessException;

	Negociacao pesquisarById(Integer idNegociacao);

}
