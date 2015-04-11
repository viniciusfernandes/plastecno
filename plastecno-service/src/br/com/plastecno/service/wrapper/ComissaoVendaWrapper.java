package br.com.plastecno.service.wrapper;

import java.util.HashSet;
import java.util.Set;

public class ComissaoVendaWrapper {
	private Integer idVendedor;
	private String nomeVendedor;
	private int quantidadeVendida;
	private String valorVendidoFormatado;
	private double valorVendido;
	private String valorComissaoFormatado;
	private double valorComissao;

	private Set<Integer> listaIdPedido = new HashSet<Integer>();

	public void addPedido(int idPedido) {
		if (listaIdPedido.contains(idPedido)) {
			return;
		}
		listaIdPedido.add(idPedido);
		quantidadeVendida = listaIdPedido.size();
	}

	public void addValorComissionado(double valor) {
		valorComissao += valor;
	}

	public void addValorVendido(double valor) {
		valorVendido += valor;
	}

	public Integer getIdVendedor() {
		return idVendedor;
	}

	public String getNomeVendedor() {
		return nomeVendedor;
	}

	public Integer getQuantidadeVendida() {
		return quantidadeVendida;
	}

	public double getValorComissao() {
		return valorComissao;
	}

	public String getValorComissaoFormatado() {
		return valorComissaoFormatado;
	}

	public double getValorVendido() {
		return valorVendido;
	}

	public String getValorVendidoFormatado() {
		return valorVendidoFormatado;
	}

	public void setIdVendedor(Integer idVendedor) {
		this.idVendedor = idVendedor;
	}

	public void setNomeVendedor(String nomeVendedor) {
		this.nomeVendedor = nomeVendedor;
	}

	public void setQuantidadeVendida(Integer quantidadeVendida) {
		this.quantidadeVendida = quantidadeVendida;
	}

	public void setValorComissao(double valorComissao) {
		this.valorComissao = valorComissao;
	}

	public void setValorComissaoFormatado(String valorComissaoFormatado) {
		this.valorComissaoFormatado = valorComissaoFormatado;
	}

	public void setValorVendido(double valorVendido) {
		this.valorVendido = valorVendido;
	}

	public void setValorVendidoFormatado(String valorVendidoFormatado) {
		this.valorVendidoFormatado = valorVendidoFormatado;
	}

}
