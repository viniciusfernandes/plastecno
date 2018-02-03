package br.com.svr.service.wrapper;


public class ValorVendidoRepresentadaWrapper extends ChaveValorWrapper<String, Double> {

	public ValorVendidoRepresentadaWrapper(String nomeRepresentada, Double totalVendido) {
		super(nomeRepresentada, totalVendido);
	}
	
	public String getNomeRepresentada() {
		return this.chave;
	}

	public Double getValor() {
		return this.valor;
	}
}
