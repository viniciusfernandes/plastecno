package br.com.plastecno.service.impl.calculo;

import br.com.plastecno.service.entity.ItemPedido;

public class AlgoritmoCalculoVolumeCH implements AlgoritmoCalculo {

	@Override
	public double calcular(ItemPedido itemPedido) {
		return itemPedido.getMedidaExterna() * itemPedido.getMedidaInterna() * itemPedido.getComprimento();
	}

}
