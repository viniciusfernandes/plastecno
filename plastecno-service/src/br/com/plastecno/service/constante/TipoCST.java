package br.com.plastecno.service.constante;

public enum TipoCST {
	PRODUTO_NACIONAL("000", "Prod. Nacional"), 
	IMPORTADO_DIRETAMENTE("100", "Import. Diretamente"), 
	IMPORTADO_ADQUIRIDO_MERCADO_INTERNO("200", "Import. Mercado Interno"), 
	PRODUTO_NACIONAL_IMPORTACAO_40_70("300", "Prod. Nac. Import. de 40% a 70%"), 
	PRODUTO_NACIONAL_IMPORTACAO_ATE_40("500", "Prod. Nac. Import. ate 40%");
	private final String codigo;
	private final String descricao;

	private TipoCST(String codigo, String descricao) {
		this.codigo = codigo;
		this.descricao = descricao;
	}

	public String getCodigo() {
		return codigo;
	}

	public String getDescricao() {
		return descricao;
	}
}
