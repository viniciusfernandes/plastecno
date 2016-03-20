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
import br.com.plastecno.service.constante.TipoDocumento;
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

	@Transient
	private Double aliquotaReajuste;

	private Double comprimento;
	@Column(name = "descricao_peca")
	@InformacaoValidavel(trim = true, intervalo = { 1, 100 }, nomeExibicao = "Descrição do item do estoque")
	private String descricaoPeca;

	@Enumerated(EnumType.ORDINAL)
	@Column(name = "id_forma_material")
	@InformacaoValidavel(obrigatorio = true, nomeExibicao = "Forma do material do item do estoque")
	private FormaMaterial formaMaterial;

	@Id
	@SequenceGenerator(name = "itemEstoqueSequence", sequenceName = "vendas.seq_item_estoque_id", allocationSize = 1, initialValue = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "itemEstoqueSequence")
	private Integer id;

	@Column(name = "margem_minima_lucro")
	private Double margemMinimaLucro;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_material", referencedColumnName = "id", nullable = false)
	@InformacaoValidavel(relacionamentoObrigatorio = true, nomeExibicao = "Material associado ao item do estoque")
	private Material material;

	@Column(name = "medida_externa")
	private Double medidaExterna;

	@Column(name = "medida_interna")
	private Double medidaInterna;

	@Column(name = "ncm")
	@InformacaoValidavel(obrigatorio = false, tipoDocumento = TipoDocumento.NCM)
	private String ncm;

	@Column(name = "preco_medio")
	@InformacaoValidavel(obrigatorio = true, numerico = true, positivo = true, nomeExibicao = "Preço de médio de compra do item de estoque")
	private Double precoMedio;

	@Column(name = "preco_medio_fatoricms")
	@InformacaoValidavel(obrigatorio = true, numerico = true, positivo = true, nomeExibicao = "Preço de médio com fator ICMS")
	private Double precoMedioFatorICMS;

	@Transient
	private Double precoMinimo;

	@Column(name = "quantidade")
	@InformacaoValidavel(obrigatorio = true, numerico = true, positivo = true, nomeExibicao = "Quantidade de itens do estoque")
	private Integer quantidade = 0;

	@Column(name = "quantidade_minima")
	private Integer quantidadeMinima;

	@Transient
	private String siglaMaterial;

	public ItemEstoque() {
	}

	public ItemEstoque(FormaMaterial formaMaterial) {
		this.formaMaterial = formaMaterial;
	}

	public ItemEstoque(Integer idItemEstoque) {
		this.id = idItemEstoque;
	}

	public ItemEstoque(Integer id, Double precoMedio, Double aliquotaICMS) {
		this.id = id;
		this.precoMedio = precoMedio;
		this.aliquotaICMS = aliquotaICMS;
	}

	public ItemEstoque(Integer id, FormaMaterial formaMaterial, String descricaoPeca, String siglaMaterial,
			Double medidaExterna, Double medidaInterna, Double comprimento, Double precoMedio, Double precoMedioFatorCIMS,
			Double margemMinimaLucro, Integer quantidade, Integer quantidadeMinima, Double aliquotaIPI) {
		this.comprimento = comprimento;
		this.descricaoPeca = descricaoPeca;
		this.formaMaterial = formaMaterial;
		this.id = id;
		this.siglaMaterial = siglaMaterial;
		this.medidaExterna = medidaExterna;
		this.medidaInterna = medidaInterna;
		this.precoMedio = precoMedio;
		this.quantidade = quantidade;
		this.margemMinimaLucro = margemMinimaLucro;
		this.quantidadeMinima = quantidadeMinima;
		this.aliquotaIPI = aliquotaIPI;
		this.precoMedioFatorICMS = precoMedioFatorCIMS;
	}

	public double calcularPrecoTotal() {
		return this.quantidade != null && this.precoMedio != null ? this.quantidade * this.precoMedio : 0d;
	}

	@Override
	public ItemEstoque clone() {
		ItemEstoque clone;
		try {
			clone = (ItemEstoque) super.clone();
			// Note que ao clonar devemos cancelar o ID pois o clonagem representa uma
			// regra de negocios, assim a entidade resultante sera incluida na sessao
			// de persistencia e deve ser uma nova entidade
			clone.setId(null);
			return clone;
		} catch (CloneNotSupportedException e) {
			throw new IllegalStateException("Falha ao clonar o item de estoque " + getId(), e);
		}
	}

	public boolean contemLargura() {
		return this.formaMaterial != null && this.formaMaterial.contemLargura();
	}

	public boolean contemLimiteMinimo() {
		return quantidadeMinima != null && quantidadeMinima > 0 && margemMinimaLucro != null && margemMinimaLucro > 0;
	}

	public void copiar(ItemEstoque item) {
		setAliquotaICMS(item.getAliquotaICMS());
		setAliquotaIPI(item.getAliquotaIPI());
		setQuantidade(item.getQuantidade());
		setPrecoMedio(item.getPrecoMedio());
		setQuantidadeMinima(item.getQuantidadeMinima());
		setMargemMinimaLucro(item.getMargemMinimaLucro());
	}

	public Double getAliquotaICMS() {
		return aliquotaICMS;
	}

	public Double getAliquotaIPI() {
		return aliquotaIPI;
	}

	public Double getAliquotaReajuste() {
		return aliquotaReajuste;
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

	public Double getMargemMinimaLucro() {
		return margemMinimaLucro;
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

	public String getNcm() {
		return ncm;
	}

	public Double getPrecoMedio() {
		return precoMedio;
	}

	public Double getPrecoMedioFatorICMS() {
		return precoMedioFatorICMS;
	}

	public Double getPrecoMinimo() {
		return precoMinimo;
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

	// Metodo criado para facilitar teste unitario
	public boolean isItemEscasso() {
		return quantidade != null && quantidadeMinima != null && quantidade < quantidadeMinima;
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

	public void setAliquotaReajuste(Double aliquotaReajuste) {
		this.aliquotaReajuste = aliquotaReajuste;
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

	public void setMargemMinimaLucro(Double margemMinimaLucro) {
		this.margemMinimaLucro = margemMinimaLucro;
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

	public void setNcm(String ncm) {
		this.ncm = ncm;
	}

	public void setPrecoMedio(Double precoMedio) {
		this.precoMedio = precoMedio;
	}

	public void setPrecoMedioFatorICMS(Double precoMedioFatorICMS) {
		this.precoMedioFatorICMS = precoMedioFatorICMS;
	}

	public void setPrecoMinimo(Double precoMinimo) {
		this.precoMinimo = precoMinimo;
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

}
