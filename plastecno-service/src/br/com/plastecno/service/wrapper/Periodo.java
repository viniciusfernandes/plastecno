package br.com.plastecno.service.wrapper;

import java.util.Date;

import br.com.plastecno.service.validacao.exception.InformacaoInvalidaException;

public class Periodo {

	public static Periodo gerarPeriodo(Date inicio, Date fim) throws InformacaoInvalidaException {
		if (inicio == null && fim != null) {
			return new Periodo(fim, false);
		} else if (inicio != null && fim == null) {
			return new Periodo(inicio, true);
		} else if (inicio != null && fim != null) {
			return new Periodo(inicio, fim);
		} else {
			return null;
		}
	}

	final Date inicio;

	final Date fim;

	private Periodo(Date data, boolean isInicio) {
		if (isInicio) {
			this.inicio = data;
			this.fim = null;
		} else {
			this.inicio = null;
			this.fim = data;
		}

	}

	public Periodo(Date inicio, Date fim) throws InformacaoInvalidaException {
		if (inicio == null || fim == null) {
			throw new InformacaoInvalidaException("As datas de inicio e fim devem ser preenchidas");
		}

		if (inicio == null || fim == null || inicio.compareTo(fim) > 0) {
			throw new InformacaoInvalidaException("A data final dever superior a data inicial");
		}

		this.inicio = inicio;
		this.fim = fim;
	}

	public Date getFim() {
		return fim;
	}

	public Date getInicio() {
		return inicio;
	}

}
