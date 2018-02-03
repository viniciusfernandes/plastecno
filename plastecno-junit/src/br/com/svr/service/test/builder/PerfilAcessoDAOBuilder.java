package br.com.svr.service.test.builder;

import java.util.List;

import mockit.Mock;
import mockit.MockUp;
import br.com.svr.service.dao.PerfilAcessoDAO;
import br.com.svr.service.entity.PerfilAcesso;

public class PerfilAcessoDAOBuilder extends DAOBuilder<PerfilAcessoDAO> {

	@Override
	public PerfilAcessoDAO build() {
		new MockUp<PerfilAcessoDAO>() {
			@Mock
			public List<PerfilAcesso> pesquisarTodos() {
				return REPOSITORY.pesquisarTodos(PerfilAcesso.class);
			}
		};
		return new PerfilAcessoDAO(null);
	}

}
