package br.com.svr.service.wrapper;

import java.util.Calendar;

import br.com.svr.service.constante.TipoPagamento;

public class Fluxo {
	private final int ano;
	private final int dia;
	private Calendar dtVencimento;
	private final int mes;
	private TipoPagamento tipoPagamento;
	private double valDuplicata;
	private double valFluxo;
	private double valPagamento;

	public Fluxo(Calendar dtVencimento, double valPagamento, TipoPagamento tipoPagamento, double valDuplicata) {
		this.dtVencimento = dtVencimento;
		this.ano = this.dtVencimento.get(Calendar.YEAR);
		this.mes = this.dtVencimento.get(Calendar.MONTH);
		this.dia = this.dtVencimento.get(Calendar.DAY_OF_MONTH);
		this.tipoPagamento = tipoPagamento;
		this.valPagamento = valPagamento;
		this.valDuplicata = valDuplicata;
		calcularValoFluxo();
	}

	public void adicionar(double valPagamento, double valDuplicata) {
		this.valPagamento += valPagamento;
		this.valDuplicata += valDuplicata;
		calcularValoFluxo();
	}

	private void calcularValoFluxo() {
		valFluxo = valDuplicata - valPagamento;
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