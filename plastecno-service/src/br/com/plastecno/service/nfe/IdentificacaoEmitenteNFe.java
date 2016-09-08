package br.com.plastecno.service.nfe;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;

import br.com.plastecno.service.validacao.annotation.InformacaoValidavel;

@InformacaoValidavel
public class IdentificacaoEmitenteNFe {
	@InformacaoValidavel(tamanho = 7, nomeExibicao = "CNAE fiscal do emitente")
	@XmlElement(name = "CNAE")
	private String CNAEFiscal;

	@InformacaoValidavel(obrigatorio = true, tamanho = 14, substituicao = { "\\D", "" }, nomeExibicao = "CNPJ do emitente")
	@XmlElement(name = "CNPJ")
	private String CNPJ;

	@InformacaoValidavel(tamanho = 11, substituicao = { "\\D", "" }, nomeExibicao = "CPF do emitente")
	@XmlElement(name = "CPF")
	private String CPF;

	@InformacaoValidavel(obrigatorio = true, cascata = true, nomeExibicao = "Endereço do emitente")
	@XmlElement(name = "enderEmit")
	private EnderecoNFe enderecoEmitenteNFe;

	@InformacaoValidavel(obrigatorio = true, intervaloComprimento = { 1, 14 }, nomeExibicao = "Inscrição estadual do emitente")
	@XmlElement(name = "IE")
	private String inscricaoEstadual;

	@InformacaoValidavel(intervaloComprimento = { 2, 14 }, nomeExibicao = "Inscrição estadual do substituto tributário do emitente")
	@XmlElement(name = "IEST")
	private String inscricaoEstadualSubstitutoTributario;

	@InformacaoValidavel(intervaloComprimento = { 1, 15 }, nomeExibicao = "Inscrição municipal do emitente")
	@XmlElement(name = "IM")
	private String inscricaoMunicipal;

	@InformacaoValidavel(obrigatorio = true, intervaloComprimento = { 1, 60 }, nomeExibicao = "Nome fantasia do emitente")
	@XmlElement(name = "xFant")
	private String nomeFantasia;

	@InformacaoValidavel(obrigatorio = true, intervaloComprimento = { 2, 60 }, nomeExibicao = "razão social do emitente")
	@XmlElement(name = "xNome")
	private String razaoSocial;

	@InformacaoValidavel(obrigatorio = true, tamanho = 1, nomeExibicao = "Código do regime tributário do emitente")
	@XmlElement(name = "CRT")
	private String regimeTributario;

	@XmlTransient
	public String getCNAEFiscal() {
		return CNAEFiscal;
	}

	@XmlTransient
	public String getCNPJ() {
		return CNPJ;
	}

	@XmlTransient
	public String getCPF() {
		return CPF;
	}

	@XmlTransient
	public EnderecoNFe getEnderecoEmitenteNFe() {
		return enderecoEmitenteNFe;
	}

	@XmlTransient
	public String getInscricaoEstadual() {
		return inscricaoEstadual;
	}

	@XmlTransient
	public String getInscricaoEstadualSubstitutoTributario() {
		return inscricaoEstadualSubstitutoTributario;
	}

	@XmlTransient
	public String getInscricaoMunicipal() {
		return inscricaoMunicipal;
	}

	@XmlTransient
	public String getNomeFantasia() {
		return nomeFantasia;
	}

	@XmlTransient
	public String getRazaoSocial() {
		return razaoSocial;
	}

	@XmlTransient
	public String getRegimeTributario() {
		return regimeTributario;
	}

	public void setCNAEFiscal(String cNAEFiscal) {
		CNAEFiscal = cNAEFiscal;
	}

	public void setCNPJ(String cNPJ) {
		CNPJ = cNPJ;
	}

	public void setCPF(String cPF) {
		CPF = cPF;
	}

	public void setEnderecoEmitenteNFe(EnderecoNFe enderecoEmitenteNFe) {
		this.enderecoEmitenteNFe = enderecoEmitenteNFe;
	}

	public void setInscricaoEstadual(String inscricaoEstadual) {
		this.inscricaoEstadual = inscricaoEstadual;
	}

	public void setInscricaoEstadualSubstitutoTributario(String inscricaoEstadualSubstitutoTributario) {
		this.inscricaoEstadualSubstitutoTributario = inscricaoEstadualSubstitutoTributario;
	}

	public void setInscricaoMunicipal(String inscricaoMunicipal) {
		this.inscricaoMunicipal = inscricaoMunicipal;
	}

	public void setNomeFantasia(String nomeFantasia) {
		this.nomeFantasia = nomeFantasia;
	}

	public void setRazaoSocial(String razaoSocial) {
		this.razaoSocial = razaoSocial;
	}

	public void setRegimeTributario(String regimeTributario) {
		this.regimeTributario = regimeTributario;
	}
}