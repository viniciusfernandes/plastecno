package br.com.plastecno.service.nfe;

import javax.xml.bind.annotation.XmlElement;

public class COFINS {
	@XmlElement(name = "COFINSAliq")
	private COFINSAliquota cOFINSAliquota;

	public void setcOFINSAliquota(COFINSAliquota cOFINSAliquota) {
		this.cOFINSAliquota = cOFINSAliquota;
	}
}

class COFINSAliquota {
	@XmlElement(name = "pCOFINS")
	private Double aliquota;
	@XmlElement(name = "vCOFINS")
	private Double valor;
	@XmlElement(name = "vBC")
	private Double valorBC;

	public void setAliquota(Double aliquota) {
		this.aliquota = aliquota;
	}

	public void setValor(Double valor) {
		this.valor = valor;
	}

	public void setValorBC(Double valorBC) {
		this.valorBC = valorBC;
	}
}