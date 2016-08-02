package br.com.plastecno.service.nfe;

import javax.xml.bind.annotation.XmlElement;

public class ValoresTotaisNFe {
	@XmlElement(name = "ICMSTot")
	private ValorTotalICMS valorTotalICMS;

	public void setValorTotalICMS(ValorTotalICMS valorTotalICMS) {
		this.valorTotalICMS = valorTotalICMS;
	}

}
