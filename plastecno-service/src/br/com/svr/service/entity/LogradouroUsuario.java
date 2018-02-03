package br.com.svr.service.entity;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import br.com.svr.service.validacao.annotation.InformacaoValidavel;

@Entity
@Table(name = "tb_logradouro_usuario", schema = "vendas")
@InformacaoValidavel(validarHierarquia = true)
public class LogradouroUsuario extends Logradouro implements Serializable, Cloneable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1240171359371044991L;
	@Id
	@SequenceGenerator(name = "logradouroUsuarioSequence", sequenceName = "vendas.seq_logradouro_usuario_id", initialValue = 1, allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "logradouroUsuarioSequence")
	private Integer id;

	public LogradouroUsuario() {
	}

	public LogradouroUsuario(Integer id) {
		this.id = id;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

}
