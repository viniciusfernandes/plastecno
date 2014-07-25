package br.com.plastecno.service.entity;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import br.com.plastecno.service.validacao.annotation.InformacaoValidavel;

@Entity
@Table(name="tb_contato_cliente", schema="vendas")
@InformacaoValidavel(validarHierarquia = true)
public class ContatoCliente extends Contato {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 7108380096690493567L;
	@ManyToOne
	@JoinColumn(name = "id_cliente", referencedColumnName = "id", nullable = false)
	private Cliente cliente;
	public Cliente getCliente() {
		return cliente;
	}
	public void setCliente(Cliente cliente) {
		this.cliente = cliente;
	}
	
	@Override
	public boolean equals (Object o) {
		return o instanceof Contato && this.getId() != null && this.getId().equals(((Contato)o).getId());
	}
	@Override
	public int hashCode () {
		return this.getId()!= null ? this.getId().hashCode() : -1;
	}
}