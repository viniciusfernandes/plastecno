package br.com.plastecno.service.nfe;

public enum TipoFormaPagamento {
	OUTROS("OUTROS", 2), PRAZO("� PRAZO", 1), VISTA("� VISTA", 0);
	private final int codigo;
	private final String descricao;

	private TipoFormaPagamento(String descricao, int codigo) {
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
