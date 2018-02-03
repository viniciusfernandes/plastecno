package br.com.svr.service.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

@Entity
@Table(name = "tb_nfe_item_fracionado", schema = "vendas")
public class NFeItemFracionado {
	private String descricao;

	@Id
	@SequenceGenerator(name = "itemFracionadoSequence", sequenceName = "vendas.seq_item_fracionado_id", allocationSize = 1, initialValue = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "itemFracionadoSequence")
	private Integer id;

	@Column(name = "id_item_pedido")
	private Integer idItemPedido;

	@Column(name = "id_pedido")
	private Integer idPedido;

	@Column(name = "numero_item")
	private Integer numeroItem;

	@Column(name = "numero_nfe")
	private Integer numeroNFe;

	private Integer quantidade;

	@Column(name = "quantidade_fracionada")
	private Integer quantidadeFracionada;

	@Column(name = "valor_bruto")
	private Double valorBruto;

	public NFeItemFracionado() {
	}

	public NFeItemFracionado(Integer id, Integer idItemPedido, Integer idPedido, String descricao, Integer numeroItem,
			Integer numeroNFe, Integer quantidade, Integer quantidadeFracionada, Double valorBruto) {
		setId(id);
		this.idItemPedido = idItemPedido;
		this.idPedido = idPedido;
		this.descricao = descricao;
		this.numeroItem = numeroItem;
		this.numeroNFe = numeroNFe;
		this.quantidade = quantidade;
		this.quantidadeFracionada = quantidadeFracionada;
		this.valorBruto = valorBruto;
	}

	public NFeItemFracionado(Integer idPedido, Integer numeroItem, Integer numeroNFe, Integer quantidade,
			Integer quantidadeFracionada) {
		this(null, null, idPedido, null, numeroItem, numeroNFe, quantidade, quantidadeFracionada, null);
	}

	public String getDescricao() {
		return descricao;
	}

	public Integer getId() {
		return id;
	}

	public Integer getIdItemPedido() {
		return idItemPedido;
	}

	public Integer getIdPedido() {
		return idPedido;
	}

	public Integer getNumeroItem() {
		return numeroItem;
	}

	public Integer getNumeroNFe() {
		return numeroNFe;
	}

	public Integer getQuantidade() {
		return quantidade;
	}

	public Integer getQuantidadeFracionada() {
		return quantidadeFracionada;
	}

	public Double getValorBruto() {
		return valorBruto;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public void setIdItemPedido(Integer idItemPedido) {
		this.idItemPedido = idItemPedido;
	}

	public void setIdPedido(Integer idPedido) {
		this.idPedido = idPedido;
	}

	public void setNumeroItem(Integer numeroItem) {
		this.numeroItem = numeroItem;
	}

	public void setNumeroNFe(Integer numeroNFe) {
		this.numeroNFe = numeroNFe;
	}

	public void setQuantidade(Integer quantidade) {
		this.quantidade = quantidade;
	}

	public void setQuantidadeFracionada(Integer quantidadeFracionada) {
		this.quantidadeFracionada = quantidadeFracionada;
	}

	public void setValorBruto(Double valorBruto) {
		this.valorBruto = valorBruto;
	}

}
