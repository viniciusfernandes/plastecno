package br.com.svr.service;

import java.util.List;

import javax.ejb.Local;

import br.com.svr.service.calculo.exception.AlgoritmoCalculoException;
import br.com.svr.service.constante.FormaMaterial;
import br.com.svr.service.constante.SituacaoReservaEstoque;
import br.com.svr.service.entity.Item;
import br.com.svr.service.entity.ItemEstoque;
import br.com.svr.service.entity.ItemPedido;
import br.com.svr.service.entity.ItemReservado;
import br.com.svr.service.entity.Material;
import br.com.svr.service.exception.BusinessException;

@Local
public interface EstoqueService {

	Integer adicionarQuantidadeRecepcionadaItemCompra(Integer idItemCompra, Integer quantidadeRecepcionada)
			throws BusinessException;

	Integer adicionarQuantidadeRecepcionadaItemCompra(Integer idItemPedidoCompra, Integer quantidadeRecepcionada,
			String ncm) throws BusinessException;

	Integer alterarQuantidadeRecepcionadaItemCompra(Integer idItemCompra, Integer quantidadeRecepcionada)
			throws BusinessException;

	double calcularPrecoCustoItemEstoque(Item filtro);

	Double calcularPrecoMinimoItemEstoque(Item filtro) throws BusinessException;

	Double calcularValorEstoque(Integer idMaterial, FormaMaterial formaMaterial);

	void cancelarReservaEstoqueByIdPedido(Integer idPedido) throws BusinessException;

	boolean contemItemPedidoReservado(Integer idPedido);

	void devolverEstoqueItemPedido(List<Integer[]> listaItemDevolucao, Integer idPedido) throws BusinessException;

	void devolverItemCompradoEstoqueByIdPedido(Integer idPedido) throws BusinessException;

	void devolverItemEstoque(Integer idItemPedido);

	void empacotarPedido(List<Integer> listaIdPedido);

	void inserirConfiguracaoEstoque(ItemEstoque limite) throws BusinessException;

	boolean inserirConfiguracaoNcmEstoque(Integer idMaterial, FormaMaterial formaMaterial, String ncm);

	Integer inserirItemEstoque(ItemEstoque itemEstoque) throws BusinessException;

	Integer inserirItemPedido(Integer idItemPedido) throws BusinessException;

	Integer pesquisarIdItemEstoque(Item filtro);

	List<ItemEstoque> pesquisarItemEstoque(Integer idMaterial, FormaMaterial formaMaterial);

	ItemEstoque pesquisarItemEstoque(Item filtro);

	ItemEstoque pesquisarItemEstoqueById(Integer idItemEstoque);

	List<ItemEstoque> pesquisarItemEstoqueEscasso();

	List<ItemReservado> pesquisarItemReservadoByIdItemPedido(Integer idItemPedido);

	List<Material> pesquisarMateriaEstoque(String sigla);

	String pesquisarNcmItemEstoque(Integer idMaterial, FormaMaterial formaMaterial);

	String pesquisarNcmItemEstoque(ItemEstoque item);

	List<ItemEstoque> pesquisarPecaByDescricao(String descricao);

	Double pesquisarPrecoMedioByIdItemEstoque(Integer idItemEstoque);

	double pesquisarPrecoMedioItemEstoque(Item filtro);

	Integer pesquisarQuantidadeByIdItemEstoque(Integer idItemEstoque);

	void reajustarPrecoItemEstoque(ItemEstoque itemReajustado) throws BusinessException;

	Integer recalcularEstoqueItemCompra(Integer idItemCompra, Integer quantidade) throws BusinessException;

	ItemEstoque recalcularValorItemEstoque(ItemEstoque itemEstoque) throws AlgoritmoCalculoException;

	Integer recortarItemEstoque(ItemEstoque itemEstoque) throws BusinessException;

	void redefinirItemEstoque(ItemEstoque itemEstoque) throws BusinessException;

	void reinserirItemPedidoEstoqueByIdItem(Integer idItemPedido) throws BusinessException;

	Integer removerEstoqueItemCompra(Integer idItemCompra, Integer quantidadeRemovida) throws BusinessException;

	boolean reservarItemPedido(Integer idPedido) throws BusinessException;

	SituacaoReservaEstoque reservarItemPedido(ItemPedido itemPedido) throws BusinessException;
}
