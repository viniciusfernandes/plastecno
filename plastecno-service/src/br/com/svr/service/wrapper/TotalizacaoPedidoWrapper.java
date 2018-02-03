package br.com.svr.service.wrapper;

public class TotalizacaoPedidoWrapper {
	private Integer idProprietario;
	private Integer idRepresentada;
	private String nomeCliente;
	private String nomeFantasiaRepresentada;
	private String nomeProprietario;
	private Long quantidade;
	private Double valorTotal;
	private String valorTotalFormatado;
	private Double valorTotalIPI;
	private String valorTotalIPIFormatado;

	public TotalizacaoPedidoWrapper() {
	}

	public TotalizacaoPedidoWrapper(Integer idProprietario, String nomeProprietario, String sobrenomeProprietario,
			Integer idRepresentada, String nomeFantasiaRepresentada, Double valorTotal, Double valorTotalIPI) {
		this.idProprietario = idProprietario;
		this.nomeProprietario = nomeProprietario + " " + sobrenomeProprietario;
		this.nomeFantasiaRepresentada = nomeFantasiaRepresentada;
		this.idRepresentada = idRepresentada;
		this.valorTotal = valorTotal;
		this.valorTotalIPI = valorTotalIPI;
	}

	public TotalizacaoPedidoWrapper(String nomeCliente, Long quantidade, Double valorTotal) {
		this.nomeCliente = nomeCliente;
		this.quantidade = quantidade;
		this.valorTotal = valorTotal;
	}

	public Integer getIdProprietario() {
		return idProprietario;
	}

	public Integer getIdRepresentada() {
		return idRepresentada;
	}

	public String getNomeCliente() {
		return nomeCliente;
	}

	public String getNomeFantasiaRepresentada() {
		return nomeFantasiaRepresentada;
	}

	public String getNomeProprietario() {
		return nomeProprietario;
	}

	public Long getQuantidade() {
		return quantidade;
	}

	public Double getValorTotal() {
		return valorTotal == null ? 0 : valorTotal;
	}

	public String getValorTotalFormatado() {
		return valorTotalFormatado;
	}

	public Double getValorTotalIPI() {
		return valorTotalIPI == null ? 0 : valorTotalIPI;
	}

	public String getValorTotalIPIFormatado() {
		return valorTotalIPIFormatado;
	}

	public void setIdProprietario(Integer idProprietario) {
		this.idProprietario = idProprietario;
	}

	public void setIdRepresentada(Integer idRepresentada) {
		this.idRepresentada = idRepresentada;
	}

	public void setNomeCliente(String nomeCliente) {
		this.nomeCliente = nomeCliente;
	}

	public void setNomeFantasiaRepresentada(String nomeFantasiaRepresentada) {
		this.nomeFantasiaRepresentada = nomeFantasiaRepresentada;
	}

	public void setNomeProprietario(String nomeProprietario) {
		this.nomeProprietario = nomeProprietario;
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

	public void setValorTotalIPI(Double valorTotalIPI) {
		this.valorTotalIPI = valorTotalIPI;
	}

	public void setValorTotalIPIFormatado(String valorTotalIPIFormatado) {
		this.valorTotalIPIFormatado = valorTotalIPIFormatado;
	}
}
