package br.com.plastecno.service.nfe;

import javax.xml.bind.annotation.XmlElement;

public class TransporteNFe {
	@XmlElement(name = "modFrete")
	private Integer modalidadeFrete;
	@XmlElement(name = "transportadora")
	private TransportadorNFe transportadorNFe;
	@XmlElement(name = "vol")
	private VolumesTransportados volumesTransportados;
}
