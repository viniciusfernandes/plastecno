package br.com.plastecno.service.nfe;

import javax.xml.bind.annotation.XmlElement;

public class TransportadorNFe {
	@XmlElement(name = "CNPJ")
	private String CNPJ;
	@XmlElement(name = "CPF")
	private String CPF;
	@XmlElement(name = "xEnder")
	private String enderecoCompleto;
	@XmlElement(name = "IE")
	private String inscricaoEstadual;
	@XmlElement(name = "xMun")
	private String municipio;
	@XmlElement(name = "xNome")
	private String razaoSocial;
	@XmlElement(name = "UF")
	private String UF;

	public void setCNPJ(String cNPJ) {
		CNPJ = cNPJ;
	}

	public void setCPF(String cPF) {
		CPF = cPF;
	}

	public void setEnderecoCompleto(String enderecoCompleto) {
		this.enderecoCompleto = enderecoCompleto;
	}

	public void setInscricaoEstadual(String inscricaoEstadual) {
		this.inscricaoEstadual = inscricaoEstadual;
	}

	public void setMunicipio(String municipio) {
		this.municipio = municipio;
	}

	public void setRazaoSocial(String razaoSocial) {
		this.razaoSocial = razaoSocial;
	}

	public void setUF(String uF) {
		UF = uF;
	}

}
