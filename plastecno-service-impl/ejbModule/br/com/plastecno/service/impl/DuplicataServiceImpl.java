package br.com.plastecno.service.impl;

import java.util.List;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import br.com.plastecno.service.DuplicataService;
import br.com.plastecno.service.entity.NFeDuplicata;
import br.com.plastecno.service.wrapper.Periodo;

@Stateless
public class DuplicataServiceImpl implements DuplicataService {

	@PersistenceContext(name = "plastecno")
	private EntityManager entityManager;

	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public List<NFeDuplicata> pesquisarDuplicataByPeriodo(Periodo periodo) {
		return entityManager
				.createQuery(
						"select new NFeDuplicata(d.dataVencimento, d.id, d.nFe.numero, d.tipoSituacaoDuplicata, d.valor) from NFeDuplicata d where d.dataVencimento>= :dataInicial and d.dataVencimento<= :dataFinal",
						NFeDuplicata.class).setParameter("dataInicial", periodo.getInicio())
				.setParameter("dataFinal", periodo.getFim()).getResultList();
	}
}
