package br.com.plastecno.service.nfe;

import javax.xml.bind.annotation.XmlElement;

public class FaturaNFe {
	@XmlElement(name = "nFat")
	private String numero;
	@XmlElement(name = "vDesc")
	private Double valorDesconto;
	@XmlElement(name = "vLiq")
	private Double valorLiquido;
	@XmlElement(name = "vOrig")
	private Double valorOriginal;

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
