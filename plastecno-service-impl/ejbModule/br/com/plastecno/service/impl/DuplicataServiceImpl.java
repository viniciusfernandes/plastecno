package br.com.plastecno.service.impl;

import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import br.com.plastecno.service.DuplicataService;
import br.com.plastecno.service.dao.NFeDuplicataDAO;
import br.com.plastecno.service.entity.NFeDuplicata;
import br.com.plastecno.service.exception.BusinessException;
import br.com.plastecno.service.wrapper.Periodo;

@Stateless
public class DuplicataServiceImpl implements DuplicataService {

	@PersistenceContext(name = "plastecno")
	private EntityManager entityManager;

	private NFeDuplicataDAO nFeDuplicataDAO;

	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public void alterarDataVendimentoById(Integer idDuplicata, Date dataVencimento) throws BusinessException {
		if (idDuplicata == null) {
			return;
		}
		if (dataVencimento == null) {
			throw new BusinessException("A data de vencimento da duplicata não pode ser nula.");
		}
		nFeDuplicataDAO.alterarDataVendimentoById(idDuplicata, dataVencimento);
	}

	@PostConstruct
	public void init() {
		nFeDuplicataDAO = new NFeDuplicataDAO(entityManager);
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public void inserirDuplicata(Integer numeroNFe, List<NFeDuplicata> listaDuplicata) {
		// Devemos remover as duplicatas cadastradas no sistema pois nao temos
		// como saber qual duplicata esta sendo editada pelo usuario pois o
		// modulo de emissao de NFe nao contem os IDs das duplicatas, entao para
		// garantir a integridade dos dados vamos remover e depois incluir
		// novamente todas elas.
		nFeDuplicataDAO.removerDuplicataByNumeroNFe(numeroNFe);
		for (NFeDuplicata d : listaDuplicata) {
			nFeDuplicataDAO.alterar(d);
		}
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public List<NFeDuplicata> pesquisarDuplicataByPeriodo(Periodo periodo) {
		return nFeDuplicataDAO.pesquisarDuplicataByPeriodo(periodo);
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public NFeDuplicata pesquisarDuplicataById(Integer idDuplicata) {
		return nFeDuplicataDAO.pesquisarDuplicataById(idDuplicata);
	}
}
