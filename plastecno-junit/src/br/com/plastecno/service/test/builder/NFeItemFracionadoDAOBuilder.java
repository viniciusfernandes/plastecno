package br.com.plastecno.service.test.builder;

import mockit.MockUp;
import br.com.plastecno.service.dao.NFeItemFracionadoDAO;

public class NFeItemFracionadoDAOBuilder extends DAOBuilder<NFeItemFracionadoDAO> {

	@Override
	public NFeItemFracionadoDAO build() {
		new MockUp<NFeItemFracionadoDAO>() {
		};
		return new NFeItemFracionadoDAO(null);
	}
}
