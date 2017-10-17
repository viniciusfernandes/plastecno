package br.com.plastecno.service.nfe.constante;

public enum TipoSituacaoDuplicata {
	A_VENCER(1, "A VENCER"),
	VENCIDO(2, "VENCIDO"),
	LIQUIDADO(3, "LIQUIDADO"),
	CARTORIO(4, "CARTORIO"),
	PROTESTADO(5, "PROTESTADO"),
	A_VISTA(6, "A VISTA");
	private final Integer codigo;
	private final String descricao;

	private TipoSituacaoDuplicata(Integer codigo, String descricao) {
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
