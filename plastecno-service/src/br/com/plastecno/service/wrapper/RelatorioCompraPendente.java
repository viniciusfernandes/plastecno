package br.com.plastecno.service.wrapper;

import java.util.ArrayList;
import java.util.List;

public class RelatorioCompraPendente {
	private final String titulo;
	private final List<MasterDetail> lista = new ArrayList<MasterDetail>();

	public RelatorioCompraPendente() {
		titulo = "Relatório de Compras com Pendências";
	}

	public void add(MasterDetail linha) {
		lista.add(linha);
	}

	public List<MasterDetail> getLista() {
		return lista;
	}

	public String getTitulo() {
		return titulo;
	}

}
