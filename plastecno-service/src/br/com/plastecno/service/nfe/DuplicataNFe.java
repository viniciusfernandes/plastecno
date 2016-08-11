package br.com.plastecno.service.nfe;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;

public class DuplicataNFe {
	@XmlElement(name = "dVenc")
	private String dataVencimento;

	@XmlElement(name = "nDup")
	private String numero;

	@XmlElement(name = "vDup")
	private Double valor;

	@XmlTransient
	public String getDataVencimento() {
		return dataVencimento;
	}

	@XmlTransient
	public String getNumero() {
		return numero;
	}

	@XmlTransient
	public Double getValor() {
		return valor;
	}

	public void setDataVencimento(String dataVencimento) {
		this.dataVencimento = dataVencimento;
	}

	public void setNumero(String numero) {
		this.numero = numero;
	}

	public void setValor(Double valor) {
		this.valor = valor;
	}
}
