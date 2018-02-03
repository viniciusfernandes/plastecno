package br.com.svr.service.nfe.constante;

public enum TipoModalidadeDeterminacaoBCICMS {
	MARGEM_VALOR_AGREGADO("0 - Margem valor agregado (%)", 0), PAUTA(
			"1 - Pauta", 1), PRECO_TABELADO_MAX("2 - Preço tabelado máximo", 2), VALOR_OPERACAO(
			"3 - Valor operação", 3);
	private Integer codigo;
	private String descricao;

	private TipoModalidadeDeterminacaoBCICMS(String descricao, Integer codigo) {
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
