package br.com.plastecno.service.nfe.constante;

public enum TipoTributacaoCOFINS {
	COFINS_ST(" - Oper. Tribut�vel BC = valor opera��o al�quota normal", " "), 
	COFINS_1("1 - Oper. Tribut�vel BC = valor opera��o al�quota normal", "1"), 
	COFINS_2("2 - Oper. Tribut�vel BC = valor opera��o al�quota diferenciada", "2"), 
	COFINS_3("3 - Oper. Tribut�vel BC = qtde vendida X al�quota por unid. produto", "3"), 
	COFINS_4("4 - Oper. Tribut�vel com tributa��o monof�sica al�quota zero", "4"),
	COFINS_5("5 - Oper. Tribut�vel por substitui��o tribut�ria", "5"), 
	COFINS_6("6 - Oper. Tribut�vel al�quota zero", "6"), 
	COFINS_7("7 - Oper. isenta contribui��o", "7"), 
	COFINS_8("8 - Oper. sem incid�ncia contribui��o", "8"), 
	COFINS_9("9 - Oper. com suspens�o contribui��o", "9"), 
	COFINS_99("99 - Outros", "99");

	public static TipoTributacaoCOFINS getTipoTributacao(String codigo) {
		for (TipoTributacaoCOFINS t : values()) {
			if (t.codigo.equals(codigo)) {
				return t;
			}
		}
		return null;
	}

	private String codigo;

	private String descricao;

	private TipoTributacaoCOFINS(String descricao, String codigo) {
		this.descricao = descricao;
		this.codigo = codigo;
	}

	public String getCodigo() {
		return codigo;
	}

	public String getDescricao() {
		return descricao;
	}
}
