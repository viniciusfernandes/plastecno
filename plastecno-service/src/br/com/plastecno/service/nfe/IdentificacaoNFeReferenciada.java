package br.com.plastecno.service.nfe;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;

import br.com.plastecno.service.validacao.annotation.InformacaoValidavel;

@InformacaoValidavel
public class IdentificacaoNFeReferenciada {
	@InformacaoValidavel(obrigatorio = true, padrao = { "\\d{4}" }, padraoExemplo = "4 digitos", nomeExibicao = "Ano/Mês de emissão da NFe referenciada")
	@XmlElement(name = "AAMM")
	private Integer anoMes;

	@InformacaoValidavel(obrigatorio = true, padrao = { "\\d{14}" }, padraoExemplo = "14 digitos", nomeExibicao = "CNPJ emitente da NFe referenciada")
	@XmlElement(name = "CNPJ")
	private String cnpjEmitente;

	@InformacaoValidavel(obrigatorio = true, padrao = { "\\d{2}" }, padraoExemplo = "2 digitos", nomeExibicao = "Modelo documento fiscal da NFe referenciada")
	@XmlElement(name = "mod")
	private String modelo;

	@InformacaoValidavel(obrigatorio = true, padrao = { "\\d{1,9}" }, padraoExemplo = "1 a 9 digitos", nomeExibicao = "Número do documento fiscal da NFe referenciada")
	@XmlElement(name = "nNF")
	private Long numeroNF;

	@InformacaoValidavel(obrigatorio = true, padrao = { "\\d{1,3}" }, padraoExemplo = "1 a 3 digitos", nomeExibicao = "Série do documento fiscal da NFe referenciada")
	@XmlElement(name = "serie")
	private String serie;

	@InformacaoValidavel(obrigatorio = true, padrao = { "\\d{2}" }, padraoExemplo = "2 digitos", nomeExibicao = "Código UF do emitente da NFe referenciada")
	@XmlElement(name = "cUF")
	private Integer ufEmitente;

	@XmlTransient
	public Integer getAnoMes() {
		return anoMes;
	}

	@XmlTransient
	public String getCnpjEmitente() {
		return cnpjEmitente;
	}

	@XmlTransient
	public String getModelo() {
		return modelo;
	}

	@XmlTransient
	public Long getNumeroNF() {
		return numeroNF;
	}

	@XmlTransient
	public String getSerie() {
		return serie;
	}

	@XmlTransient
	public Integer getUfEmitente() {
		return ufEmitente;
	}

	public void setAnoMes(Integer anoMes) {
		this.anoMes = anoMes;
	}

	public void setCnpjEmitente(String cnpjEmitente) {
		this.cnpjEmitente = cnpjEmitente;
	}

	public void setModelo(String modelo) {
		this.modelo = modelo;
	}

	public void setNumeroNF(Long numeroNF) {
		this.numeroNF = numeroNF;
	}

	public void setSerie(String serie) {
		this.serie = serie;
	}

	public void setUfEmitente(Integer ufEmitente) {
		this.ufEmitente = ufEmitente;
	}

}
