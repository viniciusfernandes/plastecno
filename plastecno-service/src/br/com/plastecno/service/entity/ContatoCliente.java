package br.com.plastecno.service.entity;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import br.com.plastecno.service.validacao.annotation.InformacaoValidavel;

@Entity
@Table(name = "tb_contato_cliente", schema = "vendas")
@InformacaoValidavel(validarHierarquia = true)
public class ContatoCliente extends Contato {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7108380096690493567L;
	@ManyToOne
	@JoinColumn(name = "id_cliente", referencedColumnName = "id", nullable = false)
	private Cliente cliente;

	public ContatoCliente() {
	}

	public ContatoCliente(Contato contato) {
		super(contato);
	}

	// Construtor usado na pesquisa do contato principal do cliente
	public ContatoCliente(String ddd, String ddi, String email, String nome, String telefone) {
		setDdd(ddd);
		setDdi(ddi);
		setEmail(email);
		setNome(nome);
		setTelefone(telefone);
	}

	@Override
	public boolean equals(Object o) {
		return o instanceof Contato && this.getId() != null && this.getId().equals(((Contato) o).getId());
	}

	public Cliente getCliente() {
		return cliente;
	}

	@Override
	public int hashCode() {
		return this.getId() != null ? this.getId().hashCode() : -1;
	}

	public void setCliente(Cliente cliente) {
		this.cliente = cliente;
	}
}