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
import br.com.plastecno.service.constante.TipoVenda;
import br.com.plastecno.service.validacao.annotation.InformacaoValidavel;

@Entity
@Table(name = "tb_item_pedido", schema = "vendas")
@InformacaoValidavel
public class ItemPedido extends Item {
	/**
	 * 
	 */
	private static final long serialVersionUID = 3602081055672681943L;

	@Column(name = "aliquota_icms")
	@InformacaoValidavel(numerico = true, positivo = true, nomeExibicao = "Alíquota ICMS")
	private Double aliquotaICMS;

	@Column(name = "aliquota_ipi")
	@InformacaoValidavel(numerico = true, positivo = true, nomeExibicao = "Alíquota IPI")
	private Double aliquotaIPI;

	private Double comprimento;

	@Column(name = "descricao_peca")
	@InformacaoValidavel(intervalo = { 1, 100 }, nomeExibicao = "Descrição do item do pedido")
	private String descricaoPeca;

	@Column(name = "item_encomendado")
	private boolean encomendado;

	@Enumerated(EnumType.ORDINAL)
	@Column(name = "id_forma_material")
	@InformacaoValidavel(obrigatorio = true, nomeExibicao = "Forma do material do item do pedido")
	private FormaMaterial formaMaterial;

	@Id
	@SequenceGenerator(name = "itemPedidoSequence", sequenceName = "vendas.seq_item_pedido_id", allocationSize = 1, initialValue = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "itemPedidoSequence")
	private Integer id;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_material", referencedColumnName = "id", nullable = false)
	@InformacaoValidavel(relacionamentoObrigatorio = true, nomeExibicao = "Material associado ao pedido")
	private Material material;

	private Double comissao;

	@Column(name = "medida_externa")
	private Double medidaExterna;

	@Column(name = "medida_interna")
	private Double medidaInterna;

	@Transient
	private Integer idProprietario;

	@Transient
	private Integer idPedido;

	@Transient
	private String nomeProprietario;

	@Transient
	private String sobrenomeProprietario;

	@Transient
	private String nomeRepresentada;

	@ManyToOne
	@JoinColumn(name = "id_pedido", referencedColumnName = "id", nullable = false)
	@InformacaoValidavel(cascata = true, nomeExibicao = "Pedido associado ao item")
	private Pedido pedido;

	@Column(name = "preco_unidade")
	@InformacaoValidavel(obrigatorio = true, numerico = true, estritamentePositivo = false, nomeExibicao = "Preço da unidade item do pedido")
	private Double precoUnidade;

	@Transient
	private String precoUnidadeFormatado;

	@Column(name = "preco_unidade_ipi")
	@InformacaoValidavel(obrigatorio = true, numerico = true, estritamentePositivo = false, nomeExibicao = "Preço da unidade item do pedido com IPI")
	private Double precoUnidadeIPI;

	@Transient
	private String precoUnidadeIPIFormatado;

	@Column(name = "preco_venda")
	@InformacaoValidavel(obrigatorio = true, numerico = true, positivo = true, nomeExibicao = "Preço de venda do item do pedido")
	private Double precoVenda;

	@InformacaoValidavel(obrigatorio = true, numerico = true, positivo = true, nomeExibicao = "Quantidade de itens do pedido")
	private Integer quantidade;

	@Column(name = "quantidade_recepcionada")
	private Integer quantidadeRecepcionada;

	@Column(name = "quantidade_reservada")
	private Integer quantidadeReservada;

	@Column(name = "item_recebido")
	private boolean recebido;

	private Integer sequencial;

	@Enumerated(EnumType.ORDINAL)
	@Column(name = "id_tipo_venda")
	@InformacaoValidavel(obrigatorio = true, nomeExibicao = "Tipo de venda do item do pedido")
	private TipoVenda tipoVenda;

	public ItemPedido() {
	}

	public ItemPedido(FormaMaterial formaMaterial) {
		this.formaMaterial = formaMaterial;
	}

	public ItemPedido(Integer id) {
		this.id = id;
	}

	public ItemPedido(Integer id, Double comissao, Integer idPedido, Integer idProprietario, String nomeProprietario,
			String sobrenomeProprietario, Double precoUnidade, Integer quantidade) {
		this.id = id;
		this.comissao = comissao;
		this.idPedido = idPedido;
		this.idProprietario = idProprietario;
		this.nomeProprietario = nomeProprietario;
		this.sobrenomeProprietario = sobrenomeProprietario;
		this.precoUnidade = precoUnidade;
		this.quantidade = quantidade;
	}

	public void addQuantidadeReservada(Integer quantidadeReservada) {
		setQuantidadeReservada(getQuantidadeReservada() + quantidadeReservada);
	}

	public double calcularPrecoTotal() {
		return this.quantidade != null && this.precoVenda != null ? this.quantidade * this.precoVenda : 0d;
	}

	public double calcularValorComissionado() {
		return getComissao() * getPrecoItem();
	}

	@Override
	public ItemPedido clone() {
		ItemPedido clone;
		try {
			clone = (ItemPedido) super.clone();
			clone.setId(null);
			clone.setPedido(null);
			clone.setRecebido(false);
			clone.setQuantidadeReservada(0);
			clone.setQuantidadeRecepcionada(0);
			return clone;
		} catch (CloneNotSupportedException e) {
			throw new IllegalStateException("Falha ao clonar o item de pedido " + getId(), e);
		}
	}

	public boolean contemAlgumaReserva() {
		return quantidadeReservada != null && quantidadeReservada > 0;
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

	public Double getComissao() {
		return comissao;
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

	public Integer getIdPedido() {
		return idPedido;
	}

	public Integer getIdProprietario() {
		return idProprietario;
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

	public String getNomeProprietario() {
		return nomeProprietario;
	}

	public String getNomeRepresentada() {
		return nomeRepresentada;
	}

	public Pedido getPedido() {
		return pedido;
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

	public Double getPrecoVenda() {
		return precoVenda;
	}

	public Integer getQuantidade() {
		return quantidade == null ? 0 : quantidade;
	}

	public int getQuantidadeEncomendada() {
		return getQuantidade() - getQuantidadeReservada();
	}

	public Integer getQuantidadeRecepcionada() {
		return quantidadeRecepcionada;
	}

	public Integer getQuantidadeReservada() {
		return quantidadeReservada == null ? 0 : quantidadeReservada;
	}

	public Integer getSequencial() {
		return sequencial;
	}

	public String getSobrenomeProprietario() {
		return sobrenomeProprietario;
	}

	public TipoVenda getTipoVenda() {
		return tipoVenda;
	}

	public boolean isEncomendado() {
		return encomendado;
	}

	public boolean isNovo() {
		return this.id == null;
	}

	public boolean isRecebido() {
		return recebido;
	}

	public boolean isTodasUnidadesRecepcionadas() {
		return quantidade != null && quantidade.equals(quantidadeRecepcionada);
	}

	public boolean isTodasUnidadesReservadas() {
		return quantidade != null && quantidade.equals(quantidadeReservada);
	}

	public boolean isVendaKilo() {
		return TipoVenda.KILO.equals(this.tipoVenda);
	}

	public void setAliquotaICMS(Double aliquotaICMS) {
		this.aliquotaICMS = aliquotaICMS;
	}

	public void setAliquotaIPI(Double aliquotaIPI) {
		this.aliquotaIPI = aliquotaIPI;
	}

	public void setComissao(Double comissao) {
		this.comissao = comissao;
	}

	public void setComprimento(Double comprimento) {
		this.comprimento = comprimento;
	}

	public void setDescricaoPeca(String descricaoPeca) {
		this.descricaoPeca = descricaoPeca;
	}

	public void setEncomendado(boolean encomendado) {
		this.encomendado = encomendado;
	}

	public void setFormaMaterial(FormaMaterial formaMaterial) {
		this.formaMaterial = formaMaterial;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public void setIdPedido(Integer idPedido) {
		this.idPedido = idPedido;
	}

	public void setIdProprietario(Integer idProprietario) {
		this.idProprietario = idProprietario;
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

	public void setNomeProprietario(String nomeProprietario) {
		this.nomeProprietario = nomeProprietario;
	}

	public void setNomeRepresentada(String nomeRepresentada) {
		this.nomeRepresentada = nomeRepresentada;
	}

	public void setPedido(Pedido pedido) {
		this.pedido = pedido;
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

	public void setPrecoVenda(Double precoVenda) {
		this.precoVenda = precoVenda;
	}

	public void setQuantidade(Integer quantidade) {
		this.quantidade = quantidade;
	}

	public void setQuantidadeRecepcionada(Integer quantidadeRecepcionada) {
		this.quantidadeRecepcionada = quantidadeRecepcionada;
	}

	public void setQuantidadeReservada(Integer quantidadeReservada) {
		this.quantidadeReservada = quantidadeReservada;
	}

	public void setRecebido(boolean recebido) {
		this.recebido = recebido;
	}

	public void setSequencial(Integer sequencial) {
		this.sequencial = sequencial;
	}

	public void setSobrenomeProprietario(String sobrenomeProprietario) {
		this.sobrenomeProprietario = sobrenomeProprietario;
	}

	public void setTipoVenda(TipoVenda tipoVenda) {
		this.tipoVenda = tipoVenda;
	}
}
