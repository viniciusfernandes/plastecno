package br.com.svr.service.test;

import br.com.svr.service.validacao.InformacaoValidavel;

@InformacaoValidavel
public class EntidadeValidacaoSimples {
	private Integer id;

	@InformacaoValidavel(obrigatorio = true, nomeExibicao = "Nome")
	private String nome;

	public EntidadeValidacaoSimples(Integer id, String nome) {
		this.id = id;
		this.nome = nome;
	}

	public Integer getId() {
		return id;
	}

	public String getNome() {
		return nome;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}
}
