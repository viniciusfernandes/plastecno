package br.com.plastecno.service.entity;

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

import br.com.plastecno.service.nfe.constante.TipoSituacaoDuplicata;
import br.com.plastecno.service.validacao.annotation.InformacaoValidavel;

@Entity
@Table(name = "tb_nfe_duplicata", schema = "vendas")
public class NFeDuplicata {
	@Column(name = "data_vencimento")
	@InformacaoValidavel(obrigatorio = true, nomeExibicao = "Data de vencimento da duplicata")
	private Date dataVencimento;

	@Transient
	private String dataVencimentoFormatada;

	@Id
	@SequenceGenerator(name = "nFeDuplicataSequence", sequenceName = "vendas.seq_nfe_duplicata_id", allocationSize = 1, initialValue = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "nFeDuplicataSequence")
	private Integer id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_nfe_pedido", referencedColumnName = "numero", nullable = false)
	@InformacaoValidavel(relacionamentoObrigatorio = true, nomeExibicao = "Número da NFe da duplicata")
	private NFePedido nFe;

	// Esse campo foi criado para otimizar a pesquisa das duplicatas pois temos
	// que recuperar o nome do cliente do pedido.
	@Column(name = "nome_cliente")
	@InformacaoValidavel(obrigatorio = true, intervaloComprimento = { 1, 150 })
	private String nomeCliente;

	@Transient
	private Integer numeroNFe;

	@Enumerated(EnumType.ORDINAL)
	@Column(name = "id_situacao_duplicata")
	@InformacaoValidavel(obrigatorio = true, nomeExibicao = "Situação da duplicada")
	private TipoSituacaoDuplicata tipoSituacaoDuplicata;

	@Column(name = "valor")
	@InformacaoValidavel(obrigatorio = true, nomeExibicao = "Valor da duplicata")
	private Double valor;

	public NFeDuplicata() {
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

	public Date getDataVencimento() {
		return dataVencimento;
	}

	public String getDataVencimentoFormatada() {
		return dataVencimentoFormatada;
	}

	public Integer getId() {
		return id;
	}

	public NFePedido getnFe() {
		return nFe;
	}

	public String getNomeCliente() {
		return nomeCliente;
	}

	public Integer getNumeroNFe() {
		return numeroNFe;
	}

	public TipoSituacaoDuplicata getTipoSituacaoDuplicata() {
		return tipoSituacaoDuplicata;
	}

	public Double getValor() {
		return valor;
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

	public void setnFe(NFePedido nFe) {
		this.nFe = nFe;
	}

	public void setNomeCliente(String nomeCliente) {
		this.nomeCliente = nomeCliente;
	}

	public void setNumeroNFe(Integer numeroNFe) {
		this.numeroNFe = numeroNFe;
	}

	public void setTipoSituacaoDuplicata(TipoSituacaoDuplicata tipoSituacaoDuplicata) {
		this.tipoSituacaoDuplicata = tipoSituacaoDuplicata;
	}

	public void setValor(Double valor) {
		this.valor = valor;
	}
}
