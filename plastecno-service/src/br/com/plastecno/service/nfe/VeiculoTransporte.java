package br.com.plastecno.service.nfe;

import javax.xml.bind.annotation.XmlElement;

public class VeiculoTransporte {
	@XmlElement(name = "placa")
	private String placa;

	@XmlElement(name = "RNTC")
	private String registroNacionalTransportador;

	@XmlElement(name = "UF")
	private String uf;

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
