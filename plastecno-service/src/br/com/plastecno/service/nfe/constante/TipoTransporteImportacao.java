package br.com.plastecno.service.nfe.constante;

public enum TipoTransporteImportacao {
	MARITIMA("01 - Marítima", "01"),
	FLUVIAL("02 - Fluvial", "02"),
	LACUSTRE("03 - Lacustre", "03"),
	AEREA("04 - Aérea", "04"),
	POSTAL("05 - PostaL", "05"),
	FERROVIARIA("06 - Ferroviária", "06"),
	RODOVIARIA("07 - Rodoviária", "07"),
	CONDUTO_REDE_TRANSMICAO("08 - Conduto/Rede Transmissão", "08"),
	MEIO_PROPRIOS("09 - Meios próprios", "09"),
	ENTRADA_SAIDA_FICTA("02 - Entrada/Saída Ficta", "10"),
	COURIER("11 - Courier", "11"),
	HANDCARRY("12 - Handcarry", "12");
	
	private final String codigo;
	private final String descricao;

	private TipoTransporteImportacao(String descricao, String codigo) {
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
