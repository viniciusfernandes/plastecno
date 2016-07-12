package br.com.plastecno.service.nfe;

import javax.xml.bind.annotation.XmlElement;

public class ICMSIntegral {

	@XmlElement(name = "pICMS")
	private Double aliquota;
	@XmlElement(name = "modBC")
	private Integer modalidadeDeterminacaoBC;
	@XmlElement(name = "orig")
	private Integer origemMercadoria;
	@XmlElement(name = "CST")
	private String tributacaoICMS;
	@XmlElement(name = "vICMS")
	private Double valor;
	@XmlElement(name = "vBC")
	private Double valorBC;

	public void setAliquota(Double aliquota) {
		this.aliquota = aliquota;
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