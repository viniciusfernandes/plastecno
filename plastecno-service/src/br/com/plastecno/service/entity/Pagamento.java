package br.com.plastecno.service.entity;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.ZoneId;
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

import br.com.plastecno.service.constante.TipoPagamento;
import br.com.plastecno.service.validacao.annotation.InformacaoValidavel;

@InformacaoValidavel
@Entity
@Table(name = "tb_pagamento", schema = "vendas")
public class Pagamento implements Serializable, Cloneable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -3646353541626202858L;

	@Column(name = "data_emissao")
	private Date dataEmissao;

	@Transient
	private String dataEmissaoFormatada;

	@Column(name = "data_recebimento")
	private Date dataRecebimento;

	@Transient
	private String dataRecebimentoFormatada;

	@InformacaoValidavel(obrigatorio = true, nomeExibicao = "Data de vencimento de pagamento")
	@Column(name = "data_vencimento")
	private Date dataVencimento;

	@Transient
	private String dataVencimentoFormatada;

	@InformacaoValidavel(obrigatorio = true, intervaloComprimento = { 1, 200 }, nomeExibicao = "Descrição do pagamento")
	private String descricao;

	@Id
	@SequenceGenerator(name = "pagamentoSequence", sequenceName = "vendas.seq_pagamento_id", allocationSize = 1, initialValue = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "pagamentoSequence")
	private Integer id;

	@Column(name = "id_fornecedor")
	private Integer idFornecedor;

	@Column(name = "id_item_pedido")
	private Integer idItemPedido;

	@Column(name = "id_pedido")
	private Integer idPedido;

	private boolean liquidado;

	@Column(name = "modalidade_frete")
	private Integer modalidadeFrete;

	@Column(name = "nome_fornecedor")
	private String nomeFornecedor;

	@Column(name = "numero_nf")
	private Integer numeroNF;

	private Integer parcela = 1;

	@Column(name = "quantidade_item")
	private Integer quantidadeItem;

	@Column(name = "quantidade_total")
	private Integer quantidadeTotal;

	@Column(name = "sequencial_item")
	private Integer sequencialItem;

	@Enumerated(EnumType.ORDINAL)
	@Column(name = "id_tipo_pagamento")
	@InformacaoValidavel(obrigatorio = true, nomeExibicao = "Tipo do pagamento")
	private TipoPagamento tipoPagamento;

	@Column(name = "total_parcelas")
	private Integer totalParcelas = 1;

	@InformacaoValidavel(obrigatorio = true, nomeExibicao = "Valor do pagamento")
	@Column(name = "valor")
	private Double valor;

	@Column(name = "valor_credito_icms")
	private Double valorCreditoICMS;

	@Transient
	private String valorCreditoICMSFormatado;

	@Transient
	private String valorFormatado;

	@Column(name = "valor_nf")
	private Double valorNF;

	public Pagamento() {
	}

	public Pagamento(Integer id) {
		this.id = id;
	}

	@Override
	public Pagamento clone() {
		try {
			return (Pagamento) super.clone();
		} catch (CloneNotSupportedException e) {
			throw new IllegalStateException("Falha ao clonar o pagamento " + getDescricao(), e);
		}
	}

	public Date getDataEmissao() {
		return dataEmissao;
	}

	public String getDataEmissaoFormatada() {
		return dataEmissaoFormatada;
	}

	public Date getDataRecebimento() {
		return dataRecebimento;
	}

	public String getDataRecebimentoFormatada() {
		return dataRecebimentoFormatada;
	}

	public Date getDataVencimento() {
		return dataVencimento;
	}

	public String getDataVencimentoFormatada() {
		return dataVencimentoFormatada;
	}

	public String getDescricao() {
		return descricao;
	}

	public Integer getId() {
		return id;
	}

	public Integer getIdFornecedor() {
		return idFornecedor;
	}

	public Integer getIdItemPedido() {
		return idItemPedido;
	}

	public Integer getIdPedido() {
		return idPedido;
	}

	public Integer getModalidadeFrete() {
		return modalidadeFrete;
	}

	public String getNomeFornecedor() {
		return nomeFornecedor;
	}

	public Integer getNumeroNF() {
		return numeroNF;
	}

	public Integer getParcela() {
		return parcela;
	}

	public String getParcelaFormatada() {
		return (parcela == null ? 0 : parcela) + "/" + (totalParcelas == null ? 0 : totalParcelas);
	}

	public Integer getQuantidadeItem() {
		return quantidadeItem;
	}

	public Integer getQuantidadeTotal() {
		return quantidadeTotal;
	}

	public Integer getSequencialItem() {
		return sequencialItem;
	}

	public String getSituacaoPagamento() {
		if (!isLiquidado() && isVencido()) {
			return "VENCIDO";
		}
		if (isLiquidado()) {
			return "LIQUIDADO";
		}
		return "AGUARDANDO";
	}

	public TipoPagamento getTipoPagamento() {
		return tipoPagamento;
	}

	public Integer getTotalParcelas() {
		return totalParcelas;
	}

	public Double getValor() {
		return valor;
	}

	public Double getValorCreditoICMS() {
		return valorCreditoICMS;
	}

	public String getValorCreditoICMSFormatado() {
		return valorCreditoICMSFormatado;
	}

	public String getValorFormatado() {
		return valorFormatado;
	}

	public Double getValorNF() {
		return valorNF;
	}

	public boolean isInsumo() {
		return TipoPagamento.INSUMO.equals(tipoPagamento);
	}

	public boolean isLiquidado() {
		return liquidado;
	}

	public boolean isNovo() {
		return id == null;
	}

	public boolean isVencido() {
		if (dataVencimento == null) {
			return false;
		}
		return dataVencimento.toInstant().atZone(ZoneId.systemDefault()).toLocalDate().isBefore(LocalDate.now());
	}

	public void setDataEmissao(Date dataEmissao) {
		this.dataEmissao = dataEmissao;
	}

	public void setDataEmissaoFormatada(String dataEmissaoFormatada) {
		this.dataEmissaoFormatada = dataEmissaoFormatada;
	}

	public void setDataRecebimento(Date dataRecebimento) {
		this.dataRecebimento = dataRecebimento;
	}

	public void setDataRecebimentoFormatada(String dataRecebimentoFormatada) {
		this.dataRecebimentoFormatada = dataRecebimentoFormatada;
	}

	public void setDataVencimento(Date dataVencimento) {
		this.dataVencimento = dataVencimento;
	}

	public void setDataVencimentoFormatada(String dataVencimentoFormatada) {
		this.dataVencimentoFormatada = dataVencimentoFormatada;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public void setIdFornecedor(Integer idFornecedor) {
		this.idFornecedor = idFornecedor;
	}

	public void setIdItemPedido(Integer idItemPedido) {
		this.idItemPedido = idItemPedido;
	}

	public void setIdPedido(Integer idPedido) {
		this.idPedido = idPedido;
	}

	public void setLiquidado(boolean liquidado) {
		this.liquidado = liquidado;
	}

	public void setModalidadeFrete(Integer modalidadeFrete) {
		this.modalidadeFrete = modalidadeFrete;
	}

	public void setNomeFornecedor(String nomeFornecedor) {
		this.nomeFornecedor = nomeFornecedor;
	}

	public void setNumeroNF(Integer numeroNF) {
		this.numeroNF = numeroNF;
	}

	public void setParcela(Integer parcela) {
		this.parcela = parcela;
	}

	public void setQuantidadeItem(Integer quantidadeItem) {
		this.quantidadeItem = quantidadeItem;
	}

	public void setQuantidadeTotal(Integer quantidadeTotal) {
		this.quantidadeTotal = quantidadeTotal;
	}

	public void setSequencialItem(Integer sequencialItem) {
		this.sequencialItem = sequencialItem;
	}

	public void setTipoPagamento(TipoPagamento tipoPagamento) {
		this.tipoPagamento = tipoPagamento;
	}

	public void setTotalParcelas(Integer totalParcelas) {
		this.totalParcelas = totalParcelas;
	}

	public void setValor(Double valor) {
		this.valor = valor;
	}

	public void setValorCreditoICMS(Double valorCreditoICMS) {
		this.valorCreditoICMS = valorCreditoICMS;
	}

	public void setValorCreditoICMSFormatado(String valorCreditoICMSFormatado) {
		this.valorCreditoICMSFormatado = valorCreditoICMSFormatado;
	}

	public void setValorFormatado(String valorFormatado) {
		this.valorFormatado = valorFormatado;
	}

	public void setValorNF(Double valorNF) {
		this.valorNF = valorNF;
	}

}
