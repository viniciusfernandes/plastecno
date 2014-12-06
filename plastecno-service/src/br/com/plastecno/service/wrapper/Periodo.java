package br.com.plastecno.service.wrapper;

import java.util.Date;

import br.com.plastecno.service.validacao.exception.InformacaoInvalidaException;

public class Periodo  {
	public static Periodo getPeriodo(Date inicio, Date fim) throws InformacaoInvalidaException {
		return new Periodo(inicio, fim);
	}
	final Date inicio;
	
	final Date fim;
	
	public Periodo(Date inicio, Date fim) throws InformacaoInvalidaException {
		if(inicio == null || fim == null) {
			throw new InformacaoInvalidaException("As datas de inicio e fim devem ser preenchidas");
		}
		
		if(inicio == null || fim == null || inicio.compareTo(fim) > 0) {
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
