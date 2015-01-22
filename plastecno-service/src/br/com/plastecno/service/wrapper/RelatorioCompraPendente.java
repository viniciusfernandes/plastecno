package br.com.plastecno.service.wrapper;

import java.util.List;

public class RelatorioCompraPendente {
	private Agrupamento<RepresentadaWrapper, ItemPedidoWrapper> agrupamento;

	public RelatorioCompraPendente() {
		agrupamento = new Agrupamento<RepresentadaWrapper, ItemPedidoWrapper>("Relat�rio de Compras com Pend�ncias");
	}

	public List<RepresentadaWrapper> getListaRepresentada() {
		return agrupamento.getListaGrupo();
	}

}
