package br.com.svr.service.entity;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import br.com.svr.service.validacao.InformacaoValidavel;

@Entity
@Table(name="tb_contato_transportadora", schema="vendas")
@InformacaoValidavel(validarHierarquia = true)
public class ContatoTransportadora extends Contato {
	/**
	 * 
	 */
	private static final long serialVersionUID = -5372881724142239868L;
	@ManyToOne
	@JoinColumn(name = "id_transportadora", referencedColumnName = "id", nullable = false)
	private Transportadora transportadora;
	
	public Transportadora getTransportadora() {
		return transportadora;
	}

	public void setTransportadora(Transportadora transportadora) {
		this.transportadora = transportadora;
	}

}
