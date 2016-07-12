package br.com.plastecno.service.nfe;

import javax.xml.bind.annotation.XmlElement;

public class IdentificacaoNFeReferenciada {
	@XmlElement(name = "AAMM")
	private Integer anoMes;
	@XmlElement(name = "CNPJ")
	private String CNPJ;
	@XmlElement(name = "cUF")
	private Integer codigoUFEmitente;
	@XmlElement(name = "mod")
	private Integer modelo;
	@XmlElement(name = "nNF")
	private Integer numeroNF;
	@XmlElement(name = "serie")
	private Integer serie;

	public void setAnoMes(Integer anoMes) {
		this.anoMes = anoMes;
	}

	public void setCNPJ(String cNPJ) {
		CNPJ = cNPJ;
	}

	public void setCodigoUFEmitente(Integer codigoUFEmitente) {
		this.codigoUFEmitente = codigoUFEmitente;
	}

	public void setModelo(Integer modelo) {
		this.modelo = modelo;
	}

	public void setNumeroNF(Integer numeroNF) {
		this.numeroNF = numeroNF;
	}

	public void setSerie(Integer serie) {
		this.serie = serie;
	}

}
