package br.com.plastecno.service.nfe.constante;

public enum TipoOperacaoNFe {
	ENTRADA("0 - Entrada", "0"), SAIDA("1 - Sa�da", "1");
	private final String codigo;
	private final String descricao;

	private TipoOperacaoNFe(String descricao, String codigo) {
		this.descricao = descricao;
		this.codigo = codigo;
	}

	public String getCodigo() {
		return codigo;
	}

	public String getDescricao() {
		return descricao;
	}

}
