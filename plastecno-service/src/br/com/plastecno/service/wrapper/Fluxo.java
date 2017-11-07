package br.com.plastecno.service.wrapper;

import java.util.Calendar;

import br.com.plastecno.service.constante.TipoPagamento;

public class Fluxo {
	private final int ano;
	private final int mes;
	private final int dia;
	private double valPagamento;
	private double valDuplicata;
	private double valCredICMS;
	private TipoPagamento tipoPagamento;
	private double valFluxo;
	private Calendar dtVencimento;

	public Fluxo(Calendar dtVencimento, double valPagamento, TipoPagamento tipoPagamento, double valCredICMS,
			double valDuplicata) {
		this.dtVencimento = dtVencimento;
		this.ano = this.dtVencimento.get(Calendar.YEAR);
		this.mes = this.dtVencimento.get(Calendar.MONTH);
		this.dia = this.dtVencimento.get(Calendar.DAY_OF_MONTH);
		this.tipoPagamento = tipoPagamento;
		this.valCredICMS = valCredICMS;
		this.valPagamento = valPagamento;
		this.valDuplicata = valDuplicata;
		calcularValoFluxo();
	}

	public void adicionar(double valPagamento, double valCredICMS, double valDuplicata) {
		this.valPagamento += valPagamento;
		this.valCredICMS += valCredICMS;
		this.valDuplicata += valDuplicata;
		calcularValoFluxo();
	}

	private void calcularValoFluxo() {
		valFluxo = (this.valDuplicata - this.valPagamento) + this.valCredICMS;
	}

	public int getAno() {
		return ano;
	}

	public int getDia() {
		return dia;
	}

	public Calendar getDtVencimento() {
		return dtVencimento;
	}

	public int getMes() {
		return mes;
	}

	public TipoPagamento getTipoPagamento() {
		return tipoPagamento;
	}

	public double getValCredICMS() {
		return valCredICMS;
	}

	public double getValDuplicata() {
		return valDuplicata;
	}

	public double getValFluxo() {
		return valFluxo;
	}

	public double getValPagamento() {
		return valPagamento;
	}

	@Override
	public String toString() {
		return ano + "/" + mes + "/" + dia + " val: " + valFluxo;
	}

}