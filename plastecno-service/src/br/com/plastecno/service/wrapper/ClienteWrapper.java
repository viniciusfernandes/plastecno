package br.com.plastecno.service.wrapper;

public class ClienteWrapper {
	private final String nomeVendedor;
	private final String nome;
	private final String contato;
	
	public ClienteWrapper(String nomeVendedor, String nome, String contato) {
		this.nomeVendedor = nomeVendedor;
		this.nome = nome;
		this.contato = contato;
	}

	public String getNomeVendedor() {
		return nomeVendedor;
	}

	public String getNome() {
		return nome;
	}

	public String getContato() {
		return contato;
	}
}