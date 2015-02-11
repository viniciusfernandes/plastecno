package br.com.plastecno.service.entity;

import java.io.Serializable;

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

import br.com.plastecno.service.constante.FormaMaterial;
import br.com.plastecno.service.constante.TipoVenda;
import br.com.plastecno.service.validacao.annotation.InformacaoValidavel;

@InformacaoValidavel
public class Item implements Serializable, Cloneable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3397681910683242844L;

	private Double comprimento;

	@Column(name = "medida_interna")
	private Double medidaInterna;

	@Column(name = "medida_externa")
	private Double medidaExterna;

	@InformacaoValidavel(obrigatorio = true, numerico = true, valorNegativo = false, nomeExibicao = "Quantidade de itens do pedido")
	private Integer quantidade;

	@Column(name = "preco_unidade")
	@InformacaoValidavel(obrigatorio = true, numerico = true, valorNaoNegativo = false, nomeExibicao = "Preço da unidade item do pedido")
	private Double precoUnidade;

	@Column(name = "preco_unidade_ipi")
	@InformacaoValidavel(obrigatorio = true, numerico = true, valorNaoNegativo = false, nomeExibicao = "Preço da unidade item do pedido com IPI")
	private Double precoUnidadeIPI;

	@Column(name = "aliquota_icms")
	@InformacaoValidavel(numerico = true, valorNaoNegativo = false, nomeExibicao = "Alíquota ICMS")
	private Double aliquotaICMS;

	@Column(name = "aliquota_ipi")
	@InformacaoValidavel(numerico = true, valorNaoNegativo = false, nomeExibicao = "Alíquota IPI")
	private Double aliquotaIPI;

	@Column(name = "descricao_peca")
	@InformacaoValidavel(intervalo = { 1, 100 }, nomeExibicao = "Descrição do item do pedido")
	private String descricaoPeca;

	@Enumerated(EnumType.ORDINAL)
	@Column(name = "id_forma_material")
	@InformacaoValidavel(obrigatorio = true, nomeExibicao = "Forma do material do item do pedido")
	private FormaMaterial formaMaterial;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_material", referencedColumnName = "id", nullable = false)
	@InformacaoValidavel(relacionamentoObrigatorio = true, nomeExibicao = "Material associado ao pedido")
	private Material material;

	@Transient
	private String precoUnidadeFormatado;

	@Transient
	private String precoUnidadeIPIFormatado;

	@Transient
	private String aliquotaICMSFormatado;

	@Transient
	private String aliquotaIPIFormatado;

	@Transient
	private String medidaExternaFomatada;

	@Transient
	private String medidaInternaFomatada;

	@Transient
	private String comprimentoFormatado;

	public Item() {
	}

	public Item(FormaMaterial formaMaterial) {
		this.formaMaterial = formaMaterial;
	}

	public boolean contemLargura() {
		return this.formaMaterial != null && this.formaMaterial.contemLargura();
	}

	public Double getAliquotaICMS() {
		return aliquotaICMS;
	}

	public String getAliquotaICMSFormatado() {
		return aliquotaICMSFormatado;
	}

	public Double getAliquotaIPI() {
		return aliquotaIPI;
	}

	public String getAliquotaIPIFormatado() {
		return aliquotaIPIFormatado;
	}

	public Double getComprimento() {
		return comprimento;
	}

	public String getComprimentoFormatado() {
		if (comprimentoFormatado == null) {
			return " _ ";
		}
		return comprimentoFormatado;
	}

	public String getDescricao() {

		StringBuilder descricao = new StringBuilder();
		if (material != null) {
			descricao.append(this.formaMaterial);
			descricao.append(" - ");
			descricao.append(this.material.getSigla());
			descricao.append(" - ");
			descricao.append(this.material.getDescricao() == null ? " " : this.material.getDescricao());
			descricao.append(" - ");
		}

		if (!this.isPeca()) {
			descricao.append(getMedidaExternaFomatada());
			descricao.append(" X ");

			if (this.medidaInterna != null) {
				descricao.append(getMedidaInternaFomatada());
				descricao.append(" X ");
			}

			descricao.append(getComprimentoFormatado());
			descricao.append(" mm");
		} else {
			descricao.append(this.descricaoPeca);
		}
		return descricao.toString();
	}

	public String getDescricaoPeca() {
		return descricaoPeca;
	}

	public FormaMaterial getFormaMaterial() {
		return formaMaterial;
	}

	public Material getMaterial() {
		return material;
	}

	public Double getMedidaExterna() {
		return medidaExterna;
	}

	public String getMedidaExternaFomatada() {
		if (medidaExternaFomatada == null) {
			return " _ ";
		}
		return medidaExternaFomatada;
	}

	public Double getMedidaInterna() {
		return medidaInterna;
	}

	public String getMedidaInternaFomatada() {
		if (medidaInternaFomatada == null) {
			return " _ ";
		}
		return medidaInternaFomatada;
	}

	public double getPrecoItem() {
		if (precoUnidade == null || quantidade == null) {
			return 0d;
		}

		return precoUnidade * quantidade;
	}

	public Double getPrecoUnidade() {
		return precoUnidade;
	}

	public String getPrecoUnidadeFormatado() {
		return precoUnidadeFormatado;
	}

	public Double getPrecoUnidadeIPI() {
		return precoUnidadeIPI;
	}

	public String getPrecoUnidadeIPIFormatado() {
		return precoUnidadeIPIFormatado;
	}

	public Integer getQuantidade() {
		return quantidade;
	}

	public boolean isFormaMaterialVazada() {
		return formaMaterial != null && formaMaterial.isFormaMaterialVazada();
	}

	public boolean isMedidaExternaIgualInterna() {
		return this.formaMaterial != null && this.formaMaterial.isMedidaExternaIgualInterna();
	}

	public boolean isPeca() {
		return FormaMaterial.PC.equals(this.formaMaterial);
	}

	public void setAliquotaICMS(Double aliquotaICMS) {
		this.aliquotaICMS = aliquotaICMS;
	}

	public void setAliquotaICMSFormatado(String aliquotaICMSFormatado) {
		this.aliquotaICMSFormatado = aliquotaICMSFormatado;
	}

	public void setAliquotaIPI(Double aliquotaIPI) {
		this.aliquotaIPI = aliquotaIPI;
	}

	public void setAliquotaIPIFormatado(String aliquotaIPIFormatado) {
		this.aliquotaIPIFormatado = aliquotaIPIFormatado;
	}

	public void setComprimento(Double comprimento) {
		this.comprimento = comprimento;
	}

	public void setComprimentoFormatado(String comprimentoFormatado) {
		this.comprimentoFormatado = comprimentoFormatado;
	}

	public void setDescricaoPeca(String descricaoPeca) {
		this.descricaoPeca = descricaoPeca;
	}

	public void setFormaMaterial(FormaMaterial formaMaterial) {
		this.formaMaterial = formaMaterial;
	}

	public void setMaterial(Material material) {
		this.material = material;
	}

	public void setMedidaExterna(Double medidaExterna) {
		this.medidaExterna = medidaExterna;
	}

	public void setMedidaExternaFomatada(String medidaExternaFomatada) {
		this.medidaExternaFomatada = medidaExternaFomatada;
	}

	public void setMedidaInterna(Double medidaInterna) {
		this.medidaInterna = medidaInterna;
	}

	public void setMedidaInternaFomatada(String medidaInternaFomatada) {
		this.medidaInternaFomatada = medidaInternaFomatada;
	}

	public void setPrecoUnidade(Double precoUnidade) {
		this.precoUnidade = precoUnidade;
	}

	public void setPrecoUnidadeFormatado(String precoUnidadeFormatado) {
		this.precoUnidadeFormatado = precoUnidadeFormatado;
	}

	public void setPrecoUnidadeIPI(Double precoUnidadeIPI) {
		this.precoUnidadeIPI = precoUnidadeIPI;
	}

	public void setPrecoUnidadeIPIFormatado(String precoUnidadeIPIFormatado) {
		this.precoUnidadeIPIFormatado = precoUnidadeIPIFormatado;
	}

	public void setQuantidade(Integer quantidade) {
		this.quantidade = quantidade;
	}

}
