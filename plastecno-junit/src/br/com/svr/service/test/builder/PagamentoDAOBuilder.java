package br.com.svr.service.test.builder;

import java.util.ArrayList;
import java.util.List;

import mockit.Mock;
import mockit.MockUp;
import br.com.svr.service.constante.TipoPagamento;
import br.com.svr.service.dao.PagamentoDAO;
import br.com.svr.service.entity.Pagamento;

public class PagamentoDAOBuilder extends DAOBuilder<PagamentoDAO> {

	@Override
	public PagamentoDAO build() {

		new MockUp<PagamentoDAO>() {
			@Mock
			public void alterarValorNFPagamentoInsumo(Integer numeroNF, Integer idFornecedor, Double valorNF) {
				// Estamos garantindo que o valor das nfs serao alterados apenas
				// para os
				// insumos quando passamos o tipo de pagamento.
				List<Pagamento> l = REPOSITORY.pesquisarTodos(Pagamento.class);
				for (Pagamento p : l) {
					if (idFornecedor.equals(p.getIdFornecedor()) && numeroNF.equals(p.getNumeroNF())
							&& TipoPagamento.INSUMO.equals(p.getTipoPagamento())) {
						p.setValorNF(valorNF);
					}
				}
			}

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
