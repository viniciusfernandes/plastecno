package br.com.svr.service.entity;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import br.com.svr.service.validacao.InformacaoValidavel;

@Entity
@Table(name = "tb_logradouro_representada", schema = "vendas")
@InformacaoValidavel(validarHierarquia = true)
public class LogradouroRepresentada extends Logradouro implements Serializable, Cloneable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -882848210368069356L;
	@Id
	@SequenceGenerator(name = "logradouroRepresentadaSequence", sequenceName = "vendas.seq_logradouro_representada_id", initialValue = 1, allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "logradouroRepresentadaSequence")
	private Integer id;

	public LogradouroRepresentada() {
	}

	public LogradouroRepresentada(Integer id) {
		this.id = id;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

}
