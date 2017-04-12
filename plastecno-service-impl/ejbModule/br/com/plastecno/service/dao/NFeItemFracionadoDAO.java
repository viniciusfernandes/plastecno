package br.com.plastecno.service.dao;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;

import br.com.plastecno.service.entity.NFeItemFracionado;
import br.com.plastecno.service.impl.util.QueryUtil;

public class NFeItemFracionadoDAO extends GenericDAO<NFeItemFracionado> {

	public NFeItemFracionadoDAO(EntityManager entityManager) {
		super(entityManager);
	}

	public void alterarQuantidadeFracionadaByNumeroItem(List<Integer[]> listaQtdFracionada, Integer numeroNFe) {
		if (listaQtdFracionada == null || listaQtdFracionada.isEmpty()) {
			return;
		}
		for (Integer[] q : listaQtdFracionada) {
			entityManager
					.createQuery(
							"update NFeItemFracionado i set i.quantidadeFracionada =:qFrac where i.numeroItem =:numItem and i.numeroNFe =:numeroNFe")
					.setParameter("qFrac", q[1]).setParameter("numItem", q[0]).setParameter("numeroNFe", numeroNFe)
					.executeUpdate();
		}

	}

	public Integer pesquisarIdItemFracionado(Integer idItemPedido, Integer numeroNFe) {
		return QueryUtil
				.gerarRegistroUnico(
						entityManager
								.createQuery(
										"select i.id from NFeItemFracionado i where i.idItemPedido =:idItemPedido and i.numeroNFe = :numeroNFe")
								.setParameter("idItemPedido", idItemPedido).setParameter("numeroNFe", numeroNFe),
						Integer.class, null);
	}

	public List<NFeItemFracionado> pesquisarItemFracionado() {
		return entityManager.createQuery("select i from NFeItemFracionado i", NFeItemFracionado.class).getResultList();
	}

	public List<NFeItemFracionado> pesquisarNFeItemFracionadoQuantidades(Integer numeroNFe) {
		return entityManager
				.createQuery(
						"select new NFeItemFracionado(n.idPedido, n.numeroItem, n.numeroNFe, n.quantidade, n.quantidadeFracionada) from NFeItemFracionado n where n.numeroNFe =:numeroNFe",
						NFeItemFracionado.class).setParameter("numeroNFe", numeroNFe).getResultList();
	}

	public Integer pesquisarQuantidadeFracionada(Integer idItemPedido) {
		return QueryUtil.gerarRegistroUnico(
				entityManager.createQuery(
						"select i.quantidadeFracionada from NFeItemFracionado i where i.idItemPedido =:idItemPedido")
						.setParameter("idItemPedido", idItemPedido), Integer.class, 0);
	}

	public List<Integer[]> pesquisarQuantidadeFracionadaByNumeroItem(List<Integer> listaNumeroItem, Integer numeroNFe) {
		List<Object[]> l = entityManager
				.createQuery(
						"select i.numeroItem, i.quantidadeFracionada from NFeItemFracionado i where i.numeroNFe = :numeroNFe and i.numeroItem in (:listaNumeroItem)",
						Object[].class).setParameter("numeroNFe", numeroNFe)
				.setParameter("listaNumeroItem", listaNumeroItem).getResultList();

		List<Integer[]> lista = new ArrayList<Integer[]>();
		for (Object[] o : l) {
			lista.add(new Integer[] { (Integer) o[0], (Integer) o[1] });
		}
		return lista;
	}

	public List<Integer[]> pesquisarQuantidadeTotalItemFracionado(Integer idPedido) {
		List<Object[]> listaTot = entityManager
				.createQuery(
						"select i.numeroItem, sum(i.quantidadeFracionada) from NFeItemFracionado i where i.idPedido =:idPedido group by i.numeroItem",
						Object[].class).setParameter("idPedido", idPedido).getResultList();

		List<Integer[]> l = new ArrayList<Integer[]>();
		for (Object[] o : listaTot) {
			l.add(new Integer[] { o != null ? (Integer) o[0] : 0, o != null ? ((Long) o[1]).intValue() : 0 });
		}
		return l;
	}

	public Integer pesquisarTotalItemFracionadoByNumeroItemNumeroNFe(Integer numeroItem, Integer numeroNFe) {
		if (numeroItem == null || numeroNFe == null) {
			return 0;
		}
		Long tot = QueryUtil
				.gerarRegistroUnico(
						entityManager
								.createQuery(
										"select sum(i.quantidadeFracionada) from NFeItemFracionado i where i.numeroNFe =:numeroNFe and i.numeroItem =:numeroItem")
								.setParameter("numeroNFe", numeroNFe).setParameter("numeroItem", numeroItem),
						Long.class, 0l);
		return tot.intValue();
	}

	public List<Integer[]> pesquisarQuantidadeTotalItemFracionadoByNumeroNFe(Integer numeroNFe) {
		List<Object[]> listaTot = entityManager
				.createQuery(
						"select i.numeroItem, sum(i.quantidadeFracionada) from NFeItemFracionado i where i.numeroNFe =:numeroNFe group by i.numeroItem",
						Object[].class).setParameter("numeroNFe", numeroNFe).getResultList();

		List<Integer[]> l = new ArrayList<Integer[]>();
		for (Object[] o : listaTot) {
			l.add(new Integer[] { o != null ? (Integer) o[0] : 0, o != null ? ((Long) o[1]).intValue() : 0 });
		}
		return l;
	}

	public Integer pesqusisarQuantidadeTotalFracionadoByIdItemPedidoNFeExcluida(Integer idItemPedido, Integer numeroNFe) {
		Long tot = QueryUtil
				.gerarRegistroUnico(
						entityManager
								.createQuery(
										"select SUM(i.quantidadeFracionada) from NFeItemFracionado i where i.idItemPedido =:idItemPedido and i.numeroNFe != :numeroNFe")
								.setParameter("idItemPedido", idItemPedido).setParameter("numeroNFe", numeroNFe),
						Long.class, 0l);
		return tot == null ? 0 : tot.intValue();
	}

	public void removerItemFracionadoByIdItemPedido(Integer idItemPedido) {
		entityManager.createQuery("delete from NFeItemFracionado i where i.idItemPedido =:idItemPedido ")
				.setParameter("idItemPedido", idItemPedido).executeUpdate();
	}

	public void removerItemFracionadoByNumeroNFe(Integer numeroNFe) {
		entityManager.createQuery("delete NFeItemFracionado n where n.numeroNFe = :numeroNFe")
				.setParameter("numeroNFe", numeroNFe).executeUpdate();
	}
}
