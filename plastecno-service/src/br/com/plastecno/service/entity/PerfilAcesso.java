package br.com.plastecno.service.entity;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

@Entity
@Table(name="tb_perfil_acesso", schema="vendas")
public class PerfilAcesso implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1443539622924823746L;
	@Id
	@SequenceGenerator(name = "perfilAcessoSequence", sequenceName = "seq_perfil_acesso_id")
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "perfilAcessoSequence")
	private Integer id;
	private String descricao;
	
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getDescricao() {
		return descricao;
	}
	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}
	
	@Override
	public boolean equals (Object o) {
		return o instanceof PerfilAcesso && id != null && id.equals(((PerfilAcesso)o).id);
	}
	
	@Override
	public int hashCode () {
		return id != null ? id : -1;
	}
}
