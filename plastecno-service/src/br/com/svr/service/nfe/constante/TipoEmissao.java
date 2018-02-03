package br.com.svr.service.nfe.constante;

public enum TipoEmissao {
	NORMAL("1 - Normal", "1"),
	CONTIGENCIA_FS("2 - Contingência FS", "2"),
	CONTIGENCIA_SCAN("3 - Contingência SCAN", "3"),
	CONTIGENCIA_DPEC("4 - Contingência DPEC", "4"),
	CONTIGENCIA_FS_DA("5 - Contingência FS-DA", "5"), ;
	
	private final String codigo;
	private final String descricao;

	private TipoEmissao(String descricao, String codigo) {
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
