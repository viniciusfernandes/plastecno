package br.com.svr.service.wrapper;

public class ClienteWrapper {
	private final String nomeVendedor;
	private final String nome;
	private final String contato;
	
	public ClienteWrapper(String nomeVendedor, String nome, String contato) {
		this.nomeVendedor = nomeVendedor;
		this.nome = nome;
		this.contato = contato;
	}

	public String getContato() {
		return contato;
	}

	public String getNome() {
		return nome;
	}

	public String getNomeVendedor() {
		return nomeVendedor;
	}
}