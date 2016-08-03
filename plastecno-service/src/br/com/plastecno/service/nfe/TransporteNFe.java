package br.com.plastecno.service.nfe;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;

public class TransporteNFe {
	@XmlElement(name = "reboque")
	private List<VeiculoTransporte> listaReboque;

	@XmlElement(name = "vol")
	private List<VolumeTransportado> listaVolume;

	@XmlElement(name = "modFrete")
	private Integer modalidadeFrete;

	@XmlElement(name = "retTransp")
	private RetencaoICMSTransporteNFe retencaoICMS;

	@XmlElement(name = "transportadora")
	private TransportadoraNFe transportadoraNFe;

	@XmlElement(name = "veicTransp")
	private VeiculoTransporte veiculo;

	public void setListaReboque(List<VeiculoTransporte> listaReboque) {
		this.listaReboque = listaReboque;
	}

	public void setListaVolume(List<VolumeTransportado> listaVolume) {
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

	public void setVeiculo(VeiculoTransporte veiculo) {
		this.veiculo = veiculo;
	}

}
