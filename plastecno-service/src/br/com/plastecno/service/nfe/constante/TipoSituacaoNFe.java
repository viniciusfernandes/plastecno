package br.com.plastecno.service.nfe.constante;

public enum TipoSituacaoNFe {
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
