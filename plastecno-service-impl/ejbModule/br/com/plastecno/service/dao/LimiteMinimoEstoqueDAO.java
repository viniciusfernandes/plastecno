package br.com.plastecno.service.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import br.com.plastecno.service.entity.LimiteMinimoEstoque;
import br.com.plastecno.service.impl.util.QueryUtil;

public class LimiteMinimoEstoqueDAO extends GenericDAO<LimiteMinimoEstoque> {

	public LimiteMinimoEstoqueDAO(EntityManager entityManager) {
		super(entityManager);
	}

	public void associarLimiteMinimoItemEstoque(Integer idLimiteMinimo, Integer idItemEstoque) {
		entityManager
				.createQuery("update ItemEstoque i set i.limiteMinimoEstoque.id = :idLimiteMinimo where i.id = :idItemEstoque")
				.setParameter("idLimiteMinimo", idLimiteMinimo).setParameter("idItemEstoque", idItemEstoque).executeUpdate();
	}

	public void associarLimiteMinimoItemEstoque(Integer idLimiteMinimo, List<Integer> listaIdItemEstoque) {
		entityManager
				.createQuery(
						"update ItemEstoque i set i.limiteMinimoEstoque.id = :idLimiteMinimo where i.id in (:listaIdItemEstoque)")
				.setParameter("idLimiteMinimo", idLimiteMinimo).setParameter("listaIdItemEstoque", listaIdItemEstoque)
				.executeUpdate();
	}

	public void desassociarLimiteMinimoItemEstoque(Integer idLimiteMinimo) {
		entityManager
				.createQuery(
						"update ItemEstoque i set i.limiteMinimoEstoque.id = null where limiteMinimoEstoque.id = :idLimiteMinimo")
				.setParameter("idLimiteMinimo", idLimiteMinimo).executeUpdate();
	}

	public List<Integer> pesquisarIdItemEstoqueDentroLimiteMinimo(LimiteMinimoEstoque limite) {
		StringBuilder select = new StringBuilder("select i.id from ItemEstoque i ");
		Double medidaExterna = limite.getMedidaExterna();
		Double medidaInterna = limite.getMedidaInterna();
		Double comprimento = limite.getComprimento();
		final boolean contemMedida = medidaExterna != null || medidaInterna != null || comprimento != null;

		if (contemMedida) {
			select.append("where ");
			if (medidaExterna != null) {
				select.append(" i.medidaExterna = :medidaExterna and ");
			} else {
				select.append(" i.medidaExterna is null and ");
			}

			if (medidaInterna != null) {
				select.append(" i.medidaInterna = :medidaInterna and ");
			} else {
				select.append(" i.medidaInterna is null and ");
			}

			if (comprimento != null) {
				select.append(" i.comprimento = :comprimento and ");
			} else {
				select.append(" i.comprimento is null and ");
			}

		}

		select.append("i.formaMaterial = :formaMaterial and i.material = :material ");

		TypedQuery<Integer> query = entityManager.createQuery(select.toString(), Integer.class);
		if (contemMedida) {
			if (medidaExterna != null) {
				query.setParameter("medidaExterna", medidaExterna);
			}
			if (medidaInterna != null) {
				query.setParameter("medidaInterna", medidaInterna);
			}
			if (comprimento != null) {
				query.setParameter("comprimento", comprimento);
			}

		}

		query.setParameter("formaMaterial", limite.getFormaMaterial()).setParameter("material", limite.getMaterial());
		return query.getResultList();
	}

	public LimiteMinimoEstoque pesquisarLimiteById(Integer id) {
		return super.pesquisarById(LimiteMinimoEstoque.class, id);
	}

	public LimiteMinimoEstoque pesquisarLimiteMinimoEstoque(LimiteMinimoEstoque filtro) {
		StringBuilder select = new StringBuilder("select l from LimiteMinimoEstoque l where ");
		select.append("l.formaMaterial = :formaMaterial and l.material = :material ");

		StringBuilder order = new StringBuilder("order by ");

		if (filtro.getMedidaExterna() != null) {
			select.append("and l.medidaExterna = :medidaExterna ");
			order.append("l.medidaExterna, ");
		} else {
			select.append("and l.medidaExterna is null ");
		}

		if (filtro.getMedidaInterna() != null) {
			select.append("and l.medidaInterna = :medidaInterna ");
			order.append("l.medidaInterna, ");
		} else {
			select.append("and l.medidaInterna is null ");
		}

		if (filtro.getComprimento() != null) {
			select.append("and l.comprimento = :comprimento ");
			order.append("l.comprimento, ");
		} else {
			select.append("and l.comprimento is null ");
		}

		select.append(order.substring(0, order.lastIndexOf(", "))).append(" desc");
		TypedQuery<LimiteMinimoEstoque> query = entityManager.createQuery(select.toString(), LimiteMinimoEstoque.class);
		query.setParameter("formaMaterial", filtro.getFormaMaterial()).setParameter("material", filtro.getMaterial());

		if (filtro.getMedidaExterna() != null) {
			query.setParameter("medidaExterna", filtro.getMedidaExterna());
		}
		if (filtro.getMedidaInterna() != null) {
			query.setParameter("medidaInterna", filtro.getMedidaInterna());
		}
		if (filtro.getComprimento() != null) {
			query.setParameter("comprimento", filtro.getComprimento());
		}

		return QueryUtil.gerarRegistroUnico(query, LimiteMinimoEstoque.class, null);
	}
}
