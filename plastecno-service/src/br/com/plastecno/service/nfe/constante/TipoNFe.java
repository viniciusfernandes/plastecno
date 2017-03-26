package br.com.plastecno.service.nfe.constante;

public enum TipoNFe {
	SAIDA(1, "SA�DA"),
	ENTRADA(2, "ENTRADA"),
	DEVOLUCAO(2, "DEVOLU��O"),;
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
