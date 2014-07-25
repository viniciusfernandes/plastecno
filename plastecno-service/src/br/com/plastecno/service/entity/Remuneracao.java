package br.com.plastecno.service.entity;

import java.io.Serializable;
import java.util.Calendar;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import br.com.plastecno.service.validacao.annotation.InformacaoValidavel;

@Entity
@Table(name="tb_remuneracao", schema="vendas")
@InformacaoValidavel
public class Remuneracao implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 133874591936120741L;
	@Id
	@SequenceGenerator(name = "remuneracaoSequence", sequenceName = "vendas.seq_remuneracao_id", allocationSize=1, initialValue=1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "remuneracaoSequence")
	private Integer id;
	private Double salario;
	private Double comissao;
	
	@Column(name="data_inicio_vigencia")
	private Calendar dataInicioVigencia;
	
	@Column(name="data_fim_vigencia")
	private Calendar dataFimVigencia;
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="id_usuario", referencedColumnName="id", nullable=false)
	private Usuario usuario;
	
	public Remuneracao(){
		
	}
	public Remuneracao(Double salario, Double comissao) {
		this.salario = salario;
		this.comissao = comissao;
	}
	
	public Remuneracao(Double salario, Double comissao, Calendar dataInicioVigencia) {
		this(salario, comissao);
		this.dataInicioVigencia = dataInicioVigencia;
	}



	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public Double getSalario() {
		return salario;
	}
	public void setSalario(Double salario) {
		this.salario = salario;
	}
	public Double getComissao() {
		return comissao;
	}
	public void setComissao(Double comissao) {
		this.comissao = comissao;
	}
	public Calendar getDataInicioVigencia() {
		return dataInicioVigencia;
	}
	public void setDataInicioVigencia(Calendar dataInicioVigencia) {
		this.dataInicioVigencia = dataInicioVigencia;
	}
	public Calendar getDataFimVigencia() {
		return dataFimVigencia;
	}
	public void setDataFimVigencia(Calendar dataFimVigencia) {
		this.dataFimVigencia = dataFimVigencia;
	}
	public Usuario getUsuario() {
		return usuario;
	}
	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}
}
