package br.com.plastecno.service.nfe.constante;

public enum TipoTributacaoISS {
	ISENTA("I - Isenta", "I"), 
	NORMAL("N - Normal", "N"), 
	RETIDA("R - Retida", "R"), 
	SUBSTITUTA("S - Substituta", "S");

	private final String codigo;
	private final String descricao;

	private TipoTributacaoISS(String descricao, String codigo) {

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
