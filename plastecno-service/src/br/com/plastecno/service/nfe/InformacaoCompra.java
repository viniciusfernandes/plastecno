package br.com.plastecno.service.nfe;

import javax.xml.bind.annotation.XmlElement;

public class InformacaoCompra {
	@XmlElement(name = "xCont")
	private String contrato;
	@XmlElement(name = "xNEmp")
	private String notaEmpenho;
	@XmlElement(name = "xPed")
	private String pedido;

	public void setContrato(String contrato) {
		this.contrato = contrato;
	}

	public void setNotaEmpenho(String notaEmpenho) {
		this.notaEmpenho = notaEmpenho;
	}

	public void setPedido(String pedido) {
		this.pedido = pedido;
	}
}
