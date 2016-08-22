package br.com.plastecno.service.nfe;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;

import br.com.plastecno.service.validacao.annotation.InformacaoValidavel;

@InformacaoValidavel
public class EnderecoNFe {

	@InformacaoValidavel(obrigatorio = true, intervaloComprimento = { 2, 60 }, nomeExibicao = "Bairro do endereço")
	@XmlElement(name = "xBairro")
	private String bairro;

	@InformacaoValidavel(tamanho = 8, nomeExibicao = "CEP do endereço")
	@XmlElement(name = "CEP")
	private String cep;

	@InformacaoValidavel(obrigatorio = true, intervaloNumerico = { 1, 99999999 }, nomeExibicao = "Código do município")
	@XmlElement(name = "cMun")
	private String codigoMunicipio;

	@InformacaoValidavel(intervaloNumerico = { 1, 9999 }, nomeExibicao = "Bairro do endereço")
	@XmlElement(name = "cPais")
	private String codigoPais;

	@InformacaoValidavel(intervaloComprimento = { 0, 60 }, nomeExibicao = "Complemento do endereço")
	@XmlElement(name = "xCpl")
	private String complemento;

	@InformacaoValidavel(obrigatorio = true, intervaloComprimento = { 2, 60 }, nomeExibicao = "Logradouro do endereço")
	@XmlElement(name = "xLgr")
	private String logradouro;

	@InformacaoValidavel(obrigatorio = true, intervaloComprimento = { 2, 60 }, nomeExibicao = "Nome do município do endereço")
	@XmlElement(name = "xMun")
	private String nomeMunicipio;

	@InformacaoValidavel(intervaloComprimento = { 1, 60 }, nomeExibicao = "Código do país do endereço")
	@XmlElement(name = "xPais")
	private String nomePais;

	@InformacaoValidavel(obrigatorio = true, intervaloComprimento = { 1, 60 }, nomeExibicao = "Número do endereço")
	@XmlElement(name = "nro")
	private String numero;

	@InformacaoValidavel(padrao = "\\d{6,14}", nomeExibicao = "telefone do endereço")
	@XmlElement(name = "fone")
	private String telefone;

	@InformacaoValidavel(obrigatorio = true, tamanho = 2, nomeExibicao = "Sigla da UF do endereço")
	@XmlElement(name = "UF")
	private String UF;

	@XmlTransient
	public String getBairro() {
		return bairro;
	}

	@XmlTransient
	public String getCep() {
		return cep;
	}

	@XmlTransient
	public String getCodigoMunicipio() {
		return codigoMunicipio;
	}

	@XmlTransient
	public String getCodigoPais() {
		return codigoPais;
	}

	@XmlTransient
	public String getComplemento() {
		return complemento;
	}

	@XmlTransient
	public String getLogradouro() {
		return logradouro;
	}

	@XmlTransient
	public String getNomeMunicipio() {
		return nomeMunicipio;
	}

	@XmlTransient
	public String getNomePais() {
		return nomePais;
	}

	@XmlTransient
	public String getNumero() {
		return numero;
	}

	@XmlTransient
	public String getTelefone() {
		return telefone;
	}

	@XmlTransient
	public String getUF() {
		return UF;
	}

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