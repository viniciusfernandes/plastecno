package br.com.plastecno.service.test;

import br.com.plastecno.service.validacao.annotation.InformacaoValidavel;

@InformacaoValidavel(campoCondicional = "tipo")
public class EntidadeValidacaoTipo {

	@InformacaoValidavel(obrigatorio = true, nomeExibicao = "Codigo")
	private String codigo;

	@InformacaoValidavel(obrigatorio = true, nomeExibicao = "Tipo")
	private String tipo;

	@InformacaoValidavel(condicionaisPermitidos = { "1", "2" }, condicionaisNaoPermitidos = { "3" }, nomeExibicao = "Valor")
	private Double valor;

	public EntidadeValidacaoTipo(String codigo, String tipo, Double valor) {
		super();
		this.codigo = codigo;
		this.tipo = tipo;
		this.valor = valor;
	}

	public String getCodigo() {
		return codigo;
	}

	public String getTipo() {
		return tipo;
	}

	public Double getValor() {
		return valor;
	}

	public void setCodigo(String codigo) {
		this.codigo = codigo;
	}

	public void setTipo(String tipo) {
		this.tipo = tipo;
	}

	public void setValor(Double valor) {
		this.valor = valor;
	}
}
