package br.com.plastecno.service.entity;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import br.com.plastecno.service.validacao.annotation.InformacaoValidavel;

@Entity
@Table(name="tb_ramo_atividade", schema="vendas")
@InformacaoValidavel
public class RamoAtividade implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -2973917112551905075L;
	@Id
	@SequenceGenerator(name = "ramoAtividadeSequence", sequenceName = "vendas.seq_ramo_atividade_id", allocationSize=1, initialValue=1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ramoAtividadeSequence")
	private Integer id;
	
	@InformacaoValidavel(obrigatorio=true, intervalo={1, 10}, nomeExibicao="Siga do ramo de atividade")
	private String sigla;
	
	@InformacaoValidavel(obrigatorio=true, intervalo={1, 100}, nomeExibicao="Descrição do ramo de atividade")
	private String descricao;
	
	private boolean ativo = true;

	public String getSigla() {
		return sigla;
	}
	public void setSigla(String sigla) {
		this.sigla = sigla;
	}
	public String getDescricao() {
		return descricao;
	}
	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public boolean isAtivo() {
		return ativo;
	}
	public void setAtivo(boolean ativo) {
		this.ativo = ativo;
	}
}
