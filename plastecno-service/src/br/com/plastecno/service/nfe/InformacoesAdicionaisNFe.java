package br.com.plastecno.service.nfe;

import javax.xml.bind.annotation.XmlElement;

public class InformacoesAdicionaisNFe {

	@XmlElement(name = "infAdFisco")
	private String informacoesAdicionaisInteresseFisco;
	@XmlElement(name = "infCpl")
	private String informacoesComplementaresInteresseContribuinte;

	public void setInformacoesAdicionaisInteresseFisco(String informacoesAdicionaisInteresseFisco) {
		this.informacoesAdicionaisInteresseFisco = informacoesAdicionaisInteresseFisco;
	}

	public void setInformacoesComplementaresInteresseContribuinte(String informacoesComplementaresInteresseContribuinte) {
		this.informacoesComplementaresInteresseContribuinte = informacoesComplementaresInteresseContribuinte;
	}
}
