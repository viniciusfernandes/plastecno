package br.com.plastecno.service.nfe.constante;

public enum TipoOrigemMercadoria {
	NACIONAL("0 - Nacional", 0), ESTRANGEIRA_IMPORTACAO_DIRETA(
			"1 - Estrangeira com importação direta", 1), ESTRANGEIRA_MERCADO_INTERNO(
			"2 - Estrangeira adquirida no mercado interno", 2);
	private Integer codigo;
	private String descricao;

	private TipoOrigemMercadoria(String descricao, Integer codigo) {
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
