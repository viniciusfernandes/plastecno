package br.com.svr.service.nfe.constante;

public enum TipoDestinoOperacao {
	NORMAL("1 - Operação Interna", "1"),
	CONTIGENCIA_FS("2 - Operação Interestadual", "2"),
	CONTIGENCIA_SCAN("3 - Operação com exterior", "3");
	
	private final String codigo;
	private final String descricao;

	private TipoDestinoOperacao(String descricao, String codigo) {
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
