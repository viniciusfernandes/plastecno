package br.com.plastecno.service.entity;

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

	@Column(name = "aliquota_comissao")
	private Double aliquotaComissao;

	@Transient
	private String aliquotaComissaoFormatado;

	@Transient
	private Double aliquotaComissaoPedido;

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

	@Transient
	private Integer idPedido;

	@Transient
	private Integer idProprietario;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_material", referencedColumnName = "id", nullable = false)
	@InformacaoValidavel(relacionamentoObrigatorio = true, nomeExibicao = "Material associado ao pedido")
	private Material material;

	@Column(name = "medida_externa")
	private Double medidaExterna;

	@Column(name = "medida_interna")
	private Double medidaInterna;

	@Transient
	private String nomeProprietario;

	@Transient
	private Date dataEntrega;

	@Transient
	private String nomeRepresentada;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_pedido", referencedColumnName = "id", nullable = false)
	@InformacaoValidavel(relacionamentoObrigatorio = true, nomeExibicao = "Pedido associado ao item")
	private Pedido pedido;

	@Column(name = "preco_custo")
	@InformacaoValidavel(obrigatorio = false, numerico = true, positivo = true, nomeExibicao = "Preço de custo do item do pedido")
	private Double precoCusto;

	@Transient
	private String precoCustoItemFormatado;

	@Column(name = "preco_minimo")
	private Double precoMinimo;

	@Transient
	private String precoMinimoFormatado;

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

	@Transient
	private String sobrenomeProprietario;

	@Enumerated(EnumType.ORDINAL)
	@Column(name = "id_tipo_venda")
	@InformacaoValidavel(obrigatorio = true, nomeExibicao = "Tipo de venda do item do pedido")
	private TipoVenda tipoVenda;

	@Column(name = "valor_comissionado")
	private Double valorComissionado;

	@Transient
	private String valorComissionadoFormatado;

	public ItemPedido() {
	}

	public ItemPedido(Double precoUnidade, Integer quantidade, Double aliquotaIPI, Double aliquotaICMS) {
		this.precoUnidade = precoUnidade;
		this.quantidade = quantidade;
		this.aliquotaIPI = aliquotaIPI;
		this.aliquotaICMS = aliquotaICMS;
	}

	public ItemPedido(Double precoUnidade, Integer quantidade, Double aliquotaIPI, Double aliquotaICMS,
			Double valorComissionado, Double aliquotaComissaoPedido) {
		this(precoUnidade, quantidade, aliquotaIPI, aliquotaICMS);
		this.valorComissionado = valorComissionado;
		this.aliquotaComissaoPedido = aliquotaComissaoPedido;
	}

	public ItemPedido(FormaMaterial formaMaterial) {
		this.formaMaterial = formaMaterial;
	}

	public ItemPedido(Integer id) {
		this.id = id;
	}

	// Construtor para relatorio de comissao
	public ItemPedido(Integer id, Integer sequencial, Integer idPedido, Integer idProprietario, String nomeProprietario,
			String sobrenomeProprietario, Double precoUnidade, Double precoCusto, Integer quantidade,
			Double valorComissionado, Double aliquotaComissiao, FormaMaterial formaMaterial, String siglaMaterial,
			String descricaoMaterial, String descricaoPeca, Double medidaExterna, Double medidaInterna, Double comprimento) {
		this.id = id;
		this.sequencial = sequencial;
		this.idPedido = idPedido;
		this.idProprietario = idProprietario;
		this.precoUnidade = precoUnidade;
		this.precoCusto = precoCusto;
		this.quantidade = quantidade;
		this.valorComissionado = valorComissionado;
		this.aliquotaComissao = aliquotaComissiao;
		this.formaMaterial = formaMaterial;
		this.material = new Material(null, siglaMaterial, descricaoMaterial);
		this.descricaoPeca = descricaoPeca;
		this.medidaExterna = medidaExterna;
		this.medidaInterna = medidaInterna;
		this.comprimento = comprimento;
		this.nomeProprietario = nomeProprietario;
		this.sobrenomeProprietario = sobrenomeProprietario;
	}

	public ItemPedido(Integer id, Integer sequencial, Integer idPedido, String nomeProprietario, Integer quantidade,
			Integer quantidadeRecepcionada, Double precoUnidade, String nomeRepresentada, Date dataEntrega,
			FormaMaterial formaMaterial, String siglaMaterial, String descricaoMaterial, String descricaoPeca,
			Double medidaExterna, Double medidaInterna, Double comprimento) {

		this(id, sequencial, idPedido, null, nomeProprietario, "", precoUnidade, null, quantidade, (Double) null,
				(Double) null, formaMaterial, siglaMaterial, descricaoMaterial, descricaoPeca, medidaExterna, medidaInterna,
				comprimento);
		this.nomeRepresentada = nomeRepresentada;
		this.dataEntrega = dataEntrega;
		this.quantidadeRecepcionada = quantidadeRecepcionada;
	}

	public void addQuantidadeReservada(Integer quantidadeReservada) {
		setQuantidadeReservada(getQuantidadeReservada() + quantidadeReservada);
	}

	public double calcularPrecoTotal() {
		return this.quantidade != null && this.precoVenda != null ? this.quantidade * this.precoVenda : 0d;
	}

	@Override
	public ItemPedido clone() {
		ItemPedido clone;
		try {
			// Note que ao clonar devemos cancelar o ID pois o clonagem representa uma
			// regra de negocios, assim a entidade resultante sera incluida na sessao
			// de persistencia e deve ser uma nova entidade. Ja as outras informacoes
			// de quantidade devem ser anuladas pois elas nao devem refletir no banco
			// de dados.
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

	public boolean contemValorComissionado() {
		return valorComissionado != null && aliquotaComissao != null;
	}

	public Double getAliquotaComissao() {
		return aliquotaComissao;
	}

	public String getAliquotaComissaoFormatado() {
		return aliquotaComissaoFormatado;
	}

	public Double getAliquotaComissaoPedido() {
		return aliquotaComissaoPedido;
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

	public Date getDataEntrega() {
		return dataEntrega;
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

	public double getPercentualComissao() {
		return aliquotaComissao == null ? 0 : aliquotaComissao * 100;
	}

	public Double getPrecoCusto() {
		return precoCusto;
	}

	public String getPrecoCustoItemFormatado() {
		return precoCustoItemFormatado;
	}

	public Double getPrecoMinimo() {
		return precoMinimo;
	}

	public String getPrecoMinimoFormatado() {
		return precoMinimoFormatado;
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

	public Double getValorComissaoPedido() {
		return aliquotaComissaoPedido;
	}

	public Double getValorComissionado() {
		return valorComissionado;
	}

	public String getValorComissionadoFormatado() {
		return valorComissionadoFormatado;
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

	public void setAliquotaComissao(Double aliquotaComissao) {
		this.aliquotaComissao = aliquotaComissao;
	}

	public void setAliquotaComissaoFormatado(String aliquotaComissaoFormatado) {
		this.aliquotaComissaoFormatado = aliquotaComissaoFormatado;
	}

	public void setAliquotaComissaoPedido(Double aliquotaComissaoPedido) {
		this.aliquotaComissaoPedido = aliquotaComissaoPedido;
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

	public void setDataEntrega(Date dataEntrega) {
		this.dataEntrega = dataEntrega;
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

	public void setPrecoCusto(Double precoCusto) {
		this.precoCusto = precoCusto;
	}

	public void setPrecoCustoItemFormatado(String precoCustoFormatado) {
		this.precoCustoItemFormatado = precoCustoFormatado;
	}

	public void setPrecoMinimo(Double precoMinimo) {
		this.precoMinimo = precoMinimo;
	}

	public void setPrecoMinimoFormatado(String precoMinimoFormatado) {
		this.precoMinimoFormatado = precoMinimoFormatado;
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

	public void setValorComissaoPedido(Double valorComissaoPedido) {
		this.aliquotaComissaoPedido = valorComissaoPedido;
	}

	public void setValorComissionado(Double valorComissionado) {
		this.valorComissionado = valorComissionado;
	}

	public void setValorComissionadoFormatado(String valorComissionadoFormatado) {
		this.valorComissionadoFormatado = valorComissionadoFormatado;
	}

}
