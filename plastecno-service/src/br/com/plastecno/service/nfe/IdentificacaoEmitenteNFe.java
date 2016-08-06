package br.com.plastecno.service.nfe;

import javax.xml.bind.annotation.XmlElement;

public class IdentificacaoEmitenteNFe {
	@XmlElement(name = "CNAE")
	private String CNAEFiscal;

	@XmlElement(name = "CNPJ")
	private String CNPJ;

	@XmlElement(name = "CPF")
	private String CPF;

	@XmlElement(name = "enderEmit")
	private EnderecoNFe enderecoEmitenteNFe;

	@XmlElement(name = "IE")
	private String inscricaoEstadual;

	@XmlElement(name = "IEST")
	private String inscricaoEstadualSubstitutoTributario;

	@XmlElement(name = "IM")
	private String inscricaoMunicipal;

	@XmlElement(name = "xFant")
	private String nomeFantasia;

	@XmlElement(name = "xNome")
	private String razaoSocial;

	@XmlElement(name = "CRT")
	private String regimeTributario;

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

	public void setRegimeTributario(String regimeTributario) {
		this.regimeTributario = regimeTributario;
	}
}