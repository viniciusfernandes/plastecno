package br.com.plastecno.service.dao;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import br.com.plastecno.service.entity.LimiteMinimoEstoque;

public class LimiteMinimoEstoqueDAO extends GenericDAO<LimiteMinimoEstoque> {
	private Logger logger = Logger.getLogger(LimiteMinimoEstoque.class.getName());

	public LimiteMinimoEstoqueDAO(EntityManager entityManager) {
		super(entityManager);
	}

	public void associarLimiteMinimoItemEstoque(Integer idLimiteMinimo, List<Integer> listaIdItemEstoque) {
		entityManager
				.createQuery(
						"update ItemEstoque i set i.limiteMinimoEstoque.id = :idLimiteMinimo where i.id in (:listaIdItemEstoque)")
				.setParameter("idLimiteMinimo", idLimiteMinimo).setParameter("listaIdItemEstoque", listaIdItemEstoque)
				.executeUpdate();
	}

	public List<Integer> pesquisarIdItemEstoqueDentroLimiteMinimo(LimiteMinimoEstoque limite, double tolerancia) {
		StringBuilder select = new StringBuilder("select i.id from ItemEstoque i ");
		Double medidaExterna = limite.getMedidaExterna();
		Double medidaInterna = limite.getMedidaInterna();
		Double comprimento = limite.getComprimento();
		final boolean contemMedida = medidaExterna != null || medidaInterna != null || comprimento != null;

		if (contemMedida) {
			select.append("where ");
			if (medidaExterna != null) {
				select.append(" ABS(i.medidaExterna - :medidaExterna) <= :tolerancia and ");
			}

			if (medidaInterna != null) {
				select.append(" ABS(i.medidaInterna - :medidaInterna) <= :tolerancia and ");
			}

			if (comprimento != null) {
				select.append(" ABS(i.comprimento - :comprimento) <= :tolerancia and ");
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

			query.setParameter("tolerancia", tolerancia);
		}

		query.setParameter("formaMaterial", limite.getFormaMaterial()).setParameter("material", limite.getMaterial());
		return query.getResultList();
	}

	public Integer pesquisarIdLimiteMinimoEstoque(LimiteMinimoEstoque filtro) {
		StringBuilder select = new StringBuilder("select l.id from LimiteMinimoEstoque l where ");
		select.append("l.formaMaterial = :formaMaterial and l.material = :material and ");
		select
				.append("l.medidaExterna = :medidaExterna and l.medidaInterna = :medidaInterna and l.comprimento = :comprimento ");
		TypedQuery<Integer> query = entityManager.createQuery(select.toString(), Integer.class);
		query.setParameter("formaMaterial", filtro.getFormaMaterial()).setParameter("material", filtro.getMaterial());
		query.setParameter("medidaExterna", filtro.getMedidaExterna())
				.setParameter("medidaInterna", filtro.getMedidaInterna()).setParameter("comprimento", filtro.getComprimento());

		List<Integer> listaId = query.getResultList();
		if (listaId.size() > 1) {
			logger.log(Level.WARNING, "Existem mais de 1 limite de estoque cadastrado. Total encontrado de " + listaId.size()
					+ " para o Limite minimo \"" + filtro.getDescricao() + "\"");
			return listaId.get(0);
		}
		return null;
	}
}
