package br.com.plastecno.service.wrapper;

import java.util.List;

import br.com.plastecno.service.entity.ItemPedido;
import br.com.plastecno.service.entity.Pedido;

public class PedidoWrapper extends ChaveValorWrapper<Pedido, List<ItemPedido>> {

	public PedidoWrapper(Pedido pedido, List<ItemPedido> listaItemPedido) {
		super(pedido, listaItemPedido);
	}

	public Pedido getPedido() {
		return this.chave;
	}
	
	public List<ItemPedido> getListaItemPedido() {
		return this.valor;
	}
}
