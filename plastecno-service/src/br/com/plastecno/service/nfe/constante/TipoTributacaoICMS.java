package br.com.plastecno.service.nfe.constante;

public enum TipoTributacaoICMS {
	ICMS_0("0 - Tributada integralmente", 0), 
	ICMS_10("10 - Tributada  com cobran�a por substitui��o tribut�ria", 10), 
	ICMS_20("20 - Tributada com redu��o da base de c�lculo", 20), 
	ICMS_30("30 - Isenta ou n�o tributada com cobran�a por substitui��o tribut�ria", 30), 
	ICMS_40("40, 41, 50 - Isenta, N�o tributada ou Suspens�o", 40), 
	ICMS_51("51 - Deferimento", 51), 
	ICMS_60("60 - Cobrado anteriormente por substitui��o tribut�ria", 60), 
	ICMS_70("70 - Com Redu��o da base de c�lculo e cobran�a por substitui��o tribut�ria", 70), 
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
