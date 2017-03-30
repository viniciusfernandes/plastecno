package br.com.plastecno.service.entity;

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

import br.com.plastecno.service.nfe.constante.TipoSituacaoDuplicata;
import br.com.plastecno.service.validacao.annotation.InformacaoValidavel;

@Entity
@Table(name = "tb_nfe_duplicata", schema = "vendas")
public class NFeDuplicata {
	@Column(name = "data_vencimento")
	@InformacaoValidavel(obrigatorio = true, nomeExibicao = "Data de vencimento da duplicata")
	private Date dataVencimento;

	@Id
	private Integer id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_nfe_pedido", referencedColumnName = "numero", nullable = false)
	@InformacaoValidavel(relacionamentoObrigatorio = true, nomeExibicao = "Número da NFe da duplicata")
	private NFePedido nFe;

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
	public NFeDuplicata(Date dataVencimento, Integer id, Integer numeroNFe,
			TipoSituacaoDuplicata tipoSituacaoDuplicata, Double valor) {
		this.dataVencimento = dataVencimento;
		this.id = id;
		this.numeroNFe = numeroNFe;
		this.tipoSituacaoDuplicata = tipoSituacaoDuplicata;
		this.valor = valor;
	}

	public Date getDataVencimento() {
		return dataVencimento;
	}

	public Integer getId() {
		return id;
	}

	public NFePedido getnFe() {
		return nFe;
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

	public void setId(Integer id) {
		this.id = id;
	}

	public void setnFe(NFePedido nFe) {
		this.nFe = nFe;
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
