package br.com.svr.service.entity.crm;

import java.io.Serializable;
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

import br.com.svr.service.constante.crm.CategoriaNegociacao;
import br.com.svr.service.constante.crm.SituacaoNegociacao;
import br.com.svr.service.constante.crm.TipoNaoFechamento;
import br.com.svr.service.validacao.annotation.InformacaoValidavel;

@Entity
@Table(name = "tb_negociacao", schema = "crm")
@InformacaoValidavel
public class Negociacao implements Serializable {
	private static final long serialVersionUID = -8125636304167827730L;

	@Column(name = "id_categoria_negociacao")
	@Enumerated(EnumType.ORDINAL)
	@InformacaoValidavel(obrigatorio = true, nomeExibicao = "Categoria da negociação")
	private CategoriaNegociacao categoriaNegociacao;

	private String comentario;

	@Column(name = "data_encerramento")
	private Date dataEncerramento;

	@Id
	@SequenceGenerator(name = "negociacaoSequence", sequenceName = "crm.seq_negociacao_id", allocationSize = 1, initialValue = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "negociacaoSequence")
	private Integer id;

	@Column(name = "id_orcamento")
	@InformacaoValidavel(obrigatorio = true, nomeExibicao = "Orçamento da negociação")
	private Integer idOrcamento;

	@Column(name = "id_vendedor")
	@InformacaoValidavel(obrigatorio = true, nomeExibicao = "Vendedor da negociação")
	private Integer idVendedor;

	@Column(name = "nome_cliente")
	@InformacaoValidavel(obrigatorio = true, nomeExibicao = "Nome do cliente da negociação")
	private String nomeCliente;

	@Column(name = "nome_contato")
	@InformacaoValidavel(nomeExibicao = "Nome do contato da negociação")
	private String nomeContato;

	@Column(name = "id_situacao_negociacao")
	@Enumerated(EnumType.ORDINAL)
	@InformacaoValidavel(obrigatorio = true, nomeExibicao = "Situação da negociação")
	private SituacaoNegociacao situacaoNegociacao;

	@Column(name = "telefone_contato")
	@InformacaoValidavel(nomeExibicao = "Telefone do contato da negociação")
	private String telefoneContato;

	@Column(name = "id_tipo_nao_fechamento")
	@Enumerated(EnumType.ORDINAL)
	@InformacaoValidavel(obrigatorio = true, nomeExibicao = "Tipo de não-fechamento da negociação")
	private TipoNaoFechamento tipoNaoFechamento;

	@Column(name = "valor")
	@InformacaoValidavel(nomeExibicao = "Valor da negociação")
	private double valor;

	public CategoriaNegociacao getCategoriaNegociacao() {
		return categoriaNegociacao;
	}

	public String getComentario() {
		return comentario;
	}

	public Date getDataEncerramento() {
		return dataEncerramento;
	}

	public Integer getId() {
		return id;
	}

	public Integer getIdOrcamento() {
		return idOrcamento;
	}

	public Integer getIdVendedor() {
		return idVendedor;
	}

	public String getNomeCliente() {
		return nomeCliente;
	}

	public String getNomeContato() {
		return nomeContato;
	}

	public SituacaoNegociacao getSituacaoNegociacao() {
		return situacaoNegociacao;
	}

	public String getTelefoneContato() {
		return telefoneContato;
	}

	public TipoNaoFechamento getTipoNaoFechamento() {
		return tipoNaoFechamento;
	}

	public double getValor() {
		return valor;
	}

	public void setCategoriaNegociacao(CategoriaNegociacao categoriaNegociacao) {
		this.categoriaNegociacao = categoriaNegociacao;
	}

	public void setComentario(String comentario) {
		this.comentario = comentario;
	}

	public void setDataEncerramento(Date dataEncerramento) {
		this.dataEncerramento = dataEncerramento;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public void setIdOrcamento(Integer idOrcamento) {
		this.idOrcamento = idOrcamento;
	}

	public void setIdVendedor(Integer idVendedor) {
		this.idVendedor = idVendedor;
	}

	public void setNomeCliente(String nomeCliente) {
		this.nomeCliente = nomeCliente;
	}

	public void setNomeContato(String nomeContato) {
		this.nomeContato = nomeContato;
	}

	public void setSituacaoNegociacao(SituacaoNegociacao situacaoNegociacao) {
		this.situacaoNegociacao = situacaoNegociacao;
	}

	public void setTelefoneContato(String telefoneContato) {
		this.telefoneContato = telefoneContato;
	}

	public void setTipoNaoFechamento(TipoNaoFechamento tipoNaoFechamento) {
		this.tipoNaoFechamento = tipoNaoFechamento;
	}

	public void setValor(double valor) {
		this.valor = valor;
	}
}
