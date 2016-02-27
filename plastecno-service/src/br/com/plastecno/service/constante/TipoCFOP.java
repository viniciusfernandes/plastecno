package br.com.plastecno.service.constante;

public enum TipoCFOP {
	IMPORTADO_DIRETAMENTE("Import. Diretamente"), IMPORTADO_ADQUIRIDO_MERCADO_INTERNO("Import. Mercado Interno");
	private String descricao;

	private TipoCFOP(String descricao) {
		this.descricao = descricao;
	}

	public String getDescricao() {
		return descricao;
	}
}
