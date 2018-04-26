package br.com.svr.service.wrapper;

import java.util.ArrayList;
import java.util.List;

import br.com.svr.service.entity.ItemEstoque;

public class RecorteItemEstoqueWrapper {
	private final ItemEstoque itemEstocado;
	private final ItemEstoque itemRecortado;
	List<ItemEstoque> listaItemNovo;

	public RecorteItemEstoqueWrapper(ItemEstoque itemEstocado, ItemEstoque itemRecortado) {
		this.itemRecortado = itemRecortado;
		this.itemEstocado = itemEstocado;
	}

	public void addItemNovo(ItemEstoque itemNovo) {
		if (listaItemNovo == null) {
			listaItemNovo = new ArrayList<ItemEstoque>();
		}
		listaItemNovo.add(itemNovo);
	}

	public ItemEstoque getItemEstocado() {
		return itemEstocado;
	}

	public ItemEstoque getItemRecortado() {
		return itemRecortado;
	}

	public List<ItemEstoque> getListaItemNovo() {
		return listaItemNovo;
	}

}
