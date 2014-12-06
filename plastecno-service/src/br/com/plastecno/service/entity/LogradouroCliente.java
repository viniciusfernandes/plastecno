package br.com.plastecno.service.entity;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import br.com.plastecno.service.validacao.annotation.InformacaoValidavel;

@Entity
@Table(name="tb_logradouro_cliente", schema="vendas")
@InformacaoValidavel(validarHierarquia = true)
public class LogradouroCliente extends Logradouro {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8911271247053259317L;
	
	private boolean cancelado = false;
	
	/*
	 * Aqui o cascade MERGE/PERSIST eh necessario pois o servico de logradouro insere
	 * o novo logradouro na sessao e tem que construir o relacionamento com o cliente,
	 * no caso em que o cascade nao exista, teremos um exception lancada pois a chave do
	 * relacionamento id_cliente estara nula.
	 */
	@ManyToOne(cascade={CascadeType.MERGE, CascadeType.PERSIST})
	@JoinColumn(name = "id_cliente", referencedColumnName = "id", nullable = false)
	private Cliente cliente;
	
	public LogradouroCliente() {
		super();
	}
	
	public LogradouroCliente(Endereco endereco) {
		super(endereco);
	}
	
	Cliente getCliente() {
		return cliente;
	}
	public boolean isCancelado() {
		return cancelado;
	}

	public void setCancelado(boolean cancelado) {
		this.cancelado = cancelado;
	}

	void setCliente(Cliente cliente) {
		this.cliente = cliente;
	}
}
