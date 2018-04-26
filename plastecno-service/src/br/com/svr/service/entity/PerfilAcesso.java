package br.com.svr.service.entity;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

@Entity
@Table(name = "tb_perfil_acesso", schema = "vendas")
public class PerfilAcesso implements Serializable, Cloneable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1443539622924823746L;
	private String descricao;
	@Id
	@SequenceGenerator(name = "perfilAcessoSequence", sequenceName = "seq_perfil_acesso_id", allocationSize = 1, initialValue = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "perfilAcessoSequence")
	private Integer id;

	public PerfilAcesso() {
	}

	public PerfilAcesso(Integer id) {
		this.id = id;
	}

	public PerfilAcesso(String descricao, Integer id) {
		this.descricao = descricao;
		this.id = id;
	}

	@Override
	public PerfilAcesso clone() {
		return new PerfilAcesso(this.descricao, this.id);
	}

	@Override
	public boolean equals(Object o) {
		return o instanceof PerfilAcesso && id != null && id.equals(((PerfilAcesso) o).id);
	}

	public String getDescricao() {
		return descricao;
	}

	public Integer getId() {
		return id;
	}

	@Override
	public int hashCode() {
		return id != null ? id : -1;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public void setId(Integer id) {
		this.id = id;
	}
}
