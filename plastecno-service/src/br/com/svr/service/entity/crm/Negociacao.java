package br.com.svr.service.entity.crm;

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

import br.com.svr.service.constante.crm.CategoriaNegociacao;
import br.com.svr.service.constante.crm.SituacaoNegociacao;
import br.com.svr.service.constante.crm.TipoNaoFechamento;
import br.com.svr.service.entity.Pedido;
import br.com.svr.service.entity.Usuario;
import br.com.svr.service.validacao.annotation.InformacaoValidavel;

@Entity
@Table(name = "tb_negociacao", schema = "crm")
@InformacaoValidavel
public class Negociacao {
	@Column(name = "id_categoria_negociacao")
	@Enumerated(EnumType.ORDINAL)
	@InformacaoValidavel(relacionamentoObrigatorio = true, nomeExibicao = "Categoria da negociação")
	private CategoriaNegociacao categoriaNegociacao;

	private String comentario;

	@Column(name = "data_encerramento")
	private Date dataEncerramento;

	@Id
	@SequenceGenerator(name = "negociacaoSequence", sequenceName = "crm.seq_negociacao_id", allocationSize = 1, initialValue = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "negociacaoSequence")
	private Integer id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_pedido")
	@InformacaoValidavel(relacionamentoObrigatorio = true, nomeExibicao = "Orçamento da negociação")
	private Pedido orcamento;

	@Column(name = "id_situacao_negociacao")
	@Enumerated(EnumType.ORDINAL)
	@InformacaoValidavel(relacionamentoObrigatorio = true, nomeExibicao = "Situação da negociação")
	private SituacaoNegociacao situacaoNegociacao;

	@Column(name = "id_tipo_nao_fechamento")
	@Enumerated(EnumType.ORDINAL)
	@InformacaoValidavel(relacionamentoObrigatorio = true, nomeExibicao = "Tipo de não-fechamento da negociação")
	private TipoNaoFechamento tipoNaoFechamento;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_vendedor")
	@InformacaoValidavel(relacionamentoObrigatorio = true, nomeExibicao = "Vendedor da negociação")
	private Usuario vendedor;

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

	public Pedido getOrcamento() {
		return orcamento;
	}

	public SituacaoNegociacao getSituacaoNegociacao() {
		return situacaoNegociacao;
	}

	public TipoNaoFechamento getTipoNaoFechamento() {
		return tipoNaoFechamento;
	}

	public Usuario getVendedor() {
		return vendedor;
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

	public void setOrcamento(Pedido orcamento) {
		this.orcamento = orcamento;
	}

	public void setSituacaoNegociacao(SituacaoNegociacao situacaoNegociacao) {
		this.situacaoNegociacao = situacaoNegociacao;
	}

	public void setTipoNaoFechamento(TipoNaoFechamento tipoNaoFechamento) {
		this.tipoNaoFechamento = tipoNaoFechamento;
	}

	public void setVendedor(Usuario vendedor) {
		this.vendedor = vendedor;
	}
}
