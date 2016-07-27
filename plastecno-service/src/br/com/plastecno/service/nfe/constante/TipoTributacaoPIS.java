package br.com.plastecno.service.nfe.constante;

public enum TipoTributacaoPIS {
	PIS_1("1 - Oper. Tributável BC = valor operação alíquota normal", 1), 
	PIS_2("2 - Oper. Tributável BC = valor operação alíquota diferenciada", 2),
	PIS_3("3 - Oper. Tributável BC = qtde vendida X alíquota por unid. produto", 3),
	PIS_4("4 - Oper. Tributável com tributação monofásica alíquota zero", 4),
	PIS_6("6 - Oper. Tributável alíquota zero", 6),
	PIS_7("7 - Oper. isenta contribuição", 7),
	PIS_8("8 - Oper. sem incidência contribuição", 8),
	PIS_9("9 - Oper. com suspensão contribuição", 9),
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
