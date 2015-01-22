package br.com.plastecno.service.wrapper;

import java.util.List;

public class RelatorioCompraPendente {
	private Agrupamento<RepresentadaWrapper, ItemPedidoWrapper> agrupamento;

	public RelatorioCompraPendente() {
		agrupamento = new Agrupamento<RepresentadaWrapper, ItemPedidoWrapper>("Relatório de Compras com Pendências");
	}

	public List<RepresentadaWrapper> getListaRepresentada() {
		return agrupamento.getListaGrupo();
	}

}
