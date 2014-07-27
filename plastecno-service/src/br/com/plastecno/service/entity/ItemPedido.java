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

@Entity
@Table(name="tb_item_pedido", schema="vendas")
@InformacaoValidavel
public class ItemPedido implements Serializable, Cloneable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3397681910683242844L;
	
	@Id
	@SequenceGenerator(name = "itemPedidoSequence", sequenceName = "vendas.seq_item_pedido_id", allocationSize=1, initialValue=1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "itemPedidoSequence")
	private Integer id;
	private Double comprimento;
	
	@Column(name="medida_interna")
	private Double medidaInterna;
	
	@Column(name="medida_externa")
	private Double medidaExterna;
	
	@InformacaoValidavel(obrigatorio= true, numerico=true, 
			valorNegativo=false, nomeExibicao="Quantidade de itens do pedido")
	private Integer quantidade;
	
	@Column(name="preco_unidade")
	@InformacaoValidavel(obrigatorio= true, numerico=true, 
		valorNaoNegativo=false, nomeExibicao="Preço da unidade item do pedido")
	private Double precoUnidade;
	
	@Column(name="preco_unidade_ipi")
	@InformacaoValidavel(obrigatorio= true, numerico=true, 
		valorNaoNegativo=false, nomeExibicao="Preço da unidade item do pedido com IPI")
	private Double precoUnidadeIPI;
	
	@Column(name="aliquota_icms")
	@InformacaoValidavel(numerico=true, 
	valorNaoNegativo=false, nomeExibicao="Alíquota ICMS")
	private Double aliquotaICMS;
	
	@Column(name="descricao_peca")
	@InformacaoValidavel(intervalo={1, 100}, nomeExibicao="Descrição do item do pedido")
	private String descricaoPeca;
	
	@Column(name="preco_venda")
	@InformacaoValidavel(obrigatorio= true, numerico=true, 
		valorNegativo=false, nomeExibicao="Preço de venda do item do pedido")
	private Double precoVenda;
	
	@Enumerated(EnumType.ORDINAL)
	@Column(name="id_forma_material")
	@InformacaoValidavel(obrigatorio=true, nomeExibicao="Forma do material do item do pedido")
	private FormaMaterial formaMaterial;
	
	@Enumerated(EnumType.ORDINAL)
	@Column(name="id_tipo_venda")
	@InformacaoValidavel(obrigatorio=true, nomeExibicao="Tipo de venda do item do pedido")
	private TipoVenda tipoVenda;

	@ManyToOne
	@JoinColumn(name = "id_pedido", referencedColumnName = "id", nullable = false)
	@InformacaoValidavel(cascata=true, nomeExibicao="Pedido associado ao item")
	private Pedido pedido;
	
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name = "id_material", referencedColumnName = "id", nullable = false)
	@InformacaoValidavel(relacionamentoObrigatorio=true, nomeExibicao="Material associado ao pedido")
	private Material material;
	
	@Transient
	private String precoUnidadeFormatado;
	
	@Transient
	private String precoUnidadeIPIFormatado;
	
	@Transient
	private String precoItemFormatado;
	
	@Transient
	private String precoVendaFormatado;
	
	@Transient
	private String aliquotaICMSFormatado;
	
	@Transient
	private String medidaExternaFomatada;
	
	@Transient
	private String medidaInternaFomatada;
	
	@Transient
	private String comprimentoFormatado;
	
	public ItemPedido() {}
	
	public ItemPedido(FormaMaterial formaMaterial) {
		this.formaMaterial = formaMaterial;
	}
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public Double getComprimento() {
		return comprimento;
	}
	public void setComprimento(Double comprimento) {
		this.comprimento = comprimento;
	}
	public Integer getQuantidade() {
		return quantidade;
	}
	public void setQuantidade(Integer quantidade) {
		this.quantidade = quantidade;
	}
	
	public FormaMaterial getFormaMaterial() {
		return formaMaterial;
	}
	public void setFormaMaterial(FormaMaterial formaMaterial) {
		this.formaMaterial = formaMaterial;
	}
	public TipoVenda getTipoVenda() {
		return tipoVenda;
	}
	public void setTipoVenda(TipoVenda tipoVenda) {
		this.tipoVenda = tipoVenda;
	}
	public Double getMedidaInterna() {
		return medidaInterna;
	}
	public void setMedidaInterna(Double medidaInterna) {
		this.medidaInterna = medidaInterna;
	}
	public Double getMedidaExterna() {
		return medidaExterna;
	}
	public void setMedidaExterna(Double medidaExterna) {
		this.medidaExterna = medidaExterna;
	}
	public Double getPrecoUnidade() {
		return precoUnidade;
	}
	public void setPrecoUnidade(Double precoUnidade) {
		this.precoUnidade = precoUnidade;
	}
	public Pedido getPedido() {
		return pedido;
	}
	public void setPedido(Pedido pedido) {
		this.pedido = pedido;
	}
	public Material getMaterial() {
		return material;
	}
	public void setMaterial(Material material) {
		this.material = material;
	}
	
	public String getDescricaoPeca() {
		return descricaoPeca;
	}
	public void setDescricaoPeca(String descricaoPeca) {
		this.descricaoPeca = descricaoPeca;
	}
	
	public Double getPrecoVenda() {
		return precoVenda;
	}
	
	public void setPrecoVenda(Double precoVenda) {
		this.precoVenda = precoVenda;
	}
	
	public Double getPrecoUnidadeIPI() {
		return precoUnidadeIPI;
	}

	public void setPrecoUnidadeIPI(Double precoUnidadeIPI) {
		this.precoUnidadeIPI = precoUnidadeIPI;
	}
	
	public Double getAliquotaICMS() {
		return aliquotaICMS;
	}

	public void setAliquotaICMS(Double aliquotaICMS) {
		this.aliquotaICMS = aliquotaICMS;
	}
	
	public String getPrecoItemFormatado() {
		return precoItemFormatado;
	}

	public void setPrecoItemFormatado(String precoItemFormatado) {
		this.precoItemFormatado = precoItemFormatado;
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
	
	public boolean isPeca() {
		return FormaMaterial.PC.equals(this.formaMaterial);
	}
	
	public boolean isVendaKilo() {
		return TipoVenda.KILO.equals(this.tipoVenda);
	}
	
	public double calcularPrecoTotal() {
		return this.quantidade != null && this.precoVenda != null ? 
				this.quantidade * this.precoVenda : 0d;
	}
	
	public boolean contemLargura() {
		return this.formaMaterial != null && this.formaMaterial.contemLargura();
	}
	
	public boolean isMedidaExternaIgualInterna(){
		return this.formaMaterial != null && this.formaMaterial.isMedidaExternaIgualInterna();
	}

	public String getPrecoUnidadeFormatado() {
		return precoUnidadeFormatado;
	}

	public void setPrecoUnidadeFormatado(String precoUnidadeFormatado) {
		this.precoUnidadeFormatado = precoUnidadeFormatado;
	}

	public String getPrecoUnidadeIPIFormatado() {
		return precoUnidadeIPIFormatado;
	}

	public void setPrecoUnidadeIPIFormatado(String precoUnidadeIPIFormatado) {
		this.precoUnidadeIPIFormatado = precoUnidadeIPIFormatado;
	}

	public String getPrecoVendaFormatado() {
		return precoVendaFormatado;
	}

	public void setPrecoVendaFormatado(String precoVendaFormatado) {
		this.precoVendaFormatado = precoVendaFormatado;
	}

	public String getAliquotaICMSFormatado() {
		return aliquotaICMSFormatado;
	}

	public void setAliquotaICMSFormatado(String aliquotaICMSFormatado) {
		this.aliquotaICMSFormatado = aliquotaICMSFormatado;
	}

	public String getMedidaExternaFomatada() {
		if (medidaExternaFomatada == null) {
			return " _ ";
		}
		return medidaExternaFomatada;
	}

	public void setMedidaExternaFomatada(String medidaExternaFomatada) {
		this.medidaExternaFomatada = medidaExternaFomatada;
	}

	public String getMedidaInternaFomatada() {
		if (medidaInternaFomatada == null) {
			return " _ ";
		}
		return medidaInternaFomatada;
	}

	public void setMedidaInternaFomatada(String medidaInternaFomatada) {
		this.medidaInternaFomatada = medidaInternaFomatada;
	}

	public String getComprimentoFormatado() {
		if (comprimentoFormatado == null) {
			return " _ ";
		}
		return comprimentoFormatado;
	}

	public void setComprimentoFormatado(String comprimentoFormatado) {
		this.comprimentoFormatado = comprimentoFormatado;
	}
	
	public boolean isFormaMaterialVazada() {
		return formaMaterial != null && formaMaterial.isFormaMaterialVazada();
	}
	
	public double getPrecoItem() {
		if(precoUnidade == null|| quantidade == null) {
			return 0d;
		}
		
		return precoUnidade * quantidade;
	}
	
	public int getAliquotaIPI() {
		if(precoUnidade == null || precoUnidadeIPI == null) {
			return 0;
		}
		
		return (int) ((precoUnidadeIPI/precoUnidade - 1d) * 100);
	}
	
	@Override
	public ItemPedido clone() throws CloneNotSupportedException{
		ItemPedido clone = (ItemPedido) super.clone();
		clone.setId(null);
		clone.setPedido(null);
		return clone;
	}
}
