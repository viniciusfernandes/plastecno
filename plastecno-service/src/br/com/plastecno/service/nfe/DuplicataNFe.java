package br.com.plastecno.service.nfe;

import javax.xml.bind.annotation.XmlElement;

public class DuplicataNFe {
	@XmlElement(name = "dVenc")
	private String dataVendimento;
	@XmlElement(name = "nDup")
	private String numero;
	@XmlElement(name = "vDup")
	private Double valor;

	public void setDataVendimento(String dataVendimento) {
		this.dataVendimento = dataVendimento;
	}

	public void setNumero(String numero) {
		this.numero = numero;
	}

	public void setValor(Double valor) {
		this.valor = valor;
	}
}
