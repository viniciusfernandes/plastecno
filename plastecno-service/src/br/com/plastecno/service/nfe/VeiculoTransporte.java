package br.com.plastecno.service.nfe;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;

import br.com.plastecno.service.validacao.annotation.InformacaoValidavel;

@InformacaoValidavel
public class VeiculoTransporte {
	@InformacaoValidavel(obrigatorio = true, padrao ="\\[A,Z]{2,4}\\d{3,4}", padraoExemplo="XX9999, XXX999, XXX9999, XXXX999", nomeExibicao = "Placa do veículo")
	@XmlElement(name = "placa")
	private String placa;

	@InformacaoValidavel(intervaloComprimento = { 1, 20 }, nomeExibicao = "Registro nacional de transportador de carga")
	@XmlElement(name = "RNTC")
	private String registroNacionalTransportador;

	@InformacaoValidavel(obrigatorio = true, intervaloComprimento = { 1, 8 }, nomeExibicao = "Placa do veículo")
	@XmlElement(name = "UF")
	private String uf;

	@XmlTransient
	public String getPlaca() {
		return placa;
	}

	@XmlTransient
	public String getRegistroNacionalTransportador() {
		return registroNacionalTransportador;
	}

	@XmlTransient
	public String getUf() {
		return uf;
	}

	public void setPlaca(String placa) {
		this.placa = placa;
	}

	public void setRegistroNacionalTransportador(
			String registroNacionalTransportador) {
		this.registroNacionalTransportador = registroNacionalTransportador;
	}

	public void setUf(String uf) {
		this.uf = uf;
	}

}
