package br.com.plastecno.service.nfe;

import javax.xml.bind.annotation.XmlElement;

public class IdentificacaoNFeReferenciada {
	@XmlElement(name = "AAMM")
	private Integer anoMes;

	@XmlElement(name = "CNPJ")
	private String cnpjEmitente;

	@XmlElement(name = "mod")
	private Integer modelo;

	@XmlElement(name = "nNF")
	private Integer numeroNF;

	@XmlElement(name = "serie")
	private Integer serie;

	@XmlElement(name = "cUF")
	private Integer ufEmitente;

	public void setAnoMes(Integer anoMes) {
		this.anoMes = anoMes;
	}

	public void setCnpjEmitente(String cnpjEmitente) {
		this.cnpjEmitente = cnpjEmitente;
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

	public void setUfEmitente(Integer ufEmitente) {
		this.ufEmitente = ufEmitente;
	}

}
