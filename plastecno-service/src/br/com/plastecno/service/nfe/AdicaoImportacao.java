package br.com.plastecno.service.nfe;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;

import br.com.plastecno.service.validacao.annotation.InformacaoValidavel;

@InformacaoValidavel
public class AdicaoImportacao {

	@InformacaoValidavel(obrigatorio = true, padrao = "\\d{1,60}", padraoExemplo = "de 1 a 60 digitos", nomeExibicao = "Cógido do fabricante da adição de importação")
	@XmlElement(name = "cFabricante")
	private String codigoFabricante;

	@InformacaoValidavel(obrigatorio = true, padrao = "\\d{1,3}", padraoExemplo = "de 1 a 3 digitos", nomeExibicao = "Número da adição de importação")
	@XmlElement(name = "nAdicao")
	private String numero;

	@InformacaoValidavel(padrao = "\\d{1,3}", padraoExemplo = "de 1 a 3 digitos", nomeExibicao = "Número da adição de importação")
	@XmlElement(name = "nDraw")
	private String numeroDrawback;

	@InformacaoValidavel(obrigatorio = true, padrao = "\\d{1,3}", padraoExemplo = "de 1 a 3 digitos", nomeExibicao = "Número da adição de importação")
	@XmlElement(name = "nSeqAdic")
	private String numeroSequencialItem;

	@InformacaoValidavel(decimal = { 13, 2 }, nomeExibicao = "Valor do desconto da adição de importação")
	@XmlElement(name = "vDescDI")
	private Double valorDesconto;

	@XmlTransient
	public String getCodigoFabricante() {
		return codigoFabricante;
	}

	@XmlTransient
	public String getNumero() {
		return numero;
	}

	@XmlTransient
	public String getNumeroDrawback() {
		return numeroDrawback;
	}

	@XmlTransient
	public String getNumeroSequencialItem() {
		return numeroSequencialItem;
	}

	@XmlTransient
	public Double getValorDesconto() {
		return valorDesconto;
	}

	public void setCodigoFabricante(String codigoFabricante) {
		this.codigoFabricante = codigoFabricante;
	}

	public void setNumero(String numero) {
		this.numero = numero;
	}

	public void setNumeroDrawback(String numeroDrawback) {
		this.numeroDrawback = numeroDrawback;
	}

	public void setNumeroSequencialItem(String numeroSequencialItem) {
		this.numeroSequencialItem = numeroSequencialItem;
	}

	public void setValorDesconto(Double valorDesconto) {
		this.valorDesconto = valorDesconto;
	}
}
