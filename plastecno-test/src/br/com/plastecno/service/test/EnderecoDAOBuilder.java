package br.com.plastecno.service.test;

import mockit.Mock;
import mockit.MockUp;
import br.com.plastecno.service.dao.EnderecoDAO;

public class EnderecoDAOBuilder extends DAOBuilder<EnderecoDAO> {

	@Override
	public EnderecoDAO build() {
		new MockUp<EnderecoDAO>() {
			@Mock
			boolean isUFExistente(String sigla, Integer idPais) {
				return true;
			}

			@Mock
			Integer pesquisarIdBairroByDescricao(String descricao, Integer idCidade) {
				return 1;
			}

			@Mock
			Integer pesquisarIdCidadeByDescricao(String descricao, Integer idPais) {
				return 1;
			}

			@Mock
			Integer pesquisarIdPaisByDescricao(String descricao) {
				return 1;
			}

		};
		return new EnderecoDAO(null);
	}

}
