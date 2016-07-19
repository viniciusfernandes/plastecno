package br.com.plastecno.service.nfe;

public enum TipoFinalidadeEmissao {
	AJUSTE("AJUSTE", 3), COMPLEMENTAR("COMPLEMENTAR", 2), NORMAL("NORMAL", 1);
	private final int codigo;
	private final String descricao;

	private TipoFinalidadeEmissao(String descricao, int codigo) {
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
