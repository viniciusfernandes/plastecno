package br.com.plastecno.service.nfe.constante;

public enum TipoModalidadeFrete {
	EMITENTE("0 - Por conta do emitente", 0), 
	DESTINATARIO_REMETENTE("1 - Por conta do destinatário/remetente", 1), 
	TERCEIROS("2 - Por conta de terceiros", 2), 
	SEM_FRETE("9 - Sem frete", 9);
	
	private Integer codigo;
	private String descricao;

	private TipoModalidadeFrete(String descricao, Integer codigo) {
		this.descricao = descricao;
		this.codigo = codigo;
	}

	public Integer getCodigo() {
		return codigo;
	}

	public String getDescricao() {
		return descricao;
	}

}
