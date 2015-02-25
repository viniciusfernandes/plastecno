package br.com.plastecno.service.entity;

import java.util.Date;

public class ItemReservado {
	private Date dataReserva;
	private ItemEstoque itemEstoque;
	private ItemPedido itemPedido;
	private Integer quantidadeReservada;

	public ItemReservado() {
	}

	public ItemReservado(ItemEstoque itemEstoque, ItemPedido itemPedido, Integer quantidadeReservada) {
		this.itemEstoque = itemEstoque;
		this.itemPedido = itemPedido;
		this.quantidadeReservada = quantidadeReservada;
	}

	public Date getDataReserva() {
		return dataReserva;
	}

	public ItemEstoque getItemEstoque() {
		return itemEstoque;
	}

	public ItemPedido getItemPedido() {
		return itemPedido;
	}

	public Integer getQuantidadeReservada() {
		return quantidadeReservada;
	}

	public void setDataReserva(Date dataReserva) {
		this.dataReserva = dataReserva;
	}

	public void setItemEstoque(ItemEstoque itemEstoque) {
		this.itemEstoque = itemEstoque;
	}

	public void setItemPedido(ItemPedido itemPedido) {
		this.itemPedido = itemPedido;
	}

	public void setQuantidadeReservada(Integer quantidadeReservada) {
		this.quantidadeReservada = quantidadeReservada;
	}
}