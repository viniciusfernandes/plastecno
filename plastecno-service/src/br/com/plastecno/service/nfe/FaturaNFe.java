package br.com.plastecno.service.nfe;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;

import br.com.plastecno.service.validacao.annotation.InformacaoValidavel;

@InformacaoValidavel
public class FaturaNFe {
	@InformacaoValidavel(intervaloComprimento = { 1, 60 }, nomeExibicao = "Número da fatura")
	@XmlElement(name = "nFat")
	private String numero;

	@InformacaoValidavel(decimal = { 15, 2 }, nomeExibicao = "Valor desconto da fatura")
	@XmlElement(name = "vDesc")
	private Double valorDesconto;

	@InformacaoValidavel(decimal = { 15, 2 }, nomeExibicao = "Valor líquido da fatura")
	@XmlElement(name = "vLiq")
	private Double valorLiquido;

	@InformacaoValidavel(decimal = { 15, 2 }, nomeExibicao = "Valor original da fatura")
	@XmlElement(name = "vOrig")
	private Double valorOriginal;

	@XmlTransient
	public String getNumero() {
		return numero;
	}

	@XmlTransient
	public Double getValorDesconto() {
		return valorDesconto;
	}

	@XmlTransient
	public Double getValorLiquido() {
		return valorLiquido;
	}

	@XmlTransient
	public Double getValorOriginal() {
		return valorOriginal;
	}

	public void setNumero(String numero) {
		this.numero = numero;
	}

	public void setValorDesconto(Double valorDesconto) {
		this.valorDesconto = valorDesconto;
	}

	public void setValorLiquido(Double valorLiquido) {
		this.valorLiquido = valorLiquido;
	}

	public void setValorOriginal(Double valorOriginal) {
		this.valorOriginal = valorOriginal;
	}

}
