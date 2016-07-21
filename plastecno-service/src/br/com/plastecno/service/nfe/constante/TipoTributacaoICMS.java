package br.com.plastecno.service.nfe.constante;

public enum TipoTributacaoICMS {
	ICMS_00("00 - Tributada integralmente", "00"), ICMS_10(
			"10 - Tributada  com cobrança por substituição tributária", "10"), ICMS_20(
			"20 - Tributada com redução da base de cálculo", "20"), ICMS_30(
			"30 - Isenta ou não tributada com cobrança por substituição tributária",
			"30"), ICMS_40("40 - Isenta", "40"), ICMS_41("41 - Não tributada",
			"41"), ICMS_50("50 - Suspensão", "50"), ICMS_51("51 - Deferimento", "51"), ICMS_60(
			"60 - Cobrado anteriormente por substituição tributária", "60"), ICMS_70(
			"70 - Com Redução da base de cálculo e cobrança por substituição tributária",
			"70"), ICMS_90("90 - Outros", "90");
	private String codigo;
	private String descricao;

	private TipoTributacaoICMS(String descricao, String codigo) {
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
