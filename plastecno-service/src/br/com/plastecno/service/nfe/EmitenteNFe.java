package br.com.plastecno.service.nfe;

public class EmitenteNFe {
	private String cnpj;
	private EnderecoNFe enderecoEmitenteNFe;
	private String inscricaoEstadual;
	private String nomeFantasia;
	private String razaoSocial;
	private String regimeTributario;

	public String getCnpj() {
		return cnpj;
	}

	public EnderecoNFe getEnderecoEmitenteNFe() {
		return enderecoEmitenteNFe;
	}

	public String getInscricaoEstadual() {
		return inscricaoEstadual;
	}

	public String getNomeFantasia() {
		return nomeFantasia;
	}

	public String getRazaoSocial() {
		return razaoSocial;
	}

	public String getRegimeTributario() {
		return regimeTributario;
	}

	public void setCnpj(String cnpj) {
		this.cnpj = cnpj;
	}

	public void setEnderecoEmitenteNFe(EnderecoNFe enderecoEmitenteNFe) {
		this.enderecoEmitenteNFe = enderecoEmitenteNFe;
	}

	public void setInscricaoEstadual(String inscricaoEstadual) {
		this.inscricaoEstadual = inscricaoEstadual;
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