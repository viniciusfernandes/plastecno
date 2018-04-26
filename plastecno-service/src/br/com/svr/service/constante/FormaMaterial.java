package br.com.svr.service.constante;

public enum FormaMaterial {
	BR("BARRA REDONDA", 0.1, false), 
	BQ("BARRA QUADRADA", 0.1, true), 
	BS("BARRA SEXTAVADA", 0.1, false), 
	CH("CHAPA", 0.15, true),
	TB("TUBO", 0, true), 
	PC("PEÇA", 0, false);

	private String descricao;
	private double ipi;
	private boolean temlargura;

	private FormaMaterial(String descricao, double ipi, boolean temlargura) {
		this.descricao = descricao;
		this.ipi = ipi;
		this.temlargura = temlargura;
	}

	public boolean contemLargura() {
		return this.temlargura;
	}

	public static FormaMaterial get(Integer index) {
		index -= 1;
		for (int i = 0; i < values().length; i++) {
			if (i == index) {
				return values()[i];
			}
		}
		return null;
	}

	public String getDescricao() {
		return this.descricao;
	}

	public double getIpi() {
		return this.ipi;
	}

	public int getIpiPercentual() {
		return (int) (this.getIpi() * 100);
	}

	public int indexOf() {
		return ordinal() + 1;
	}

	public boolean isFormaMaterialVazada() {
		return TB.equals(this);
	}

	public boolean isMedidaExternaIgualInterna() {
		return BQ.equals(this);
	}
}
