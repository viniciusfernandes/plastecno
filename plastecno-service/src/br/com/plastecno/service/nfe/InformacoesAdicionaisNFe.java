package br.com.plastecno.service.nfe;

import javax.xml.bind.annotation.XmlElement;

import br.com.plastecno.service.validacao.annotation.InformacaoValidavel;

@InformacaoValidavel
public class InformacoesAdicionaisNFe {
	@InformacaoValidavel(intervaloComprimento = { 1, 2000 }, nomeExibicao = "Informações adicionais de interesse do fisco")
	@XmlElement(name = "infAdFisco")
	private String informacoesAdicionaisInteresseFisco;

	@InformacaoValidavel(intervaloComprimento = { 1, 5000 }, nomeExibicao = "Informações adicionais de interesse do contribuinte")
	@XmlElement(name = "infCpl")
	private String informacoesComplementaresInteresseContribuinte;

	public void setInformacoesAdicionaisInteresseFisco(
			String informacoesAdicionaisInteresseFisco) {
		this.informacoesAdicionaisInteresseFisco = informacoesAdicionaisInteresseFisco;
	}

	public void setInformacoesComplementaresInteresseContribuinte(
			String informacoesComplementaresInteresseContribuinte) {
		this.informacoesComplementaresInteresseContribuinte = informacoesComplementaresInteresseContribuinte;
	}
}
