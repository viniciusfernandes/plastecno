package br.com.plastecno.service.entity;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import br.com.plastecno.service.validacao.annotation.InformacaoValidavel;

@Entity
@Table(name="tb_contato_representada", schema="vendas")
@InformacaoValidavel(validarHierarquia = true)
public class ContatoRepresentada extends Contato {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7386437714576333105L;

	@ManyToOne
	@JoinColumn(name = "id_representada", referencedColumnName = "id", nullable = false)
	private Representada representada;
	
	public Representada getRepresentada() {
		return representada;
	}

	public void setRepresentada(Representada representada) {
		this.representada = representada;
	}
}
