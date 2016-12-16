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

	public Long pesquisarNumeroItemFracionado(Integer idPedido) {
		Long tot = QueryUtil.gerarRegistroUnico(
				entityManager.createQuery(
						"select count(i.idPedido) from NFeItemFracionado i where i.idPedido = :idPedido").setParameter(
						"idPedido", idPedido), Long.class, 0l);
		return tot == null ? 0l : tot;
	}

	public Integer pesquisarQuantidadeFracionada(Integer idItemPedido) {
		return QueryUtil.gerarRegistroUnico(
				entityManager.createQuery(
						"select i.quantidadeFracionada from NFeItemFracionado i where i.idItemPedido =:idItemPedido")
						.setParameter("idItemPedido", idItemPedido), Integer.class, 0);
	}

	public List<Integer[]> pesquisarTotalFracionado(Integer idPedido) {
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

	public Integer pesqusisarSomaQuantidadeFracionada(Integer idItemPedido, Integer numeroNFe) {
		Long tot = QueryUtil
				.gerarRegistroUnico(
						entityManager
								.createQuery(
										"select SUM(i.quantidadeFracionada) from NFeItemFracionado i where i.idItemPedido =:idItemPedido and i.numeroNFe != :numeroNFe")
								.setParameter("idItemPedido", idItemPedido).setParameter("numeroNFe", numeroNFe),
						Long.class, 0l);
		return tot == null ? 0 : tot.intValue();
	}

	public void removerItemFracionado(Integer idItemPedido) {
		entityManager.createQuery("delete from NFeItemFracionado i where i.idItemPedido =:idItemPedido ")
				.setParameter("idItemPedido", idItemPedido).executeUpdate();
	}
}
