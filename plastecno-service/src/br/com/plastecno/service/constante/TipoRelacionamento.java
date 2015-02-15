package br.com.plastecno.service.constante;

public enum TipoRelacionamento {
	REPRESENTACAO("Representação"), FORNECIMENTO("Fornecimento"), REPRESENTACAO_FORNECIMENTO("Ambos");

	private String descricao;

	private TipoRelacionamento(String descricao) {
		this.descricao = descricao;
	}

	public String getDescricao() {
		return this.descricao;
	}

}
