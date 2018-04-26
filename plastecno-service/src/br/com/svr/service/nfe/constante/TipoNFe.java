package br.com.svr.service.nfe.constante;

public enum TipoNFe {
	INDEFINIDO(0, "INDEFINIDO"),
	SAIDA(1, "SAÍDA"),
	ENTRADA(2, "ENTRADA"),
	DEVOLUCAO(3, "DEVOLUÇÃO"),
	TRIANGULARIZACAO(4, "TRIANGULARIZAÇÃO");
	private final Integer codigo;
	private final String descricao;

	private TipoNFe(Integer codigo, String descricao) {
		this.codigo = codigo;
		this.descricao = descricao;
	}

	public Integer getCodigo() {
		return codigo;
	}

	public String getDescricao() {
		return descricao;
	}
}
