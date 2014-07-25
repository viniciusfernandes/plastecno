package br.com.plastecno.service.entity;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import br.com.plastecno.service.validacao.annotation.InformacaoValidavel;


@Entity
@Table(name="tb_contato_usuario", schema="vendas")
@InformacaoValidavel(validarHierarquia = true)
public class ContatoUsuario extends Contato {

	/**
	 * 
	 */
	private static final long serialVersionUID = -296458703675128348L;
	
	@ManyToOne
	@JoinColumn(name = "id_usuario", referencedColumnName = "id", nullable = false)
	private Usuario usuario;

	public Usuario getUsuario() {
		return usuario;
	}

	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}
	
}
