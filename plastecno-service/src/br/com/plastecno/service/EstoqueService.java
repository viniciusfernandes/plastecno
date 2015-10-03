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

	void devolverItemCompradoEstoqueByIdPedido(Integer idPedido) throws BusinessException;

	void empacotarPedido(List<Integer> listaIdPedido);

	Integer inserirItemEstoque(ItemEstoque itemEstoque) throws BusinessException;

	Integer inserirItemPedido(Integer idItemPedido) throws BusinessException;

	void inserirLimiteMinimoPadrao(ItemEstoque limite) throws BusinessException;

	Integer pesquisarIdItemEstoque(Item filtro);

	List<ItemEstoque> pesquisarItemEstoque(Integer idMaterial, FormaMaterial formaMaterial);

	ItemEstoque pesquisarItemEstoque(Item filtro);

	ItemEstoque pesquisarItemEstoqueById(Integer idItemEstoque);

	List<ItemEstoque> pesquisarItemEstoqueEscasso();

	List<Material> pesquisarMateriaEstoque(String sigla);

	List<ItemEstoque> pesquisarPecaByDescricao(String descricao);

	double pesquisarPrecoMedioItemEstoque(Item filtro);

	Integer recepcionarItemCompra(Integer idItemPedido) throws BusinessException;

	Integer recepcionarParcialmenteItemCompra(Integer idItemPedido, Integer quantidadeParcial) throws BusinessException;

	Integer recortarItemEstoque(ItemEstoque itemEstoque) throws BusinessException;

	void redefinirItemEstoque(ItemEstoque itemEstoque) throws BusinessException;

	boolean reservarItemPedido(Integer idPedido) throws BusinessException;

	SituacaoReservaEstoque reservarItemPedido(ItemPedido itemPedido) throws BusinessException;
}
