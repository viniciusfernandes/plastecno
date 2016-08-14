package br.com.plastecno.service.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import br.com.plastecno.service.validacao.annotation.InformacaoValidavel;

@Entity
@Table(name="tb_pais", schema="enderecamento")
@InformacaoValidavel
public class Pais implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -586616424759511410L;

	@Id
	@SequenceGenerator(name = "paisSequence", sequenceName = "enderecamento.seq_pais_id", allocationSize=1)
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="paisSequence")
	@Column(name="id_pais")
	private Integer id;
	
	@Column(name="pais")
	@InformacaoValidavel(obrigatorio=true, intervaloComprimento={1, 50}, nomeExibicao="Pais")
	private String descricao;
	
	public String getDescricao() {
		return descricao;
	}
	public Integer getId() {
		return id;
	}
	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	
	
}
