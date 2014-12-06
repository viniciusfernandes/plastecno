package br.com.plastecno.service.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import br.com.plastecno.service.constante.FinalidadePedido;
import br.com.plastecno.service.constante.SituacaoPedido;
import br.com.plastecno.service.constante.TipoEntrega;
import br.com.plastecno.service.constante.TipoLogradouro;
import br.com.plastecno.service.validacao.annotation.InformacaoValidavel;

@Entity
@Table(name="tb_pedido", schema="vendas")
@InformacaoValidavel
public class Pedido implements Serializable, Cloneable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -7474382741231270790L;

	@Id
	@SequenceGenerator(name = "pedidoSequence", sequenceName = "vendas.seq_pedido_id", allocationSize=1, initialValue=1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "pedidoSequence")
	private Integer id;
	
	@Column(name="numero_pedido_cliente")
	private String numeroPedidoCliente;
	
	@Temporal(TemporalType.DATE)
	@Column(name="data_inclusao")
	private Date dataInclusao;
	
	@Temporal(TemporalType.DATE)
	@Column(name="data_envio")
	private Date dataEnvio;
	
	@Temporal(TemporalType.DATE)
	@Column(name="data_entrega")
	private Date dataEntrega;
	
	@InformacaoValidavel(intervalo={0, 800}, nomeExibicao="Observacao do pedido")
	private String observacao;
	/*
	 * Atributo criado para usar em relatorios evitando o calculo do pedido
	 */
	@Column(name="valor_pedido")
	private Double valorPedido;
	
	@Column(name="valor_pedido_ipi")
	private Double valorPedidoIPI;
	
	@Column(name="forma_pagamento")
	private String formaPagamento;
	
	@Column(name="cliente_notificado_venda")
	private boolean clienteNotificadoVenda = false;
	
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name="id_cliente")
	@InformacaoValidavel(relacionamentoObrigatorio=true, nomeExibicao="Cliente do pedido")
	private Cliente cliente;
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="id_representada")
	@InformacaoValidavel(relacionamentoObrigatorio=true, nomeExibicao="Representada do pedido")
	private Representada representada;
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="id_transportadora")
	@InformacaoValidavel(nomeExibicao="Transportadora do pedido")
	private Transportadora transportadora;
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="id_transportadora_redespacho")
	@InformacaoValidavel(nomeExibicao="Redespacho do pedido")
	private Transportadora transportadoraRedespacho;
	
	@Enumerated(EnumType.ORDINAL)
	@Column(name="id_tipo_entrega")
	private TipoEntrega tipoEntrega;
	
	@Column(name="id_situacao_pedido")
	@Enumerated(EnumType.ORDINAL)
	@InformacaoValidavel(obrigatorio=true, nomeExibicao="Situacao do pedido")
	private SituacaoPedido situacaoPedido;
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="id_vendedor")
	@InformacaoValidavel(relacionamentoObrigatorio=true, nomeExibicao="Vendedor do pedido")
	private Usuario vendedor;
	
	@Enumerated(EnumType.STRING)
	@Column(name="id_finalidade_pedido")
	@InformacaoValidavel(obrigatorio=true, nomeExibicao="Finalidade do pedido")
	private FinalidadePedido finalidadePedido;
	
	@OneToOne(fetch=FetchType.LAZY, cascade={CascadeType.ALL})
	@JoinColumn(name="id_contato")
	@InformacaoValidavel(obrigatorio=true, cascata=true, nomeExibicao="Contato do pedido")
	private Contato contato;
	
	@OneToMany(fetch=FetchType.LAZY, cascade={CascadeType.MERGE, CascadeType.PERSIST})
	@JoinTable(name="tb_pedido_tb_logradouro", schema="vendas", 
			joinColumns={@JoinColumn(name = "id_pedido", referencedColumnName = "id")},
			inverseJoinColumns={@JoinColumn(name = "id_logradouro", referencedColumnName = "id")})
	private List<Logradouro> listaLogradouro;
	
	@Transient
	private String dataInclusaoFormatada;
	
	@Transient
	private String dataEnvioFormatada;
	
	@Transient
	private String dataEntregaFormatada;
	
	@Transient
	private String valorPedidoFormatado;
	
	@Transient
	private String valorPedidoIPIFormatado;
		
	public void addLogradouro(List<? extends Logradouro> listaLogradouro) {
		for (Logradouro logradouro : listaLogradouro) {
			this.addLogradouro(logradouro);
		}
	}

	public void addLogradouro(Logradouro logradouro) {
		if (this.listaLogradouro == null){
			this.setListaLogradouro(new ArrayList<Logradouro>());
		}
		this.listaLogradouro.add(logradouro);
	}

	public double addValorPedido (double valor) {
		return this.valorPedido += valor;
	}

	@Override
	public Pedido clone() throws CloneNotSupportedException {
		return (Pedido) super.clone();
	}

	public Cliente getCliente() {
		return cliente;
	}

	public Contato getContato() {
		return contato;
	}

	public Date getDataEntrega() {
		return dataEntrega;
	}

	public String getDataEntregaFormatada() {
		return dataEntregaFormatada;
	}

	public Date getDataEnvio() {
		return dataEnvio;
	}

	public String getDataEnvioFormatada() {
		return dataEnvioFormatada;
	}

	public Date getDataInclusao() {
		return dataInclusao;
	}

	public String getDataInclusaoFormatada() {
		return dataInclusaoFormatada;
	}

	public FinalidadePedido getFinalidadePedido() {
		return finalidadePedido;
	}

	public String getFormaPagamento() {
		return formaPagamento;
	}

	public Integer getId() {
		return id;
	}

	public List<Logradouro> getListaLogradouro() {
		return listaLogradouro;
	}

	public Logradouro getLogradouro(TipoLogradouro tipoLogradouro) {
		return EntityUtils.getLogradouro(listaLogradouro, tipoLogradouro);
	}

	public String getNumeroPedidoCliente() {
		return numeroPedidoCliente;
	}

	public String getObservacao() {
		return observacao;
	}

	public Representada getRepresentada() {
		return representada;
	}

	public SituacaoPedido getSituacaoPedido() {
		return situacaoPedido;
	}

	public TipoEntrega getTipoEntrega() {
		return tipoEntrega;
	}

	public Transportadora getTransportadora() {
		return transportadora;
	}

	public Transportadora getTransportadoraRedespacho() {
		return transportadoraRedespacho;
	}

	public Double getValorPedido() {
		return valorPedido;
	}

	public String getValorPedidoFormatado() {
		return valorPedidoFormatado;
	}

	public Double getValorPedidoIPI() {
		return valorPedidoIPI;
	}

	public String getValorPedidoIPIFormatado() {
		return valorPedidoIPIFormatado;
	}

	public Usuario getVendedor() {
		return vendedor;
	}

	public boolean isClienteNotificadoVenda() {
		return this.clienteNotificadoVenda;
	}

	public boolean isEnviado() {
		return SituacaoPedido.ENVIADO.equals(this.situacaoPedido);
	}

	public boolean isOrcamento() {
		return SituacaoPedido.ORCAMENTO.equals(this.situacaoPedido);
	}

	public void setCliente(Cliente cliente) {
		this.cliente = cliente;
	}

	public void setClienteNotificadoVenda(boolean clienteNotificadoVenda) {
		this.clienteNotificadoVenda = clienteNotificadoVenda;
	}

	public void setContato(Contato contato) {
		this.contato = contato;
	}

	public void setDataEntrega(Date dataEntrega) {
		this.dataEntrega = dataEntrega;
	}
	
	public void setDataEntregaFormatada(String dataEntregaFormatada) {
		this.dataEntregaFormatada = dataEntregaFormatada;
	}

	public void setDataEnvio(Date dataEnvio) {
		this.dataEnvio = dataEnvio;
	}

	public void setDataEnvioFormatada(String dataEnvioFormatada) {
		this.dataEnvioFormatada = dataEnvioFormatada;
	}

	public void setDataInclusao(Date dataInclusao) {
		this.dataInclusao = dataInclusao;
	}

	public void setDataInclusaoFormatada(String dataInclusaoFormatada) {
		this.dataInclusaoFormatada = dataInclusaoFormatada;
	}
	
	public void setFinalidadePedido(FinalidadePedido finalidadePedido) {
		this.finalidadePedido = finalidadePedido;
	}

	public void setFormaPagamento(String formaPagamento) {
		this.formaPagamento = formaPagamento;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public void setListaLogradouro(List<Logradouro> listaLogradouro) {
		this.listaLogradouro = listaLogradouro;
	}

	public void setNumeroPedidoCliente(String numeroPedidoCliente) {
		this.numeroPedidoCliente = numeroPedidoCliente;
	}

	public void setObservacao(String observacao) {
		this.observacao = observacao;
	}
	
	public void setOrcamento(boolean isOrcamento) {
		if (isOrcamento) {
			this.setSituacaoPedido(SituacaoPedido.ORCAMENTO);
		}
	}
	
	public void setRepresentada(Representada representada) {
		this.representada = representada;
	}

	public void setSituacaoPedido(SituacaoPedido situacaoPedido) {
		this.situacaoPedido = situacaoPedido;
	}

	public void setTipoEntrega(TipoEntrega tipoEntrega) {
		this.tipoEntrega = tipoEntrega;
	}
	
	public void setTransportadora(Transportadora transportadora) {
		this.transportadora = transportadora;
	}
	
	public void setTransportadoraRedespacho(Transportadora transportadoraRedespacho) {
		this.transportadoraRedespacho = transportadoraRedespacho;
	}
	
	public void setValorPedido(Double valorPedido) {
		this.valorPedido = valorPedido;
	}
	
	public void setValorPedidoFormatado(String valorPedidoFormatado) {
		this.valorPedidoFormatado = valorPedidoFormatado;
	}

	public void setValorPedidoIPI(Double valorPedidoIPI) {
		this.valorPedidoIPI = valorPedidoIPI;
	}

	public void setValorPedidoIPIFormatado(String valorPedidoIPIFormatado) {
		this.valorPedidoIPIFormatado = valorPedidoIPIFormatado;
	}
	
	public void setVendedor(Usuario vendedor) {
		this.vendedor = vendedor;
	}
}
