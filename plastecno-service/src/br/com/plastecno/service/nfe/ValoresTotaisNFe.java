package br.com.plastecno.service.nfe;

import javax.xml.bind.annotation.XmlElement;

public class ValoresTotaisNFe {
	@XmlElement(name = "ICMSTot")
	private ValoresTotaisICMS valoresTotaisICMS;

	@XmlElement(name = "ISSQNtot")
	private ValoresTotaisISSQN valoresTotaisISSQN;

	public void setValoresTotaisICMS(ValoresTotaisICMS valoresTotaisICMS) {
		this.valoresTotaisICMS = valoresTotaisICMS;
	}

	public void setValoresTotaisISSQN(ValoresTotaisISSQN valoresTotaisISSQN) {
		this.valoresTotaisISSQN = valoresTotaisISSQN;
	}

}
