package br.com.plastecno.service.nfe;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;

public class IdentificacaoDestinatarioNFe {
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
	private Integer tipoContribuinte;
	private Integer tipoDocumento;

	@XmlTransient
	public String getEmail() {
		return email;
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

	public void setTipoContribuinte(Integer tipoContribuinte) {
		this.tipoContribuinte = tipoContribuinte;
	}

	public void setTipoDocumento(Integer tipoDocumento) {
		this.tipoDocumento = tipoDocumento;
	}

}
