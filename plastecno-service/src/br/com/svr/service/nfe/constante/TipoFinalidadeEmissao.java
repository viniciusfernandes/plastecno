package br.com.svr.service.nfe.constante;

public enum TipoFinalidadeEmissao {
	NORMAL("1 - Normal", "1"),
	COMPLEMENTAR("2 - Complementar", "2"),
	AJUSTE("3 - Ajuste", "3"),
	DEVOLUCAO("4 - Devolução", "4");
	
	private final String codigo;
	private final String descricao;

	private TipoFinalidadeEmissao(String descricao, String codigo) {
		this.descricao = descricao;
		this.codigo = codigo;
	}

	public String  getCodigo() {
		return codigo;
	}

	public String getDescricao() {
		return descricao;
	}

}
