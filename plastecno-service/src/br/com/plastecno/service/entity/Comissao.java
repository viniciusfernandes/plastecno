package br.com.plastecno.service.entity;

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

import br.com.plastecno.service.constante.FormaMaterial;
import br.com.plastecno.service.validacao.annotation.InformacaoValidavel;

@Entity
@Table(name = "tb_comissao", schema = "vendas")
@InformacaoValidavel
public class Comissao implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3780636031987775613L;

	@Id
	@SequenceGenerator(name = "comissaoSequence", sequenceName = "vendas.seq_comissao_id", allocationSize = 1, initialValue = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "comissaoSequence")
	private Integer id;

	@InformacaoValidavel(obrigatorio = true, positivo = true, nomeExibicao = "Valor da comissão")
	private Double valor;

	@InformacaoValidavel(obrigatorio = true, nomeExibicao = "Data de início da comissão")
	@Column(name = "data_inicio")
	private Date dataInicio;

	@Column(name = "data_fim")
	private Date dataFim;

	@Column(name = "id_vendedor")
	private Integer idVendedor;

	@Column(name = "id_forma_material")
	private Integer idFormaMaterial;

	@Column(name = "id_material")
	private Integer idMaterial;

	@Transient
	private FormaMaterial formaMaterial;

	@Transient
	private String nomeVendedor;

	@Transient
	private String dataInicioFormatado;

	@Transient
	private String dataFimFormatado;

	@Transient
	private String valorFormatado;

	public String getDataInicioFormatado() {
		return dataInicioFormatado;
	}

	public void setDataInicioFormatado(String dataInicioFormatado) {
		this.dataInicioFormatado = dataInicioFormatado;
	}

	public String getDataFimFormatado() {
		return dataFimFormatado;
	}

	public void setDataFimFormatado(String dataFimFormatado) {
		this.dataFimFormatado = dataFimFormatado;
	}

	public String getValorFormatado() {
		return valorFormatado;
	}

	public void setValorFormatado(String valorFormatado) {
		this.valorFormatado = valorFormatado;
	}

	public Comissao() {
	}

	public Comissao(Double valor, Date dataInicio) {
		this.valor = valor;
		this.dataInicio = dataInicio;
	}

	public Date getDataFim() {
		return dataFim;
	}

	public Date getDataInicio() {
		return dataInicio;
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

	public Double getValor() {
		return valor;
	}

	public void setDataFim(Date dataFim) {
		this.dataFim = dataFim;
	}

	public void setDataInicio(Date dataInicio) {
		this.dataInicio = dataInicio;
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

	public void setValor(Double valor) {
		this.valor = valor;
	}
}
