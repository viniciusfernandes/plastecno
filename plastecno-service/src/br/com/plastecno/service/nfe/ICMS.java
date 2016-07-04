package br.com.plastecno.service.nfe;

class ICMSIntegral {
	private Double aliquotaBC;
	private Integer modalidadeDeterminacaoBC;
	private Integer origemMercadoria;
	private String tributacaoICMS;
	private Double valor;
	private Double valorBC;

	public Double getAliquotaBC() {
		return aliquotaBC;
	}

	public Integer getModalidadeDeterminacaoBC() {
		return modalidadeDeterminacaoBC;
	}

	public Integer getOrigemMercadoria() {
		return origemMercadoria;
	}

	public String getTributacaoICMS() {
		return tributacaoICMS;
	}

	public Double getValor() {
		return valor;
	}

	public Double getValorBC() {
		return valorBC;
	}

	public void setAliquotaBC(Double aliquotaBC) {
		this.aliquotaBC = aliquotaBC;
	}

	public void setModalidadeDeterminacaoBC(Integer modalidadeDeterminacaoBC) {
		this.modalidadeDeterminacaoBC = modalidadeDeterminacaoBC;
	}

	public void setOrigemMercadoria(Integer origemMercadoria) {
		this.origemMercadoria = origemMercadoria;
	}

	public void setTributacaoICMS(String tributacaoICMS) {
		this.tributacaoICMS = tributacaoICMS;
	}

	public void setValor(Double valor) {
		this.valor = valor;
	}

	public void setValorBC(Double valorBC) {
		this.valorBC = valorBC;
	}
}

public class ICMS {
	private ICMSIntegral icmsIntegral;

	public ICMSIntegral getIcmsIntegral() {
		return icmsIntegral;
	}

	public void setIcmsIntegral(ICMSIntegral icmsIntegral) {
		this.icmsIntegral = icmsIntegral;
	}
}