package br.com.plastecno.service.mensagem.email;

import java.io.Serializable;

public class AnexoEmail implements Serializable {
    private static final long serialVersionUID = -428641823064584642L;
    private final byte[] conteudo;
	private final String tipoAnexo;
	private final String nome;
	private final String descricao;
	public AnexoEmail(byte[] conteudo, String tipoAnexo, String nome,
			String descricao) {
		this.conteudo = conteudo;
		this.tipoAnexo = tipoAnexo;
		this.nome = nome;
		this.descricao = descricao;
	}
	public byte[] getConteudo() {
		return conteudo;
	}
	public String getTipoAnexo() {
		return tipoAnexo;
	}
	public String getNome() {
		return nome;
	}
	public String getDescricao() {
		return descricao;
	}
	
}
