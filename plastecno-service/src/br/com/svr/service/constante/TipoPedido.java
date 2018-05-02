package br.com.svr.service.constante;

public enum TipoPedido {
	REPRESENTACAO("Pedido por Representação"), 
	REVENDA("Pedido de Revenda"), 
	COMPRA("Pedido de Compra");

	private String descricao;

	private TipoPedido(String descricao) {
		this.descricao = descricao;
	}

	public String getDescricao() {
		return this.descricao;
	}

	public static boolean isCompra(TipoPedido tipoPedido) {
		return COMPRA.equals(tipoPedido);
	}

	public static boolean isVenda(TipoPedido tipoPedido) {
		return !isCompra(tipoPedido);
	}

	public boolean isCompra() {
		return COMPRA.equals(this);
	}

	public boolean isVenda() {
		return !isCompra(this);
	}

	public boolean isRevenda() {
		return REVENDA.equals(this);
	}
}
