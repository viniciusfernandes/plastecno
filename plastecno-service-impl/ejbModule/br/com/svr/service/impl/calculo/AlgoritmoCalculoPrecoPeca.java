package br.com.svr.service.impl.calculo;

import br.com.svr.service.calculo.exception.AlgoritmoCalculoException;
import br.com.svr.service.entity.ItemPedido;

public class AlgoritmoCalculoPrecoPeca implements AlgoritmoCalculo {

	@Override
	public double calcular(ItemPedido itemPedido) throws AlgoritmoCalculoException {
		CalculadoraItem.validarVolume(itemPedido);
		return itemPedido.calcularPrecoTotalVenda();
	}

}
