package br.com.plastecno.service.nfe.constante;

public enum TipoTributacaoPIS {
	PIS_1("1 - Oper. Tribut�vel BC = valor opera��o al�quota normal", 1), 
	PIS_2("2 - Oper. Tribut�vel BC = valor opera��o al�quota diferenciada", 2),
	PIS_3("3 - Oper. Tribut�vel BC = qtde vendida X al�quota por unid. produto", 3),
	PIS_4("4 - Oper. Tribut�vel com tributa��o monof�sica al�quota zero", 4),
	PIS_6("6 - Oper. Tribut�vel al�quota zero", 6),
	PIS_7("7 - Oper. isenta contribui��o", 7),
	PIS_8("8 - Oper. sem incid�ncia contribui��o", 8),
	PIS_9("9 - Oper. com suspens�o contribui��o", 9),
	PIS_99("99 - Outros", 99);
	
	public static TipoTributacaoPIS getTipoTributacao(Integer codigo) {
		for (TipoTributacaoPIS t : values()) {
			if (t.codigo.equals(codigo)) {
				return t;
			}
		}
		return null;
	}
	
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
