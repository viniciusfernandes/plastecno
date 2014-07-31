package br.com.plastecno.service.impl.calculo;

import br.com.plastecno.service.entity.ItemPedido;

public class AlgoritmoCalculoVolumeTB implements AlgoritmoCalculo {

	@Override
	public double calcular(ItemPedido itemPedido) {
		final Double raioExterno = itemPedido.getMedidaExterna()/2d;
		final Double raioInterno = itemPedido.getMedidaInterna()/2d;
		return Math.PI 
				* (Math.pow(raioExterno, 2) - Math.pow(raioInterno, 2)) 
				* itemPedido.getComprimento();
	}

}
