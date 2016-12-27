package br.com.plastecno.service.nfe;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import br.com.plastecno.service.validacao.annotation.InformacaoValidavel;

@InformacaoValidavel
@XmlType(propOrder = { "quantidade", "especie", "marca", "numeracao", "pesoLiquido", "pesoBruto" })
public class VolumeTransportado {

	@InformacaoValidavel(intervaloComprimento = { 1, 60 }, nomeExibicao = "Espécie dos volumes transportados")
	@XmlElement(name = "esp")
	private String especie;

	@InformacaoValidavel(intervaloComprimento = { 1, 60 }, nomeExibicao = "Marca dos volumes transportados")
	@XmlElement(name = "marca")
	private String marca;

	@InformacaoValidavel(intervaloComprimento = { 1, 60 }, nomeExibicao = "Numeração dos volumes transportados")
	@XmlElement(name = "nVol")
	private String numeracao;

	@InformacaoValidavel(decimal = { 15, 3 }, nomeExibicao = "Peso bruto dos volumes transportados")
	@XmlElement(name = "pesoB")
	private Double pesoBruto;

	@InformacaoValidavel(decimal = { 15, 3 }, nomeExibicao = "Peso líquido dos volumes transportados")
	@XmlElement(name = "pesoL")
	private Double pesoLiquido;

	@InformacaoValidavel(padrao = "\\d{1,15}", padraoExemplo = "1 a 15 digitos", nomeExibicao = "Quantidade de volumes transportados")
	@XmlElement(name = "qVol")
	private String quantidade;

	@XmlTransient
	public String getEspecie() {
		return especie;
	}

	@XmlTransient
	public String getMarca() {
		return marca;
	}

	@XmlTransient
	public String getNumeracao() {
		return numeracao;
	}

	@XmlTransient
	public Double getPesoBruto() {
		return pesoBruto;
	}

	@XmlTransient
	public Double getPesoLiquido() {
		return pesoLiquido;
	}

	@XmlTransient
	public String getQuantidade() {
		return quantidade;
	}

	public void setEspecie(String especie) {
		this.especie = especie;
	}

	public void setMarca(String marca) {
		this.marca = marca;
	}

	public void setNumeracao(String numeracao) {
		this.numeracao = numeracao;
	}

	public void setPesoBruto(Double pesoBruto) {
		this.pesoBruto = pesoBruto;
	}

	public void setPesoLiquido(Double pesoLiquido) {
		this.pesoLiquido = pesoLiquido;
	}

	public void setQuantidade(String quantidade) {
		this.quantidade = quantidade;
	}

}
