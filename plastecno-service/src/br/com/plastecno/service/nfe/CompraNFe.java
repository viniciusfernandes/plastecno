package br.com.plastecno.service.nfe;

import javax.xml.bind.annotation.XmlElement;

import br.com.plastecno.service.validacao.annotation.InformacaoValidavel;

@InformacaoValidavel
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
