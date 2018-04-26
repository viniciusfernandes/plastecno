package br.com.svr.service.nfe;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import br.com.svr.service.validacao.InformacaoValidavel;

@InformacaoValidavel
@XmlType(propOrder = { "placa", "uf", "registroNacionalTransportador" })
public class VeiculoTransporte {
	@InformacaoValidavel(obrigatorio = true, padrao = { "[a-zA-Z]{2}\\d{4}", "[a-zA-Z]{3}\\d{3}", "[a-zA-Z]{3}\\d{4}",
			"[a-zA-Z]{4}\\d{3}" }, padraoExemplo = "XX9999, XXX999, XXX9999, XXXX999", nomeExibicao = "Placa do veículo de transporte")
	@XmlElement(name = "placa")
	private String placa;

	@InformacaoValidavel(intervaloComprimento = { 1, 20 }, nomeExibicao = "Registro nacional de transportador de carga do veículo de transporte")
	@XmlElement(name = "RNTC")
	private String registroNacionalTransportador;

	@InformacaoValidavel(obrigatorio = true, tamanho = 2, nomeExibicao = "UF do veículo de transporte")
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

	public void setRegistroNacionalTransportador(String registroNacionalTransportador) {
		this.registroNacionalTransportador = registroNacionalTransportador;
	}

	public void setUf(String uf) {
		this.uf = uf;
	}
}
