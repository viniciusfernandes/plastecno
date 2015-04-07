package br.com.plastecno.service.entity;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

@Entity
@Table(name = "tb_perfil_acesso", schema = "vendas")
public class PerfilAcesso implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1443539622924823746L;
	@Id
	@SequenceGenerator(name = "perfilAcessoSequence", sequenceName = "seq_perfil_acesso_id")
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "perfilAcessoSequence")
	private Integer id;
	private String descricao;

	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(name = "tb_usuario_tb_perfil_acesso", schema = "vendas", joinColumns = { @JoinColumn(name = "id_perfil_acesso") }, inverseJoinColumns = { @JoinColumn(name = "id_usuario") })
	private List<Usuario> listaUsuario;

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

	public List<Usuario> getListaUsuario() {
		return listaUsuario;
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

	public void setListaUsuario(List<Usuario> listaUsuario) {
		this.listaUsuario = listaUsuario;
	}
}
