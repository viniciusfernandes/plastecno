package br.com.plastecno.service.nfe;

import javax.xml.bind.annotation.XmlElement;

import br.com.plastecno.service.validacao.annotation.InformacaoValidavel;

public class IdentificacaoNFeReferenciada {
	@InformacaoValidavel(obrigatorio = true, intervaloNumerico = { 1, 9999 }, nomeExibicao = "Ano/Mês de emissão da NFe")
	@XmlElement(name = "AAMM")
	private Integer anoMes;

	@InformacaoValidavel(obrigatorio = true, tamanho = 14, nomeExibicao = "CNPJ emitente")
	@XmlElement(name = "CNPJ")
	private String cnpjEmitente;

	@InformacaoValidavel(obrigatorio = true, intervaloNumerico = { 1, 99 }, nomeExibicao = "Modelo documento fiscal")
	@XmlElement(name = "mod")
	private Integer modelo;

	@InformacaoValidavel(obrigatorio = true, intervaloNumerico = { 1, 999999999 }, nomeExibicao = "Número do documento fiscal")
	@XmlElement(name = "nNF")
	private Long numeroNF;

	@InformacaoValidavel(obrigatorio = true, intervaloNumerico = { 1, 999 }, nomeExibicao = "Série do documento fiscal")
	@XmlElement(name = "serie")
	private Integer serie;

	@InformacaoValidavel(obrigatorio = true, intervaloNumerico = { 1, 99 }, nomeExibicao = "Código UF do emitente")
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

	public void setNumeroNF(Long numeroNF) {
		this.numeroNF = numeroNF;
	}

	public void setSerie(Integer serie) {
		this.serie = serie;
	}

	public void setUfEmitente(Integer ufEmitente) {
		this.ufEmitente = ufEmitente;
	}

}
