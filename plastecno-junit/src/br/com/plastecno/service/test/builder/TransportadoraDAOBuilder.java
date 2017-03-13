package br.com.plastecno.service.test.builder;

import java.util.List;

import mockit.Mock;
import mockit.MockUp;
import br.com.plastecno.service.dao.TransportadoraDAO;
import br.com.plastecno.service.entity.Transportadora;

public class TransportadoraDAOBuilder extends DAOBuilder<TransportadoraDAO> {

	@Override
	public TransportadoraDAO build() {
		new MockUp<TransportadoraDAO>() {
			@Mock
			public Transportadora pesquisarByCNPJ(String cnpj) {
				if (cnpj == null) {
					return null;
				}

				List<Transportadora> lista = REPOSITORY.pesquisarTodos(Transportadora.class);
				for (Transportadora t : lista) {
					if (cnpj.equals(t.getCnpj())) {
						return t;
					}
				}
				return null;
			}
		};
		return new TransportadoraDAO(null);
	}
}
