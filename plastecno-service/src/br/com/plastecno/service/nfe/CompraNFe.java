package br.com.plastecno.service.nfe;

import javax.xml.bind.annotation.XmlElement;

public class CompraNFe {
	@XmlElement(name = "xCont")
	private String contrato;

	@XmlElement(name = "xNEmp")
	private String notaEmprenho;

	@XmlElement(name = "xPed")
	private String pedido;

	public void setContrato(String contrato) {
		this.contrato = contrato;
	}

	public void setNotaEmprenho(String notaEmprenho) {
		this.notaEmprenho = notaEmprenho;
	}

	public void setPedido(String pedido) {
		this.pedido = pedido;
	}

}