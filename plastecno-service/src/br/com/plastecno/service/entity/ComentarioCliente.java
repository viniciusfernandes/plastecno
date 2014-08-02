package br.com.plastecno.service.entity;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import br.com.plastecno.service.validacao.annotation.InformacaoValidavel;

@Entity
@Table(name = "tb_comentario_cliente", schema = "vendas")
@InformacaoValidavel
public class ComentarioCliente implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 422899612343389268L;
	private static final DateFormat DATE_FORMAT = new SimpleDateFormat(
			"dd/MM/yyyy");

	@Id
	@SequenceGenerator(name = "clienteComentarioSequence", sequenceName = "vendas.seq_comentario_cliente_id", allocationSize = 1, initialValue = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "clienteComentarioSequence")
	private Integer id;

	@Temporal(TemporalType.DATE)
	@InformacaoValidavel(obrigatorio = true, nomeExibicao = "Data de inclusão do comentário sobre o cliente")
	@Column(name = "data_inclusao")
	private Date dataInclusao;

	@InformacaoValidavel(obrigatorio = true, intervalo = { 1, 300 }, nomeExibicao = "Conteúdo do comentário sobre o cliente")
	private String conteudo;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_vendedor")
	@InformacaoValidavel(obrigatorio = true, nomeExibicao = "Vendedor que fez o comentário")
	private Usuario vendedor;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_cliente")
	@InformacaoValidavel(obrigatorio = true, nomeExibicao = "Cliente")
	private Cliente cliente;

	/*
	 * Atributo criado para ser exibido na tela de cliente de forma que nao
	 * fosse necessario carregar todos os dados do vendedor.
	 */
	@Transient
	private String nomeVendedor;

	@Transient
	private String sobrenomeVendedor;

	public ComentarioCliente() {

	}

	public ComentarioCliente(Date dataInclusao, String conteudo,
			String nomeVendedor, String sobrenomeVendedor) {
		this.dataInclusao = dataInclusao;
		this.conteudo = conteudo;
		this.nomeVendedor = nomeVendedor;
		this.sobrenomeVendedor = sobrenomeVendedor;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Date getDataInclusao() {
		return dataInclusao;
	}

	public void setDataInclusao(Date dataInclusao) {
		this.dataInclusao = dataInclusao;
	}

	public String getConteudo() {
		return conteudo;
	}

	public void setConteudo(String conteudo) {
		this.conteudo = conteudo;
	}

	public Usuario getVendedor() {
		return vendedor;
	}

	public void setVendedor(Usuario vendedor) {
		this.vendedor = vendedor;
	}

	public Cliente getCliente() {
		return cliente;
	}

	public void setCliente(Cliente cliente) {
		this.cliente = cliente;
	}

	public String getNomeVendedor() {
		return nomeVendedor;
	}

	public void setNomeVendedor(String nomeVendedor) {
		this.nomeVendedor = nomeVendedor;
	}

	public String getSobrenomeVendedor() {
		return sobrenomeVendedor;
	}

	public void setSobrenomeVendedor(String sobrenomeVendedor) {
		this.sobrenomeVendedor = sobrenomeVendedor;
	}
}
