package br.com.plastecno.validacao.test;

import br.com.plastecno.service.validacao.annotation.InformacaoValidavel;

@InformacaoValidavel(validarHierarquia = true)
public class EntidadeValidacaoHeranca extends EntidadeValidacaoSimples {

	public EntidadeValidacaoHeranca(Integer id, String nome) {
		super(id, nome);
	}

	@InformacaoValidavel(obrigatorio = true, estritamentePositivo = true, nomeExibicao = "Valor herdado")
	private Double valor;

	public Double getValor() {
		return valor;
	}

	public void setValor(Double valor) {
		this.valor = valor;
	}

}
