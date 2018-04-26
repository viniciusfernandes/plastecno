package br.com.svr.service.wrapper;

import java.util.List;

public class RepresentadaWrapper extends Grupo {

	public RepresentadaWrapper(String nome) {
		super(nome);
	}

	public void addVenda(VendaClienteWrapper venda) {
		this.addSubgrupo(venda);
	}
	
	public List<Grupo> getListaVenda() {
		return this.listaSubgrupo;
	}
	
	public int getNumeroVendas() {
		return this.getListaVenda().size();
	}
	
	public String getValorVendaTotalFormatado() {
		return this.getValorTotalFormatado();
	}
}
