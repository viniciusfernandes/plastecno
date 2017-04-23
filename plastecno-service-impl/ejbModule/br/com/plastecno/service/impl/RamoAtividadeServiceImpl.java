package br.com.plastecno.service.impl;

import java.util.Collections;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import br.com.plastecno.service.RamoAtividadeService;
import br.com.plastecno.service.dao.RamoAtividadeDAO;
import br.com.plastecno.service.entity.RamoAtividade;
import br.com.plastecno.service.exception.BusinessException;
import br.com.plastecno.service.impl.util.QueryUtil;
import br.com.plastecno.service.wrapper.PaginacaoWrapper;
import br.com.plastecno.util.StringUtils;
import br.com.plastecno.validacao.ValidadorInformacao;

@Stateless
@TransactionManagement(TransactionManagementType.CONTAINER)
public class RamoAtividadeServiceImpl implements RamoAtividadeService {
	@PersistenceContext(unitName = "plastecno")
	private EntityManager entityManager;

	private RamoAtividadeDAO ramoAtividadeDAO;

	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public void desativar(Integer id) {
		Query query = this.entityManager.createQuery("update RamoAtividade r set r.ativo = false where r.id = :id");
		query.setParameter("id", id);
		query.executeUpdate();
	}

	private Query gerarQueryPesquisa(RamoAtividade filtro, StringBuilder select) {
		Query query = this.entityManager.createQuery(select.toString());
		if (StringUtils.isNotEmpty(filtro.getSigla())) {
			query.setParameter("sigla", "%" + filtro.getSigla() + "%");
		}

		if (StringUtils.isNotEmpty(filtro.getDescricao())) {
			query.setParameter("descricao", "%" + filtro.getDescricao() + "%");
		}
		return query;
	}

	private void gerarRestricaoPesquisa(RamoAtividade filtro, Boolean apenasAtivos, StringBuilder select) {
		StringBuilder restricao = new StringBuilder();
		if (StringUtils.isNotEmpty(filtro.getSigla())) {
			restricao.append("r.sigla LIKE :sigla AND ");
		}

		if (StringUtils.isNotEmpty(filtro.getDescricao())) {
			restricao.append("r.descricao LIKE :descricao AND ");
		}

		if (apenasAtivos != null && Boolean.TRUE.equals(apenasAtivos)) {
			restricao.append("r.ativo = true AND ");
		} else if (apenasAtivos != null && Boolean.FALSE.equals(apenasAtivos)) {
			restricao.append("r.ativo = false AND ");
		}

		if (restricao.length() > 0) {
			select.append("WHERE ");
			select.append(restricao);
			select.delete(select.lastIndexOf("AND"), select.length() - 1);
		}
	}

	@PostConstruct
	public void init() {
		this.ramoAtividadeDAO = new RamoAtividadeDAO(entityManager);
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public RamoAtividade inserir(RamoAtividade ramoAtividade) throws BusinessException {

		ValidadorInformacao.validar(ramoAtividade);

		if (this.isSiglaExistente(ramoAtividade.getId(), ramoAtividade.getSigla())) {
			throw new BusinessException("A sigla do ramo de atividade ja existe no sistema");
		}

		return ramoAtividadeDAO.alterar(ramoAtividade);
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public boolean isSiglaExistente(Integer id, String sigla) {
		return this.ramoAtividadeDAO.isEntidadeExistente(RamoAtividade.class, id, "sigla", sigla);
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public boolean isSiglaExistente(String sigla) {
		return this.ramoAtividadeDAO.isEntidadeExistente(RamoAtividade.class, "sigla", sigla);
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public PaginacaoWrapper<RamoAtividade> paginarRamoAtividade(RamoAtividade filtro, Boolean apenasAtivos,
			Integer indiceRegistroInicial, Integer numeroMaximoRegistros) {

		return new PaginacaoWrapper<RamoAtividade>(this.pesquisarTotalRegistros(filtro, apenasAtivos),
				this.pesquisarBy(filtro, apenasAtivos, indiceRegistroInicial, numeroMaximoRegistros));
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public List<RamoAtividade> pesquisar() {
		return this.pesquisar(null);
	}

	@Override
	@SuppressWarnings("unchecked")
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public List<RamoAtividade> pesquisar(Boolean ativo) {

		StringBuilder select = new StringBuilder("SELECT r FROM RamoAtividade r ");
		if (ativo != null) {
			select.append("where r.ativo = :ativo ");
		}
		select.append("order by r.sigla ");

		Query query = this.entityManager.createQuery(select.toString());

		if (ativo != null) {
			query.setParameter("ativo", ativo);
		}
		return query.getResultList();
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public List<RamoAtividade> pesquisarAtivo() {
		return this.pesquisar(true);
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public List<RamoAtividade> pesquisarBy(RamoAtividade filtro) {
		return this.pesquisarBy(filtro, null, null, null);
	}

	@Override
	@SuppressWarnings("unchecked")
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public List<RamoAtividade> pesquisarBy(RamoAtividade filtro, Boolean apenasAtivos, Integer indiceRegistroInicial,
			Integer numeroMaximoRegistros) {
		if (filtro == null) {
			return Collections.emptyList();
		}

		final StringBuilder select = new StringBuilder("SELECT r FROM RamoAtividade r ");
		this.gerarRestricaoPesquisa(filtro, apenasAtivos, select);

		select.append("order by r.sigla ");

		Query query = this.gerarQueryPesquisa(filtro, select);

		if (indiceRegistroInicial != null && indiceRegistroInicial >= 0 && numeroMaximoRegistros != null
				&& numeroMaximoRegistros >= 0) {
			query.setFirstResult(indiceRegistroInicial).setMaxResults(numeroMaximoRegistros);
		}
		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public RamoAtividade pesquisarById(Integer id) {
		Query query = this.entityManager.createQuery("SELECT r FROM RamoAtividade r where r.id = :id");
		query.setParameter("id", id);
		List<RamoAtividade> lista = query.getResultList();
		return lista.size() == 1 ? lista.get(0) : null;
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public RamoAtividade pesquisarRamoAtividadePadrao() throws BusinessException {
		RamoAtividade r = ramoAtividadeDAO.pesquisarRamoAtividadePadrao();
		if (r != null) {
			return r;
		}
		r = new RamoAtividade();
		r.setDescricao("A DEFINIR");
		r.setSigla("NDEFINIDO");
		return inserir(r);
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public String pesquisarSigleById(Integer idRamo) {
		return QueryUtil.gerarRegistroUnico(
				entityManager.createQuery("select r.sigla from RamoAtividade r where r.id=:idRamo").setParameter(
						"idRamo", idRamo), String.class, null);
	}

	@SuppressWarnings("unchecked")
	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public Long pesquisarTotalRegistros(RamoAtividade filtro, Boolean apenasRamoAtividadeAtivo) {
		if (filtro == null) {
			return 0L;
		}

		final StringBuilder select = new StringBuilder("SELECT count(r.id) FROM RamoAtividade r ");
		this.gerarRestricaoPesquisa(filtro, apenasRamoAtividadeAtivo, select);
		Query query = this.gerarQueryPesquisa(filtro, select);
		List<Long> lista = query.getResultList();
		return lista.size() == 1 ? lista.get(0) : 0L;
	}

	@Override
	public void remover(RamoAtividade ramoAtividade) {
		this.entityManager.remove(this.entityManager.merge(ramoAtividade));
	}
}
