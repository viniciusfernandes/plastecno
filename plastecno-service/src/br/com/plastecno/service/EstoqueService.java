package br.com.plastecno.service;

import java.util.List;

import javax.ejb.Local;

import br.com.plastecno.service.constante.FormaMaterial;
import br.com.plastecno.service.entity.ItemEstoque;
import br.com.plastecno.service.entity.ItemPedido;
import br.com.plastecno.service.entity.Material;
import br.com.plastecno.service.exception.BusinessException;

@Local
public interface EstoqueService {
	void cancelarReservaEstoqueByIdPedido(Integer idPedido) throws BusinessException;

	boolean contemItemPedidoReservado(Integer idPedido);

	void devolverItemCompradoEstoqueByIdPedido(Integer idPedido) throws BusinessException;

	void empacotarItemPedido(Integer idItemPedido);

	Integer inserirItemEstoque(ItemEstoque itemEstoque) throws BusinessException;

	Integer inserirItemPedido(Integer idItemPedido) throws BusinessException;

	List<ItemEstoque> pesquisarItemEstoque(Integer idMaterial, FormaMaterial formaMaterial);

	ItemEstoque pesquisarItemEstoque(Integer idMaterial, FormaMaterial formaMaterial, Double medidaExterna,
			Double medidaInterna, Double comprimento);

	ItemEstoque pesquisarItemEstoque(Integer idMaterial, FormaMaterial formaMaterial, String descricaoPeca)
			throws BusinessException;

	ItemEstoque pesquisarItemEstoqueById(Integer idItemEstoque);

	List<Material> pesquisarMateriaEstoque(String sigla);

	void redefinirItemEstoque(ItemEstoque itemEstoque) throws BusinessException;

	boolean reservarItemPedido(Integer idPedido) throws BusinessException;

	boolean reservarItemPedido(ItemPedido itemPedido) throws BusinessException;
}
