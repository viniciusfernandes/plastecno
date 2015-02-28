package br.com.plastecno.service.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

@Entity
@Table(name = "tb_item_reservado", schema = "vendas")
public class ItemReservado {
	@Column(name = "data_reserva")
	private Date dataReserva;

	@Id
	@SequenceGenerator(name = "itemReservadoSequence", sequenceName = "vendas.seq_item_reservado_id", allocationSize = 1, initialValue = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "itemReservadoSequence")
	private Integer id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_item_estoque", referencedColumnName = "id", nullable = false)
	private ItemEstoque itemEstoque;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_item_pedido", referencedColumnName = "id", nullable = false)
	private ItemPedido itemPedido;

	public ItemReservado() {
	}

	public ItemReservado(Date dataReserva, ItemEstoque itemEstoque, ItemPedido itemPedido) {
		this.dataReserva = dataReserva;
		this.itemEstoque = itemEstoque;
		this.itemPedido = itemPedido;
	}

	public ItemReservado(Integer id, Integer idItemPedido, Integer quantidadeReservada, Integer idItemEstoque,
			Integer quantidadeEstoque) {
		this.id = id;
		itemPedido = new ItemPedido();
		itemPedido.setId(idItemPedido);
		itemPedido.setQuantidade(quantidadeReservada);

		itemEstoque = new ItemEstoque();
		itemEstoque.setId(idItemEstoque);
		itemEstoque.setQuantidade(quantidadeEstoque);
	}

	public Date getDataReserva() {
		return dataReserva;
	}

	public Integer getId() {
		return id;
	}

	public ItemEstoque getItemEstoque() {
		return itemEstoque;
	}

	public ItemPedido getItemPedido() {
		return itemPedido;
	}

	public void setDataReserva(Date dataReserva) {
		this.dataReserva = dataReserva;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public void setItemEstoque(ItemEstoque itemEstoque) {
		this.itemEstoque = itemEstoque;
	}

	public void setItemPedido(ItemPedido itemPedido) {
		this.itemPedido = itemPedido;
	}

}