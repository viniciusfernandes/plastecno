package br.com.svr.service.nfe;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import br.com.svr.service.validacao.annotation.InformacaoValidavel;

@InformacaoValidavel
@XmlType(propOrder = { "cnpj", "cpf", "razaoSocial", "inscricaoEstadual", "enderecoCompleto", "municipio", "uf" })
public class TransportadoraNFe {
	@InformacaoValidavel(padrao = "\\d{14}", padraoExemplo = "14 digitos", substituicao = { "\\D", "" }, nomeExibicao = "CNPJ da transportadora")
	@XmlElement(name = "CNPJ")
	private String cnpj;

	@InformacaoValidavel(padrao = "\\d{11}", padraoExemplo = "11 digitos", substituicao = { "\\D", "" }, nomeExibicao = "CPF da transportadora")
	@XmlElement(name = "CPF")
	private String cpf;

	@InformacaoValidavel(intervaloComprimento = { 1, 60 }, nomeExibicao = "Endereço completo da transportadora")
	@XmlElement(name = "xEnder")
	private String enderecoCompleto;

	@InformacaoValidavel(intervaloComprimento = { 2, 14 }, nomeExibicao = "Inscrição estadutal da transportadora")
	@XmlElement(name = "IE")
	private String inscricaoEstadual;

	@InformacaoValidavel(intervaloComprimento = { 1, 60 }, nomeExibicao = "Município da transportadora")
	@XmlElement(name = "xMun")
	private String municipio;

	@InformacaoValidavel(intervaloComprimento = { 1, 60 }, nomeExibicao = "Razão social ou nome da transportadora")
	@XmlElement(name = "xNome")
	private String razaoSocial;

	@InformacaoValidavel(tamanho = 2, nomeExibicao = "UF da transportadora")
	@XmlElement(name = "UF")
	private String uf;

	@XmlTransient
	public String getCnpj() {
		return cnpj;
	}

	@XmlTransient
	public String getCpf() {
		return cpf;
	}

	@XmlTransient
	public String getEnderecoCompleto() {
		return enderecoCompleto;
	}

	@XmlTransient
	public String getInscricaoEstadual() {
		return inscricaoEstadual;
	}

	@XmlTransient
	public String getMunicipio() {
		return municipio;
	}

	@XmlTransient
	public String getRazaoSocial() {
		return razaoSocial;
	}

	@XmlTransient
	public String getUf() {
		return uf;
	}

	public void setCnpj(String cnpj) {
		this.cnpj = cnpj;
	}

	public void setCpf(String cpf) {
		this.cpf = cpf;
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

	public void setUf(String uF) {
		uf = uF;
	}

}
