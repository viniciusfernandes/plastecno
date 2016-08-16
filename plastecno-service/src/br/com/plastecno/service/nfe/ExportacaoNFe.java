package br.com.plastecno.service.nfe;

import javax.xml.bind.annotation.XmlElement;

import br.com.plastecno.service.validacao.annotation.InformacaoValidavel;

@InformacaoValidavel
public class ExportacaoNFe {

	@InformacaoValidavel(intervaloComprimento = { 1, 60 }, nomeExibicao = "Local de embarque da exportação")
	@XmlElement(name = "xLocEmbarq")
	private String localEmbarque;

	@InformacaoValidavel(tamanho = 2, nomeExibicao = "UF de embarque da exportação")
	@XmlElement(name = "UFEmbarq")
	private String ufEmbarque;

	public void setLocalEmbarque(String localEmbarque) {
		this.localEmbarque = localEmbarque;
	}

	public void setUfEmbarque(String ufEmbarque) {
		this.ufEmbarque = ufEmbarque;
	}
}
