package br.com.svr.service.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

@Entity
@Table(name="tb_finalidade_pedido", schema="vendas")
public class FinalidadePedido {
	
	@Id
	@SequenceGenerator(name = "finalidadeSequence", sequenceName = "vendas.seq_finalidade_pedido_id", allocationSize=1, initialValue=1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "finalidadeSequence")
	private Integer id;
	private String sigla;
	private String descricao;
	
	public String getDescricao() {
		return descricao;
	}
	public Integer getId() {
		return id;
	}
	public String getSigla() {
		return sigla;
	}
	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public void setSigla(String sigla) {
		this.sigla = sigla;
	}
}
