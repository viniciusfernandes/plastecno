package br.com.svr.service.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;

import br.com.svr.service.constante.FormaMaterial;
import br.com.svr.service.validacao.annotation.InformacaoValidavel;

@Entity
@Table(name = "tb_comissao", schema = "vendas")
@InformacaoValidavel
public class Comissao implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3780636031987775613L;

	@InformacaoValidavel(obrigatorio = true, positivo = true, nomeExibicao = "Aliquota da comissão representação")
	@Column(name = "aliquota_representacao")
	private Double aliquotaRepresentacao;

	@Transient
	private String aliquotaRepresentacaoFormatada;

	@InformacaoValidavel(obrigatorio = true, positivo = true, nomeExibicao = "Aliquota da comissão revenda")
	@Column(name = "aliquota_revenda")
	private Double aliquotaRevenda;

	@Transient
	private String aliquotaRevendaFormatada;

	@Column(name = "data_fim")
	private Date dataFim;

	@Transient
	private String dataFimFormatado;

	@InformacaoValidavel(obrigatorio = true, nomeExibicao = "Data de início da comissão")
	@Column(name = "data_inicio")
	private Date dataInicio;

	@Transient
	private String dataInicioFormatado;

	@Transient
	private String descricaoProduto;

	@Transient
	private FormaMaterial formaMaterial;

	@Id
	@SequenceGenerator(name = "comissaoSequence", sequenceName = "vendas.seq_comissao_id", allocationSize = 1, initialValue = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "comissaoSequence")
	private Integer id;

	@Column(name = "id_forma_material")
	private Integer idFormaMaterial;

	@Column(name = "id_material")
	private Integer idMaterial;

	@Column(name = "id_vendedor")
	private Integer idVendedor;

	@Transient
	private String nomeVendedor;

	public Comissao() {
	}

	public Comissao(Double aliquotaRevenda, Date dataInicio) {
		this.aliquotaRevenda = aliquotaRevenda;
		this.dataInicio = dataInicio;
	}

	public Comissao(Double aliquotaRevenda, Double aliquotaRepresentacao, Date dataInicio) {
		this(null, null, aliquotaRevenda, aliquotaRepresentacao, dataInicio);
	}

	public Comissao(Integer id, Integer idVendedor, Double aliquotaRevenda, Double aliquotaRepresentacao, Date dataInicio) {
		this.id = id;
		this.idVendedor = idVendedor;
		this.aliquotaRevenda = aliquotaRevenda;
		this.aliquotaRepresentacao = aliquotaRepresentacao;
		this.dataInicio = dataInicio;
	}

	public Comissao(Integer id, Integer idFormaMaterial, Integer idMaterial, Double aliquotaRevenda,
			Double aliquotaRepresentacao, Date dataInicio) {
		this(id, null, aliquotaRevenda, aliquotaRepresentacao, dataInicio);
		this.idFormaMaterial = idFormaMaterial;
		this.idMaterial = idMaterial;
	}

	public Double getAliquotaRepresentacao() {
		return aliquotaRepresentacao;
	}

	public String getAliquotaRepresentacaoFormatada() {
		return aliquotaRepresentacaoFormatada;
	}

	public Double getAliquotaRevenda() {
		return aliquotaRevenda;
	}

	public String getAliquotaRevendaFormatada() {
		return aliquotaRevendaFormatada;
	}

	public Date getDataFim() {
		return dataFim;
	}

	public String getDataFimFormatado() {
		return dataFimFormatado;
	}

	public Date getDataInicio() {
		return dataInicio;
	}

	public String getDataInicioFormatado() {
		return dataInicioFormatado;
	}

	public String getDescricaoProduto() {
		return descricaoProduto;
	}

	public FormaMaterial getFormaMaterial() {
		return formaMaterial;
	}

	public Integer getId() {
		return id;
	}

	public Integer getIdFormaMaterial() {
		return idFormaMaterial;
	}

	public Integer getIdMaterial() {
		return idMaterial;
	}

	public Integer getIdVendedor() {
		return idVendedor;
	}

	public String getNomeVendedor() {
		return nomeVendedor;
	}

	public boolean isComissaoVendedor() {
		return idVendedor != null;
	}

	public boolean isVigente() {
		return dataFim == null;
	}

	public void setAliquotaRepresentacao(Double aliquotaRepresentacao) {
		this.aliquotaRepresentacao = aliquotaRepresentacao;
	}

	public void setAliquotaRepresentacaoFormatada(String aliquotaRepresentacaoFormatada) {
		this.aliquotaRepresentacaoFormatada = aliquotaRepresentacaoFormatada;
	}

	public void setAliquotaRevenda(Double aliquotaRevenda) {
		this.aliquotaRevenda = aliquotaRevenda;
	}

	public void setAliquotaRevendaFormatada(String aliquotaRevendaFormatada) {
		this.aliquotaRevendaFormatada = aliquotaRevendaFormatada;
	}

	public void setDataFim(Date dataFim) {
		this.dataFim = dataFim;
	}

	public void setDataFimFormatado(String dataFimFormatado) {
		this.dataFimFormatado = dataFimFormatado;
	}

	public void setDataInicio(Date dataInicio) {
		this.dataInicio = dataInicio;
	}

	public void setDataInicioFormatado(String dataInicioFormatado) {
		this.dataInicioFormatado = dataInicioFormatado;
	}

	public void setDescricaoProduto(String descricaoProduto) {
		this.descricaoProduto = descricaoProduto;
	}

	public void setFormaMaterial(FormaMaterial formaMaterial) {
		this.formaMaterial = formaMaterial;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public void setIdFormaMaterial(Integer idFormaMaterial) {
		this.idFormaMaterial = idFormaMaterial;
	}

	public void setIdMaterial(Integer idMaterial) {
		this.idMaterial = idMaterial;
	}

	public void setIdVendedor(Integer idVendedor) {
		this.idVendedor = idVendedor;
	}

	public void setNomeVendedor(String nomeVendedor) {
		this.nomeVendedor = nomeVendedor;
	}

}
