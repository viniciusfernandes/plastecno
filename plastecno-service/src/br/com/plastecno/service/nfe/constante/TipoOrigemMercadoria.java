package br.com.plastecno.service.nfe.constante;

public enum TipoOrigemMercadoria {
	NACIONAL("0 - Nacional", "0"), 
	ESTRANGEIRA_IMPORTACAO_DIRETA("1 - Estrangeira com importação direta", "1"), 
	ESTRANGEIRA_MERCADO_INTERNO("2 - Estrangeira adquirida no mercado interno", "2"),
	NACIONAL_MERCADORIA_IMPORT_40_E_70("3 - Nacional, mercadoria ou bem com Conteudo de Importacao superior a 40% e inferior ou igual a 70%", "3"),
	NACIONAL_PRODUCAO_AJUSTES("3 - Nacional, cuja producao tenha sido feita em conformidade com os processos produtivos básicos de que tratam as legislacoes citadas nos Ajustes", "4"),
	NACIONAL_MERCADORIA_IMPORT_INF_40("5 - Nacional, mercadoria ou bem com Conteudo de Importacao inferior ou igual a 40%", "5"),
	ESTRANGEIRA_SEM_SIMILAR_NACIONAL("6 - Estrangeira - Importação direta, sem similar nacional, constante em lista da CAMEX e gas natural", "6"),
	ESTRANGEIRA_MERCADO_INTERNO_SEM_SIMILAR("7 - Estrangeira - Adquirida no mercado interno, sem similar nacional, constante lista CAMEX e gas natural", "7"),
	NACIONAL_MERCADORIA_IMPORT_SUP_70("8 - Nacional, mercadoria ou bem com conteudo de importacao superior a 70%", "8");
	
	private String codigo;
	private String descricao;

	private TipoOrigemMercadoria(String descricao, String codigo) {
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
