package br.com.svr.service.nfe.constante;

public enum TipoSituacaoNFe {
	INDEFINIDO(0, "INDEFINIDO"),
	EMITIDA(1, "EMITIDA"),
	CANCELADA(2, "CANCELADA");
	private final Integer codigo;
	private final String descricao;

	private TipoSituacaoNFe(Integer codigo, String descricao) {
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
