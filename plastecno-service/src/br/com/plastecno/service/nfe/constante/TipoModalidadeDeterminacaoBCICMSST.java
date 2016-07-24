package br.com.plastecno.service.nfe.constante;

public enum TipoModalidadeDeterminacaoBCICMSST {
	PRECO_TABELADO_OU_MAXIMO(
			"0 - Preço tabelado ou máximo", 0),
	LISTA_NEGATIVA("1 - Lista negativa", 1), LISTA_NEUTRA("3 - Lista Neutra", 3), LISTA_POSITIVA(
			"2 - Lista positiva", 2), MARGEM_VALOR_AGREGADO(
			"4 - Margem valor agregado (%)", 4), PAUTA("5 - Pauta", 5) ;
	private Integer codigo;
	private String descricao;

	private TipoModalidadeDeterminacaoBCICMSST(String descricao, Integer codigo) {
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
