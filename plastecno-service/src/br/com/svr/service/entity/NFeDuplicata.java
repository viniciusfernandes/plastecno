package br.com.svr.service.entity;

import java.util.Date;

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
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;

import br.com.svr.service.nfe.constante.TipoSituacaoDuplicata;
import br.com.svr.service.validacao.annotation.InformacaoValidavel;

@Entity
@Table(name = "tb_nfe_duplicata", schema = "vendas")
public class NFeDuplicata {
	@Column(name = "codigo_banco")
	private String codigoBanco;

	@Column(name = "data_vencimento")
	@InformacaoValidavel(obrigatorio = true, nomeExibicao = "Data de vencimento da duplicata")
	private Date dataVencimento;

	@Transient
	private String dataVencimentoFormatada;

	@Id
	@SequenceGenerator(name = "nFeDuplicataSequence", sequenceName = "vendas.seq_nfe_duplicata_id", allocationSize = 1, initialValue = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "nFeDuplicataSequence")
	private Integer id;

	@InformacaoValidavel(obrigatorio = true, nomeExibicao = "Id do cliente da duplicata")
	@Column(name = "id_cliente")
	private Integer idCliente;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_nfe_pedido", referencedColumnName = "numero", nullable = false)
	@InformacaoValidavel(relacionamentoObrigatorio = true, nomeExibicao = "Número da NFe da duplicata")
	private NFePedido nFe;

	@Column(name = "nome_banco")
	private String nomeBanco;

	// Esse campo foi criado para otimizar a pesquisa das duplicatas pois temos
	// que recuperar o nome do cliente do pedido.
	@Column(name = "nome_cliente")
	@InformacaoValidavel(obrigatorio = true, intervaloComprimento = { 1, 150 })
	private String nomeCliente;

	@Transient
	private Integer numeroNFe;

	private Integer parcela;

	@Enumerated(EnumType.ORDINAL)
	@Column(name = "id_situacao_duplicata")
	@InformacaoValidavel(obrigatorio = true, nomeExibicao = "Situação da duplicada")
	private TipoSituacaoDuplicata tipoSituacaoDuplicata;

	@Column(name = "total_parcelas")
	private Integer totalParcelas;

	@Column(name = "valor")
	@InformacaoValidavel(obrigatorio = true, nomeExibicao = "Valor da duplicata")
	private Double valor;

	public NFeDuplicata() {
	}

	public NFeDuplicata(Date dataVencimento, Integer id, Integer idCliente, String nomeCliente, Integer numeroNFe,
			TipoSituacaoDuplicata tipoSituacaoDuplicata, Double valor) {
		this(dataVencimento, nomeCliente, numeroNFe, tipoSituacaoDuplicata, valor);
		this.id = id;
		this.idCliente = idCliente;
	}

	// Construtor utilizado na pesquisa de duplicatas para gerar o relatorio de
	// duplicatas
	public NFeDuplicata(Date dataVencimento, Integer id, Integer parcela, TipoSituacaoDuplicata tipoSituacaoDuplicata,
			Integer totalParcelas, Double valor) {
		this(dataVencimento, id, null, null, tipoSituacaoDuplicata, valor);
		this.parcela = parcela;
		this.totalParcelas = totalParcelas;
	}

	// Construtor utilizado no relatorio de duplicatas
	public NFeDuplicata(Date dataVencimento, Integer id, String nomeCliente, Integer numeroNFe,
			TipoSituacaoDuplicata tipoSituacaoDuplicata, Double valor) {
		this.dataVencimento = dataVencimento;
		this.id = id;
		this.nomeCliente = nomeCliente;
		this.nFe = new NFePedido(numeroNFe);
		this.numeroNFe = numeroNFe;
		this.tipoSituacaoDuplicata = tipoSituacaoDuplicata;
		this.valor = valor;
	}

	// Construtor utilizado na pesquisa de duplicatas
	public NFeDuplicata(Date dataVencimento, Integer id, TipoSituacaoDuplicata tipoSituacaoDuplicata, Double valor) {
		this(dataVencimento, id, null, null, tipoSituacaoDuplicata, valor);
	}

	public NFeDuplicata(Date dataVencimento, String nomeCliente, Integer numeroNFe,
			TipoSituacaoDuplicata tipoSituacaoDuplicata, Double valor) {
		this(dataVencimento, null, nomeCliente, numeroNFe, tipoSituacaoDuplicata, valor);

	}

	// Construtor utilizado no relatorio de duplicatas
	public NFeDuplicata(String codigoBanco, Date dataVencimento, Integer id, String nomeBanco, String nomeCliente,
			Integer numeroNFe, Integer parcela, TipoSituacaoDuplicata tipoSituacaoDuplicata, Integer totalParcelas,
			Double valor) {
		this(dataVencimento, id, null, nomeCliente, numeroNFe, tipoSituacaoDuplicata, valor);
		this.parcela = parcela;
		this.totalParcelas = totalParcelas;
		this.codigoBanco = codigoBanco;
		this.nomeBanco = nomeBanco;
	}

	public String getCodigoBanco() {
		return codigoBanco;
	}

	public Date getDataVencimento() {
		return dataVencimento;
	}

	public String getDataVencimentoFormatada() {
		return dataVencimentoFormatada;
	}

	public Integer getId() {
		return id;
	}

	public Integer getIdCliente() {
		return idCliente;
	}

	public NFePedido getnFe() {
		return nFe;
	}

	public String getNomeBanco() {
		return nomeBanco;
	}

	public String getNomeCliente() {
		return nomeCliente;
	}

	public Integer getNumeroNFe() {
		return numeroNFe;
	}

	public Integer getParcela() {
		return parcela;
	}

	public String getParcelaFormatada() {
		return parcela == null || totalParcelas == null ? "" : parcela + "/" + totalParcelas;
	}

	public TipoSituacaoDuplicata getTipoSituacaoDuplicata() {
		return tipoSituacaoDuplicata;
	}

	public Integer getTotalParcelas() {
		return totalParcelas;
	}

	public Double getValor() {
		return valor;
	}

	public boolean isLiquidado() {
		return TipoSituacaoDuplicata.LIQUIDADO.equals(tipoSituacaoDuplicata);
	}

	public void setCodigoBanco(String codigoBanco) {
		this.codigoBanco = codigoBanco;
	}

	public void setDataVencimento(Date dataVencimento) {
		this.dataVencimento = dataVencimento;
	}

	public void setDataVencimentoFormatada(String dataVencimentoFormatada) {
		this.dataVencimentoFormatada = dataVencimentoFormatada;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public void setIdCliente(Integer idCliente) {
		this.idCliente = idCliente;
	}

	public void setnFe(NFePedido nFe) {
		this.nFe = nFe;
	}

	public void setNomeBanco(String nomeBanco) {
		this.nomeBanco = nomeBanco;
	}

	public void setNomeCliente(String nomeCliente) {
		this.nomeCliente = nomeCliente;
	}

	public void setNumeroNFe(Integer numeroNFe) {
		this.numeroNFe = numeroNFe;
	}

	public void setParcela(Integer parcela) {
		this.parcela = parcela;
	}

	public void setTipoSituacaoDuplicata(TipoSituacaoDuplicata tipoSituacaoDuplicata) {
		this.tipoSituacaoDuplicata = tipoSituacaoDuplicata;
	}

	public void setTotalParcelas(Integer totalParcelas) {
		this.totalParcelas = totalParcelas;
	}

	public void setValor(Double valor) {
		this.valor = valor;
	}
}
