package br.com.plastecno.service.test.builder;

import java.util.List;

import mockit.Mock;
import mockit.MockUp;
import br.com.plastecno.service.dao.PagamentoDAO;
import br.com.plastecno.service.entity.Pagamento;

public class PagamentoDAOBuilder extends DAOBuilder<PagamentoDAO> {

	@Override
	public PagamentoDAO build() {
		new MockUp<PagamentoDAO>() {
			@Mock
			public List<Pagamento> pesquisarByIdPedido(Integer idPedido) {
				return REPOSITORY.pesquisarEntidadeByAtributo(Pagamento.class, "idPedido", idPedido);
			}
		};
		return new PagamentoDAO(null);
	}

}
