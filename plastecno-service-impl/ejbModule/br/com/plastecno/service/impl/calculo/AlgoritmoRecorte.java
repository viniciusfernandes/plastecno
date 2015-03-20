package br.com.plastecno.service.impl.calculo;

import br.com.plastecno.service.entity.ItemEstoque;
import br.com.plastecno.service.impl.calculo.exception.RecorteInvalidoException;
import br.com.plastecno.service.wrapper.RecorteItemEstoqueWrapper;

public interface AlgoritmoRecorte {
	RecorteItemEstoqueWrapper recortar(ItemEstoque itemEstoque, ItemEstoque itemRecortado) throws RecorteInvalidoException;
}
