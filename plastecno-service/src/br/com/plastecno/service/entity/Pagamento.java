package br.com.plastecno.service.entity;

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

import br.com.plastecno.service.constante.SituacaoPagamento;
import br.com.plastecno.service.constante.TipoPagamento;
import br.com.plastecno.service.validacao.annotation.InformacaoValidavel;

@Entity
@Table(name = "tb_pagamento", schema = "vendas")
public class Pagamento {

	@Column(name = "data_emissao")
	private Date dataEmissao;

	@Transient
	private String dataEmissaoFormatada;

	@Column(name = "data_recebimento")
	private Date dataRecebimento;

	@Transient
	private String dataRecebimentoFormatada;

	@Column(name = "data_vencimento")
	private Date dataVencimento;

	@Transient
	private String dataVencimentoFormatada;

	private String descricao;

	@Id
	@SequenceGenerator(name = "pagamentoSequence", sequenceName = "vendas.seq_pagamento_id", allocationSize = 1, initialValue = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "pagamentoSequence")
	private Integer id;

	@Column(name = "id_fornecedor")
	private Integer idFornecedor;

	@Column(name = "id_pedido")
	private Integer idPedido;

	@Column(name = "modalidade_frete")
	private Integer modalidadeFrete;

	@Column(name = "numero_nf")
	private Integer numeroNF;

	private Integer parcela;

	@Column(name = "quantidade_item")
	private Integer quantidadeItem;

	@Column(name = "sequencial_item")
	private Integer sequencialItem;

	@Enumerated(EnumType.ORDINAL)
	@Column(name = "id_situacao_pagamento")
	@InformacaoValidavel(obrigatorio = true, nomeExibicao = "Situação do pagamento")
	private SituacaoPagamento situacaoPagamento;

	@Enumerated(EnumType.ORDINAL)
	@Column(name = "id_tipo_pagamento")
	@InformacaoValidavel(obrigatorio = true, nomeExibicao = "Tipo do pagamento")
	private TipoPagamento tipoPagamento;

	@Column(name = "total_parcelas")
	private Integer totalParcelas;

	@Column(name = "valor_credito_icms")
	private Double valorCreditoICMS;

	@Column(name = "valor_nf")
	private Double valorNF;

	@Column(name = "valor_parcela")
	private Double valorParcela;

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

	public Integer getIdPedido() {
		return idPedido;
	}

	public Integer getModalidadeFrete() {
		return modalidadeFrete;
	}

	public Integer getNumeroNF() {
		return numeroNF;
	}

	public Integer getParcela() {
		return parcela;
	}

	public Integer getQuantidadeItem() {
		return quantidadeItem;
	}

	public Integer getSequencialItem() {
		return sequencialItem;
	}

	public SituacaoPagamento getSituacaoPagamento() {
		return situacaoPagamento;
	}

	public TipoPagamento getTipoPagamento() {
		return tipoPagamento;
	}

	public Integer getTotalParcelas() {
		return totalParcelas;
	}

	public Double getValorCreditoICMS() {
		return valorCreditoICMS;
	}

	public Double getValorNF() {
		return valorNF;
	}

	public Double getValorParcela() {
		return valorParcela;
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

	public void setIdPedido(Integer idPedido) {
		this.idPedido = idPedido;
	}

	public void setModalidadeFrete(Integer modalidadeFrete) {
		this.modalidadeFrete = modalidadeFrete;
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

	public void setSequencialItem(Integer sequencialItem) {
		this.sequencialItem = sequencialItem;
	}

	public void setSituacaoPagamento(SituacaoPagamento situacaoPagamento) {
		this.situacaoPagamento = situacaoPagamento;
	}

	public void setTipoPagamento(TipoPagamento tipoPagamento) {
		this.tipoPagamento = tipoPagamento;
	}

	public void setTotalParcelas(Integer totalParcelas) {
		this.totalParcelas = totalParcelas;
	}

	public void setValorCreditoICMS(Double valorCreditoICMS) {
		this.valorCreditoICMS = valorCreditoICMS;
	}

	public void setValorNF(Double valorNF) {
		this.valorNF = valorNF;
	}

	public void setValorParcela(Double valorParcela) {
		this.valorParcela = valorParcela;
	}
}
