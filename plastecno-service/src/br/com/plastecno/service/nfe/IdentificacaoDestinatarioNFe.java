package br.com.plastecno.service.nfe;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;

import br.com.plastecno.service.constante.RegexValidacao;
import br.com.plastecno.service.validacao.annotation.InformacaoValidavel;

public class IdentificacaoDestinatarioNFe {

	@InformacaoValidavel(obrigatorio = true, regex = RegexValidacao.CNPJ, nomeExibicao = "CNPJ do destinatário")
	@XmlElement(name = "CNPJ")
	private String cnpj;

	@InformacaoValidavel(obrigatorio = true, regex = RegexValidacao.CPF, nomeExibicao = "CPF do destinatário")
	@XmlElement(name = "CPF")
	private String cpf;

	@InformacaoValidavel(intervaloComprimento = { 1, 60 }, nomeExibicao = "Email do destinatário")
	@XmlElement(name = "email")
	private String email;

	@InformacaoValidavel(obrigatorio = true, cascata = true, nomeExibicao = "Endereço do destinatário")
	@XmlElement(name = "enderDest")
	private EnderecoNFe enderecoDestinatarioNFe;

	@InformacaoValidavel(obrigatorio = true, intervaloComprimento = { 2, 14 }, nomeExibicao = "Inscrição estadual do destinatário")
	@XmlElement(name = "IE")
	private String inscricaoEstadual;

	@XmlElement(name = "IM")
	private String inscricaoMunicipal;

	@InformacaoValidavel(intervaloComprimento = { 8, 9 }, nomeExibicao = "Inscrição na SUFRAMA do destinatário")
	@XmlElement(name = "ISUF")
	private String inscricaoSUFRAMA;

	@InformacaoValidavel(obrigatorio = true, intervaloComprimento = { 2, 60 }, nomeExibicao = "Nome do destinatário")
	@XmlElement(name = "xNome")
	private String nomeFantasia;

	@XmlTransient
	public String getEmail() {
		return email;
	}

	@XmlTransient
	public EnderecoNFe getEnderecoDestinatarioNFe() {
		return enderecoDestinatarioNFe;
	}

	public void setCnpj(String cnpj) {
		this.cnpj = cnpj;
	}

	public void setCpf(String cpf) {
		this.cpf = cpf;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public void setEnderecoDestinatarioNFe(EnderecoNFe enderecoDestinatarioNFe) {
		this.enderecoDestinatarioNFe = enderecoDestinatarioNFe;
	}

	public void setInscricaoEstadual(String inscricaoEstadual) {
		this.inscricaoEstadual = inscricaoEstadual;
	}

	public void setInscricaoMunicipal(String inscricaoMunicipal) {
		this.inscricaoMunicipal = inscricaoMunicipal;
	}

	public void setInscricaoSUFRAMA(String inscricaoSUFRAMA) {
		this.inscricaoSUFRAMA = inscricaoSUFRAMA;
	}

	public void setNomeFantasia(String nomeFantasia) {
		this.nomeFantasia = nomeFantasia;
	}

}
