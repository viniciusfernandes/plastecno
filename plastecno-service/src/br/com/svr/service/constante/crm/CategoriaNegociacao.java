package br.com.svr.service.constante.crm;

public enum CategoriaNegociacao {
	PROPOSTA_CLIENTE("Proposta Cliente", 1), 
	PRIMEIRO_CONTATO("Primeiro Contato", 2), 
	POTENCIAIS("Potenciais", 3), 
	PROJETOS("Projetos", 5), 
	PROVAVEIS("Prováveis", 4), 
	ESPECIAIS("Especiais", 6);
	private final String descricao;
	private final Integer ordem;

	private CategoriaNegociacao(String descricao, Integer ordem) {
		this.descricao = descricao;
		this.ordem = ordem;

	}

	public String getDescricao() {
		return descricao;
	}

	public Integer getOrdem() {
		return ordem;
	}

}
