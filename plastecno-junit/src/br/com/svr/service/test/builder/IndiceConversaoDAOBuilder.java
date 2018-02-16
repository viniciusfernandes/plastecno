package br.com.svr.service.test.builder;

import mockit.MockUp;
import br.com.svr.service.dao.crm.IndiceConversaoDAO;

public class IndiceConversaoDAOBuilder extends DAOBuilder<IndiceConversaoDAO> {

	@Override
	public IndiceConversaoDAO build() {
		new MockUp<IndiceConversaoDAO>() {
		};
		return new IndiceConversaoDAO(null);
	}

}
