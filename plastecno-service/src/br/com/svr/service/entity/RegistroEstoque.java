package br.com.svr.service.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;

import br.com.svr.service.constante.TipoOperacaoEstoque;
import br.com.svr.service.validacao.InformacaoValidavel;

@Entity
@Table(name = "tb_registro_estoque", schema = "vendas")
@InformacaoValidavel
public class RegistroEstoque {
	@InformacaoValidavel(obrigatorio = true, nomeExibicao = "Data de operacao do registro de estoque")
	@Column(name = "data_operacao")
	private Date dataOperacao;

	@Transient
	private String dataOperacaoFormatada;

	@Id
	@SequenceGenerator(name = "registroEstoqueSequence", sequenceName = "vendas.seq_registro_estoque_id", allocationSize = 1, initialValue = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "registroEstoqueSequence")
	private Integer id;

	@InformacaoValidavel(obrigatorio = true, nomeExibicao = "Id do item de estoque do registro de estoque")
	@Column(name = "id_item_estoque")
	private Integer idItemEstoque;

	@Column(name = "id_item_pedido")
	private Integer idItemPedido;

	@Column(name = "id_pedido")
	private Integer idPedido;

	@Column(name = "id_usuario")
	private Integer idUsuario;

	@Column(name = "nome_usuario")
	private String nomeUsuario;

	@Column(name = "quantidade_anterior")
	private Integer quantidadeAnterior;

	@Column(name = "quantidade_item")
	private Integer quantidadeItem;

	@Column(name = "quantidade_posterior")
	private Integer quantidadePosterior;

	@Column(name = "sequencial_item_pedido")
	private Integer sequencialItemPedido;

	@InformacaoValidavel(obrigatorio = true, nomeExibicao = "Tipo de operacao do registro de estoque")
	@Column(name = "id_tipo_operacao")
	@Enumerated(EnumType.ORDINAL)
	private TipoOperacaoEstoque tipoOperacao;

	@Column(name = "valor_anterior")
	private Double valorAnterior;

	@Column(name = "valor_posterior")
	private Double valorPosterior;

	public Date getDataOperacao() {
		return dataOperacao;
	}

	public String getDataOperacaoFormatada() {
		return dataOperacaoFormatada;
	}

	public Integer getId() {
		return id;
	}

	public Integer getIdItemEstoque() {
		return idItemEstoque;
	}

	public Integer getIdItemPedido() {
		return idItemPedido;
	}

	public Integer getIdPedido() {
		return idPedido;
	}

	public Integer getIdUsuario() {
		return idUsuario;
	}

	public String getNomeUsuario() {
		return nomeUsuario;
	}

	public Integer getQuantidadeAnterior() {
		return quantidadeAnterior;
	}

	public Integer getQuantidadeItem() {
		return quantidadeItem;
	}

	public Integer getQuantidadePosterior() {
		return quantidadePosterior;
	}

	public Integer getSequencialItemPedido() {
		return sequencialItemPedido;
	}

	public TipoOperacaoEstoque getTipoOperacao() {
		return tipoOperacao;
	}

	public Double getValorAnterior() {
		return valorAnterior;
	}

	public Double getValorPosterior() {
		return valorPosterior;
	}

	public boolean isEntrada() {
		return !isSaida();
	}

	public boolean isSaida() {
		return tipoOperacao != null && tipoOperacao.isSaida();
	}

	public void setDataOperacao(Date dataOperacao) {
		this.dataOperacao = dataOperacao;
	}

	public void setDataOperacaoFormatada(String dataOperacaoFormatada) {
		this.dataOperacaoFormatada = dataOperacaoFormatada;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public void setIdItemEstoque(Integer idItemEstoque) {
		this.idItemEstoque = idItemEstoque;
	}

	public void setIdItemPedido(Integer idItemPedido) {
		this.idItemPedido = idItemPedido;
	}

	public void setIdPedido(Integer idPedido) {
		this.idPedido = idPedido;
	}

	public void setIdUsuario(Integer idUsuario) {
		this.idUsuario = idUsuario;
	}

	public void setNomeUsuario(String nomeUsuario) {
		this.nomeUsuario = nomeUsuario;
	}

	public void setQuantidadeAnterior(Integer quantidadeAnterior) {
		this.quantidadeAnterior = quantidadeAnterior;
	}

	public void setQuantidadeItem(Integer quantidadeRegistrada) {
		this.quantidadeItem = quantidadeRegistrada;
	}

	public void setQuantidadePosterior(Integer quantidadePosterior) {
		this.quantidadePosterior = quantidadePosterior;
	}

	public void setSequencialItemPedido(Integer sequencialItemPedido) {
		this.sequencialItemPedido = sequencialItemPedido;
	}

	public void setTipoOperacao(TipoOperacaoEstoque tipoOperacao) {
		this.tipoOperacao = tipoOperacao;
	}

	public void setValorAnterior(Double valorAnterior) {
		this.valorAnterior = valorAnterior;
	}

	public void setValorPosterior(Double valorPosterior) {
		this.valorPosterior = valorPosterior;
	}

}
