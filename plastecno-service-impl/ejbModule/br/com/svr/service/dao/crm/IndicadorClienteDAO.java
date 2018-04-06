package br.com.svr.service.dao.crm;

import javax.persistence.EntityManager;

import br.com.svr.service.dao.GenericDAO;
import br.com.svr.service.entity.crm.IndicadorCliente;
import br.com.svr.service.impl.util.QueryUtil;

public class IndicadorClienteDAO extends GenericDAO<IndicadorCliente> {

	public IndicadorClienteDAO(EntityManager entityManager) {
		super(entityManager);
	}

	public void alterarQuantidadeOrcamentos(Integer idCliente, Integer quantidade) {
		entityManager
				.createQuery(
						"update IndicadorCliente i set i.quantidadeOrcamentos=:quantidade where i.idCliente =:idCliente")
				.setParameter("idCliente", idCliente).setParameter("quantidade", quantidade).executeUpdate();
	}

	public void alterarValorOrcamentos(Integer idCliente, double valor) {
		entityManager
				.createQuery("update IndicadorCliente i set i.valorOrcamentos=:valor where i.idCliente =:idCliente")
				.setParameter("idCliente", idCliente).setParameter("valor", valor).executeUpdate();
	}

	public IndicadorCliente pesquisarIndicadorById(Integer idCliente) {
		return QueryUtil.gerarRegistroUnico(
				entityManager.createQuery("select i from IndicadorCliente i where i.idCliente =:idCliente")
						.setParameter("idCliente", idCliente), IndicadorCliente.class, null);
	}

	public int pesquisarQuantidadeOrcamentos(Integer idCliente) {
		return QueryUtil.gerarRegistroUnico(
				entityManager.createQuery(
						"select i.quantidadeOrcamentos from IndicadorCliente i where i.idCliente =:idCliente")
						.setParameter("idCliente", idCliente), Integer.class, 0);
	}

	public double pesquisarValorOrcamentos(Integer idCliente) {
		return QueryUtil.gerarRegistroUnico(
				entityManager.createQuery(
						"select i.valorOrcamentos from IndicadorCliente i where i.idCliente =:idCliente", Double.class)
						.setParameter("idCliente", idCliente), Double.class, 0d);
	}
}
