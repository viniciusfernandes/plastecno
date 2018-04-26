package br.com.svr.service.nfe;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import br.com.svr.service.validacao.InformacaoValidavel;

@InformacaoValidavel
@XmlType(propOrder = { "numeroDrawback", "declaracaoExportacaoIndireta" })
public class DeclaracaoExportacao {

	@InformacaoValidavel(cascata = true, nomeExibicao = "Declaração de exportação indireta")
	@XmlElement(name = "exportInd")
	private DeclaracaoExportacaoIndireta declaracaoExportacaoIndireta;

	@InformacaoValidavel(padrao = { "\\d{9}", "\\d{11}" }, padraoExemplo = "9 ou 11 digitos", nomeExibicao = "Número de dorwback da declaração de exportação")
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
