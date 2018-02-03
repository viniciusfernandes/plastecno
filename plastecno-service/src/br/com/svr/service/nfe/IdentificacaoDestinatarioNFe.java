package br.com.svr.service.nfe;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import br.com.svr.service.validacao.annotation.InformacaoValidavel;

@InformacaoValidavel
@XmlType(propOrder = { "cnpj", "cpf", "identificacaoDestinatarioEstrangeiro", "razaoSocial", "enderecoDestinatarioNFe",
		"indicadorIEDestinatario", "inscricaoEstadual", "inscricaoSUFRAMA", "inscricaoMunicipal", "email" })
public class IdentificacaoDestinatarioNFe {

	@InformacaoValidavel(padrao = "\\d{14}", padraoExemplo = "14 digitos", substituicao = { "\\D", "" }, nomeExibicao = "CNPJ do destinatário")
	@XmlElement(name = "CNPJ")
	private String cnpj;

	@InformacaoValidavel(padrao = "\\d{11}", padraoExemplo = "11 digitos", substituicao = { "\\D", "" }, nomeExibicao = "CPF do destinatário")
	@XmlElement(name = "CPF")
	private String cpf;

	@InformacaoValidavel(intervaloComprimento = { 1, 60 }, nomeExibicao = "Email do destinatário")
	@XmlElement(name = "email")
	private String email;

	@InformacaoValidavel(obrigatorio = true, cascata = true, nomeExibicao = "Endereço do destinatário")
	@XmlElement(name = "enderDest")
	private EnderecoNFe enderecoDestinatarioNFe;

	@InformacaoValidavel(intervaloComprimento = { 5, 20 }, nomeExibicao = "Identificação do destinatário estrangeiro")
	@XmlElement(name = "idEstrangeiro")
	private String identificacaoDestinatarioEstrangeiro;

	@InformacaoValidavel(obrigatorio = true, opcoes = { "1", "2", "9" }, nomeExibicao = "Indicador da IE do destinatário")
	@XmlElement(name = "indIEDest")
	private String indicadorIEDestinatario;

	@InformacaoValidavel(intervaloComprimento = { 2, 14 }, nomeExibicao = "Inscrição estadual do destinatário")
	@XmlElement(name = "IE")
	private String inscricaoEstadual;

	@XmlElement(name = "IM")
	private String inscricaoMunicipal;

	@InformacaoValidavel(padrao = "\\d{8,9}", padraoExemplo = "8 a 9 dígitos", nomeExibicao = "Inscrição na SUFRAMA do destinatário")
	@XmlElement(name = "ISUF")
	private String inscricaoSUFRAMA;

	@InformacaoValidavel(obrigatorio = true, intervaloComprimento = { 2, 70 }, nomeExibicao = "Razão social do destinatário")
	@XmlElement(name = "xNome")
	private String razaoSocial;

	public boolean contemEndereco() {
		return enderecoDestinatarioNFe != null && enderecoDestinatarioNFe.getCep() != null
				&& !enderecoDestinatarioNFe.getCep().isEmpty() && enderecoDestinatarioNFe.getLogradouro() != null
				&& !enderecoDestinatarioNFe.getLogradouro().isEmpty()
				&& enderecoDestinatarioNFe.getNomeMunicipio() != null
				&& !enderecoDestinatarioNFe.getNomeMunicipio().isEmpty();
	}

	@XmlTransient
	public String getCnpj() {
		return cnpj;
	}

	@XmlTransient
	public String getCpf() {
		return cpf;
	}

	@XmlTransient
	public String getEmail() {
		return email;
	}

	@XmlTransient
	public EnderecoNFe getEnderecoDestinatarioNFe() {
		return enderecoDestinatarioNFe;
	}

	@XmlTransient
	public String getIdentificacaoDestinatarioEstrangeiro() {
		return identificacaoDestinatarioEstrangeiro;
	}

	@XmlTransient
	public String getIndicadorIEDestinatario() {
		return indicadorIEDestinatario;
	}

	@XmlTransient
	public String getInscricaoEstadual() {
		return inscricaoEstadual;
	}

	@XmlTransient
	public String getInscricaoMunicipal() {
		return inscricaoMunicipal;
	}

	@XmlTransient
	public String getInscricaoSUFRAMA() {
		return inscricaoSUFRAMA;
	}

	@XmlTransient
	public String getRazaoSocial() {
		return razaoSocial;
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

	public void setIdentificacaoDestinatarioEstrangeiro(String identificacaoDestinatarioEstrangeiro) {
		this.identificacaoDestinatarioEstrangeiro = identificacaoDestinatarioEstrangeiro;
	}

	public void setIndicadorIEDestinatario(String indicadorIEDestinatario) {
		this.indicadorIEDestinatario = indicadorIEDestinatario;
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

	public void setRazaoSocial(String razaoSocial) {
		this.razaoSocial = razaoSocial;
	}

}
