package br.com.svr.service.impl.calculo;

import br.com.svr.service.entity.ItemEstoque;
import br.com.svr.service.impl.calculo.exception.RecorteInvalidoException;
import br.com.svr.service.wrapper.RecorteItemEstoqueWrapper;

public interface AlgoritmoRecorte {
	RecorteItemEstoqueWrapper recortar(ItemEstoque itemEstoque, ItemEstoque itemRecortado) throws RecorteInvalidoException;
}
