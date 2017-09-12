package br.com.plastecno.service.entity;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import br.com.plastecno.service.validacao.annotation.InformacaoValidavel;

@Entity
@Table(name = "tb_logradouro_pedido", schema = "vendas")
@InformacaoValidavel(validarHierarquia = true)
public class LogradouroPedido extends Logradouro implements Serializable, Cloneable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3634035298276596815L;

	@Id
	@SequenceGenerator(name = "logradouroPedidoSequence", sequenceName = "vendas.seq_logradouro_pedido_id", initialValue = 1, allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "logradouroPedidoSequence")
	private Integer id;

	@ManyToOne
	@JoinColumn(name = "id_pedido", referencedColumnName = "id", nullable = false)
	private Pedido pedido;

	public LogradouroPedido() {
	}

	public LogradouroPedido(LogradouroCliente l) {
		if (l == null) {
			return;
		}
		setBairro(l.getBairro());
		setCep(l.getCep());
		setCidade(l.getCidade());
		setCodigoMunicipio(l.getCodigoMunicipio());
		setComplemento(l.getComplemento());
		setEndereco(l.getEndereco());
		setNumero(l.getNumero());
		setPais(l.getPais());
		setTipoLogradouro(l.getTipoLogradouro());
		setUf(l.getUf());
	}

	public LogradouroPedido(Pedido pedido, LogradouroCliente l) {
		this(l);
		this.pedido = pedido;
	}

	public Integer getId() {
		return id;
	}

	public Pedido getPedido() {
		return pedido;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public void setPedido(Pedido pedido) {
		this.pedido = pedido;
	}

}
