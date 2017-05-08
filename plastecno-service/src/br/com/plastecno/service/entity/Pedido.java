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
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import br.com.plastecno.service.constante.SituacaoPedido;
import br.com.plastecno.service.constante.TipoEntrega;
import br.com.plastecno.service.constante.TipoFinalidadePedido;
import br.com.plastecno.service.constante.TipoPedido;
import br.com.plastecno.service.validacao.annotation.InformacaoValidavel;

@Entity
@Table(name = "tb_pedido", schema = "vendas")
@InformacaoValidavel
public class Pedido implements Serializable, Cloneable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -7474382741231270790L;

	@Transient
	private Double aliquotaComissaoRepresentada;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_cliente")
	@InformacaoValidavel(relacionamentoObrigatorio = true, nomeExibicao = "Cliente do pedido")
	private Cliente cliente;

	@Column(name = "cliente_notificado_venda")
	private boolean clienteNotificadoVenda = false;

	@OneToOne(fetch = FetchType.EAGER, cascade = { CascadeType.ALL })
	@JoinColumn(name = "id_contato")
	@InformacaoValidavel(obrigatorio = true, cascata = true, nomeExibicao = "Contato do pedido")
	private Contato contato;

	@Temporal(TemporalType.DATE)
	@Column(name = "data_emissao_nf")
	private Date dataEmissaoNF;

	@Transient
	private String dataEmissaoNFFormatada;

	@Temporal(TemporalType.DATE)
	@Column(name = "data_entrega")
	private Date dataEntrega;

	@Transient
	private String dataEntregaFormatada;

	@Temporal(TemporalType.DATE)
	@Column(name = "data_envio")
	private Date dataEnvio;

	@Transient
	private String dataEnvioFormatada;

	@Transient
	private String dataInclusaoFormatada;

	@Temporal(TemporalType.DATE)
	@Column(name = "data_vencimento_nf")
	private Date dataVencimentoNF;

	@Transient
	private String dataVencimentoNFFormatada;

	@Enumerated(EnumType.STRING)
	@Column(name = "id_finalidade_pedido")
	@InformacaoValidavel(obrigatorio = true, nomeExibicao = "Finalidade do pedido")
	private TipoFinalidadePedido finalidadePedido;

	@Column(name = "forma_pagamento")
	private String formaPagamento;

	@Id
	@SequenceGenerator(name = "pedidoSequence", sequenceName = "vendas.seq_pedido_id", allocationSize = 1, initialValue = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "pedidoSequence")
	private Integer id;

	// Campo criado para atualizar o id do cliente no envio do pedido via json.
	@Transient
	private Integer idCliente;

	@Transient
	private Integer idVendedor;

	@OneToMany(mappedBy = "pedido", fetch = FetchType.LAZY, cascade = { CascadeType.ALL })
	@InformacaoValidavel(iteravel = true, nomeExibicao = "Lista de logradouro do pedido")
	private List<LogradouroPedido> listaLogradouro;

	@Column(name = "numero_coleta")
	private String numeroColeta;

	@Column(name = "numero_nf")
	private String numeroNF;

	@Column(name = "numero_pedido_cliente")
	private String numeroPedidoCliente;

	@Column(name = "numero_volumes")
	private Integer numeroVolumes;

	@InformacaoValidavel(intervaloComprimento = { 0, 799 }, nomeExibicao = "Observação do pedido")
	private String observacao;

	@Column(name = "prazo_entrega")
	private Integer prazoEntrega;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_proprietario")
	@InformacaoValidavel(relacionamentoObrigatorio = true, nomeExibicao = "Vendedor/Comprador do pedido")
	private Usuario proprietario;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_representada")
	@InformacaoValidavel(relacionamentoObrigatorio = true, nomeExibicao = "Representada do pedido")
	private Representada representada;

	@Column(name = "id_situacao_pedido")
	@Enumerated(EnumType.ORDINAL)
	@InformacaoValidavel(obrigatorio = true, nomeExibicao = "Situacao do pedido")
	private SituacaoPedido situacaoPedido;

	@Enumerated(EnumType.ORDINAL)
	@Column(name = "id_tipo_entrega")
	private TipoEntrega tipoEntrega;

	@Column(name = "id_tipo_pedido")
	@Enumerated(EnumType.ORDINAL)
	@InformacaoValidavel(obrigatorio = true, nomeExibicao = "Tipo do pedido")
	private TipoPedido tipoPedido;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_transportadora")
	@InformacaoValidavel(nomeExibicao = "Transportadora do pedido")
	private Transportadora transportadora;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_transportadora_redespacho")
	@InformacaoValidavel(nomeExibicao = "Redespacho do pedido")
	private Transportadora transportadoraRedespacho;

	@Column(name = "valor_parcela_nf")
	private Double valorParcelaNF;

	/*
	 * Atributo criado para usar em relatorios evitando o calculo do pedido
	 */
	@Column(name = "valor_pedido")
	private Double valorPedido;

	@Transient
	private String valorPedidoFormatado;

	@Column(name = "valor_pedido_ipi")
	private Double valorPedidoIPI;

	@Transient
	private String valorPedidoIPIFormatado;

	@Column(name = "valor_total_nf")
	private Double valorTotalNF;

	public Pedido() {
	}

	// Construtor utilizado na pesquisa para o calculo de comissao, por isso
	// temos poucos parametros
	public Pedido(Double aliquotaComissaoRepresentada, Integer id, Integer idVendedor,
			TipoFinalidadePedido tipoFinalidadePedido, TipoPedido tipoPedido) {
		this.aliquotaComissaoRepresentada = aliquotaComissaoRepresentada;
		this.id = id;
		this.idVendedor = idVendedor;
		this.finalidadePedido = tipoFinalidadePedido;
		this.tipoPedido = tipoPedido;
	}

	public Pedido(Integer id) {
		this.id = id;
	}

	public Pedido(Integer id, Date dataEnvio, Double valorPedido, String razaoSocialCliente) {
		this.id = id;
		this.dataEnvio = dataEnvio;
		this.valorPedido = valorPedido;

		this.cliente = new Cliente();
		this.cliente.setRazaoSocial(razaoSocialCliente);
	}

	public Pedido(Integer id, Date dataEnvio, Double valorPedido, String nomeFantasiaCliente, String nomeProprietario) {
		this.id = id;
		this.dataEnvio = dataEnvio;
		this.valorPedido = valorPedido;

		this.cliente = new Cliente();
		this.cliente.setNomeFantasia(nomeFantasiaCliente);

		this.proprietario = new Usuario();
		this.proprietario.setNome(nomeProprietario);
	}

	public Pedido(Integer id, Date dataEntrega, Double valorPedido, String nomeFantasiaCliente,
			String razaoSocialCliente, String nomeFantasiaRepresentada) {
		this.id = id;
		this.dataEntrega = dataEntrega;
		this.valorPedido = valorPedido;

		this.cliente = new Cliente();
		this.cliente.setNomeFantasia(nomeFantasiaCliente);
		this.cliente.setRazaoSocial(razaoSocialCliente);

		this.representada = new Representada();
		this.representada.setNomeFantasia(nomeFantasiaRepresentada);
	}

	// Construtor utilizado na pesquisa dos dados da nota fiscal.
	public Pedido(Integer id, String numeroNF, Date dataEmissaoNF, Date dataVencimentoNF, Double valorParcelaNF,
			Double valorTotalNF, String numeroColeta, Integer numeroVolumes) {
		this.id = id;
		this.numeroNF = numeroNF;
		this.dataEmissaoNF = dataEmissaoNF;
		this.dataVencimentoNF = dataVencimentoNF;
		this.valorParcelaNF = valorParcelaNF;
		this.valorTotalNF = valorTotalNF;
		this.numeroColeta = numeroColeta;
		this.numeroVolumes = numeroVolumes;
	}

	// Construtor utilizado na pesquisa para carregar a finalidade do pedido
	public Pedido(Integer id, TipoFinalidadePedido tipoFinalidadePedido, TipoPedido tipoPedido) {
		this.id = id;
		this.finalidadePedido = tipoFinalidadePedido;
		this.tipoPedido = tipoPedido;
	}

	public Pedido(Integer id, TipoPedido tipoPedido, Date dataEntrega, Date dataEnvio, Double valorPedido,
			String nomeFantasiaCliente, String razaoSocialCliente, String nomeFantasiaRepresentada) {
		this(id, tipoPedido, dataEntrega, valorPedido, nomeFantasiaCliente, razaoSocialCliente,
				nomeFantasiaRepresentada);
		this.dataEnvio = dataEnvio;
	}

	public Pedido(Integer id, TipoPedido tipoPedido, Date dataEntrega, Double valorPedido, String nomeFantasiaCliente,
			String razaoSocialCliente, String nomeFantasiaRepresentada) {
		this(id, dataEntrega, valorPedido, nomeFantasiaCliente, razaoSocialCliente, nomeFantasiaRepresentada);
		this.tipoPedido = tipoPedido;
	}

	public void addLogradouro(List<LogradouroPedido> listaLogradouro) {
		if (listaLogradouro == null || listaLogradouro.isEmpty()) {
			return;
		}

		for (LogradouroPedido logradouro : listaLogradouro) {
			this.addLogradouro(logradouro);
		}
	}

	public void addLogradouro(LogradouroPedido logradouro) {
		if (listaLogradouro == null) {
			setListaLogradouro(new ArrayList<LogradouroPedido>());
		}
		listaLogradouro.add(logradouro);
		logradouro.setPedido(this);
	}

	public double addValorPedido(double valor) {
		return this.valorPedido += valor;
	}

	@Override
	public Pedido clone() throws CloneNotSupportedException {
		return (Pedido) super.clone();
	}

	@Override
	public boolean equals(Object o) {
		return o instanceof Pedido && id != null && id.equals(((Pedido) o).id);
	}

	public Double getAliquotaComissaoRepresentada() {
		return aliquotaComissaoRepresentada;
	}

	public Cliente getCliente() {
		return cliente;
	}

	public Usuario getComprador() {
		return proprietario;
	}

	public Contato getContato() {
		return contato;
	}

	public Date getDataEmissaoNF() {
		return dataEmissaoNF;
	}

	public String getDataEmissaoNFFormatada() {
		return dataEmissaoNFFormatada;
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

	public String getDataInclusaoFormatada() {
		return dataInclusaoFormatada;
	}

	public Date getDataVencimentoNF() {
		return dataVencimentoNF;
	}

	public String getDataVencimentoNFFormatada() {
		return dataVencimentoNFFormatada;
	}

	public TipoFinalidadePedido getFinalidadePedido() {
		return finalidadePedido;
	}

	public String getFormaPagamento() {
		return formaPagamento;
	}

	public Integer getId() {
		return id;
	}

	public Integer getIdCliente() {
		return idCliente;
	}

	public Integer getIdVendedor() {
		return idVendedor;
	}

	public List<LogradouroPedido> getListaLogradouro() {
		return listaLogradouro;
	}

	public String getNumeroColeta() {
		return numeroColeta;
	}

	public String getNumeroNF() {
		return numeroNF;
	}

	public String getNumeroPedidoCliente() {
		return numeroPedidoCliente;
	}

	public Integer getNumeroVolumes() {
		return numeroVolumes;
	}

	public String getObservacao() {
		return observacao;
	}

	public Integer getPrazoEntrega() {
		return prazoEntrega;
	}

	public Usuario getProprietario() {
		return proprietario;
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

	public TipoPedido getTipoPedido() {
		return tipoPedido;
	}

	public Transportadora getTransportadora() {
		return transportadora;
	}

	public Transportadora getTransportadoraRedespacho() {
		return transportadoraRedespacho;
	}

	public Double getValorParcelaNF() {
		return valorParcelaNF;
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

	public Double getValorTotalNF() {
		return valorTotalNF;
	}

	public Usuario getVendedor() {
		return proprietario;
	}

	@Override
	public int hashCode() {
		return id != null ? id : -1;
	}

	public boolean isClienteNotificadoVenda() {
		return this.clienteNotificadoVenda;
	}

	public boolean isClienteNovo() {
		return cliente != null && cliente.getId() == null;
	}

	public boolean isCompra() {
		return TipoPedido.COMPRA.equals(tipoPedido);
	}

	public boolean isCompraAndamento() {
		return SituacaoPedido.COMPRA_ANDAMENTO.equals(this.situacaoPedido);
	}

	public boolean isCompraEfetuada() {
		return SituacaoPedido.COMPRA_AGUARDANDO_RECEBIMENTO.equals(situacaoPedido)
				|| SituacaoPedido.COMPRA_RECEBIDA.equals(situacaoPedido);
	}

	public boolean isEnviado() {
		return SituacaoPedido.ENVIADO.equals(this.situacaoPedido);
	}

	public boolean isItemAguardandoMaterial() {
		return TipoPedido.REVENDA.equals(tipoPedido) && SituacaoPedido.ITEM_AGUARDANDO_MATERIAL.equals(situacaoPedido);
	}

	public boolean isOrcamento() {
		return SituacaoPedido.ORCAMENTO.equals(this.situacaoPedido);
	}

	public boolean isRepresentacao() {
		return TipoPedido.REPRESENTACAO.equals(tipoPedido);
	}

	public boolean isRevenda() {
		return TipoPedido.REVENDA.equals(tipoPedido);
	}

	public boolean isRevendaEfetuada() {
		return isRevenda() && !SituacaoPedido.CANCELADO.equals(situacaoPedido);
	}

	public boolean isVenda() {
		return isRevenda() || isRepresentacao();
	}

	public boolean isVendaEfetuada() {
		return isRepresentacao() && !SituacaoPedido.CANCELADO.equals(situacaoPedido)
				&& SituacaoPedido.ENVIADO.equals(situacaoPedido);
	}

	public void setAliquotaComissaoRepresentada(Double aliquotaComissaoRepresentada) {
		this.aliquotaComissaoRepresentada = aliquotaComissaoRepresentada;
	}

	public void setCliente(Cliente cliente) {
		this.cliente = cliente;
	}

	public void setClienteNotificadoVenda(boolean clienteNotificadoVenda) {
		this.clienteNotificadoVenda = clienteNotificadoVenda;
	}

	public void setComprador(Usuario comprador) {
		proprietario = comprador;
	}

	public void setContato(Contato contato) {
		this.contato = contato;
	}

	public void setDataEmissaoNF(Date dataEmissaoNF) {
		this.dataEmissaoNF = dataEmissaoNF;
	}

	public void setDataEmissaoNFFormatada(String dataEmissaoNFFormatada) {
		this.dataEmissaoNFFormatada = dataEmissaoNFFormatada;
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

	public void setDataInclusaoFormatada(String dataInclusaoFormatada) {
		this.dataInclusaoFormatada = dataInclusaoFormatada;
	}

	public void setDataVencimentoNF(Date dataVencimentoNF) {
		this.dataVencimentoNF = dataVencimentoNF;
	}

	public void setDataVencimentoNFFormatada(String dataVencimentoNFFormatada) {
		this.dataVencimentoNFFormatada = dataVencimentoNFFormatada;
	}

	public void setFinalidadePedido(TipoFinalidadePedido finalidadePedido) {
		this.finalidadePedido = finalidadePedido;
	}

	public void setFormaPagamento(String formaPagamento) {
		this.formaPagamento = formaPagamento;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public void setIdCliente(Integer idCliente) {
		this.idCliente = idCliente;
	}

	public void setIdVendedor(Integer idVendedor) {
		this.idVendedor = idVendedor;
	}

	public void setListaLogradouro(List<LogradouroPedido> listaLogradouro) {
		this.listaLogradouro = listaLogradouro;
	}

	public void setNumeroColeta(String numeroColeta) {
		this.numeroColeta = numeroColeta;
	}

	public void setNumeroNF(String numeroNF) {
		this.numeroNF = numeroNF;
	}

	public void setNumeroPedidoCliente(String numeroPedidoCliente) {
		this.numeroPedidoCliente = numeroPedidoCliente;
	}

	public void setNumeroVolumes(Integer numeroVolumes) {
		this.numeroVolumes = numeroVolumes;
	}

	public void setObservacao(String observacao) {
		this.observacao = observacao;
	}

	public void setOrcamento(boolean isOrcamento) {
		if (isOrcamento) {
			this.setSituacaoPedido(SituacaoPedido.ORCAMENTO);
		}
	}

	public void setPrazoEntrega(Integer prazoEntrega) {
		this.prazoEntrega = prazoEntrega;
	}

	public void setProprietario(Usuario proprietario) {
		this.proprietario = proprietario;
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

	public void setTipoPedido(TipoPedido tipoPedido) {
		this.tipoPedido = tipoPedido;
	}

	public void setTransportadora(Transportadora transportadora) {
		this.transportadora = transportadora;
	}

	public void setTransportadoraRedespacho(Transportadora transportadoraRedespacho) {
		this.transportadoraRedespacho = transportadoraRedespacho;
	}

	public void setValorParcelaNF(Double valorParcelaNF) {
		this.valorParcelaNF = valorParcelaNF;
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

	public void setValorTotalNF(Double valorTotalNF) {
		this.valorTotalNF = valorTotalNF;
	}

	public void setVendedor(Usuario vendedor) {
		this.proprietario = vendedor;
	}
}
