package br.com.plastecno.service.nfe;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;

public class ImpostoImportacao {
	@XmlElement(name = "vII")
	private Double valor;

	@XmlElement(name = "vBC")
	private Double valorBC;

	@XmlElement(name = "vDespAdu")
	private Double valorDespesaAduaneira;

	@XmlElement(name = "vIOF")
	private Double valorIOF;

	@XmlTransient
	public Double getValor() {
		return valor;
	}

	@XmlTransient
	public Double getValorBC() {
		return valorBC;
	}

	public void setValor(Double valor) {
		this.valor = valor;
	}

	public void setValorBC(Double valorBC) {
		this.valorBC = valorBC;
	}

	public void setValorDespesaAduaneira(Double valorDespesaAduaneira) {
		this.valorDespesaAduaneira = valorDespesaAduaneira;
	}

	public void setValorIOF(Double valorIOF) {
		this.valorIOF = valorIOF;
	}

}
