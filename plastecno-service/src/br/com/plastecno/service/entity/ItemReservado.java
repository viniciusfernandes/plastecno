package br.com.plastecno.service.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

@Entity
@Table(name = "tb_item_reservado")
public class ItemReservado {
	@Column(name = "data_reserva")
	private Date dataReserva;

	@Id
	@SequenceGenerator(name = "itemReservadoSequence", sequenceName = "vendas.seq_item_reservado_id", allocationSize = 1, initialValue = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "itemReservadoSequence")
	private Integer id;
	@JoinColumn(name = "id_item_estoque", referencedColumnName = "id", nullable = false)
	private ItemEstoque itemEstoque;

	@JoinColumn(name = "id_item_pedido", referencedColumnName = "id", nullable = false)
	private ItemPedido itemPedido;

	@Column(name = "quantidade_reservada")
	private Integer quantidadeReservada;

	public ItemReservado() {
	}

	public ItemReservado(Date dataReserva, ItemEstoque itemEstoque, ItemPedido itemPedido, Integer quantidadeReservada) {
		this.dataReserva = dataReserva;
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