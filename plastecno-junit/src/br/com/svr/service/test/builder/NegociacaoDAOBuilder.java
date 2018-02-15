package br.com.svr.service.test.builder;

import java.util.ArrayList;
import java.util.List;

import mockit.Mock;
import mockit.MockUp;
import br.com.svr.service.constante.crm.SituacaoNegociacao;
import br.com.svr.service.dao.crm.NegociacaoDAO;
import br.com.svr.service.entity.crm.IndiceConversao;
import br.com.svr.service.entity.crm.Negociacao;

public class NegociacaoDAOBuilder extends DAOBuilder<NegociacaoDAO> {

	@Override
	public NegociacaoDAO build() {

		new MockUp<NegociacaoDAO>() {

			@Mock
			public Negociacao pesquisarById(Integer id) {
				return REPOSITORY.pesquisarEntidadeById(Negociacao.class, id);
			}

			@Mock
			public double pesquisarIndiceConversaoValorByIdCliente(Integer idCliente) {
				List<IndiceConversao> lIndx = REPOSITORY.pesquisarTodos(IndiceConversao.class);
				for (IndiceConversao i : lIndx) {
					if (idCliente.equals(i.getIdCliente())) {
						return i.getIndiceValor();
					}
				}
				return 0d;
			}

			@Mock
			public List<Negociacao> pesquisarNegociacaoAbertaByIdVendedor(Integer idVendedor) {
				List<Negociacao> lNeg = REPOSITORY.pesquisarTodos(Negociacao.class);
				List<Negociacao> l = new ArrayList<>(lNeg);
				for (Negociacao n : lNeg) {
					if (idVendedor.equals(n.getIdVendedor())
							|| SituacaoNegociacao.ABERTO.equals(n.getSituacaoNegociacao())) {
						l.add(n);
					}
				}
				return l;
			}
		};
		return new NegociacaoDAO(null);
	}

}
