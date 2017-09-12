package br.com.plastecno.service.mensagem.email;

import java.io.Serializable;

public class AnexoEmail implements Serializable {
	private static final long serialVersionUID = -428641823064584642L;
	private final byte[] conteudo;
	private String descricao;
	private String nome;
	private String tipoAnexo;

	public AnexoEmail(byte[] conteudo) {
		this.conteudo = conteudo;
	}

	public AnexoEmail(byte[] conteudo, String tipoAnexo, String nome, String descricao) {
		this.conteudo = conteudo;
		this.tipoAnexo = tipoAnexo;
		this.nome = nome;
		this.descricao = descricao;
	}

	public byte[] getConteudo() {
		return conteudo;
	}

	public String getDescricao() {
		return descricao;
	}

	public String getNome() {
		return nome;
	}

	public String getTipoAnexo() {
		return tipoAnexo;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public void setTipoAnexo(String tipoAnexo) {
		this.tipoAnexo = tipoAnexo;
	}

}
