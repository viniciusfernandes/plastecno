package br.com.plastecno.service;

import java.util.List;

import javax.ejb.Local;

import br.com.plastecno.service.constante.FormaMaterial;
import br.com.plastecno.service.constante.SituacaoReservaEstoque;
import br.com.plastecno.service.entity.Item;
import br.com.plastecno.service.entity.ItemEstoque;
import br.com.plastecno.service.entity.ItemPedido;
import br.com.plastecno.service.entity.Material;
import br.com.plastecno.service.exception.BusinessException;

@Local
public interface EstoqueService {

	double calcularPrecoCustoItemEstoque(Item filtro);

	Double calcularPrecoMinimoItemEstoque(Item filtro) throws BusinessException;

	Double calcularValorEstoque(Integer idMaterial, FormaMaterial formaMaterial);

	void cancelarReservaEstoqueByIdPedido(Integer idPedido) throws BusinessException;

	boolean contemItemPedidoReservado(Integer idPedido);

	void devolverEstoqueItemPedido(List<Integer[]> listaItemDevolucao, Integer idPedido) throws BusinessException;

	void devolverItemCompradoEstoqueByIdPedido(Integer idPedido) throws BusinessException;

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

	List<Material> pesquisarMateriaEstoque(String sigla);

	String pesquisarNcmItemEstoque(Integer idMaterial, FormaMaterial formaMaterial);

	String pesquisarNcmItemEstoque(ItemEstoque configuracao);

	List<ItemEstoque> pesquisarPecaByDescricao(String descricao);

	Double pesquisarPrecoMedioByIdItemEstoque(Integer idItemEstoque);

	double pesquisarPrecoMedioItemEstoque(Item filtro);

	void reajustarPrecoItemEstoque(ItemEstoque itemReajustado) throws BusinessException;

	Integer recepcionarItemCompra(Integer idItemPedidoCompra, Integer quantidadeRecepcionada) throws BusinessException;

	Integer recepcionarItemCompra(Integer idItemPedidoCompra, Integer quantidadeRecepcionada, String ncm)
			throws BusinessException;

	Integer recortarItemEstoque(ItemEstoque itemEstoque) throws BusinessException;

	void redefinirItemEstoque(ItemEstoque itemEstoque) throws BusinessException;

	boolean reservarItemPedido(Integer idPedido) throws BusinessException;

	SituacaoReservaEstoque reservarItemPedido(ItemPedido itemPedido) throws BusinessException;
}
