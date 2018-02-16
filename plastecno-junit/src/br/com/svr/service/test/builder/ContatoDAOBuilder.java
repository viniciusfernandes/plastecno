package br.com.svr.service.test.builder;

import mockit.MockUp;
import br.com.svr.service.dao.ContatoDAO;

public class ContatoDAOBuilder extends DAOBuilder<ContatoDAO> {

	@Override
	public ContatoDAO build() {
		new MockUp<ContatoDAO>() {
			
		};
		return new ContatoDAO(null);
	}

}
