package br.com.plastecno.service.nfe;

import javax.xml.bind.annotation.XmlElement;

import br.com.plastecno.service.validacao.annotation.InformacaoValidavel;

@InformacaoValidavel
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

	@InformacaoValidavel(intervaloNumerico = { 1, 999999 }, nomeExibicao = "Quantidade de volumes transportados")
	@XmlElement(name = "qVol")
	private Integer quantidade;

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

	public void setQuantidade(Integer quantidade) {
		this.quantidade = quantidade;
	}

}
