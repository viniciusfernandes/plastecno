package br.com.svr.service.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import br.com.svr.service.nfe.constante.TipoNFe;
import br.com.svr.service.nfe.constante.TipoSituacaoNFe;
import br.com.svr.service.validacao.InformacaoValidavel;

@Entity
@Table(name = "tb_nfe_pedido", schema = "vendas")
@InformacaoValidavel
public class NFePedido {

	@Column(name = "data_emissao")
	@InformacaoValidavel(obrigatorio = true, nomeExibicao = "Data de emissão da NFe do pedido")
	private Date dataEmissao;

	@Transient
	private String dataEmissaoFormatada;
	@Transient
	private Integer idPedido;

	@InformacaoValidavel(obrigatorio = true, nomeExibicao = "Número do modelo da NFe do pedido")
	private Integer modelo;

	@Column(name = "nome_cliente")
	@InformacaoValidavel(obrigatorio = true, nomeExibicao = "nome do Cliente da NFe do pedido")
	private String nomeCliente;

	@Id
	@InformacaoValidavel(obrigatorio = true, nomeExibicao = "Número da NFe do pedido")
	private Integer numero;

	@Column(name = "numero_associado")
	private Integer numeroAssociado;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_pedido")
	@InformacaoValidavel(relacionamentoObrigatorio = true, nomeExibicao = "Pedido da NFe de Pedido")
	private Pedido pedido;

	@InformacaoValidavel(obrigatorio = true, nomeExibicao = "Série da NFe do pedido")
	private Integer serie;

	@Enumerated(EnumType.ORDINAL)
	@Column(name = "id_tipo_nfe")
	@InformacaoValidavel(obrigatorio = true, nomeExibicao = "Tipo da NFe do pedido")
	private TipoNFe tipoNFe;

	@Enumerated(EnumType.ORDINAL)
	@Column(name = "id_situacao_nfe")
	private TipoSituacaoNFe tipoSituacaoNFe;

	@Column(name = "valor")
	@InformacaoValidavel(obrigatorio = true, nomeExibicao = "Valor NFe do pedido")
	private Double valor;

	@Column(name = "valor_icms")
	@InformacaoValidavel(obrigatorio = true, nomeExibicao = "Valor ICMS NFe do pedido")
	private Double valorICMS;

	// Aqui a tributo lenght esta presente para que o xml seja inserido no banco
	// de dados em memoria dos testes de integracao.
	@Column(name = "xml_nfe", length = 999999999)
	private String xmlNFe;

	public NFePedido() {
	}

	public NFePedido(Date dataEmissao, Integer idPedido, Integer modelo, String nomeCliente, Integer numero,
			Integer numeroAssociado, Integer serie, TipoNFe tipoNFe, TipoSituacaoNFe tipoSituacaoNFe, String xmlNFe) {
		this(idPedido, modelo, nomeCliente, numero, numeroAssociado, serie, tipoNFe, tipoSituacaoNFe, xmlNFe);
		this.dataEmissao = dataEmissao;
		this.nomeCliente = nomeCliente;
	}

	// Construtor usado no relatorio de faturamento
	public NFePedido(Date dataEmissao, Integer idPedido, String nomeCliente, Integer numero, Double valor,
			Double valorICMS) {
		this(dataEmissao, idPedido, null, nomeCliente, numero, null, null, null, null, null);
		this.valor = valor;
		this.valorICMS = valorICMS;
	}

	public NFePedido(Integer numero) {
		this.numero = numero;
	}

	public NFePedido(Integer numero, Integer serie, Integer modelo, String xmlNFe, Integer idPedido,
			Integer numeroAssociado, TipoNFe tipoNFe, TipoSituacaoNFe tipoSituacaoNFe) {
		this.numero = numero;
		this.serie = serie;
		this.modelo = modelo;
		this.idPedido = idPedido;
		this.pedido = new Pedido(idPedido);
		this.xmlNFe = xmlNFe;
		this.numeroAssociado = numeroAssociado;
		this.tipoNFe = tipoNFe;
		this.tipoSituacaoNFe = tipoSituacaoNFe;
	}

	public NFePedido(Integer idPedido, Integer modelo, String nomeCliente, Integer numero, Integer numeroAssociado,
			Integer serie, TipoNFe tipoNFe, TipoSituacaoNFe tipoSituacaoNFe, String xmlNFe) {
		this(numero, serie, modelo, xmlNFe, idPedido, numeroAssociado, tipoNFe, tipoSituacaoNFe);
	}

	public Date getDataEmissao() {
		return dataEmissao;
	}

	public String getDataEmissaoFormatada() {
		return dataEmissaoFormatada;
	}

	public Integer getIdPedido() {
		return idPedido;
	}

	public Integer getModelo() {
		return modelo;
	}

	public String getNomeCliente() {
		return nomeCliente;
	}

	public Integer getNumero() {
		return numero;
	}

	public Integer getNumeroAssociado() {
		return numeroAssociado;
	}

	public Integer getSerie() {
		return serie;
	}

	public TipoNFe getTipoNFe() {
		return tipoNFe;
	}

	public TipoSituacaoNFe getTipoSituacaoNFe() {
		return tipoSituacaoNFe;
	}

	public Double getValor() {
		return valor;
	}

	public Double getValorICMS() {
		return valorICMS;
	}

	public String getXmlNFe() {
		return xmlNFe;
	}

	public boolean isTriangularizacao() {
		return TipoNFe.TRIANGULARIZACAO.equals(tipoNFe);
	}

	public void setDataEmissao(Date dataEmissao) {
		this.dataEmissao = dataEmissao;
	}

	public void setDataEmissaoFormatada(String dataEmissaoFormatada) {
		this.dataEmissaoFormatada = dataEmissaoFormatada;
	}

	public void setIdPedido(Integer idPedido) {
		this.idPedido = idPedido;
	}

	public void setModelo(Integer modelo) {
		this.modelo = modelo;
	}

	public void setNomeCliente(String nomeCliente) {
		this.nomeCliente = nomeCliente;
	}

	public void setNumero(Integer numero) {
		this.numero = numero;
	}

	public void setNumeroAssociado(Integer numeroAssociado) {
		this.numeroAssociado = numeroAssociado;
	}

	public void setSerie(Integer serie) {
		this.serie = serie;
	}

	public void setTipoNFe(TipoNFe tipoNFe) {
		this.tipoNFe = tipoNFe;
	}

	public void setTipoSituacaoNFe(TipoSituacaoNFe tipoSituacaoNFe) {
		this.tipoSituacaoNFe = tipoSituacaoNFe;
	}

	public void setValor(Double valor) {
		this.valor = valor;
	}

	public void setValorICMS(Double valorICMS) {
		this.valorICMS = valorICMS;
	}

	public void setXmlNFe(String xmlNFe) {
		this.xmlNFe = xmlNFe;
	}

}
