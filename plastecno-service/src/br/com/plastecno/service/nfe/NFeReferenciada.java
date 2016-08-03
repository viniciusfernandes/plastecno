package br.com.plastecno.service.nfe;

import javax.xml.bind.annotation.XmlElement;

public class NFeReferenciada {
	@XmlElement(name = "refNFe")
	private Integer chaveAcessoReferenciada;

	@XmlElement(name = "refNF")
	private IdentificacaoNFeReferenciada identificacaoNFeReferenciada;

	public void setChaveAcessoReferenciada(Integer chaveAcessoReferenciada) {
		this.chaveAcessoReferenciada = chaveAcessoReferenciada;
	}

	public void setIdentificacaoNFeReferenciada(
			IdentificacaoNFeReferenciada identificacaoNFeReferenciada) {
		this.identificacaoNFeReferenciada = identificacaoNFeReferenciada;
	}
}
