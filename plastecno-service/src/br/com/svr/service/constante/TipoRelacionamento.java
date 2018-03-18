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
	
	public boolean isFornecimento(){
		return this.equals(FORNECIMENTO) || this.equals(REPRESENTACAO_FORNECIMENTO);
	}
	
	public boolean isRevenda(){
		return this.equals(REVENDA);
	}
	
	public boolean isRepresentacao(){
		return this.equals(REPRESENTACAO)|| this.equals(REPRESENTACAO_FORNECIMENTO);
	}
}
