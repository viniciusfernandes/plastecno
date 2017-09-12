package br.com.plastecno.service.entity;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import br.com.plastecno.service.validacao.annotation.InformacaoValidavel;

@Entity
@Table(name = "tb_logradouro_contato", schema = "vendas")
@InformacaoValidavel(validarHierarquia = true)
public class LogradouroContato extends Logradouro implements Serializable, Cloneable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -988522640700294794L;
	@Id
	@SequenceGenerator(name = "logradouroContatoSequence", sequenceName = "vendas.seq_logradouro_contato_id", initialValue = 1, allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "logradouroContatoSequence")
	private Integer id;

	public LogradouroContato() {
	}

	public LogradouroContato(Integer id) {
		this.id = id;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}
}
