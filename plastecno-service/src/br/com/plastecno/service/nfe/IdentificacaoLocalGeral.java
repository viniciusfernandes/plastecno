package br.com.plastecno.service.nfe;

import javax.xml.bind.annotation.XmlElement;

public class IdentificacaoLocalGeral {
	@XmlElement(name = "CNPJ")
	private String cnpj;

	@XmlElement(name = "cMun")
	private Integer codigoMunicipio;

	@XmlElement(name = "xCpl")
	private String complemento;

	@XmlElement(name = "CPF")
	private String cpf;

	@XmlElement(name = "xLgr")
	private String logradouro;

	@XmlElement(name = "xMun")
	private String municipio;

	@XmlElement(name = "nro")
	private String numero;

	@XmlElement(name = "UF")
	private String uf;

	public void setCnpj(String cnpj) {
		this.cnpj = cnpj;
	}

	public void setCodigoMunicipio(Integer codigoMunicipio) {
		this.codigoMunicipio = codigoMunicipio;
	}

	public void setComplemento(String complemento) {
		this.complemento = complemento;
	}

	public void setCpf(String cpf) {
		this.cpf = cpf;
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

	public void setUf(String uf) {
		this.uf = uf;
	}

}
