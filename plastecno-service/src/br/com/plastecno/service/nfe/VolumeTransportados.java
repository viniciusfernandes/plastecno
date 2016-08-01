package br.com.plastecno.service.nfe;

import javax.xml.bind.annotation.XmlElement;

public class VolumeTransportados {
	@XmlElement(name = "esp")
	private String especie;

	@XmlElement(name = "marca")
	private String marca;

	@XmlElement(name = "nVol")
	private String numeracao;

	@XmlElement(name = "pesoB")
	private Double pesoBruto;

	@XmlElement(name = "pesoL")
	private Double pesoLiquido;

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
