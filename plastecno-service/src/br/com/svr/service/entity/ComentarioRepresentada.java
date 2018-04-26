package br.com.svr.service.entity;

import java.io.Serializable;
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

import br.com.svr.service.validacao.InformacaoValidavel;

@Entity
@Table(name = "tb_comentario_representada", schema = "vendas")
@InformacaoValidavel
public class ComentarioRepresentada implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3583260038132215816L;

	@Id
	@SequenceGenerator(name = "representadaComentarioSequence", sequenceName = "vendas.seq_comentario_representada_id", allocationSize = 1, initialValue = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "representadaComentarioSequence")
	private Integer id;

	@Temporal(TemporalType.DATE)
	@InformacaoValidavel(obrigatorio = true, nomeExibicao = "Data de inclusão do comentário sobre a representada")
	@Column(name = "data_inclusao")
	private Date dataInclusao;

	@InformacaoValidavel(obrigatorio = true, intervaloComprimento = { 1, 300 }, nomeExibicao = "Conteúdo do comentário sobre a representada")
	private String conteudo;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_usuario")
	@InformacaoValidavel(obrigatorio = true, nomeExibicao = "Usuário que fez o comentário")
	private Usuario usuario;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_representada")
	@InformacaoValidavel(relacionamentoObrigatorio = true, nomeExibicao = "Representada")
	private Representada representada;

	/*
	 * Atributo criado para ser exibido na tela de cliente de forma que nao fosse
	 * necessario carregar todos os dados do Usuario.
	 */
	@Transient
	private String nomeUsuario;

	@Transient
	private String sobrenomeUsuario;

	public ComentarioRepresentada() {

	}

	public ComentarioRepresentada(Date dataInclusao, String conteudo, String nomeUsuario, String sobrenomeUsuario) {
		this.dataInclusao = dataInclusao;
		this.conteudo = conteudo;
		this.nomeUsuario = nomeUsuario;
		this.sobrenomeUsuario = sobrenomeUsuario;
	}

	public String getConteudo() {
		return conteudo;
	}

	public Date getDataInclusao() {
		return dataInclusao;
	}

	public Integer getId() {
		return id;
	}

	public String getNomeUsuario() {
		return nomeUsuario;
	}

	public Representada getRepresentada() {
		return representada;
	}

	public String getSobrenomeUsuario() {
		return sobrenomeUsuario;
	}

	public Usuario getUsuario() {
		return usuario;
	}

	public void setConteudo(String conteudo) {
		this.conteudo = conteudo;
	}

	public void setDataInclusao(Date dataInclusao) {
		this.dataInclusao = dataInclusao;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public void setNomeUsuario(String nomeUsuario) {
		this.nomeUsuario = nomeUsuario;
	}

	public void setRepresentada(Representada representada) {
		this.representada = representada;
	}

	public void setSobrenomeUsuario(String sobrenomeUsuario) {
		this.sobrenomeUsuario = sobrenomeUsuario;
	}

	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}
}
