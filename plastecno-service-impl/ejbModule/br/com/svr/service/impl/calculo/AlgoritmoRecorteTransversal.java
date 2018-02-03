package br.com.svr.service.impl.calculo;

import br.com.svr.service.entity.ItemEstoque;
import br.com.svr.service.impl.calculo.exception.RecorteInvalidoException;
import br.com.svr.service.wrapper.RecorteItemEstoqueWrapper;

public class AlgoritmoRecorteTransversal implements AlgoritmoRecorte {

	@Override
	public RecorteItemEstoqueWrapper recortar(ItemEstoque itemEstoque, ItemEstoque itemRecortado)
			throws RecorteInvalidoException {
		ItemEstoque itemNovo = itemEstoque.clone();
		itemNovo.setComprimento(itemEstoque.getComprimento() - itemRecortado.getComprimento());
		itemNovo.setQuantidade(itemRecortado.getQuantidade());
		RecorteItemEstoqueWrapper recorte = new RecorteItemEstoqueWrapper(itemEstoque, itemRecortado);
		recorte.addItemNovo(itemNovo);
		return recorte;
	}

}
