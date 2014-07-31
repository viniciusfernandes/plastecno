package br.com.plastecno.service.impl.calculo;

import br.com.plastecno.service.entity.ItemPedido;

public class AlgoritmoCalculoVolumeBR implements AlgoritmoCalculo {
	

	@Override
	public double calcular(ItemPedido itemPedido) {
		final double raio = itemPedido.getMedidaExterna()/2d;
		return Math.PI * Math.pow(raio, 2) * itemPedido.getComprimento();
	}
	
}
