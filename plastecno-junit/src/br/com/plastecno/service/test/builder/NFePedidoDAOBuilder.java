package br.com.plastecno.service.test.builder;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import mockit.Mock;
import mockit.MockUp;
import br.com.plastecno.service.dao.NFePedidoDAO;
import br.com.plastecno.service.entity.NFePedido;

public class NFePedidoDAOBuilder extends DAOBuilder<NFePedidoDAO> {

	@Override
	public NFePedidoDAO build() {
		new MockUp<NFePedidoDAO>() {

			@Mock
			public Object[] pesquisarNumeroSerieModeloNFe() {

				List<NFePedido> listaNFe = REPOSITORY.pesquisarTodos(NFePedido.class);

				if (listaNFe == null || listaNFe.isEmpty()) {
					return new Object[] { 1, 1, 55, null };
				}

				Integer maxNumeroAssociado = null;
				NFePedido ultimaNfe = null;
				Collections.sort(listaNFe, new Comparator<NFePedido>() {
					@Override
					public int compare(NFePedido o1, NFePedido o2) {
						return o1.getNumeroAssociado() != null && o2.getNumeroAssociado() != null ? o1
								.getNumeroAssociado().compareTo(o2.getNumeroAssociado()) : 0;
					}
				});

				maxNumeroAssociado = listaNFe.get(0).getNumeroAssociado();

				Collections.sort(listaNFe, new Comparator<NFePedido>() {
					@Override
					public int compare(NFePedido o1, NFePedido o2) {
						return o1.getNumero() != null && o2.getNumero() != null ? o1.getNumero().compareTo(
								o2.getNumero()) : 0;
					}
				});

				ultimaNfe = listaNFe.get(0);

				return new Object[] { ultimaNfe.getNumero(), ultimaNfe.getSerie(), ultimaNfe.getModelo(),
						maxNumeroAssociado };
			}
		};
		return new NFePedidoDAO(null);
	}
}
