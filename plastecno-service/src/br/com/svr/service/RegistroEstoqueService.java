package br.com.svr.service;

import java.util.List;

import br.com.svr.service.entity.RegistroEstoque;
import br.com.svr.service.exception.BusinessException;
import br.com.svr.service.wrapper.PaginacaoWrapper;

public interface RegistroEstoqueService {

	void inserirRegistroAlteracaoValorItemEstoque(Integer idItemEstoque, Integer idUsuario, String nomeUsuario,
			Double valorAnterior, Double valorPosterior);

	void inserirRegistroAlteracaoValorItemEstoque(Integer idItemEstoque, Integer idUsuario, String nomeUsuario,
			Integer quantidadeAnterior, Integer quantidadePosterior, Double valorAnterior, Double valorPosterior);

	void inserirRegistroConfiguracaoItemEstoque(Integer idItemEstoque);

	void inserirRegistroEntradaDevolucaoItemVenda(Integer idItemEstoque, Integer idItemPedido, Integer quantidade,
			Integer sequencialItem);

	void inserirRegistroEntradaItemCompra(Integer idItemEstoque, Integer idItemPedido, Integer quantidade,
			Integer sequencialItem);

	void inserirRegistroSaidaItemVenda(Integer idItemEstoque, Integer idItemPedido, Integer quantidade,
			Integer sequencialItem);

	PaginacaoWrapper<RegistroEstoque> paginarRegistroByIdItemEstoque(Integer idItemEstoque, Integer indiceInicial,
			Integer numMaxRegistros);

	List<RegistroEstoque> pesquisarRegistroByIdItemEstoque(Integer idItemEstoque);

	List<RegistroEstoque> pesquisarRegistroByIdItemEstoque(Integer idItemEstoque, Integer indiceInicial,
			Integer numeroMaxRegistros);

	List<RegistroEstoque> pesquisarRegistroByIdItemPedido(Integer idItemPedido);

	List<RegistroEstoque> pesquisarRegistroByIdPedido(Integer idPedido);

	void removerRegistroExpirado() throws BusinessException;
}
