package br.com.svr.service.nfe.constante;

public enum TipoAliquotaICMSInterestadual {
	PRODUTO_IMPORTADO("4% - Produto Importado", 4.0),
	ESTADOS_SUL_SUDESTE("7% - Estados do Sul e Sudeste", 7.0), 
	OUTROS("12% - Outros", 12.0);
	private final Double aliquota;
	private final String descricao;
	private TipoAliquotaICMSInterestadual(String descricao,Double aliquota) {
		this.descricao = descricao;
		this.aliquota = aliquota;
	}

	public String getDescricao() {
		return descricao;
	}

	public Double getAliquota() {
		return aliquota;
	}
}
