package br.com.svr.service.nfe.constante;

public enum TipoDesoneracaoICMS {
	TAXI("01 - Taxi", "01"),
	PRODUTOR_AGROPECUARIO("03 - Produtor Agropecuário", "03"), 
	DEFICIENTE_FISICO("02 - Deficiente físico", "02"), 
	FROTISTA_LOCADORA("04 - Produtor Agropecuário", "04"), 
	DIPLOMATICO_CONSULAR("05 - Diplomático ou Consular", "05"), 
	UTILITARIOS_MOTOCICLETAS_AMAZONIA_OCIDENTAL_LIVRE_COMERCIO("06 - Utilitários e motocicletas da Amazônia Ocidental e Áreas livre comércio ", "06"), 
	SUFRAMA("07 - SUFRAMA", "07"),
	OUTROS("09 - Outros", "09"),
	DECIFIENTE_CONDUTOR("10 - Deficiente condutor", "10"),
	DECIFIENTE_NAO_CONDUTOR("11 - Deficiente não condutor", "11"),
	FOMENTO_AGROPECUARIO("12 - Orgão de fomento e desenv. agropecuário", "12");
	private String codigo;
	private String descricao;

	private TipoDesoneracaoICMS(String descricao, String codigo) {
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
