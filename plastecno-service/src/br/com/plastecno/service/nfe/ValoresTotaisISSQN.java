package br.com.plastecno.service.nfe;

import javax.xml.bind.annotation.XmlElement;

public class ValoresTotaisISSQN {
	@XmlElement(name = "vServ")
	private Double valorServico;

	@XmlElement(name = "vBC")
	private Double valorBC;

	@XmlElement(name = "vISS")
	private Double valorIss;

	@XmlElement(name = "vPIS")
	private Double valorPis;

	@XmlElement(name = "vCOFINS")
	private Double valorCofins;

	public void setValorServico(Double valorServico) {
		this.valorServico = valorServico;
	}

	public void setValorBC(Double valorBC) {
		this.valorBC = valorBC;
	}

	public void setValorIss(Double valorIss) {
		this.valorIss = valorIss;
	}

	public void setValorPis(Double valorPis) {
		this.valorPis = valorPis;
	}

	public void setValorCofins(Double valorCofins) {
		this.valorCofins = valorCofins;
	}
}
