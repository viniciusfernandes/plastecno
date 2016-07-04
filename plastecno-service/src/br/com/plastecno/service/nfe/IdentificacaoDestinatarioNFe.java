package br.com.plastecno.service.nfe;

public class IdentificacaoDestinatarioNFe {
	private String email;
	private EnderecoNFe enderecoDestinatarioNFe;
	private String inscricaoEstadual;
	private String inscricaoMunicipal;
	private String inscricaoSUFRAMA;
	private String nomeFantasia;
	private Integer tipoContribuinte;
	private Integer tipoDocumento;

	public String getEmail() {
		return email;
	}

	public EnderecoNFe getEnderecoDestinatarioNFe() {
		return enderecoDestinatarioNFe;
	}

	public String getInscricaoEstadual() {
		return inscricaoEstadual;
	}

	public String getInscricaoMunicipal() {
		return inscricaoMunicipal;
	}

	public String getInscricaoSUFRAMA() {
		return inscricaoSUFRAMA;
	}

	public String getNomeFantasia() {
		return nomeFantasia;
	}

	public Integer getTipoContribuinte() {
		return tipoContribuinte;
	}

	public Integer getTipoDocumento() {
		return tipoDocumento;
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
