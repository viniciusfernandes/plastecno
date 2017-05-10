package br.com.plastecno.service.test.builder;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import mockit.Mock;
import mockit.MockUp;
import br.com.plastecno.service.dao.EnderecoDAO;
import br.com.plastecno.service.entity.Endereco;

public class EnderecoDAOBuilder extends DAOBuilder<EnderecoDAO> {

	@Override
	public EnderecoDAO build() {
		new MockUp<EnderecoDAO>() {

			@Mock
			boolean isUFExistente(String sigla, Integer idPais) {
				return true;
			}

			@Mock
			public Endereco pesquisarByCep(String cep) {
				if (cep == null) {
					return null;
				}

				List<Endereco> l = REPOSITORY.pesquisarTodos(Endereco.class);
				for (Endereco e : l) {
					if (cep.equals(e.getCep())) {
						return e;
					}
				}
				return null;
			}

			@Mock
			public List<String> pesquisarCEPExistente(Set<String> listaCep) {
				return REPOSITORY.pesquisarTodos(Endereco.class).stream().map(l -> l.getCep())
						.filter(c -> listaCep.contains(c)).collect(Collectors.toList());
			}

			@Mock
			public Integer pesquisarIdBairroByDescricao(String descricao, Integer idCidade) {
				return 1;
			}

			@Mock
			public Integer pesquisarIdCidadeByDescricao(String descricao, Integer idPais) {
				return 1;
			}

			@Mock
			public Integer pesquisarIdPaisByDescricao(String descricao) {
				return 1;
			}

		};
		return new EnderecoDAO(null);
	}

}
