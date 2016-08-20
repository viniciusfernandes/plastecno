package br.com.plastecno.service.nfe;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;

import br.com.plastecno.service.constante.RegexValidacao;
import br.com.plastecno.service.validacao.annotation.InformacaoValidavel;

@InformacaoValidavel
public class IdentificacaoLocalGeral {
	@InformacaoValidavel(obrigatorio = true, intervaloComprimento = { 1, 60 }, nomeExibicao = "Bairro do local do produto")
	@XmlElement(name = "xBairro")
	private String bairro;

	@InformacaoValidavel(obrigatorio = true, regex = RegexValidacao.CNPJ, nomeExibicao = "CNPJ do local do produto")
	@XmlElement(name = "CNPJ")
	private String cnpj;

	@InformacaoValidavel(obrigatorio = true, tamanho = 7, nomeExibicao = "Código do município do local do produto")
	@XmlElement(name = "cMun")
	private Integer codigoMunicipio;

	@InformacaoValidavel(intervaloComprimento = { 1, 60 }, nomeExibicao = "Complemento do local do produto")
	@XmlElement(name = "xCpl")
	private String complemento;

	@InformacaoValidavel(obrigatorio = true, regex = RegexValidacao.CPF, nomeExibicao = "CPF do local do produto")
	@XmlElement(name = "CPF")
	private String cpf;

	@InformacaoValidavel(obrigatorio = true, intervaloComprimento = { 2, 60 }, nomeExibicao = "Logradouro do local do produto")
	@XmlElement(name = "xLgr")
	private String logradouro;

	@InformacaoValidavel(obrigatorio = true, intervaloComprimento = { 2, 60 }, nomeExibicao = "Nome do município do local do produto")
	@XmlElement(name = "xMun")
	private String municipio;

	@InformacaoValidavel(obrigatorio = true, intervaloComprimento = { 1, 60 }, nomeExibicao = "Número do local do produto")
	@XmlElement(name = "nro")
	private String numero;

	@InformacaoValidavel(obrigatorio = true, tamanho = 2, nomeExibicao = "UF do local do produto")
	@XmlElement(name = "UF")
	private String uf;

	@XmlTransient
	public String getBairro() {
		return bairro;
	}

	@XmlTransient
	public String getCnpj() {
		return cnpj;
	}

	@XmlTransient
	public Integer getCodigoMunicipio() {
		return codigoMunicipio;
	}

	@XmlTransient
	public String getComplemento() {
		return complemento;
	}

	@XmlTransient
	public String getCpf() {
		return cpf;
	}

	@XmlTransient
	public String getLogradouro() {
		return logradouro;
	}

	@XmlTransient
	public String getMunicipio() {
		return municipio;
	}

	@XmlTransient
	public String getNumero() {
		return numero;
	}

	@XmlTransient
	public String getUf() {
		return uf;
	}

	public void setBairro(String bairro) {
		this.bairro = bairro;
	}

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
