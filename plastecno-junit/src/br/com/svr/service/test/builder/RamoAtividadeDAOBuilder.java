package br.com.svr.service.test.builder;

import java.util.List;

import mockit.Mock;
import mockit.MockUp;
import br.com.svr.service.dao.RamoAtividadeDAO;
import br.com.svr.service.entity.RamoAtividade;

public class RamoAtividadeDAOBuilder extends DAOBuilder<RamoAtividadeDAO> {
	private static final EntidadeRepository REPOSITORY = EntidadeRepository.getInstance();

	@Override
	public RamoAtividadeDAO build() {

		new MockUp<RamoAtividadeDAO>() {

			@Mock
			public RamoAtividade pesquisarRamoAtividadePadrao() {

				List<RamoAtividade> l = REPOSITORY.pesquisarTodos(RamoAtividade.class);
				for (RamoAtividade r : l) {
					if ("NDEFINIDO".equals(r.getSigla())) {
						return r;
					}
				}
				return null;
			}
		};
		return new RamoAtividadeDAO(null);
	}
}
