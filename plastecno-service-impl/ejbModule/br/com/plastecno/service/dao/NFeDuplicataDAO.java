package br.com.plastecno.service.dao;

import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

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

	public Date pesquisarDataVencimentoById(Integer idDuplicata) {
		return QueryUtil.gerarRegistroUnico(
				entityManager.createQuery("select d.dataVencimento from NFeDuplicata d where d.id=:idDuplicata")
						.setParameter("idDuplicata", idDuplicata), Date.class, null);
	}

	public List<NFeDuplicata> pesquisarDuplicataByCNPJCliente(String cnpj, Date dataInicial, Date dataFinal) {
		StringBuilder s = new StringBuilder();
		s.append("select new NFeDuplicata(d.dataVencimento, d.id, d.nomeCliente, d.nFe.numero, d.tipoSituacaoDuplicata, d.valor) from NFeDuplicata d where ");
		if (dataInicial != null && dataFinal != null) {
			s.append("and d.dataVencimento >=:dataInicial and d.dataVencimento <=:dataFinal");
		}
		TypedQuery<NFeDuplicata> q = entityManager.createQuery(s.toString(), NFeDuplicata.class);
		if (dataInicial != null && dataFinal != null) {
			q.setParameter("dataInicial", dataInicial).setParameter("dataFinal", dataFinal);

		}
		return q.getResultList();
	}

	public NFeDuplicata pesquisarDuplicataById(Integer idDuplicata) {
		return QueryUtil
				.gerarRegistroUnico(
						entityManager
								.createQuery(
										"select new NFeDuplicata(d.dataVencimento, d.id, d.parcela, d.tipoSituacaoDuplicata, d.totalParcelas, d.valor) from NFeDuplicata d where d.id =:idDuplicata")
								.setParameter("idDuplicata", idDuplicata), NFeDuplicata.class, null);
	}

	public List<NFeDuplicata> pesquisarDuplicataByIdCliente(Integer idCliente) {
		return pesquisarDuplicataByNumeroNFeOuPedido(null, null, idCliente);
	}

	public List<NFeDuplicata> pesquisarDuplicataByIdPedido(Integer idPedido) {
		return pesquisarDuplicataByNumeroNFeOuPedido(null, idPedido, null);
	}

	public List<NFeDuplicata> pesquisarDuplicataByNumeroNFe(Integer numeroNFe) {
		return pesquisarDuplicataByNumeroNFeOuPedido(numeroNFe, null, null);
	}

	private List<NFeDuplicata> pesquisarDuplicataByNumeroNFeOuPedido(Integer numeroNFe, Integer idPedido,
			Integer idCliente) {
		StringBuilder s = new StringBuilder();
		s.append("select new NFeDuplicata(d.dataVencimento, d.id, d.nomeCliente, d.nFe.numero, d.parcela, d.tipoSituacaoDuplicata, d.totalParcelas, d.valor) from NFeDuplicata d ");

		if (numeroNFe != null) {
			s.append("where d.nFe.numero =:numeroNFe ");
		} else if (idPedido != null) {
			s.append("where d.nFe.idPedido =:idPedido ");
		} else if (idCliente != null) {
			s.append("where d.idCliente =:idCliente ");
		}

		TypedQuery<NFeDuplicata> q = entityManager.createQuery(s.toString(), NFeDuplicata.class);
		if (numeroNFe != null) {
			q.setParameter("numeroNFe", numeroNFe);
		} else if (idPedido != null) {
			q.setParameter("idPedido", idPedido);
		} else if (idCliente != null) {
			q.setParameter("idCliente", idCliente);
		}
		return q.getResultList();
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
