package br.com.plastecno.service.wrapper;

public class RepresentadaValorWrapper extends Grupo{

	public RepresentadaValorWrapper(String nome, Double valor) {
		super(nome, valor);
	}

	public String getNomeRepresentada() {
		return this.nome;
	}
	
	public Double getValorVenda() {
		return this.valor;
	}
	
	public String getValorVendaFormatado() {
		return this.getValorFormatado();
	}
}
