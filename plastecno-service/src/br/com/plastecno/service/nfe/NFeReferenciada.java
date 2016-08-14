package br.com.plastecno.service.nfe;

import javax.xml.bind.annotation.XmlElement;

import br.com.plastecno.service.validacao.annotation.InformacaoValidavel;

public class NFeReferenciada {
	@InformacaoValidavel(obrigatorio = true, nomeExibicao = "Chave acesso  da NFe Referenciada")
	@XmlElement(name = "refNFe")
	private Long chaveAcessoReferenciada;

	@XmlElement(name = "refNF")
	private IdentificacaoNFeReferenciada identificacaoNFeReferenciada;

	public void setChaveAcessoReferenciada(Long chaveAcessoReferenciada) {
		this.chaveAcessoReferenciada = chaveAcessoReferenciada;
	}

	public void setIdentificacaoNFeReferenciada(
			IdentificacaoNFeReferenciada identificacaoNFeReferenciada) {
		this.identificacaoNFeReferenciada = identificacaoNFeReferenciada;
	}
}
