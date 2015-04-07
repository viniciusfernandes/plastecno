package br.com.plastecno.service;

import javax.ejb.Local;

import br.com.plastecno.service.entity.Comissao;
import br.com.plastecno.service.exception.BusinessException;

@Local
public interface ComissaoService {

	Integer inserir(Comissao comissao) throws BusinessException;

	Comissao pesquisarById(Integer idComissao);

	Comissao pesquisarComissaoVigente(Integer idVendedor, Integer idMaterial, Integer idFormaMaterial);

}
