package br.com.svr.service.impl.calculo;

import br.com.svr.service.entity.ItemPedido;

public class AlgoritmoCalculoVolumeCH implements AlgoritmoCalculo {

	@Override
	public double calcular(ItemPedido itemPedido) {
		return itemPedido.getMedidaExterna() * itemPedido.getMedidaInterna() * itemPedido.getComprimento();
	}

}
