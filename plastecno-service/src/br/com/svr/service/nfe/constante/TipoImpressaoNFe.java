package br.com.svr.service.nfe.constante;

public enum TipoImpressaoNFe {
	SEM_DANFE("0 - Sem DANFE", "0"),
	RETRATO("1 - Retrato", "1"), 
	PAISAGEM("2 - Paisagem", "2"),
	SIMPLIFICADO("3 - Paisagem", "3"),
	NFC("4 - NFC-e", "4"),
	NFC_ELETRONICO("5 - NFC-e mens. eletônica", "5");
	
	private String codigo;
	private String descricao;

	private TipoImpressaoNFe(String descricao, String codigo) {
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
