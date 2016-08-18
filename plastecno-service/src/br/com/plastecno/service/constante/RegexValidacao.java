package br.com.plastecno.service.constante;

public enum RegexValidacao {
	CNPJ("\\d{14}"), CPF("\\d{11}"), NENHUM("");
	private final String padrao;

	private RegexValidacao(String padrao) {
		this.padrao = padrao;
	}

	public String getPadrao() {
		return padrao;
	}

}
