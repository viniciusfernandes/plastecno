package br.com.svr.service;

import java.util.List;

import br.com.svr.service.entity.RegistroEstoque;

public interface RegistroEstoqueService {

	void inserirRegistroEntradaDevolucaoItemVenda(Integer idItemEstoque, Integer idItemPedido, Integer quantidade);

	void inserirRegistroEntradaItemCompra(Integer idItemEstoque, Integer idItemPedido, Integer quantidade);

	void inserirRegistroSaidaItemVenda(Integer idItemEstoque, Integer idItemPedido, Integer quantidade);

	List<RegistroEstoque> pesquisarRegistroByIdItemEstoque(Integer idItemEstoque);

	List<RegistroEstoque> pesquisarRegistroByIdPedido(Integer idPedido);

	List<RegistroEstoque> pesquisarRegistroEstoqueByIdItemPedido(Integer idItemPedido);
}
