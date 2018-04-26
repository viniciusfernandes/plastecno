package br.com.svr.service.nfe;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import br.com.svr.service.validacao.InformacaoValidavel;

@InformacaoValidavel
@XmlType(propOrder = { "notaEmpenho", "pedido", "contrato" })
public class CompraNFe {
	@InformacaoValidavel(intervaloComprimento = { 1, 60 }, nomeExibicao = "Número do contrato da compra")
	@XmlElement(name = "xCont")
	private String contrato;

	@InformacaoValidavel(intervaloComprimento = { 1, 17 }, nomeExibicao = "Nota de empenho da compra")
	@XmlElement(name = "xNEmp")
	private String notaEmpenho;

	@InformacaoValidavel(intervaloComprimento = { 1, 60 }, nomeExibicao = "Número do pedido da compra")
	@XmlElement(name = "xPed")
	private String pedido;

	@XmlTransient
	public String getContrato() {
		return contrato;
	}

	@XmlTransient
	public String getNotaEmpenho() {
		return notaEmpenho;
	}

	@XmlTransient
	public String getPedido() {
		return pedido;
	}

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
