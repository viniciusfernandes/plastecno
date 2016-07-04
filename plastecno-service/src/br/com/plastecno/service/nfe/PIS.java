package br.com.plastecno.service.nfe;

import javax.xml.bind.annotation.XmlElement;

public class PIS {

	@XmlElement(name = "PISAliq")
	private PISAliquota pisAliquota;

	public void setPisAliquota(PISAliquota pisAliquota) {
		this.pisAliquota = pisAliquota;
	}

}

class PISAliquota {
	@XmlElement(name = "pPIS")
	private Double aliquota;
	@XmlElement(name = "CST")
	private Integer codigoSituacaoTributaria;
	@XmlElement(name = "vPIS")
	private Double valor;
	@XmlElement(name = "vBC")
	private Double valorBC;

	public void setAliquota(Double aliquota) {
		this.aliquota = aliquota;
	}

	public void setCodigoSituacaoTributaria(Integer codigoSituacaoTributaria) {
		this.codigoSituacaoTributaria = codigoSituacaoTributaria;
	}

	public void setValor(Double valor) {
		this.valor = valor;
	}

	public void setValorBC(Double valorBC) {
		this.valorBC = valorBC;
	}
}