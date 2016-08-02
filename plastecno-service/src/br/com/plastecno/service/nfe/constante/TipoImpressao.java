package br.com.plastecno.service.nfe.constante;

public enum TipoImpressao {
	PAISAGEM("PAISAGEM", 2), RETRATO("RETRATO", 1);
	private final int codigo;
	private final String descricao;

	private TipoImpressao(String descricao, int codigo) {
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
