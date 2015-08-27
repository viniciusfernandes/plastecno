package br.com.plastecno.service.entity;

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
import br.com.plastecno.service.validacao.annotation.InformacaoValidavel;

@Entity
@Table(name = "tb_item_estoque", schema = "vendas")
@InformacaoValidavel
public class ItemEstoque extends Item {

	/**
	 * 
	 */
	private static final long serialVersionUID = 589189336309859982L;

	@Column(name = "aliquota_icms")
	@InformacaoValidavel(numerico = true, positivo = true, nomeExibicao = "Alíquota ICMS")
	private Double aliquotaICMS;

	@Column(name = "aliquota_ipi")
	@InformacaoValidavel(numerico = true, positivo = true, nomeExibicao = "Alíquota IPI")
	private Double aliquotaIPI;

	private Double comprimento;

	@Column(name = "descricao_peca")
	@InformacaoValidavel(intervalo = { 1, 100 }, nomeExibicao = "Descrição do item do estoque")
	private String descricaoPeca;

	@Enumerated(EnumType.ORDINAL)
	@Column(name = "id_forma_material")
	@InformacaoValidavel(obrigatorio = true, nomeExibicao = "Forma do material do item do estoque")
	private FormaMaterial formaMaterial;
	@Id
	@SequenceGenerator(name = "itemEstoqueSequence", sequenceName = "vendas.seq_item_estoque_id", allocationSize = 1, initialValue = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "itemEstoqueSequence")
	private Integer id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_limite_minimo_estoque", referencedColumnName = "id", nullable = false)
	private LimiteMinimoEstoque limiteMinimoEstoque;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_material", referencedColumnName = "id", nullable = false)
	@InformacaoValidavel(relacionamentoObrigatorio = true, nomeExibicao = "Material associado ao item do estoque")
	private Material material;

	@Column(name = "medida_externa")
	private Double medidaExterna;

	@Column(name = "medida_interna")
	private Double medidaInterna;

	@Column(name = "preco_medio")
	@InformacaoValidavel(obrigatorio = true, numerico = true, positivo = true, nomeExibicao = "Preço de médio de compra do item de estoque")
	private Double precoMedio;

	@Transient
	private Double taxaMinima;

	@InformacaoValidavel(obrigatorio = true, numerico = true, positivo = true, nomeExibicao = "Quantidade de itens do estoque")
	private Integer quantidade = 0;

	@Transient
	private String siglaMaterial;

	@Transient
	private Double precoSugerido;

	@Transient
	private Integer quantidadeMinima;

	public ItemEstoque() {
	}

	public ItemEstoque(FormaMaterial formaMaterial) {
		this.formaMaterial = formaMaterial;
	}

	public ItemEstoque(Integer idItemEstoque) {
		this.id = idItemEstoque;
	}

	public ItemEstoque(Integer id, FormaMaterial formaMaterial, String descricaoPeca, String siglaMaterial,
			Double medidaExterna, Double medidaInterna, Double comprimento, Double precoMedio, Double taxaMinima,
			Integer quantidade, Integer quantidadeMinima) {
		this.comprimento = comprimento;
		this.descricaoPeca = descricaoPeca;
		this.formaMaterial = formaMaterial;
		this.id = id;
		this.siglaMaterial = siglaMaterial;
		this.medidaExterna = medidaExterna;
		this.medidaInterna = medidaInterna;
		this.precoMedio = precoMedio;
		this.quantidade = quantidade;
		this.taxaMinima = taxaMinima;
		this.quantidadeMinima = quantidadeMinima;
	}

	public double calcularPrecoTotal() {
		return this.quantidade != null && this.precoMedio != null ? this.quantidade * this.precoMedio : 0d;
	}

	@Override
	public ItemEstoque clone() {
		ItemEstoque clone;
		try {
			clone = (ItemEstoque) super.clone();
			clone.setId(null);
			return clone;
		} catch (CloneNotSupportedException e) {
			throw new IllegalStateException("Falha ao clonar o item de estoque " + getId(), e);
		}
	}

	public boolean contemLargura() {
		return this.formaMaterial != null && this.formaMaterial.contemLargura();
	}

	public Double getAliquotaICMS() {
		return aliquotaICMS;
	}

	public Double getAliquotaIPI() {
		return aliquotaIPI;
	}

	public Double getComprimento() {
		return comprimento;
	}

	public String getDescricaoPeca() {
		return descricaoPeca;
	}

	public FormaMaterial getFormaMaterial() {
		return formaMaterial;
	}

	public Integer getId() {
		return id;
	}

	public LimiteMinimoEstoque getLimiteMinimoEstoque() {
		return limiteMinimoEstoque;
	}

	public Material getMaterial() {
		return material;
	}

	public Double getMedidaExterna() {
		return medidaExterna;
	}

	public Double getMedidaInterna() {
		return medidaInterna;
	}

	public Double getPrecoMedio() {
		return precoMedio;
	}

	public Double getPrecoSugerido() {
		return precoSugerido;
	}

	@Override
	public Double getPrecoUnidade() {
		return getPrecoMedio();
	}

	@Override
	public Double getPrecoUnidadeIPI() {
		throw new UnsupportedOperationException();
	}

	public Double getPrecoVenda() {
		return precoMedio;
	}

	public Integer getQuantidade() {
		return quantidade;
	}

	public Integer getQuantidadeMinima() {
		return quantidadeMinima;
	}

	public String getSiglaMaterial() {
		return siglaMaterial;
	}

	public Double getTaxaMinima() {
		return taxaMinima;
	}

	// Metodo criado para facilitar teste unitario
	public boolean isItemEscasso() {
		return limiteMinimoEstoque != null && quantidade != null && quantidade < limiteMinimoEstoque.getQuantidadeMinima();
	}

	public boolean isNovo() {
		return this.id == null;
	}

	public void setAliquotaICMS(Double aliquotaICMS) {
		this.aliquotaICMS = aliquotaICMS;
	}

	public void setAliquotaIPI(Double aliquotaIPI) {
		this.aliquotaIPI = aliquotaIPI;
	}

	public void setComprimento(Double comprimento) {
		this.comprimento = comprimento;
	}

	public void setDescricaoPeca(String descricaoPeca) {
		this.descricaoPeca = descricaoPeca;
	}

	public void setFormaMaterial(FormaMaterial formaMaterial) {
		this.formaMaterial = formaMaterial;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public void setLimiteMinimoEstoque(LimiteMinimoEstoque limiteMinimoEstoque) {
		this.limiteMinimoEstoque = limiteMinimoEstoque;
	}

	public void setMaterial(Material material) {
		this.material = material;
	}

	public void setMedidaExterna(Double medidaExterna) {
		this.medidaExterna = medidaExterna;
	}

	public void setMedidaInterna(Double medidaInterna) {
		this.medidaInterna = medidaInterna;
	}

	public void setPrecoMedio(Double precoMedio) {
		this.precoMedio = precoMedio;
	}

	public void setPrecoSugerido(Double precoSugerido) {
		this.precoSugerido = precoSugerido;
	}

	@Override
	public void setPrecoUnidade(Double precoUnidade) {
		setPrecoMedio(precoUnidade);
	}

	@Override
	public void setPrecoUnidadeIPI(Double precoUnidadeIPI) {
		throw new UnsupportedOperationException();
	}

	public void setQuantidade(Integer quantidade) {
		this.quantidade = quantidade;
	}

	public void setQuantidadeMinima(Integer quantidadeMinima) {
		this.quantidadeMinima = quantidadeMinima;
	}

	public void setSiglaMaterial(String siglaMaterial) {
		this.siglaMaterial = siglaMaterial;
	}

	public void setTaxaMinima(Double taxaMinima) {
		this.taxaMinima = taxaMinima;
	}

}
