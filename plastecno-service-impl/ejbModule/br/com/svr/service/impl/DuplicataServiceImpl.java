package br.com.svr.service.impl;

import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import br.com.svr.service.DuplicataService;
import br.com.svr.service.dao.NFeDuplicataDAO;
import br.com.svr.service.entity.NFeDuplicata;
import br.com.svr.service.exception.BusinessException;
import br.com.svr.service.nfe.constante.TipoBanco;
import br.com.svr.service.nfe.constante.TipoSituacaoDuplicata;
import br.com.svr.service.wrapper.Periodo;
import br.com.svr.util.DateUtils;

@Stateless
public class DuplicataServiceImpl implements DuplicataService {

	@PersistenceContext(name = "svr")
	private EntityManager entityManager;

	private NFeDuplicataDAO nFeDuplicataDAO;

	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public void alterarDuplicataById(NFeDuplicata duplicata) throws BusinessException {
		if (duplicata == null || duplicata.getId() == null) {
			throw new BusinessException("Não é possível alterar duplicata nula ou com código nulo.");
		}

		if (duplicata.getDataVencimento() == null) {
			throw new BusinessException("A data de vencimento da duplicata não pode ser nula.");
		}
		if (duplicata.getValor() == null) {
			throw new BusinessException("O valor da duplicata não pode ser nulo.");
		}
		TipoBanco tpBanco = TipoBanco.getTipoBanco(duplicata.getCodigoBanco());
		nFeDuplicataDAO.alterarDuplicataById(duplicata.getId(), duplicata.getDataVencimento(), duplicata.getValor(),
				tpBanco != null ? tpBanco.getCodigo() : null, tpBanco != null ? tpBanco.getNome() : null,
				duplicata.getTipoSituacaoDuplicata());
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public void atualizarSituacaoDuplicataVencida() {
		entityManager
				.createQuery(
						"update NFeDuplicata d set d.tipoSituacaoDuplicata =:tipoVencida where d.dataVencimento >= :dataAtual and d.tipoSituacaoDuplicata =:tipoAVencer")
				.setParameter("tipoVencida", TipoSituacaoDuplicata.VENCIDO)
				.setParameter("tipoAVencer", TipoSituacaoDuplicata.A_VENCER).setParameter("dataAtual", new Date());
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public void cancelarLiquidacaoDuplicataById(Integer idDuplicata) throws BusinessException {
		if (idDuplicata == null) {
			return;
		}
		Date dtVenc = nFeDuplicataDAO.pesquisarDataVencimentoById(idDuplicata);
		nFeDuplicataDAO
				.alterarSituacaoById(idDuplicata,
						!DateUtils.isAnteriorDataAtual(dtVenc) ? TipoSituacaoDuplicata.A_VENCER
								: TipoSituacaoDuplicata.VENCIDO);
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public void configurarIdCliente() {
		List<Object[]> l = entityManager
				.createQuery("select d.id , d.nFe.idPedido from NFeDuplicata d", Object[].class).getResultList();
		for (Object[] o : l) {

			entityManager
					.createQuery(
							"update NFeDuplicata d set d.idCliente = (select p.cliente.id from Pedido p where p.id = :idPedido) where d.id = :id")
					.setParameter("idPedido", o[1]).setParameter("id", o[0]).executeUpdate();
		}
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
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public void liquidarDuplicataById(Integer idDuplicata) throws BusinessException {
		if (idDuplicata == null) {
			return;
		}
		nFeDuplicataDAO.alterarSituacaoById(idDuplicata, TipoSituacaoDuplicata.LIQUIDADO);
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public NFeDuplicata pesquisarDuplicataById(Integer idDuplicata) {
		return nFeDuplicataDAO.pesquisarDuplicataById(idDuplicata);
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public List<NFeDuplicata> pesquisarDuplicataByIdCliente(Integer idCliente) {
		return nFeDuplicataDAO.pesquisarDuplicataByIdCliente(idCliente);
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public List<NFeDuplicata> pesquisarDuplicataByIdPedido(Integer idPedido) {
		return nFeDuplicataDAO.pesquisarDuplicataByIdPedido(idPedido);
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public List<NFeDuplicata> pesquisarDuplicataByNumeroNFe(Integer numeroNFe) {
		return nFeDuplicataDAO.pesquisarDuplicataByNumeroNFe(numeroNFe);
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public List<NFeDuplicata> pesquisarDuplicataByPeriodo(Periodo periodo) {
		return nFeDuplicataDAO.pesquisarDuplicataByPeriodo(periodo);
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public void removerDuplicataById(Integer idDuplicata) throws BusinessException {
		if (idDuplicata == null) {
			return;
		}
		nFeDuplicataDAO.removerDuplicataById(idDuplicata);
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public void removerDuplicataByNumeroNFe(Integer numeroNFe) {
		nFeDuplicataDAO.removerDuplicataByNumeroNFe(numeroNFe);
	}
}
