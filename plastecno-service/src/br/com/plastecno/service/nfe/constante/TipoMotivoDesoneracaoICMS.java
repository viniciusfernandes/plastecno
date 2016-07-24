package br.com.plastecno.service.nfe.constante;

public enum TipoMotivoDesoneracaoICMS {
	TAXI("1 - Taxi", 1),
	PRODUTOR_AGROPECUARIO("3 - Produtor Agropecu�rio", 3), 
	DEFICIENTE_FISICO("2 - Deficiente f�sico", 2), 
	FROTISTA_LOCADORA("4 - Produtor Agropecu�rio", 4), 
	DIPLOMATICO_CONSULAR("5 - Diplom�tico ou Consular", 5), 
	UTILITARIOS_MOTOCICLETAS_AMAZONIA_OCIDENTAL_LIVRE_COMERCIO("6 - Utilit�rios e motocicletas da Amaz�nia Ocidental e �reas livre com�rcio ", 6), 
	SUFRAMA("7 - SUFRAMA", 7),
	OUTROS("9 - Outros", 9);
	private Integer codigo;
	private String descricao;

	private TipoMotivoDesoneracaoICMS(String descricao, Integer codigo) {
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
