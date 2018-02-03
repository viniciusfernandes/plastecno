package br.com.svr.service.nfe.constante;

public enum TipoOperacaoConsumidorFinal {
	NORMAL("0 - Não", "0"), 
	CONSUMIDOR_FINAL("1 - Sim", "1");

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