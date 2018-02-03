package br.com.svr.service.test.builder;

import java.util.List;

import mockit.Mock;
import mockit.MockUp;
import br.com.svr.service.dao.LogradouroDAO;
import br.com.svr.service.entity.Endereco;

public class LogradouroDAOBuilder extends DAOBuilder<LogradouroDAO> {

	@Override
	public LogradouroDAO build() {
		new MockUp<LogradouroDAO>() {
			@Mock
			public String pesquisarCodigoMunicipioByCep(String cep) {
				if (cep == null || cep.isEmpty()) {
					return null;
				}
				List<Endereco> l = REPOSITORY.pesquisarTodos(Endereco.class);
				for (Endereco e : l) {
					if (cep.equals(e.getCep())) {
						return e.getCidade().getCodigoMunicipio();
					}
				}
				return null;
			}
		};
		return new LogradouroDAO(null);
	}
}
