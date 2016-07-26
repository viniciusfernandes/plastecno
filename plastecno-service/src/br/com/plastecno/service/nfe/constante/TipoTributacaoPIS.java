package br.com.plastecno.service.nfe.constante;

public enum TipoTributacaoPIS {
	ICMS_1("1 - Oper. Tributável BC = valor operação alíquota normal", 1), 
	ICMS_2("2 - Oper. Tributável BC = valor operação alíquota diferenciada", 2),
	ICMS_3("3 - Oper. Tributável BC = qtde vendida X alíquota por unid. produto", 3),
	ICMS_4("4 - Oper. Tributável com tributação monofásica alíquota zero", 4),
	ICMS_6("6 - Oper. Tributável alíquota zero", 6),
	ICMS_7("7 - Oper. isenta contribuição", 7),
	ICMS_8("8 - Oper. sem incidência contribuição", 8),
	ICMS_9("9 - Oper. com suspensão contribuição", 9);
	
	private Integer codigo;
	private String descricao;

	private TipoTributacaoPIS(String descricao, Integer codigo) {
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
