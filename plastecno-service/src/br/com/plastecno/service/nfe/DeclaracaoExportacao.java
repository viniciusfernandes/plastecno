package br.com.plastecno.service.nfe;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;

import br.com.plastecno.service.validacao.annotation.InformacaoValidavel;

@InformacaoValidavel
public class DeclaracaoExportacao {

	@InformacaoValidavel(cascata = true, nomeExibicao = "Declaração de exportação indireta")
	@XmlElement(name = "exportInd")
	private DeclaracaoExportacaoIndireta declaracaoExportacaoIndireta;

	@InformacaoValidavel(padrao = { "\\d{9}", "\\d{11}" }, nomeExibicao = "Número de dorwback da declaração de exportação")
	@XmlElement(name = "nDraw")
	private String numeroDrawback;

	@XmlTransient
	public DeclaracaoExportacaoIndireta getDeclaracaoExportacaoIndireta() {
		return declaracaoExportacaoIndireta;
	}

	@XmlTransient
	public DeclaracaoExportacaoIndireta getExpIndireta() {
		return getDeclaracaoExportacaoIndireta();
	}

	@XmlTransient
	public String getNumeroDrawback() {
		return numeroDrawback;
	}

	public void setDeclaracaoExportacaoIndireta(DeclaracaoExportacaoIndireta declaracaoExportacaoIndireta) {
		this.declaracaoExportacaoIndireta = declaracaoExportacaoIndireta;
	}

	public void setExpIndireta(DeclaracaoExportacaoIndireta declaracaoExportacaoIndireta) {
		this.declaracaoExportacaoIndireta = declaracaoExportacaoIndireta;
	}

	public void setNumeroDrawback(String numeroDrawback) {
		this.numeroDrawback = numeroDrawback;
	}

}
