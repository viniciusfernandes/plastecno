package br.com.plastecno.service.wrapper;

public class StringDoubleWrapper extends ChaveValorWrapper<String, Double>{

	public StringDoubleWrapper(String chave, Double valor) {
		super(chave, valor);
	}

	public String getChave () {
		return this.chave;
	}
	
	public Double getValor () {
		return this.valor;
	}
}
