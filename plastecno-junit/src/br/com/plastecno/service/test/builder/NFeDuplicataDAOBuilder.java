package br.com.plastecno.service.test.builder;

import mockit.MockUp;
import br.com.plastecno.service.dao.NFeDuplicataDAO;
import br.com.plastecno.service.dao.NFeItemFracionadoDAO;

public class NFeDuplicataDAOBuilder extends DAOBuilder<NFeDuplicataDAO> {

	@Override
	public NFeDuplicataDAO build() {
		new MockUp<NFeItemFracionadoDAO>() {
		};
		return new NFeDuplicataDAO(null);
	}
}
