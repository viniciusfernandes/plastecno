package br.com.plastecno.service.test.builder;

import java.util.List;

import mockit.Mock;
import mockit.MockUp;
import br.com.plastecno.service.dao.ComissaoDAO;
import br.com.plastecno.service.entity.Comissao;

public class ComissaoDAOBuilder extends DAOBuilder<ComissaoDAO> {

	@Override
	public ComissaoDAO build() {
		new MockUp<ComissaoDAO>() {
			@Mock
			public Comissao pesquisarComissaoVigenteProduto(Integer idMaterial, Integer idFormaMaterial) {
				List<Comissao> lista = REPOSITORY.pesquisarTodos(Comissao.class);
				boolean ok = false;
				for (Comissao comissao : lista) {
					ok = false;
					if (comissao.getDataFim() != null) {
						continue;
					}

					if (idFormaMaterial != null) {
						ok = idFormaMaterial.equals(comissao.getIdFormaMaterial());
					}

					if (idMaterial != null) {
						ok |= idMaterial.equals(comissao.getIdMaterial());
					}

					if (ok) {
						return comissao;
					}
				}
				return null;
			}

			@Mock
			public Comissao pesquisarComissaoVigenteVendedor(Integer idVendedor) {
				List<Comissao> lista = REPOSITORY.pesquisarTodos(Comissao.class);
				for (Comissao comissao : lista) {
					if (comissao.getDataFim() != null) {
						continue;
					}

					if (idVendedor.equals(comissao.getIdVendedor())) {
						return comissao;
					}
				}
				return null;
			}
		};
		return new ComissaoDAO(null);
	}

}
