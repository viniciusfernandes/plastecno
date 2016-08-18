package br.com.plastecno.service.nfe;

import javax.xml.bind.annotation.XmlElement;

import br.com.plastecno.service.validacao.annotation.InformacaoValidavel;

@InformacaoValidavel
public class ValoresTotaisNFe {

	@InformacaoValidavel(obrigatorio = true, cascata = true, nomeExibicao = "Valores totais de ICMS")
	@XmlElement(name = "ICMSTot")
	private ValoresTotaisICMS valoresTotaisICMS;

	@InformacaoValidavel(cascata = true, nomeExibicao = "Valores totais de ISS")
	@XmlElement(name = "ISSQNtot")
	private ValoresTotaisISSQN valoresTotaisISSQN;

	public void setValoresTotaisICMS(ValoresTotaisICMS valoresTotaisICMS) {
		this.valoresTotaisICMS = valoresTotaisICMS;
	}

	public void setValoresTotaisISSQN(ValoresTotaisISSQN valoresTotaisISSQN) {
		this.valoresTotaisISSQN = valoresTotaisISSQN;
	}

}
