package br.com.plastecno.service.nfe;

import javax.xml.bind.annotation.XmlElement;

import br.com.plastecno.service.validacao.annotation.InformacaoValidavel;

public class IdentificacaoEmitenteNFe {
	@XmlElement(name = "CNAE")
	private String CNAEFiscal;

	@InformacaoValidavel(obrigatorio = true, tamanho = 14, nomeExibicao = "CNPJ do emitente")
	@XmlElement(name = "CNPJ")
	private String CNPJ;

	@InformacaoValidavel(obrigatorio = true, tamanho = 11, nomeExibicao = "CPF do retemente")
	@XmlElement(name = "CPF")
	private String CPF;

	@InformacaoValidavel(obrigatorio = true, cascata = true, nomeExibicao = "Endereço do emitente")
	@XmlElement(name = "enderEmit")
	private EnderecoNFe enderecoEmitenteNFe;

	@XmlElement(name = "IE")
	private String inscricaoEstadual;

	@XmlElement(name = "IEST")
	private String inscricaoEstadualSubstitutoTributario;

	@XmlElement(name = "IM")
	private String inscricaoMunicipal;

	@InformacaoValidavel(obrigatorio = true, intervaloComprimento = { 1, 60 }, nomeExibicao = "Nome fantasia do emitente")
	@XmlElement(name = "xFant")
	private String nomeFantasia;

	@InformacaoValidavel(obrigatorio = true, intervaloComprimento = { 2, 60 }, nomeExibicao = "razão social do emitente")
	@XmlElement(name = "xNome")
	private String razaoSocial;

	@XmlElement(name = "CRT")
	private Integer regimeTributario;

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

	public void setInscricaoEstadualSubstitutoTributario(
			String inscricaoEstadualSubstitutoTributario) {
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

	public void setRegimeTributario(Integer regimeTributario) {
		this.regimeTributario = regimeTributario;
	}
}