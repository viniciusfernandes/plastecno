package br.com.plastecno.service.nfe;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;

public class IdentificacaoDestinatarioNFe {
	@XmlElement(name = "CNPJ")
	private String cnpj;

	@XmlElement(name = "CPF")
	private String cpf;

	@XmlElement(name = "email")
	private String email;

	@XmlElement(name = "enderDest")
	private EnderecoNFe enderecoDestinatarioNFe;

	@XmlElement(name = "IE")
	private String inscricaoEstadual;

	@XmlElement(name = "IM")
	private String inscricaoMunicipal;

	@XmlElement(name = "ISUF")
	private String inscricaoSUFRAMA;

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
