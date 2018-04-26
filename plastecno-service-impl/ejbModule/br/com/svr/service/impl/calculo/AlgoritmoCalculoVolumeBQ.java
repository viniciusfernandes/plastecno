package br.com.svr.service.impl.calculo;

import br.com.svr.service.entity.ItemPedido;

public class AlgoritmoCalculoVolumeBQ implements AlgoritmoCalculo {

	@Override
	public double calcular(ItemPedido itemPedido) {
		return Math.pow(itemPedido.getMedidaExterna(), 2) * itemPedido.getComprimento();
	}
	
}
