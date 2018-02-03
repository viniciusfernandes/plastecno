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
@Table(name = "tb_logradouro_transportadora", schema = "vendas")
@InformacaoValidavel(validarHierarquia = true)
public class LogradouroTransportadora extends Logradouro implements Serializable, Cloneable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -4781847910298569280L;
	@Id
	@SequenceGenerator(name = "logradouroTransportadoraSequence", sequenceName = "vendas.seq_logradouro_transportadora_id", initialValue = 1, allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "logradouroTransportadoraSequence")
	private Integer id;

	public LogradouroTransportadora() {
	}

	public LogradouroTransportadora(Integer id) {
		this.id = id;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

}
