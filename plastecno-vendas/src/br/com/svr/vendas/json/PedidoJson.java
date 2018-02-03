package br.com.svr.vendas.json;

import br.com.svr.service.entity.Pedido;

public class PedidoJson {
    private final Double valorPedido;
    private final Double valorPedidoIPI;

    public PedidoJson(Pedido pedido) {
        this.valorPedido = pedido == null ? (Double) 0d : pedido.getValorPedido();
        this.valorPedidoIPI = pedido == null ? (Double) 0d : pedido.getValorPedidoIPI();
    }

    public Double getValorPedido() {
        return valorPedido;
    }

    public Double getValorPedidoIPI() {
        return valorPedidoIPI;
    }

}
