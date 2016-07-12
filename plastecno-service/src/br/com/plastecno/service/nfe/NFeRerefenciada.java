package br.com.plastecno.service.nfe;

import javax.xml.bind.annotation.XmlElement;

public class NFeRerefenciada {
	@XmlElement(name = "refNFe")
	private Integer chaveAcessoNFeReferenciada;
	@XmlElement(name = "refNF")
	private IdentificacaoNFeReferenciada identificacaoNFeReferenciada;

	public void setChaveAcessoNFeReferenciada(Integer chaveAcessoNFeReferenciada) {
		this.chaveAcessoNFeReferenciada = chaveAcessoNFeReferenciada;
	}

	public void setIdentificacaoNFeReferenciada(
			IdentificacaoNFeReferenciada identificacaoNFeReferenciada) {
		this.identificacaoNFeReferenciada = identificacaoNFeReferenciada;
	}
}
