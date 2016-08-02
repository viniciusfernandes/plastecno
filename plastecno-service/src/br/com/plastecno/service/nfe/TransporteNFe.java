package br.com.plastecno.service.nfe;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;

public class TransporteNFe {
	@XmlElement(name = "vol")
	private List<VolumeTransportados> listaVolume;

	@XmlElement(name = "modFrete")
	private Integer modalidadeFrete;

	@XmlElement(name = "retTransp")
	private RetencaoICMSTransporteNFe retencaoICMS;

	@XmlElement(name = "transportadora")
	private TransportadoraNFe transportadoraNFe;

	public void setListaVolume(List<VolumeTransportados> listaVolume) {
		this.listaVolume = listaVolume;
	}

	public void setModalidadeFrete(Integer modalidadeFrete) {
		this.modalidadeFrete = modalidadeFrete;
	}

	public void setRetencaoICMS(RetencaoICMSTransporteNFe retencaoICMS) {
		this.retencaoICMS = retencaoICMS;
	}

	public void setTransportadoraNFe(TransportadoraNFe transportadoraNFe) {
		this.transportadoraNFe = transportadoraNFe;
	}

}
