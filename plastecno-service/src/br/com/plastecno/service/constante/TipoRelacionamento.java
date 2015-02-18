package br.com.plastecno.service.constante;

public enum TipoRelacionamento {
	REPRESENTACAO("Representa��o"), FORNECIMENTO("Fornecimento"), REPRESENTACAO_FORNECIMENTO("Ambos");

	private String descricao;

	private TipoRelacionamento(String descricao) {
		this.descricao = descricao;
	}

	public String getDescricao() {
		return this.descricao;
	}

}