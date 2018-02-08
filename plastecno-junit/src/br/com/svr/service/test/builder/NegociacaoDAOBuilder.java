package br.com.svr.service.test.builder;

import mockit.Mock;
import mockit.MockUp;
import br.com.svr.service.dao.crm.NegociacaoDAO;
import br.com.svr.service.entity.crm.Negociacao;

public class NegociacaoDAOBuilder extends DAOBuilder<NegociacaoDAO> {

	@Override
	public NegociacaoDAO build() {

		new MockUp<NegociacaoDAO>() {
			@Mock
			public Negociacao pesquisarById(Integer id) {
				return REPOSITORY.pesquisarEntidadeById(Negociacao.class, id);
			}
		};
		return new NegociacaoDAO(null);
	}

}
