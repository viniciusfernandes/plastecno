package br.com.svr.service.nfe.constante;

public enum TipoTributacaoCOFINS {
	COFINS_ST("ST - Oper. Tributável por substituição tributária em percentual", "ST"), 
	COFINS_1("01 - Oper. Tributável BC = valor operação alíquota normal", "01"), 
	COFINS_2("02 - Oper. Tributável BC = valor operação alíquota diferenciada", "02"), 
	COFINS_3("03 - Oper. Tributável BC = qtde vendida X alíquota por unid. produto", "03"), 
	COFINS_4("04 - Oper. Tributável com tributação monofásica alíquota zero", "04"),
	COFINS_5("05 - Oper. Tributável por substituição tributária", "05"), 
	COFINS_6("06 - Oper. Tributável alíquota zero", "06"), 
	COFINS_7("07 - Oper. isenta contribuição", "07"), 
	COFINS_8("08 - Oper. sem incidência contribuição", "08"), 
	COFINS_9("09 - Oper. com suspensão contribuição", "09"), 
	COFINS_49("49 - Outras oper. saída", "49"),
	COFINS_50("50 - Oper. com Direito a Crédito - Vinculada Exclusivamente a Receita Tributada no Mercado Interno", "50"),
	COFINS_51("51 - Oper. com Direito a Crédito - Vinculada Exclusivamente a Receita Não Tributada no Mercado Interno", "51"),
	COFINS_52("52 - Oper. com Direito a Crédito – Vinculada Exclusivamente a Receita de Exportação", "52"),
	COFINS_53("53 - Oper. com Direito a Crédito - Vinculada a Receitas Tributadas e Não-Tributadas no Mercado Interno", "53"),
	COFINS_54("54 - Oper. com Direito a Crédito - Vinculada a Receitas Tributadas no Mercado Interno e de Exportação", "54"),
	COFINS_55("55 - Oper. com Direito a Crédito - Vinculada a Receitas Não-Tributadas no Mercado Interno e de Exportação", "55"),
	COFINS_56("56 - Oper. com Direito a Crédito - Vinculada a Receitas Tributadas e Não-Tributadas no Mercado Interno, e de Exportação", "56"),
	COFINS_60("60 - Crédito Presumido - Operação de Aquisição Vinculada Exclusivamente a Receita Tributada no Mercado Interno", "60"),
	COFINS_61("61 - Crédito Presumido - Operação de Aquisição Vinculada Exclusivamente a Receita Não-Tributada no Mercado Interno", "61"),
	COFINS_62("62 - Crédito Presumido - Operação de Aquisição Vinculada Exclusivamente a Receita de Exportação", "62"),
	COFINS_63("63 - Crédito Presumido - Operação de Aquisição Vinculada a Receitas Tributadas e Não-Tributadas no Mercado Interno", "63"),
	COFINS_64("64 - Crédito Presumido - Operação de Aquisição Vinculada a Receitas Tributadas no Mercado Interno e de Exportação", "64"),
	COFINS_65("65 - Crédito Presumido - Operação de Aquisição Vinculada a Receitas Não-Tributadas no Mercado Interno e de Exportação", "65"),
	COFINS_66("66 - Crédito Presumido - Operação de Aquisição Vinculada a Receitas Tributadas e Não-Tributadas no Mercado Interno, e de Exportação", "66"),
	COFINS_67("67 - Crédito Presumido - Outras Operações", "67"),
	COFINS_70("70 - Operação de Aquisição sem Direito a Crédito", "70"),
	COFINS_71("71 - Operação de Aquisição com Isenção", "71"),
	COFINS_72("72 - Operação de Aquisição com Suspensão", "72"),
	COFINS_73("73 - Operação de Aquisição a Alíquota Zero", "73"),
	COFINS_74("74 - Operação de Aquisição; sem Incidência da Contribuição;", "74"),
	COFINS_75("75 - Operação de Aquisição por Substituição Tributária", "75"),
	COFINS_98("98 - Outras Operações de Entrada", "98"),
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
