package br.com.plastecno.service.nfe.constante;

public enum TipoTributacaoCOFINS {
	COFINS_ST(" - Oper. Tributável BC = valor operação alíquota normal", " "), 
	COFINS_1("1 - Oper. Tributável BC = valor operação alíquota normal", "1"), 
	COFINS_2("2 - Oper. Tributável BC = valor operação alíquota diferenciada", "2"), 
	COFINS_3("3 - Oper. Tributável BC = qtde vendida X alíquota por unid. produto", "3"), 
	COFINS_4("4 - Oper. Tributável com tributação monofásica alíquota zero", "4"),
	COFINS_5("5 - Oper. Tributável por substituição tributária", "5"), 
	COFINS_6("6 - Oper. Tributável alíquota zero", "6"), 
	COFINS_7("7 - Oper. isenta contribuição", "7"), 
	COFINS_8("8 - Oper. sem incidência contribuição", "8"), 
	COFINS_9("9 - Oper. com suspensão contribuição", "9"), 
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
