package br.com.plastecno.service.dao;

import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;

import br.com.plastecno.service.entity.NFeDuplicata;
import br.com.plastecno.service.impl.util.QueryUtil;
import br.com.plastecno.service.nfe.constante.TipoSituacaoDuplicata;
import br.com.plastecno.service.wrapper.Periodo;

public class NFeDuplicataDAO extends GenericDAO<NFeDuplicata> {
	public NFeDuplicataDAO(EntityManager entityManager) {
		super(entityManager);
	}

	public void alterarDataVendimentoById(Integer idDuplicata, Date dataVencimento, Double valor) {
		entityManager
				.createQuery(
						"update NFeDuplicata d set d.dataVencimento =:dataVencimento, d.valor =:valor where d.id = :idDuplicata")
				.setParameter("dataVencimento", dataVencimento).setParameter("idDuplicata", idDuplicata)
				.setParameter("valor", valor).executeUpdate();
	}

	public void alterarSituacaoById(Integer idDuplicata, TipoSituacaoDuplicata tipoSituacaoDuplicata) {
		entityManager
				.createQuery(
						"update NFeDuplicata d set d.tipoSituacaoDuplicata=:tipoSituacaoDuplicata where d.id = :idDuplicata")
				.setParameter("tipoSituacaoDuplicata", tipoSituacaoDuplicata).setParameter("idDuplicata", idDuplicata)
				.executeUpdate();
	}

	public NFeDuplicata pesquisarDuplicataById(Integer idDuplicata) {
		return QueryUtil
				.gerarRegistroUnico(
						entityManager
								.createQuery(
										"select new NFeDuplicata(d.dataVencimento, d.id, d.tipoSituacaoDuplicata, d.valor) from NFeDuplicata d where d.id =:idDuplicata")
								.setParameter("idDuplicata", idDuplicata), NFeDuplicata.class, null);
	}

	public List<NFeDuplicata> pesquisarDuplicataByPeriodo(Periodo periodo) {
		return entityManager
				.createQuery(
						"select new NFeDuplicata(d.dataVencimento, d.id, d.nomeCliente, d.nFe.numero, d.tipoSituacaoDuplicata, d.valor) from NFeDuplicata d where d.dataVencimento>= :dataInicial and d.dataVencimento<= :dataFinal",
						NFeDuplicata.class).setParameter("dataInicial", periodo.getInicio())
				.setParameter("dataFinal", periodo.getFim()).getResultList();
	}

	public void removerDuplicataById(Integer idDuplicata) {
		entityManager.createQuery("delete NFeDuplicata d where d.id = :idDuplicata")
				.setParameter("idDuplicata", idDuplicata).executeUpdate();
	}

	public void removerDuplicataByNumeroNFe(Integer numeroNFe) {
		entityManager.createQuery("delete NFeDuplicata d where d.nFe.numero =:numeroNFe")
				.setParameter("numeroNFe", numeroNFe).executeUpdate();
	}
}