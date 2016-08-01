package br.com.plastecno.service.nfe;

import javax.xml.bind.annotation.XmlElement;

public class TransporteNFe {
	@XmlElement(name = "modFrete")
	private Integer modalidadeFrete;

	@XmlElement(name = "retTransp")
	private RetencaoICMSTransporteNFe retencaoICMS;

	@XmlElement(name = "transportadora")
	private TransportadoraNFe transportadoraNFe;


	@XmlElement(name = "vol")
	private VolumesTransportados volumesTransportados;

	public void setModalidadeFrete(Integer modalidadeFrete) {
		this.modalidadeFrete = modalidadeFrete;
	}

	public void setRetencaoICMS(RetencaoICMSTransporteNFe retencaoICMS) {
		this.retencaoICMS = retencaoICMS;
	}

	public void setTransportadoraNFe(TransportadoraNFe transportadoraNFe) {
		this.transportadoraNFe = transportadoraNFe;
	}

	public void setVolumesTransportados(
			VolumesTransportados volumesTransportados) {
		this.volumesTransportados = volumesTransportados;
	}
}
