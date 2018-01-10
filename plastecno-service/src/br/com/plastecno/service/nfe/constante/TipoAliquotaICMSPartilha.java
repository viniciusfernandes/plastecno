package br.com.plastecno.service.nfe.constante;

public enum TipoAliquotaICMSPartilha {
	ANO_2016("40% - Em 2016", 40.0),
	ANO_2017("60% - Em 2017", 60.0),
	ANO_2018("80% - Em 2018", 80.0),
	ANO_2019("100% - A partir de 2019", 100.0);
	private final Double aliquota;
	private final String descricao;
	private TipoAliquotaICMSPartilha(String descricao,Double aliquota) {
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
