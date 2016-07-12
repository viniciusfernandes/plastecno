package br.com.plastecno.service.nfe;

import javax.xml.bind.annotation.XmlElement;

public class ICMSST extends ICMSIntegral {
	@XmlElement(name = "pICMSST")
	private Double aliquota;
	@XmlElement(name = "pMVAST")
	private Double percentualMargemAdicionadoST;
	@XmlElement(name = "pRedBCST")
	private Double percentualReducaoBC;
	@XmlElement(name = "vICMSST")
	private Double valor;
	@XmlElement(name = "vBCST")
	private Double valorBC;

	public void setAliquota(Double aliquota) {
		this.aliquota = aliquota;
	}

	public void setPercentualMargemAdicionadoST(
			Double percentualMargemAdicionadoST) {
		this.percentualMargemAdicionadoST = percentualMargemAdicionadoST;
	}

	public void setPercentualReducaoBC(Double percentualReducaoBC) {
		this.percentualReducaoBC = percentualReducaoBC;
	}

	public void setValor(Double valor) {
		this.valor = valor;
	}

	public void setValorBC(Double valorBC) {
		this.valorBC = valorBC;
	}

}