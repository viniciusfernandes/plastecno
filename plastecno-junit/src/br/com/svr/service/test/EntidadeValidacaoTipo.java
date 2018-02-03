package br.com.svr.service.test;

import br.com.svr.service.validacao.annotation.InformacaoValidavel;

@InformacaoValidavel(campoCondicional = "tipo", nomeExibicao = "Entidade de validação", nomeExibicaoCampoCondicional = "Tipo da entidade")
public class EntidadeValidacaoTipo {

	@InformacaoValidavel(obrigatorio = true, nomeExibicao = "Codigo")
	private String codigo;

	@InformacaoValidavel(obrigatorio = true, tiposNaoPermitidos = { "3" }, nomeExibicao = "Tipo")
	private String tipo;

	@InformacaoValidavel(tiposObrigatorios = { "4" }, tiposPermitidos = { "1", "2" }, tiposNaoPermitidos = { "3" }, nomeExibicao = "Valor")
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
