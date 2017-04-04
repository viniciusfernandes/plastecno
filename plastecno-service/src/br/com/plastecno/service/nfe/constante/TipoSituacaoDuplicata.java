package br.com.plastecno.service.nfe.constante;

public enum TipoSituacaoDuplicata {
	A_VENCER(1, "A VENCER"),
	VENCIDO(2, "VENCIDO"),
	LIQUIDADO(3, "LIQUIDADO");
	
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
