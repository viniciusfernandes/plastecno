package br.com.svr.service.constante;

public enum TipoOperacaoEstoque {

	ENTRADA_PEDIDO_COMPRA(0, "ENTRADA_PEDIDO_COMPRA"),
	SAIDA_PEDIDO_VENDA(1, "SAIDA_PEDIDO_VENDA"),
	SAIDA_DEVOLUCAO_COMPRA (2, "SAIDA_DEVOLUCAO_COMPRA"),
	ENTRADA_DEVOLUCAO_VENDA(3, "ENTRADA_DEVOLUCAO_VENDA"),
	ENTRADA_MANUAL(4, "ENTRADA_MANUAL"),
	SAIDA_MANUAL(5, "SAIDA_MANUAL");
	
	private final Integer codigo;
	private final String descricao;
	
	private TipoOperacaoEstoque(Integer codigo,String descricao){
		this.codigo=codigo;
		this.descricao=descricao;
	}

	public Integer getCodigo() {
		return codigo;
	}

	public String getDescricao() {
		return descricao;
	}
	
	public boolean isSaida(){
		return this.equals(SAIDA_DEVOLUCAO_COMPRA)
				|| this.equals(SAIDA_MANUAL)
				|| this.equals(SAIDA_PEDIDO_VENDA);
	}
	
	public boolean isEntrada(){
		return this.equals(ENTRADA_DEVOLUCAO_VENDA)
				|| this.equals(ENTRADA_MANUAL)
				|| this.equals(ENTRADA_PEDIDO_COMPRA);
	}
	
}
