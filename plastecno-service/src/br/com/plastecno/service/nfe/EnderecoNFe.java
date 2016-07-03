package br.com.plastecno.service.nfe;

public class EnderecoNFe {
	private String bairro;
	private String cep;
	private String complemento;
	private String logradouro;
	private String municipio;
	private String numero;
	private String pais;
	private String telefone;
	private String UF;

	public String getBairro() {
		return bairro;
	}

	public String getCep() {
		return cep;
	}

	public String getComplemento() {
		return complemento;
	}

	public String getLogradouro() {
		return logradouro;
	}

	public String getMunicipio() {
		return municipio;
	}

	public String getNumero() {
		return numero;
	}

	public String getPais() {
		return pais;
	}

	public String getTelefone() {
		return telefone;
	}

	public String getUF() {
		return UF;
	}

	public void setBairro(String bairro) {
		this.bairro = bairro;
	}

	public void setCep(String cep) {
		this.cep = cep;
	}

	public void setComplemento(String complemento) {
		this.complemento = complemento;
	}

	public void setLogradouro(String logradouro) {
		this.logradouro = logradouro;
	}

	public void setMunicipio(String municipio) {
		this.municipio = municipio;
	}

	public void setNumero(String numero) {
		this.numero = numero;
	}

	public void setPais(String pais) {
		this.pais = pais;
	}

	public void setTelefone(String telefone) {
		this.telefone = telefone;
	}

	public void setUF(String uF) {
		UF = uF;
	}
}
