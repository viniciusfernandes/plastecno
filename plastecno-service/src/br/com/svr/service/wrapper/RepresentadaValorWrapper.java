package br.com.svr.service.wrapper;

import br.com.svr.util.NumeroUtils;

public class RepresentadaValorWrapper extends Grupo {
	private Double valorVendaIPI;
	private String valorVendaIPIFormatado;

	public RepresentadaValorWrapper(String nome, Double valor) {
		super(nome, valor);
	}

	public RepresentadaValorWrapper(String nome, Double valor, Double valorVendaIPI) {
		this(nome, valor);
		this.valorVendaIPI = valorVendaIPI;
		this.valorVendaIPIFormatado = NumeroUtils.formatarValor2Decimais(valorVendaIPI);
	}

	public String getNomeRepresentada() {
		return this.nome;
	}

	public Double getValorVendaIPI() {
		return valorVendaIPI;
	}

	public String getValorVendaIPIFormatado() {
		return valorVendaIPIFormatado;
	}

	public Double getValorVenda() {
		return this.valor;
	}

	public String getValorVendaFormatado() {
		return this.getValorFormatado();
	}

	public void setValorVendaIPI(Double valorVendaIPI) {
		this.valorVendaIPI = valorVendaIPI;
	}

	public void setValorVendaIPIFormatado(String valorVendaIPIFormatado) {
		this.valorVendaIPIFormatado = valorVendaIPIFormatado;
	}
}
