package br.com.plastecno.service.constante;

public enum TipoPedido {
	REPRESENTACAO("Pedido por Representa��o"), 
	REVENDA("Pedido de Revenda"), 
	COMPRA("Pedido de Compra");

	private String descricao;

	private TipoPedido(String descricao) {
		this.descricao = descricao;
	}

	public String getDescricao() {
		return this.descricao;
	}
}
