package br.com.svr.service.constante;

public enum TipoRelacionamento {
	REPRESENTACAO("Representação"), 
	FORNECIMENTO("Fornecimento"), 
	REPRESENTACAO_FORNECIMENTO("Ambos"), 
	REVENDA("Revenda");

	private String descricao;

	private TipoRelacionamento(String descricao) {
		this.descricao = descricao;
	}

	public String getDescricao() {
		return this.descricao;
	}
}
