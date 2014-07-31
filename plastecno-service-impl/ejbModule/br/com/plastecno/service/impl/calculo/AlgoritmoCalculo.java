package br.com.plastecno.service.impl.calculo;

import br.com.plastecno.service.calculo.exception.AlgoritmoCalculoException;
import br.com.plastecno.service.entity.ItemPedido;

public interface AlgoritmoCalculo {
	double calcular(ItemPedido itemPedido) throws AlgoritmoCalculoException;
}
