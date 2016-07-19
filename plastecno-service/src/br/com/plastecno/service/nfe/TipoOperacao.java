package br.com.plastecno.service.nfe;

public enum TipoOperacao {
	ENTRADA("ENTRADA", 0), SAIDA("SAÍDA", 1);
	private final int codigo;
	private final String descricao;

	private TipoOperacao(String descricao, int codigo) {
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
