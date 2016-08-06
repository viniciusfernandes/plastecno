package br.com.plastecno.service.nfe;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

@XmlRootElement(name = "nfe")
public class NFe {
	@XmlElement(name = "infNFe")
	private DadosNFe dadosNFe;

	public NFe() {
	}

	public NFe(DadosNFe dadosNFe) {
		this.dadosNFe = dadosNFe;
	}

	@XmlTransient
	public DadosNFe getDadosNFe() {
		return dadosNFe;
	}

	public void setDadosNFe(DadosNFe dadosNFe) {
		this.dadosNFe = dadosNFe;
	}
}
