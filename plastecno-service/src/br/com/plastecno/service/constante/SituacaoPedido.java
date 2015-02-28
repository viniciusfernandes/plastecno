package br.com.plastecno.service.constante;

public enum SituacaoPedido {
	DIGITACAO ("PEDIDO EM DIGITAÇÃO") ,
	ORCAMENTO ("PEDIDO EM ORÇAMENTO"),
	ENVIADO ("PEDIDO ENVIADO"),
	CANCELADO ("PEDIDO CANCELADO"),
	COMPRA_PENDENTE_RECEBIMENTO("COMPRA COM PENDÊNCIA DE RECEBIMENTO"),
	COMPRA_RECEBIDA("COMPRA RECEBIDA"),
	EMPACOTAMENTO("PEDIDO PARA EMPACOTAR"),
	ITEM_PENDENTE_RESERVA("ITEM COM PENDENCIA DE RESERVA"),
	EMPACOTADO("PEDIDO EMPACOTADO");
	
	private String descricao;
	
	private SituacaoPedido(String descricao){
		this.descricao = descricao;
	}
	
	public String getDescricao(){
		return this.descricao;
	}
}
