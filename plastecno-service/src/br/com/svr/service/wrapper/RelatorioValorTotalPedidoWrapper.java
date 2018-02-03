package br.com.svr.service.wrapper;

import br.com.svr.util.NumeroUtils;

public class RelatorioValorTotalPedidoWrapper extends RelatorioWrapper<Integer, TotalizacaoPedidoWrapper> {
	double totalGeral = 0d;
	double totalGeralIPI = 0d;

	public RelatorioValorTotalPedidoWrapper(String titulo) {
		super(titulo);
	}

	public TotalizacaoPedidoWrapper addElemento(Integer idRepresentada, TotalizacaoPedidoWrapper totalizacao) {

		TotalizacaoPedidoWrapper t = super.getElemento(idRepresentada);
		if (t == null) {
			t = new TotalizacaoPedidoWrapper();
			t.setIdRepresentada(totalizacao.getIdRepresentada());
			t.setNomeFantasiaRepresentada(totalizacao.getNomeFantasiaRepresentada());
			t.setValorTotal(totalizacao.getValorTotal());
			t.setValorTotalIPI(totalizacao.getValorTotalIPI());
			t = super.addElemento(idRepresentada, t);
		} else {
			// Acumulando os valores dos pedidos de cada representada
			t.setValorTotal(t.getValorTotal() + totalizacao.getValorTotal());
			t.setValorTotalIPI(t.getValorTotalIPI() + totalizacao.getValorTotalIPI());
		}

		return t;
	}

	@Override
	public GrupoWrapper<Integer, TotalizacaoPedidoWrapper> addGrupo(Integer idProprietario,
			TotalizacaoPedidoWrapper totalizacao) {

		GrupoWrapper<Integer, TotalizacaoPedidoWrapper> g = super.addGrupo(idProprietario, totalizacao);
		Double valorTotal = (Double) g.getPropriedade("valorTotal");
		Double valorTotalIPI = (Double) g.getPropriedade("valorTotalIPI");

		if (valorTotal == null) {
			valorTotal = 0d;
		}

		if (valorTotalIPI == null) {
			valorTotalIPI = 0d;
		}

		valorTotal += totalizacao.getValorTotal();
		valorTotalIPI += totalizacao.getValorTotalIPI();

		g.setPropriedade("valorTotal", valorTotal);
		g.setPropriedade("valorTotalIPI", valorTotalIPI);
		return g;
	}

	public void calcularTotaisGerais() {
		for (TotalizacaoPedidoWrapper t : getListaElemento()) {
			totalGeral += t.getValorTotal();
			totalGeralIPI += t.getValorTotalIPI();
		}
	}

	public RelatorioValorTotalPedidoWrapper formatarValores() {
		for (GrupoWrapper<Integer, TotalizacaoPedidoWrapper> g : super.getListaGrupo()) {
			for (TotalizacaoPedidoWrapper t : g.getListaElemento()) {
				t.setValorTotalFormatado(NumeroUtils.formatarValorMonetario(t.getValorTotal()));
				t.setValorTotalIPIFormatado(NumeroUtils.formatarValorMonetario(t.getValorTotalIPI()));
			}
			g.setPropriedade("valorTotalFormatado",
					NumeroUtils.formatarValorMonetario((Double) g.getPropriedade("valorTotal")));
			g.setPropriedade("valorTotalIPIFormatado",
					NumeroUtils.formatarValorMonetario((Double) g.getPropriedade("valorTotalIPI")));
		}

		// Formatando a totalizacao das representadas
		for (TotalizacaoPedidoWrapper t : super.getListaElemento()) {
			t.setValorTotalFormatado(NumeroUtils.formatarValorMonetario(t.getValorTotal()));
			t.setValorTotalIPIFormatado(NumeroUtils.formatarValorMonetario(t.getValorTotalIPI()));
		}

		calcularTotaisGerais();

		return this;
	}

	public double getTotalGeral() {
		return totalGeral;
	}

	public String getTotalGeralFormatado() {
		return NumeroUtils.formatarValorMonetario(totalGeral);
	}

	public double getTotalGeralIPI() {
		return totalGeralIPI;
	}

	public String getTotalGeralIPIFormatado() {
		return NumeroUtils.formatarValorMonetario(totalGeralIPI);
	}

	public String getValorIPIFormatado() {
		return NumeroUtils.formatarValorMonetario(totalGeralIPI - totalGeral);
	}
}