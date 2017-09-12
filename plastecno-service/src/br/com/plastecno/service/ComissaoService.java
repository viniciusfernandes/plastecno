package br.com.plastecno.service;

import java.util.List;

import javax.ejb.Local;

import br.com.plastecno.service.constante.FormaMaterial;
import br.com.plastecno.service.constante.TipoPedido;
import br.com.plastecno.service.entity.Comissao;
import br.com.plastecno.service.exception.BusinessException;

@Local
public interface ComissaoService {

	Integer inserir(Comissao comissao) throws BusinessException;

	Integer inserirComissaoProduto(FormaMaterial formaMaterial, Integer idMaterial, Double valorComissao)
			throws BusinessException;

	Integer inserirComissaoRevendaVendedor(Integer idVendedor, Double valorComissao) throws BusinessException;

	Integer inserirComissaoVendedor(Integer idVendedor, Double comissaoRevenda, Double comissaoRepresentacao)
			throws BusinessException;

	Double pesquisarAliquotaComissaoByIdVendedor(Integer idVendedor, TipoPedido tipoPedido);

	Comissao pesquisarAliquotaComissaoVigenteVendedor(Integer idVendedor);

	Comissao pesquisarById(Integer idComissao);

	List<Comissao> pesquisarComissaoByIdVendedor(Integer idVendedor);

	List<Comissao> pesquisarComissaoByProduto(FormaMaterial formaMaterial, Integer idMaterial);

	Comissao pesquisarComissaoVigenteProduto(Integer idMaterial, Integer idFormaMaterial);

	Comissao pesquisarComissaoVigenteVendedor(Integer idVendedor);

}
