package br.com.svr.service.impl.calculo;

import br.com.svr.service.entity.ItemEstoque;
import br.com.svr.service.impl.calculo.exception.RecorteInvalidoException;
import br.com.svr.service.wrapper.RecorteItemEstoqueWrapper;

public class AlgoritmoRecorteChapa implements AlgoritmoRecorte {

	@Override
	public RecorteItemEstoqueWrapper recortar(ItemEstoque itemEstoque, ItemEstoque itemRecortado)
			throws RecorteInvalidoException {

		RecorteItemEstoqueWrapper recorte = new RecorteItemEstoqueWrapper(itemEstoque, itemRecortado);

		boolean isRecorteDoisItens = itemEstoque.getComprimento() > itemRecortado.getComprimento();
		if (!isRecorteDoisItens) {
			ItemEstoque itemNovo = itemEstoque.clone();
			itemNovo.setComprimento(itemEstoque.getComprimento() - itemRecortado.getComprimento());
			itemNovo.setQuantidade(itemRecortado.getQuantidade());
			recorte.addItemNovo(itemNovo);
		} else {
			ItemEstoque item1 = itemEstoque.clone();
			ItemEstoque item2 = itemEstoque.clone();
		}
		return recorte;
	}

}
