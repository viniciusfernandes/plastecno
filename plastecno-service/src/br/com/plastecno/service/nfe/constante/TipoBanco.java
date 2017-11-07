package br.com.plastecno.service.nfe.constante;

public enum TipoBanco {
	BANCO_BRASIL("Banco do Brasil S.A.", "001"), BANCO_SANTANDER("Banco Santander (Brasil) S.A.", "033"), BRADESCO(
			"Banco Bradesco S.A.", "237"), ITAU_UNIBANCO("Itau Unibanco S.A.", "341"), DEPOSITO_CC("Depósito em CC",
			"dpcc");
	public static TipoBanco getTipoBanco(String codigo) {
		for (TipoBanco t : values()) {
			if (t.codigo.equals(codigo)) {
				return t;
			}
		}
		return null;
	}

	private final String codigo;

	private final String nome;

	private TipoBanco(String nome, String codigo) {
		this.nome = nome;
		this.codigo = codigo;
	}

	public String getCodigo() {
		return codigo;
	}

	public String getNome() {
		return nome;
	}
}
