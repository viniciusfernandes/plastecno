package br.com.plastecno.service.test.builder;

import java.util.ArrayList;
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

			@Mock
			public List<Pagamento> pesquisarPagamentoByNF(Integer numeroNF) {
				if (numeroNF == null) {
					return new ArrayList<Pagamento>();
				}
				List<Pagamento> l = REPOSITORY.pesquisarTodos(Pagamento.class);
				List<Pagamento> lpag = new ArrayList<Pagamento>();
				for (Pagamento p : l) {
					if (numeroNF.equals(p.getNumeroNF())) {
						lpag.add(p);
					}
				}
				return lpag;
			}
		};
		return new PagamentoDAO(null);
	}

}
