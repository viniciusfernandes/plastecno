package br.com.plastecno.service.impl.calculo;

import br.com.plastecno.service.entity.ItemPedido;

public class AlgoritmoCalculoVolumeBQ implements AlgoritmoCalculo {

	@Override
	public double calcular(ItemPedido itemPedido) {
		return Math.pow(itemPedido.getMedidaExterna(), 2) * itemPedido.getComprimento();
	}
	
}
