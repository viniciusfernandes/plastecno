package br.com.plastecno.service.wrapper;

public class TotalizacaoPedidoWrapper {
	private String nomeCliente;
	private Long quantidade;
	private Double valorTotal;
	private String valorTotalFormatado;

	public TotalizacaoPedidoWrapper(String nomeCliente, Long quantidade, Double valorTotal) {
		this.nomeCliente = nomeCliente;
		this.quantidade = quantidade;
		this.valorTotal = valorTotal;
	}

	public String getNomeCliente() {
		return nomeCliente;
	}

	public Long getQuantidade() {
		return quantidade;
	}

	public Double getValorTotal() {
		return valorTotal;
	}

	public String getValorTotalFormatado() {
		return valorTotalFormatado;
	}

	public void setNomeCliente(String nomeCliente) {
		this.nomeCliente = nomeCliente;
	}

	public void setQuantidade(Long quantidade) {
		this.quantidade = quantidade;
	}

	public void setValorTotal(Double valorTotal) {
		this.valorTotal = valorTotal;
	}

	public void setValorTotalFormatado(String valorTotalFormatado) {
		this.valorTotalFormatado = valorTotalFormatado;
	}
}
