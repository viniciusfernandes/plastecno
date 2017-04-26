package br.com.plastecno.service.test.builder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import mockit.Mock;
import mockit.MockUp;
import br.com.plastecno.service.dao.NFeItemFracionadoDAO;
import br.com.plastecno.service.entity.NFeItemFracionado;

public class NFeItemFracionadoDAOBuilder extends DAOBuilder<NFeItemFracionadoDAO> {

	@Override
	public NFeItemFracionadoDAO build() {
		new MockUp<NFeItemFracionadoDAO>() {

			@Mock
			public Integer pesquisarIdItemFracionado(Integer idItemPedido, Integer numeroNFe) {
				if (idItemPedido == null || numeroNFe == null) {
					return null;
				}

				List<NFeItemFracionado> l = REPOSITORY.pesquisarTodos(NFeItemFracionado.class);
				for (NFeItemFracionado i : l) {
					if (idItemPedido.equals(i.getIdItemPedido()) && numeroNFe.equals(i.getNumeroNFe())) {
						return i.getId();
					}
				}
				return null;
			}

			@Mock
			public List<NFeItemFracionado> pesquisarItemFracionadoByNumeroNFe(Integer numeroNFe) {
				if (numeroNFe == null) {
					return new ArrayList<NFeItemFracionado>();
				}
				List<NFeItemFracionado> l = REPOSITORY.pesquisarTodos(NFeItemFracionado.class);
				List<NFeItemFracionado> lItem = new ArrayList<NFeItemFracionado>();
				for (NFeItemFracionado i : l) {
					if (numeroNFe.equals(i.getNumeroNFe())) {
						lItem.add(i);
					}
				}
				return lItem;
			}

			@Mock
			public List<NFeItemFracionado> pesquisarNFeItemFracionadoQuantidades(Integer numeroNFe) {

				List<NFeItemFracionado> l = REPOSITORY.pesquisarTodos(NFeItemFracionado.class);
				List<NFeItemFracionado> lItem = new ArrayList<NFeItemFracionado>();
				for (NFeItemFracionado i : l) {
					if (numeroNFe.equals(i.getNumeroNFe())) {
						lItem.add(i);
					}
				}
				return lItem;
			}

			@Mock
			public List<Integer[]> pesquisarQuantidadeFracionadaByNumeroItem(List<Integer> listaNumeroItem,
					Integer numeroNFe) {
				if (numeroNFe == null || listaNumeroItem == null || listaNumeroItem.isEmpty()) {
					return new ArrayList<Integer[]>();
				}
				List<Integer[]> lqtd = new ArrayList<Integer[]>();
				List<NFeItemFracionado> lItem = REPOSITORY.pesquisarTodos(NFeItemFracionado.class);
				for (NFeItemFracionado i : lItem) {
					if (numeroNFe.equals(i.getNumeroNFe()) && listaNumeroItem.contains(i.getNumeroItem())) {
						lqtd.add(new Integer[] { i.getNumeroItem(), i.getQuantidadeFracionada() });
					}
				}
				return lqtd;
			}

			@Mock
			public List<Integer[]> pesquisarQuantidadeTotalItemFracionado(Integer idPedido) {
				if (idPedido == null) {
					return new ArrayList<Integer[]>();
				}

				List<NFeItemFracionado> lItem = REPOSITORY.pesquisarTodos(NFeItemFracionado.class);
				if (lItem == null || lItem.isEmpty()) {
					return new ArrayList<Integer[]>();
				}

				Integer qFrac = null;
				Map<Integer, Integer> total = new HashMap<Integer, Integer>();
				// Aqui estamos acumulando o total de itens fracionados de um
				// determinado item
				for (NFeItemFracionado i : lItem) {
					if (!idPedido.equals(i.getIdPedido())) {
						continue;
					}

					qFrac = i.getQuantidadeFracionada() == null ? 0 : i.getQuantidadeFracionada();
					if (!total.containsKey(i.getNumeroItem())) {
						total.put(i.getNumeroItem(), qFrac);
					} else {
						total.put(i.getNumeroItem(), total.get(i.getNumeroItem()) + qFrac);
					}
				}

				List<Integer[]> l = new ArrayList<Integer[]>();
				Set<Entry<Integer, Integer>> entry = total.entrySet();
				for (Entry<Integer, Integer> e : entry) {
					l.add(new Integer[] { e.getKey(), e.getValue() });
				}
				return l;
			}

			@Mock
			public List<Integer[]> pesquisarQuantidadeTotalItemFracionadoByNumeroNFe(Integer numeroNFe) {
				if (numeroNFe == null) {
					return new ArrayList<Integer[]>();
				}

				List<NFeItemFracionado> lItem = REPOSITORY.pesquisarTodos(NFeItemFracionado.class);
				if (lItem == null || lItem.isEmpty()) {
					return new ArrayList<Integer[]>();
				}

				Integer qFrac = null;
				Map<Integer, Integer> total = new HashMap<Integer, Integer>();
				// Aqui estamos acumulando o total de itens fracionados de um
				// determinado item
				for (NFeItemFracionado i : lItem) {
					if (!numeroNFe.equals(i.getNumeroNFe())) {
						continue;
					}

					qFrac = i.getQuantidadeFracionada() == null ? 0 : i.getQuantidadeFracionada();
					if (!total.containsKey(i.getNumeroItem())) {
						total.put(i.getNumeroItem(), qFrac);
					} else {
						total.put(i.getNumeroItem(), total.get(i.getNumeroItem()) + qFrac);
					}
				}

				List<Integer[]> l = new ArrayList<Integer[]>();
				Set<Entry<Integer, Integer>> entry = total.entrySet();
				for (Entry<Integer, Integer> e : entry) {
					l.add(new Integer[] { e.getKey(), e.getValue() });
				}
				return l;
			}

			@Mock
			public Integer pesquisarTotalItemFracionadoByNumeroItemNumeroNFe(Integer numeroItem, Integer numeroNFe) {
				if (numeroItem == null || numeroNFe == null) {
					return 0;
				}
				List<NFeItemFracionado> lItem = REPOSITORY.pesquisarTodos(NFeItemFracionado.class);
				if (lItem == null || lItem.isEmpty()) {
					return 0;
				}

				int totFrac = 0;
				for (NFeItemFracionado i : lItem) {
					if (!numeroNFe.equals(i.getNumeroNFe()) || !numeroItem.equals(i.getNumeroItem())) {
						continue;
					}

					totFrac += i.getQuantidadeFracionada() == null ? 0 : i.getQuantidadeFracionada();
				}
				return totFrac;
			}

			@Mock
			public Integer pesqusisarQuantidadeTotalFracionadoByIdItemPedidoNFeExcluida(Integer idItemPedido,
					Integer numeroNFe) {
				if (idItemPedido == null || numeroNFe == null) {
					return null;
				}
				List<NFeItemFracionado> l = REPOSITORY.pesquisarTodos(NFeItemFracionado.class);
				int totalFrac = 0;
				for (NFeItemFracionado i : l) {
					if (idItemPedido.equals(i.getIdItemPedido()) && !numeroNFe.equals(i.getNumeroNFe())
							&& i.getQuantidadeFracionada() != null) {
						totalFrac += i.getQuantidadeFracionada();
					}
				}
				return totalFrac;
			}

			@Mock
			public void removerItemFracionadoByNumeroNFe(Integer numeroNFe) {
				if (numeroNFe == null) {
					return;
				}
				List<NFeItemFracionado> l = REPOSITORY.pesquisarTodos(NFeItemFracionado.class);
				for (NFeItemFracionado i : l) {
					if (numeroNFe.equals(i.getNumeroNFe())) {
						REPOSITORY.removerEntidade(NFeItemFracionado.class, i.getId());
					}
				}
			}
		};
		return new NFeItemFracionadoDAO(null);
	}
}
