package br.com.svr.service;

import java.util.List;

import br.com.svr.service.entity.RegistroEstoque;

public interface RegistroEstoqueService {

	void inserirRegistroAlteracaoValorItemEstoque(Integer idItemEstoque, Integer idUsuario, String nomeUsuario, Double valorAnterior, Double valorPosterior);

	void inserirRegistroAlteracaoValorItemEstoque(Integer idItemEstoque, Integer idUsuario, String nomeUsuario,
			Integer quantidadeAnterior, Integer quantidadePosterior, Double valorAnterior, Double valorPosterior);

	void inserirRegistroConfiguracaoItemEstoque(Integer idItemEstoque);

	void inserirRegistroEntradaDevolucaoItemVenda(Integer idItemEstoque, Integer idItemPedido, Integer quantidade, Integer sequencialItem);

	void inserirRegistroEntradaItemCompra(Integer idItemEstoque, Integer idItemPedido, Integer quantidade, Integer sequencialItem);

	void inserirRegistroSaidaItemVenda(Integer idItemEstoque, Integer idItemPedido, Integer quantidade, Integer sequencialItem);

	List<RegistroEstoque> pesquisarRegistroByIdItemEstoque(Integer idItemEstoque);

	List<RegistroEstoque> pesquisarRegistroByIdItemPedido(Integer idItemPedido);

	List<RegistroEstoque> pesquisarRegistroByIdPedido(Integer idPedido);
}
