package br.com.svr.service.entity;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import br.com.svr.service.validacao.InformacaoValidavel;

@Entity
@Table(name = "tb_logradouro_cliente", schema = "vendas")
@InformacaoValidavel(validarHierarquia = true)
public class LogradouroCliente extends Logradouro implements Serializable, Cloneable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6731042785674747189L;

	@ManyToOne
	@JoinColumn(name = "id_cliente", referencedColumnName = "id", nullable = false)
	private Cliente cliente;

	@Id
	@SequenceGenerator(name = "logradouroClienteSequence", sequenceName = "vendas.seq_logradouro_cliente_id", initialValue = 1, allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "logradouroClienteSequence")
	private Integer id;

	public LogradouroCliente() {
	}

	public LogradouroCliente(Integer id) {
		this.id = id;
	}
	public void setCliente(Cliente cliente) {
		this.cliente = cliente;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}
}
