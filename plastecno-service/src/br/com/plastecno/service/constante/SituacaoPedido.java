package br.com.plastecno.service.constante;

public enum SituacaoPedido {
	DIGITACAO ("PEDIDO EM DIGITAÇÃO") ,
	ORCAMENTO ("PEDIDO EM ORÇAMENTO"),
	ENVIADO ("PEDIDO ENVIADO"),
	CANCELADO ("PEDIDO CANCELADO");
	
	private String descricao;
	
	private SituacaoPedido(String descricao){
		this.descricao = descricao;
	}
	
	public String getDescricao(){
		return this.descricao;
	}
}
