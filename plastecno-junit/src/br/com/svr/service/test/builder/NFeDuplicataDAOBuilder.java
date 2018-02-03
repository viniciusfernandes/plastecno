package br.com.svr.service.test.builder;

import java.util.ArrayList;
import java.util.List;

import mockit.Mock;
import mockit.MockUp;
import br.com.svr.service.dao.NFeDuplicataDAO;
import br.com.svr.service.entity.NFeDuplicata;

public class NFeDuplicataDAOBuilder extends DAOBuilder<NFeDuplicataDAO> {

	@Override
	public NFeDuplicataDAO build() {
		new MockUp<NFeDuplicataDAO>() {

			@Mock
			private List<NFeDuplicata> pesquisarDuplicataByNumeroNFeOuPedido(Integer numeroNFe, Integer idPedido,
					Integer idCliente) {
				List<NFeDuplicata> l = REPOSITORY.pesquisarTodos(NFeDuplicata.class);
				List<NFeDuplicata> ldup = new ArrayList<>();
				for (NFeDuplicata d : l) {
					if (numeroNFe != null && numeroNFe.equals(d.getnFe().getNumero())) {
						ldup.add(d);
					} else if (idPedido != null && idPedido.equals(d.getnFe().getIdPedido())) {
						ldup.add(d);
					} else if (idCliente != null && idCliente.equals(d.getIdCliente())) {
						ldup.add(d);
					}
				}
				return ldup;
			}

			@Mock
			public void removerDuplicataByNumeroNFe(Integer numeroNFe) {
				if (numeroNFe == null) {
					return;
				}
				List<NFeDuplicata> l = REPOSITORY.pesquisarTodos(NFeDuplicata.class);
				for (NFeDuplicata d : l) {
					if (numeroNFe.equals(d.getnFe().getNumero())) {
						REPOSITORY.removerEntidade(NFeDuplicata.class, d.getId());
					}
				}
			}

		};
		return new NFeDuplicataDAO(null);
	}
}
