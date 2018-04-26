package br.com.svr.service.constante;

public enum TipoEntrega {
	CIF("ENTREGUE NO CLIENTE"), CIF_TRANS("ENTREGUE PARA REDESPACHO"), FOB("RETIRADO PELO CLIENTE");

	private String descricao;

	private TipoEntrega(String descricao) {
		this.descricao = descricao;
	}

	public String getDescricao() {
		return this.toString() + " - " + this.descricao;
	}
}
