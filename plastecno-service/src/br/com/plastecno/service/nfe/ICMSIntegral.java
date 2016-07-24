package br.com.plastecno.service.nfe;

import javax.xml.bind.annotation.XmlElement;

public class ICMSIntegral {

	@XmlElement(name = "pICMS")
	private Double aliquota;

	@XmlElement(name = "pICMSST")
	private Double aliquotaST;

	@XmlElement(name = "modBC")
	private Integer modalidadeDeterminacaoBC;

	@XmlElement(name = "modBCST")
	private Integer modalidadeDeterminacaoBCST;

	@XmlElement(name = "orig")
	private Integer origemMercadoria;

	@XmlElement(name = "pMVAST")
	private Double percentualMargemValorAdicionadoICMSST;

	@XmlElement(name = "pRedBC")
	private Double percentualReducaoBC;

	@XmlElement(name = "CST")
	private String tributacaoICMS;

	@XmlElement(name = "vICMS")
	private Double valor;

	@XmlElement(name = "vBC")
	private Double valorBC;

	@XmlElement(name = "vBCST")
	private Double valorBCST;

	@XmlElement(name = "vICMSST")
	private Double valorST;

	@XmlElement(name = "motDesICMS")
	private String motivoDesoneracao;

	public void setMotivoDesoneracao(String motivoDesoneracao) {
		this.motivoDesoneracao = motivoDesoneracao;
	}

	public void setAliquota(Double aliquota) {
		this.aliquota = aliquota;
	}

	public void setAliquotaST(Double aliquotaST) {
		this.aliquotaST = aliquotaST;
	}

	public void setModalidadeDeterminacaoBC(Integer modalidadeDeterminacaoBC) {
		this.modalidadeDeterminacaoBC = modalidadeDeterminacaoBC;
	}

	public void setModalidadeDeterminacaoBCST(Integer modalidadeDeterminacaoBCST) {
		this.modalidadeDeterminacaoBCST = modalidadeDeterminacaoBCST;
	}

	public void setOrigemMercadoria(Integer origemMercadoria) {
		this.origemMercadoria = origemMercadoria;
	}

	public void setPercentualMargemValorAdicionadoICMSST(
			Double percentualMargemValorAdicionadoICMSST) {
		this.percentualMargemValorAdicionadoICMSST = percentualMargemValorAdicionadoICMSST;
	}

	public void setPercentualReducaoBC(Double percentualReducaoBC) {
		this.percentualReducaoBC = percentualReducaoBC;
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

	public void setValorBCST(Double valorBCST) {
		this.valorBCST = valorBCST;
	}

	public void setValorST(Double valorST) {
		this.valorST = valorST;
	}

}