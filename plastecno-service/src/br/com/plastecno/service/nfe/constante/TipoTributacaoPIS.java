package br.com.plastecno.service.nfe.constante;

public enum TipoTributacaoPIS {
	ICMS_1("1 - Oper. Tribut�vel BC = valor opera��o al�quota normal", 1), 
	ICMS_2("2 - Oper. Tribut�vel BC = valor opera��o al�quota diferenciada", 2),
	ICMS_3("3 - Oper. Tribut�vel BC = qtde vendida X al�quota por unid. produto", 3),
	ICMS_4("4 - Oper. Tribut�vel com tributa��o monof�sica al�quota zero", 4),
	ICMS_6("6 - Oper. Tribut�vel al�quota zero", 6),
	ICMS_7("7 - Oper. isenta contribui��o", 7),
	ICMS_8("8 - Oper. sem incid�ncia contribui��o", 8),
	ICMS_9("9 - Oper. com suspens�o contribui��o", 9);
	
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
