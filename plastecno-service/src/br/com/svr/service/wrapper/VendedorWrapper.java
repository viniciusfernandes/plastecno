package br.com.svr.service.wrapper;

import java.util.List;

public class VendedorWrapper extends Grupo {

	public VendedorWrapper(String nome) {
		super(nome);
	}

	public List<Grupo> getListaRepresentada() {
		return this.getListaSubgrupo();
	}
	
	public String getNomeVendedor() {
		return this.nome;
	}
	
	public int getNumeroVendas() {
		return this.getNumeroSubgrupo();
	}
	
	public String getTotalVendidoFormatado() {
		return this.getValorTotalFormatado();
	}
}
