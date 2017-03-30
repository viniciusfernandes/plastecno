package br.com.plastecno.service.nfe.constante;

public enum TipoTributacaoPIS {
	PIS_ST("ST - Oper. Tributável por substituição", "ST"), 
	PIS_1("01 - Oper. Tributável BC = valor operação alíquota normal", "01"), 
	PIS_2("02 - Oper. Tributável BC = valor operação alíquota diferenciada", "02"),
	PIS_3("03 - Oper. Tributável BC = qtde vendida X alíquota por unid. produto", "03"),
	PIS_4("04 - Oper. Tributável com tributação monofásica alíquota zero", "04"),
	PIS_5("05 - Oper. Tributável com substituição tributária", "05"),
	PIS_6("06 - Oper. Tributável alíquota zero", "06"),
	PIS_7("07 - Oper. isenta contribuição", "07"),
	PIS_8("08 - Oper. sem incidência contribuição", "08"),
	PIS_9("09 - Oper. com suspensão contribuição", "09"),
	PIS_49("49 - Outras oper. saída", "49"),
	PIS_50("50 - Oper. com Direito a Crédito - Vinculada Exclusivamente a Receita Tributada no Mercado Interno", "50"),
	PIS_51("51 - Oper. com Direito a Crédito - Vinculada Exclusivamente a Receita Não Tributada no Mercado Interno", "51"),
	PIS_52("52 - Oper. com Direito a Crédito – Vinculada Exclusivamente a Receita de Exportação", "52"),
	PIS_53("53 - Oper. com Direito a Crédito - Vinculada a Receitas Tributadas e Não-Tributadas no Mercado Interno", "53"),
	PIS_54("54 - Oper. com Direito a Crédito - Vinculada a Receitas Tributadas no Mercado Interno e de Exportação", "54"),
	PIS_55("55 - Oper. com Direito a Crédito - Vinculada a Receitas Não-Tributadas no Mercado Interno e de Exportação", "55"),
	PIS_56("56 - Oper. com Direito a Crédito - Vinculada a Receitas Tributadas e Não-Tributadas no Mercado Interno, e de Exportação", "56"),
	PIS_60("60 - Crédito Presumido - Operação de Aquisição Vinculada Exclusivamente a Receita Tributada no Mercado Interno", "60"),
	PIS_61("61 - Crédito Presumido - Operação de Aquisição Vinculada Exclusivamente a Receita Não-Tributada no Mercado Interno", "61"),
	PIS_62("62 - Crédito Presumido - Operação de Aquisição Vinculada Exclusivamente a Receita de Exportação", "62"),
	PIS_63("63 - Crédito Presumido - Operação de Aquisição Vinculada a Receitas Tributadas e Não-Tributadas no Mercado Interno", "63"),
	PIS_64("64 - Crédito Presumido - Operação de Aquisição Vinculada a Receitas Tributadas no Mercado Interno e de Exportação", "64"),
	PIS_65("65 - Crédito Presumido - Operação de Aquisição Vinculada a Receitas Não-Tributadas no Mercado Interno e de Exportação", "65"),
	PIS_66("66 - Crédito Presumido - Operação de Aquisição Vinculada a Receitas Tributadas e Não-Tributadas no Mercado Interno, e de Exportação", "66"),
	PIS_67("67 - Crédito Presumido - Outras Operações", "67"),
	PIS_70("70 - Operação de Aquisição sem Direito a Crédito", "70"),
	PIS_71("71 - Operação de Aquisição com Isenção", "71"),
	PIS_72("72 - Operação de Aquisição com Suspensão", "72"),
	PIS_73("73 - Operação de Aquisição a Alíquota Zero", "73"),
	PIS_74("74 - Operação de Aquisição; sem Incidência da Contribuição;", "74"),
	PIS_75("75 - Operação de Aquisição por Substituição Tributária", "75"),
	PIS_98("98 - Outras Operações de Entrada", "98"),
	PIS_99("99 - Outros", "99");
	
	public static TipoTributacaoPIS getTipoTributacao(String codigo) {
		for (TipoTributacaoPIS t : values()) {
			if (t.codigo.equals(codigo)) {
				return t;
			}
		}
		return null;
	}
	
	private String codigo;
	private String descricao;

	private TipoTributacaoPIS(String descricao, String codigo) {
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
