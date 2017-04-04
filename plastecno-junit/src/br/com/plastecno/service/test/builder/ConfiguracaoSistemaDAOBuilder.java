package br.com.plastecno.service.test.builder;

import java.util.HashMap;
import java.util.Map;

import mockit.Mock;
import mockit.MockUp;
import br.com.plastecno.service.constante.ParametroConfiguracaoSistema;
import br.com.plastecno.service.dao.ConfiguracaoSistemaDAO;

public class ConfiguracaoSistemaDAOBuilder extends DAOBuilder<ConfiguracaoSistemaDAO> {
	private final Map<ParametroConfiguracaoSistema, String> mapa = new HashMap<ParametroConfiguracaoSistema, String>();

	public ConfiguracaoSistemaDAOBuilder() {
		mapa.put(ParametroConfiguracaoSistema.REGIME_TRIBUTACAO, "3");
		mapa.put(ParametroConfiguracaoSistema.CNAE, "1234567");
		mapa.put(ParametroConfiguracaoSistema.CODIGO_MUNICIPIO_GERADOR_ICMS, "7777777");}

	@Override
	public ConfiguracaoSistemaDAO build() {
		new MockUp<ConfiguracaoSistemaDAO>() {
			@Mock
			public String pesquisar(ParametroConfiguracaoSistema parametro) {
				return mapa.get(parametro);
			}
		};

		return new ConfiguracaoSistemaDAO(null);
	}

}
