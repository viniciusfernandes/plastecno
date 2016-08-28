package br.com.plastecno.service.nfe.constante;

public enum TipoOperacaoConsumidorFinal {
	NORMAL("0 - Normal", "0"), 
	CONSUMIDOR_FINAL("1 - Consum. Final", "1");

	private final String descricao;
	private final String codigo;

	private TipoOperacaoConsumidorFinal(String descricao, String codigo) {
		this.descricao = descricao;
		this.codigo = codigo;
	}

	public String getDescricao() {
		return descricao;
	}

	public String getCodigo() {
		return codigo;
	}

}