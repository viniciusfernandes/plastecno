package br.com.plastecno.service.constante;

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
	
	public static boolean isCompra(TipoPedido tipoPedido){
		return COMPRA.equals(tipoPedido);
	}
	
	public static boolean isVenda(TipoPedido tipoPedido){
		return !isCompra(tipoPedido);
	}
}
