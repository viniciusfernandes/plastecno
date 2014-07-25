package br.com.plastecno.vendas.json;

import br.com.plastecno.service.entity.Pedido;

public class PedidoJson {
    private final Double valorPedido;
    private final Double valorPedidoIPI;

    public PedidoJson(Pedido pedido) {
        this.valorPedido = pedido.getValorPedido();
        this.valorPedidoIPI = pedido.getValorPedidoIPI();
    }

    public Double getValorPedido() {
        return valorPedido;
    }

    public Double getValorPedidoIPI() {
        return valorPedidoIPI;
    }

}
