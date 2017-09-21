package br.com.plastecno.service.impl.calculo;

import br.com.plastecno.service.calculo.exception.AlgoritmoCalculoException;
import br.com.plastecno.service.entity.ItemPedido;

public class AlgoritmoCalculoPrecoPeca implements AlgoritmoCalculo {

	@Override
	public double calcular(ItemPedido itemPedido) throws AlgoritmoCalculoException {
		CalculadoraItem.validarVolume(itemPedido);
		return itemPedido.calcularPrecoTotalVenda();
	}

}
