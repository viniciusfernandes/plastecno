package br.com.plastecno.service.entity;

import javax.persistence.Column;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Transient;

import br.com.plastecno.service.constante.FormaMaterial;
import br.com.plastecno.service.constante.TipoVenda;
import br.com.plastecno.service.validacao.annotation.InformacaoValidavel;

@InformacaoValidavel
public class ItemPedidoBackup extends Item {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5375978076362373033L;

	@Id
	@SequenceGenerator(name = "itemPedidoSequence", sequenceName = "vendas.seq_item_pedido_id", allocationSize = 1, initialValue = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "itemPedidoSequence")
	private Integer id;

	@Transient
	private String precoItemFormatado;

	@Column(name = "item_recebido")
	private boolean recebido;

	@Column(name = "preco_venda")
	@InformacaoValidavel(obrigatorio = true, numerico = true, valorNegativo = false, nomeExibicao = "Preço de venda do item do pedido")
	private Double precoVenda;

	@Enumerated(EnumType.ORDINAL)
	@Column(name = "id_forma_material")
	@InformacaoValidavel(obrigatorio = true, nomeExibicao = "Forma do material do item do pedido")
	private FormaMaterial formaMaterial;

	@Enumerated(EnumType.ORDINAL)
	@Column(name = "id_tipo_venda")
	@InformacaoValidavel(obrigatorio = true, nomeExibicao = "Tipo de venda do item do pedido")
	private TipoVenda tipoVenda;

	@ManyToOne
	@JoinColumn(name = "id_pedido", referencedColumnName = "id", nullable = false)
	@InformacaoValidavel(cascata = true, nomeExibicao = "Pedido associado ao item")
	private Pedido pedido;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_material", referencedColumnName = "id", nullable = false)
	@InformacaoValidavel(relacionamentoObrigatorio = true, nomeExibicao = "Material associado ao pedido")
	private Material material;

	private Integer sequencial;

	@Transient
	private String precoVendaFormatado;

	@Transient
	private String nomeProprietario;

	@Transient
	private String nomeRepresentada;

	public ItemPedidoBackup() {
	}

	public ItemPedidoBackup(FormaMaterial formaMaterial) {
		this.formaMaterial = formaMaterial;
	}

	public double calcularPrecoTotal() {
		return getQuantidade() != null && this.precoVenda != null ? getQuantidade() * this.precoVenda : 0d;
	}

	@Override
	public ItemPedidoBackup clone() throws CloneNotSupportedException {
		ItemPedidoBackup clone = (ItemPedidoBackup) super.clone();
		clone.setId(null);
		clone.setPedido(null);
		return clone;
	}

	public boolean contemLargura() {
		return this.formaMaterial != null && this.formaMaterial.contemLargura();
	}

	public Integer getId() {
		return id;
	}

	public String getNomeProprietario() {
		return nomeProprietario;
	}

	public String getNomeRepresentada() {
		return nomeRepresentada;
	}

	public Pedido getPedido() {
		return pedido;
	}

	public String getPrecoItemFormatado() {
		return precoItemFormatado;
	}

	public Double getPrecoVenda() {
		return precoVenda;
	}

	public String getPrecoVendaFormatado() {
		return precoVendaFormatado;
	}

	public Integer getSequencial() {
		return sequencial;
	}

	public TipoVenda getTipoVenda() {
		return tipoVenda;
	}

	public boolean isFormaMaterialVazada() {
		return formaMaterial != null && formaMaterial.isFormaMaterialVazada();
	}

	public boolean isMedidaExternaIgualInterna() {
		return this.formaMaterial != null && this.formaMaterial.isMedidaExternaIgualInterna();
	}

	public boolean isNovo() {
		return this.id == null;
	}

	public boolean isPeca() {
		return FormaMaterial.PC.equals(this.formaMaterial);
	}

	public boolean isRecebido() {
		return recebido;
	}

	public boolean isVendaKilo() {
		return TipoVenda.KILO.equals(this.tipoVenda);
	}

	public void setFormaMaterial(FormaMaterial formaMaterial) {
		this.formaMaterial = formaMaterial;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public void setMaterial(Material material) {
		this.material = material;
	}

	public void setNomeProprietario(String nomeProprietario) {
		this.nomeProprietario = nomeProprietario;
	}

	public void setNomeRepresentada(String nomeRepresentada) {
		this.nomeRepresentada = nomeRepresentada;
	}

	public void setPedido(Pedido pedido) {
		this.pedido = pedido;
	}

	public void setPrecoItemFormatado(String precoItemFormatado) {
		this.precoItemFormatado = precoItemFormatado;
	}

	public void setPrecoVenda(Double precoVenda) {
		this.precoVenda = precoVenda;
	}

	public void setPrecoVendaFormatado(String precoVendaFormatado) {
		this.precoVendaFormatado = precoVendaFormatado;
	}

	public void setRecebido(boolean recebido) {
		this.recebido = recebido;
	}

	public void setSequencial(Integer sequencial) {
		this.sequencial = sequencial;
	}

	public void setTipoVenda(TipoVenda tipoVenda) {
		this.tipoVenda = tipoVenda;
	}

}
