package br.com.plastecno.service.nfe;

import javax.xml.bind.annotation.XmlElement;

public class RetencaoICMSTransporteNFe {
	@XmlElement(name = "pICMSRet")
	private Double aliquota;

	@XmlElement(name = "CFOP")
	private String cfop;

	@XmlElement(name = "vServ")
	private String codigoMunicipioGerador;

	@XmlElement(name = "vICMSRet")
	private Double valor;

	@XmlElement(name = "vBCRet")
	private Double valorBC;

	@XmlElement(name = "vServ")
	private Double valorServico;

	public void setAliquota(Double aliquota) {
		this.aliquota = aliquota;
	}

	public void setCfop(String cfop) {
		this.cfop = cfop;
	}

	public void setCodigoMunicipioGerador(String codigoMunicipioGerador) {
		this.codigoMunicipioGerador = codigoMunicipioGerador;
	}

	public void setValor(Double valor) {
		this.valor = valor;
	}

	public void setValorBC(Double valorBC) {
		this.valorBC = valorBC;
	}

	public void setValorServico(Double valorServico) {
		this.valorServico = valorServico;
	}

}
