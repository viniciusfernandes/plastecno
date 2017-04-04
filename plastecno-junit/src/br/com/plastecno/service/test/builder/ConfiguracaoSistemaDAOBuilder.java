package br.com.plastecno.service.test.builder;

import mockit.Mock;
import mockit.MockUp;
import br.com.plastecno.service.constante.ParametroConfiguracaoSistema;
import br.com.plastecno.service.dao.ConfiguracaoSistemaDAO;

public class ConfiguracaoSistemaDAOBuilder extends DAOBuilder<ConfiguracaoSistemaDAO> {

	@Override
	public ConfiguracaoSistemaDAO build() {
		new MockUp<ConfiguracaoSistemaDAO>() {
			@Mock
			public String pesquisar(ParametroConfiguracaoSistema parametro) {
				return "TESTE";
			}
		};

		return new ConfiguracaoSistemaDAO(null);
	}

}
