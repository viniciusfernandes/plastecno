package br.com.plastecno.service.nfe.constante;

public enum TipoRegimeTributacao {
	SIMPLES_NACIONAL("1 - Simples Nacional", 1), 
	SIMPLES_NACIONAL_EXCESSO_SUBLIMITE_RECEITA("2 - Simples nacional excesso de sublimite de receita bruta", 2), 
	REGIME_NORMAL("3 - Regime normal", 3);

	private final Integer codigo;
	private final String descricao;

	private TipoRegimeTributacao(String descricao, Integer codigo) {
		this.descricao = descricao;
		this.codigo = codigo;
	}

	public Integer getCodigo() {
		return codigo;
	}

	public String getDescricao() {
		return descricao;
	}

}
