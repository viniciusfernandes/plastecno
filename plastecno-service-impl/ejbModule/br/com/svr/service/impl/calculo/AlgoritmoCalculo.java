package br.com.svr.service.impl.calculo;

import br.com.svr.service.calculo.exception.AlgoritmoCalculoException;
import br.com.svr.service.entity.ItemPedido;

public interface AlgoritmoCalculo {
	double calcular(ItemPedido itemPedido) throws AlgoritmoCalculoException;
}
