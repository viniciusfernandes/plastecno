package br.com.plastecno.service.nfe;

import javax.xml.bind.annotation.XmlElement;


public class ExportacaoNfe {

	@XmlElement(name = "xLocEmbarq")
	private String localEmbarque;

	@XmlElement(name = "UFEmbarq")
	private String ufEmbarque;

	public void setLocalEmbarque(String localEmbarque) {
		this.localEmbarque = localEmbarque;
	}

	public void setUfEmbarque(String ufEmbarque) {
		this.ufEmbarque = ufEmbarque;
	}
}
