package br.com.plastecno.service.wrapper;

public class Subgrupo {
	private final String nomeGrupo;
	private final String nomeSubgrupo;

	public Subgrupo(String nomeGrupo, String nomeSubgrupo) {
		this.nomeGrupo = nomeGrupo;
		this.nomeSubgrupo = nomeSubgrupo;
	}

	public String getNomeSubgrupo() {
		return nomeSubgrupo;
	}

	public String getNomeGrupo() {
		return nomeGrupo;
	}

}