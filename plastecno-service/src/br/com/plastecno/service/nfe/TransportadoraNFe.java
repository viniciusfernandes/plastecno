package br.com.plastecno.service.nfe;

import javax.xml.bind.annotation.XmlElement;

import br.com.plastecno.service.constante.RegexValidacao;
import br.com.plastecno.service.validacao.annotation.InformacaoValidavel;

@InformacaoValidavel
public class TransportadoraNFe {
	@InformacaoValidavel(regex = RegexValidacao.CNPJ, nomeExibicao = "CNPJ da transportadora")
	@XmlElement(name = "CNPJ")
	private String cnpj;

	@InformacaoValidavel(regex = RegexValidacao.CPF, nomeExibicao = "CPF da transportadora")
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
	private String UF;

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

	public void setUF(String uF) {
		UF = uF;
	}

}
