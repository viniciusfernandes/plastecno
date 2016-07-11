package br.com.plastecno.service.test.builder;

import mockit.MockUp;
import br.com.plastecno.service.dao.LogradouroDAO;

public class LogradouroDAOBuilder extends DAOBuilder<LogradouroDAO> {

	@Override
	public LogradouroDAO build() {
		new MockUp<LogradouroDAO>() {
		};
		return new LogradouroDAO(null);
	}
}
