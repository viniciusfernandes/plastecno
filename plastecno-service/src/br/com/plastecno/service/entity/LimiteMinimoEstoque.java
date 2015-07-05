package br.com.plastecno.service.entity;

import java.util.ArrayList;
import java.util.List;

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
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;

import br.com.plastecno.service.constante.FormaMaterial;
import br.com.plastecno.service.validacao.annotation.InformacaoValidavel;

@Entity
@Table(name = "tb_limite_minimo_estoque", schema = "vendas")
@InformacaoValidavel
public class LimiteMinimoEstoque {
	private Double comprimento;

	@Transient
	private String comprimentoFormatado;

	@Enumerated(EnumType.ORDINAL)
	@Column(name = "id_forma_material")
	@InformacaoValidavel(obrigatorio = true, nomeExibicao = "Forma do material do limite minimo")
	private FormaMaterial formaMaterial;
	@Id
	@SequenceGenerator(name = "itemEstoqueSequence", sequenceName = "vendas.seq_limite_minimo_estoque_id", allocationSize = 1, initialValue = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "itemEstoqueSequence")
	private Integer id;

	// Estamos utilzando o cascade merge apenas para associar o item de estoque ao
	// limite minimo
	@OneToMany(mappedBy = "limiteMinimoEstoque", fetch = FetchType.LAZY)
	private List<ItemEstoque> listaItemEstoque;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_material", referencedColumnName = "id", nullable = false)
	@InformacaoValidavel(relacionamentoObrigatorio = true, nomeExibicao = "Material do limite minimo")
	private Material material;

	@Column(name = "medida_externa")
	private Double medidaExterna;

	@Transient
	private String medidaExternaFomatada;

	@Column(name = "medida_interna")
	private Double medidaInterna;

	@Transient
	private String medidaInternaFomatada;

	@InformacaoValidavel(obrigatorio = true, numerico = true, estritamentePositivo = true, nomeExibicao = "Quantidade do limite minimo")
	@Column(name = "quantidade_minima")
	private Integer quantidadeMinima;

	public void addItemEstoque(ItemEstoque itemEstoque) {
		if (listaItemEstoque == null) {
			setListaItemEstoque(new ArrayList<ItemEstoque>());
		}
		listaItemEstoque.add(itemEstoque);
		itemEstoque.setLimiteMinimoEstoque(this);
	}

	public boolean contemQuantidadeMinima() {
		return quantidadeMinima != null && quantidadeMinima > 0;
	}

	public Double getComprimento() {
		return comprimento;
	}

	public String getComprimentoFormatado() {
		return comprimentoFormatado;
	}

	public String getDescricao() {

		StringBuilder descricao = new StringBuilder();
		if (getMaterial() != null) {
			descricao.append(getFormaMaterial());
			descricao.append(" - ");
			descricao.append(getMaterial().getDescricao() == null ? " " : getMaterial().getDescricao());
			descricao.append(" - ");
		}

		if (getMedidaExterna() != null) {
			descricao.append(getMedidaExternaFomatada());
			descricao.append(" X ");
		}

		if (getMedidaInterna() != null) {
			descricao.append(getMedidaInternaFomatada());
			descricao.append(" X ");
		}

		if (getComprimento() != null) {
			descricao.append(getComprimentoFormatado());
		}
		
		descricao.append(" mm");
		return descricao.toString();
	}

	public FormaMaterial getFormaMaterial() {
		return formaMaterial;
	}

	public Integer getId() {
		return id;
	}

	public List<ItemEstoque> getListaItemEstoque() {
		return listaItemEstoque;
	}

	public Material getMaterial() {
		return material;
	}

	public Double getMedidaExterna() {
		return medidaExterna;
	}

	public String getMedidaExternaFomatada() {
		return medidaExternaFomatada;
	}

	public Double getMedidaInterna() {
		return medidaInterna;
	}

	public String getMedidaInternaFomatada() {
		return medidaInternaFomatada;
	}

	public Integer getQuantidadeMinima() {
		return quantidadeMinima;
	}

	public void setComprimento(Double comprimento) {
		this.comprimento = comprimento;
	}

	public void setComprimentoFormatado(String comprimentoFormatado) {
		this.comprimentoFormatado = comprimentoFormatado;
	}

	public void setFormaMaterial(FormaMaterial formaMaterial) {
		this.formaMaterial = formaMaterial;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public void setListaItemEstoque(List<ItemEstoque> listaItemEstoque) {
		this.listaItemEstoque = listaItemEstoque;
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

	public void setQuantidadeMinima(Integer quantidadeMinima) {
		this.quantidadeMinima = quantidadeMinima;
	}
}
