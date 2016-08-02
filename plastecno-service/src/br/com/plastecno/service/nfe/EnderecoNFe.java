package br.com.plastecno.service.nfe;

import javax.xml.bind.annotation.XmlElement;

public class EnderecoNFe {
	@XmlElement(name = "xBairro")
	private String bairro;
	@XmlElement(name = "CEP")
	private String cep;
	@XmlElement(name = "cMun")
	private String codigoMunicipio;
	@XmlElement(name = "cPais")
	private String codigoPais;
	@XmlElement(name = "xCpl")
	private String complemento;
	@XmlElement(name = "xLgr")
	private String logradouro;
	@XmlElement(name = "xMun")
	private String nomeMunicipio;
	@XmlElement(name = "xPais")
	private String nomePais;
	@XmlElement(name = "nro")
	private String numero;
	@XmlElement(name = "fone")
	private String telefone;
	@XmlElement(name = "UF")
	private String UF;
	public void setBairro(String bairro) {
		this.bairro = bairro;
	}
	public void setCep(String cep) {
		this.cep = cep;
	}
	public void setCodigoMunicipio(String codigoMunicipio) {
		this.codigoMunicipio = codigoMunicipio;
	}
	public void setCodigoPais(String codigoPais) {
		this.codigoPais = codigoPais;
	}
	public void setComplemento(String complemento) {
		this.complemento = complemento;
	}
	public void setLogradouro(String logradouro) {
		this.logradouro = logradouro;
	}
	public void setNomeMunicipio(String nomeMunicipio) {
		this.nomeMunicipio = nomeMunicipio;
	}
	public void setNomePais(String nomePais) {
		this.nomePais = nomePais;
	}
	public void setNumero(String numero) {
		this.numero = numero;
	}
	public void setTelefone(String telefone) {
		this.telefone = telefone;
	}
	public void setUF(String uF) {
		UF = uF;
	}
}