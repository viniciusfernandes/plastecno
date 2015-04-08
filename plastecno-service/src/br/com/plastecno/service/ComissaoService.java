package br.com.plastecno.service;

import java.util.List;

import javax.ejb.Local;

import br.com.plastecno.service.entity.Comissao;
import br.com.plastecno.service.exception.BusinessException;

@Local
public interface ComissaoService {

	Integer inserir(Comissao comissao) throws BusinessException;

	Integer inserirComissaoVendedor(Integer idVendedor, Double valorComissao) throws BusinessException;

	Comissao pesquisarById(Integer idComissao);

	Comissao pesquisarComissaoVigente(Integer idVendedor, Integer idMaterial, Integer idFormaMaterial);

	List<Comissao> pesquisarComissaoByIdVendedor(Integer idVendedor);

	Double pesquisarValorComissaoVigenteVendedor(Integer idVendedor);

}
