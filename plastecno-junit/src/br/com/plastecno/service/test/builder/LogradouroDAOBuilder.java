package br.com.plastecno.service.test.builder;

import mockit.Mock;
import mockit.MockUp;
import br.com.plastecno.service.dao.LogradouroDAO;
import br.com.plastecno.service.entity.Cidade;

public class LogradouroDAOBuilder extends DAOBuilder<LogradouroDAO> {

	@Override
	public LogradouroDAO build() {
		new MockUp<LogradouroDAO>() {
			@Mock
			public String pesquisarCodigoIBGEByIdCidade(Integer idCidade) {

				Cidade c = REPOSITORY.pesquisarEntidadeById(Cidade.class, idCidade);
				// Aqui vamos retornar sempre o mesmo numero pois no codigo do
				// ibge nao foi modelado no sistema
				return c == null ? null : "123456";

			}
		};
		return new LogradouroDAO(null);
	}
}
