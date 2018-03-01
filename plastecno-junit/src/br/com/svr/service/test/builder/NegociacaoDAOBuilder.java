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
			public void alterarIndiceConversaoValorByIdCliente(Integer idCliente, Double indice,
					SituacaoNegociacao situacaoNegociacao) {
				List<Negociacao> l = REPOSITORY.pesquisarTodos(Negociacao.class);
				for (Negociacao n : l) {
					if (idCliente.equals(n.getIdCliente()) && situacaoNegociacao.equals(n.getSituacaoNegociacao())) {
						n.setIndiceConversaoValor(indice);
					}
				}
			}

			@Mock
			public void alterarSituacaoNegociacao(Integer idNegociacao, SituacaoNegociacao situacaoNegociacao) {
				REPOSITORY.alterarEntidadeAtributoById(Negociacao.class, idNegociacao, "situacaoNegociacao",
						situacaoNegociacao);
			}

			@Mock
			public Negociacao pesquisarById(Integer id) {
				return REPOSITORY.pesquisarEntidadeById(Negociacao.class, id);
			}

			@Mock
			public Integer pesquisarIdPedidoByIdNegociacao(Integer idNegociacao) {
				Negociacao n = REPOSITORY.pesquisarEntidadeById(Negociacao.class, idNegociacao);
				return n != null ? n.getOrcamento().getId() : null;
			}

			@Mock
			public IndiceConversao pesquisarIndiceByIdCliente(Integer idCliente) {
				if (idCliente == null) {
					return null;
				}
				List<IndiceConversao> l = REPOSITORY.pesquisarTodos(IndiceConversao.class);
				for (IndiceConversao i : l) {
					if (idCliente.equals(i.getIdCliente())) {
						return i;
					}
				}
				return null;
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
				List<Negociacao> l = new ArrayList<>();
				for (Negociacao n : lNeg) {
					if (idVendedor.equals(n.getIdVendedor())
							&& SituacaoNegociacao.ABERTO.equals(n.getSituacaoNegociacao())) {
						l.add(n);
					}
				}
				return l;
			}

			@Mock
			public Negociacao pesquisarNegociacaoByIdOrcamento(Integer idOrcamento) {
				if (idOrcamento == null) {
					return null;
				}
				List<Negociacao> l = REPOSITORY.pesquisarTodos(Negociacao.class);
				for (Negociacao n : l) {
					if (idOrcamento.equals(n.getOrcamento().getId())) {
						return n;
					}
				}
				return null;
			}

			@Mock
			public void removerNegociacaoByIdOrcamento(Integer idOrcamento) {
				List<Negociacao> l = REPOSITORY.pesquisarTodos(Negociacao.class);
				for (Negociacao n : l) {
					if (idOrcamento.equals(n.getOrcamento().getId())) {
						REPOSITORY.removerEntidade(Negociacao.class, n.getId());
					}
				}
			}
		};
		return new NegociacaoDAO(null);
	}

}
