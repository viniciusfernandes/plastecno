package br.com.plastecno.service.test.builder;

import java.util.ArrayList;
import java.util.List;

import mockit.Mock;
import mockit.MockUp;
import br.com.plastecno.service.dao.NFeDuplicataDAO;
import br.com.plastecno.service.entity.NFeDuplicata;

public class NFeDuplicataDAOBuilder extends DAOBuilder<NFeDuplicataDAO> {

	@Override
	public NFeDuplicataDAO build() {
		new MockUp<NFeDuplicataDAO>() {
			@Mock
			public List<NFeDuplicata> pesquisarDuplicataByNumeroNFeOuPedido(Integer numero, boolean isByNfe) {
				if (numero == null) {
					return new ArrayList<NFeDuplicata>();
				}

				List<NFeDuplicata> l = REPOSITORY.pesquisarTodos(NFeDuplicata.class);
				List<NFeDuplicata> lDup = new ArrayList<NFeDuplicata>();
				for (NFeDuplicata d : l) {
					if (isByNfe && d.getnFe() != null && numero.equals(d.getnFe().getNumero())) {
						lDup.add(d);
					} else if (!isByNfe && d.getnFe() != null && numero.equals(d.getnFe().getIdPedido())) {
						lDup.add(d);
					}
				}
				return lDup;
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
