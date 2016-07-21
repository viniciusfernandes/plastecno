package br.com.plastecno.service.nfe.constante;

public enum TipoEmissao {
	CONTIGENCIA_DPEC(
			"CONTIGÊNCIA DPEC", 4), CONTIGENCIA_FS("CONTIGÊNCIA FS", 2), CONTIGENCIA_FS_DA("CONTIGÊNCIA FS-DA", 5), CONTIGENCIA_SCAN(
			"CONTIGÊNCIA SCAN", 3), NORMAL("NORMAL", 1);
	private final int codigo;
	private final String descricao;

	private TipoEmissao(String descricao, int codigo) {
		this.descricao = descricao;
		this.codigo = codigo;
	}

	public int getCodigo() {
		return codigo;
	}

	public String getDescricao() {
		return descricao;
	}

}
