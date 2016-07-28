package br.com.plastecno.service.nfe.constante;

public enum TipoTributacaoICMS {
	ICMS_0("0 - Tributada integralmente", 0), 
	ICMS_10("10 - Tributada  com cobrança por substituição tributária", 10), 
	ICMS_20("20 - Tributada com redução da base de cálculo", 20), 
	ICMS_30("30 - Isenta ou não tributada com cobrança por substituição tributária", 30), 
	ICMS_40("40, 41, 50 - Isenta, Não tributada ou Suspensão", 40), 
	ICMS_51("51 - Deferimento", 51), 
	ICMS_60("60 - Cobrado anteriormente por substituição tributária", 60), 
	ICMS_70("70 - Com Redução da base de cálculo e cobrança por substituição tributária", 70), 
	ICMS_90("90 - Outros", 90);
	
	public static TipoTributacaoICMS getTipoTributacao(Integer codigo) {
		for (TipoTributacaoICMS t : values()) {
			if (t.codigo.equals(codigo)) {
				return t;
			}
		}
		return null;
	}
	
	private Integer codigo;
	private String descricao;

	private TipoTributacaoICMS(String descricao, Integer codigo) {
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
