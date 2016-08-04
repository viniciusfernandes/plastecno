package br.com.plastecno.service.nfe.constante;

public enum TipoImpressaoNFe {
	RETRATO("1 - Retrato", 1), 
	PAISAGEM("2 - Paisagem", 2);
	
	private Integer codigo;
	private String descricao;

	private TipoImpressaoNFe(String descricao, Integer codigo) {
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
