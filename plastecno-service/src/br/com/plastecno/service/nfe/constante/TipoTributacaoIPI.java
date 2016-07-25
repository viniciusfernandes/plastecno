package br.com.plastecno.service.nfe.constante;

public enum TipoTributacaoIPI {
	IPI_00("00 - Entrada com recuperação de crédito", "00"), IPI_01(
			"01 - Entrada tributada com alíquota zero", "01"), IPI_02(
			"02 - Entrada isenta", "02"), IPI_03("03 - Entrada não-tributada",
			"03"), IPI_04("04 - Entrada imune", "04"), IPI_05(
			"05 - Entrada com suspensão", "05"), IPI_49("49 - Outras entradas",
			"49"), IPI_50("50 - Saída tributada", "50"), IPI_51(
			"51 - Saída tributada com alíquota zero", "51"), IPI_52(
			"52 - Saída isenta", "52"), IPI_53("53 - Saída não-tributada", "53"), IPI_54(
			"54 - Saída imune", "54"), IPI_55(
			"55 - Saída com suspensão", "55"), IPI_99("99 - Outras saídas", "99");
	private String codigo;
	private String descricao;

	private TipoTributacaoIPI(String descricao, String codigo) {
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
