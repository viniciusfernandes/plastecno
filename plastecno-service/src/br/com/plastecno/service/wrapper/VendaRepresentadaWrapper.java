package br.com.plastecno.service.wrapper;

public class VendaRepresentadaWrapper extends Grupo{

	public VendaRepresentadaWrapper(String nome, Double valor) {
		super(nome, valor);
	}

	public Double getValorVenda() {
		return this.valor;
	}
	
	public String getNomeRepresentada() {
		return this.nome;
	}
	
	public String getValorVendaFormatado() {
		return this.getValorFormatado();
	}
}
