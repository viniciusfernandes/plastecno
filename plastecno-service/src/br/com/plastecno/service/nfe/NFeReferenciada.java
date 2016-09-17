package br.com.plastecno.service.nfe;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;

import br.com.plastecno.service.validacao.annotation.InformacaoValidavel;

@InformacaoValidavel
public class NFeReferenciada {
	@InformacaoValidavel(obrigatorio = true, padrao = { "\\d{44}" }, padraoExemplo = "44 digitos", nomeExibicao = "Chave acesso  da NFe Referenciada")
	@XmlElement(name = "refNFe")
	private String chaveAcessoReferenciada;

	@XmlElement(name = "refNF")
	@InformacaoValidavel(obrigatorio = true, cascata = true, nomeExibicao = "Identifica��o da NFe referenciada")
	private IdentificacaoNFeReferenciada identificacaoNFeReferenciada;

	@XmlTransient
	public String getChaveAcessoReferenciada() {
		return chaveAcessoReferenciada;
	}

	@XmlTransient
	public IdentificacaoNFeReferenciada getIdentificacaoNFeReferenciada() {
		return identificacaoNFeReferenciada;
	}

	public void setChaveAcessoReferenciada(String chaveAcessoReferenciada) {
		this.chaveAcessoReferenciada = chaveAcessoReferenciada;
	}

	public void setIdentificacaoNFeReferenciada(IdentificacaoNFeReferenciada identificacaoNFeReferenciada) {
		this.identificacaoNFeReferenciada = identificacaoNFeReferenciada;
	}
}
