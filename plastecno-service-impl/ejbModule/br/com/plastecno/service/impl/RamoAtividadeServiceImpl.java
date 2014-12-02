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
import br.com.plastecno.service.dao.impl.GenericDAOImpl;
import br.com.plastecno.service.entity.RamoAtividade;
import br.com.plastecno.service.exception.BusinessException;
import br.com.plastecno.service.wrapper.PaginacaoWrapper;
import br.com.plastecno.util.StringUtils;
import br.com.plastecno.validacao.ValidadorInformacao;

@Stateless
@TransactionManagement(TransactionManagementType.CONTAINER)
public class RamoAtividadeServiceImpl implements RamoAtividadeService {
	@PersistenceContext(unitName="plastecno")
	private EntityManager entityManager;
	
	private GenericDAOImpl genericDAO;
	
	@PostConstruct
	public void init() {
		this.genericDAO = new GenericDAOImpl(this.entityManager);
	}
	
	
	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public RamoAtividade inserir(RamoAtividade ramoAtividade) throws BusinessException {
		
		ValidadorInformacao.validar(ramoAtividade);
		
		if(this.isSiglaExistente(ramoAtividade.getId(), ramoAtividade.getSigla())) {
			throw new BusinessException("A sigla do ramo de atividade ja existe no sistema");			
		}
		
		return this.entityManager.merge(ramoAtividade);		
	}
	
	@Override
	public boolean isSiglaExistente(Integer id, String sigla) {
		return this.genericDAO.isEntidadeExistente(RamoAtividade.class, id, "sigla", sigla);
	}
	

	@Override
	public List<RamoAtividade> pesquisar() {
		return this.pesquisar(null);
	}
	
	@Override
	public List<RamoAtividade> pesquisarAtivo() {
		return this.pesquisar(true);
	}
	
	@Override
	@SuppressWarnings("unchecked")
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
	public List<RamoAtividade> pesquisarBy(RamoAtividade filtro) {
		return this.pesquisarBy(filtro, null, null, null);
	}
	
	@Override
	public PaginacaoWrapper<RamoAtividade> paginarRamoAtividade(RamoAtividade filtro, Boolean apenasAtivos,  
			Integer indiceRegistroInicial, Integer numeroMaximoRegistros) {
		
		return new PaginacaoWrapper<RamoAtividade>(
				this.pesquisarTotalRegistros(filtro, apenasAtivos), 
				this.pesquisarBy(filtro, apenasAtivos, indiceRegistroInicial, numeroMaximoRegistros));
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public Long pesquisarTotalRegistros(RamoAtividade filtro, Boolean apenasRamoAtividadeAtivo) {
		if (filtro == null){
			return 0L;
		}
		
		final StringBuilder select = new StringBuilder("SELECT count(r.id) FROM RamoAtividade r ");
		this.gerarRestricaoPesquisa(filtro, apenasRamoAtividadeAtivo, select);
		Query query = this.gerarQueryPesquisa(filtro, select);
		List<Long> lista = query.getResultList();
		return lista.size() == 1 ? lista.get(0) : 0L; 
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public List<RamoAtividade> pesquisarBy(RamoAtividade filtro, Boolean apenasAtivos,  Integer indiceRegistroInicial, Integer numeroMaximoRegistros) {
		if (filtro == null){
			return Collections.emptyList();
		}
		
		final StringBuilder select = new StringBuilder("SELECT r FROM RamoAtividade r ");
		this.gerarRestricaoPesquisa(filtro, apenasAtivos, select);
		
		select.append("order by r.sigla ");
		
		Query query = this.gerarQueryPesquisa(filtro, select);
		
		if(indiceRegistroInicial != null && indiceRegistroInicial >= 0 && numeroMaximoRegistros != null && numeroMaximoRegistros >= 0) {
			query.setFirstResult(indiceRegistroInicial).setMaxResults(numeroMaximoRegistros);
		}
		return query.getResultList();
	}
	
	private Query gerarQueryPesquisa (RamoAtividade filtro, StringBuilder select) {
		Query query = this.entityManager.createQuery(select.toString());
		if (StringUtils.isNotEmpty(filtro.getSigla())) {
			query.setParameter("sigla", "%"+filtro.getSigla()+"%");
		}
		
		if (StringUtils.isNotEmpty(filtro.getDescricao())) {
			query.setParameter("descricao", "%"+filtro.getDescricao()+"%");
		}
		return query;
	}
	
	private void gerarRestricaoPesquisa (RamoAtividade filtro, Boolean apenasAtivos, StringBuilder select) {
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
		
		if(restricao.length() > 0) {
			select.append("WHERE ");
			select.append(restricao);
			select.delete(select.lastIndexOf("AND"), select.length()-1);
		}
	}
	
	@Override
	public void remover(RamoAtividade ramoAtividade) {
		this.entityManager.remove(this.entityManager.merge(ramoAtividade));
	}

	@Override
	public boolean isSiglaExistente(String sigla) {
		return this.genericDAO.isEntidadeExistente(RamoAtividade.class, "sigla", sigla);
	}
	
	@Override
	public void desativar (Integer id) {
		Query query = this.entityManager.createQuery("update RamoAtividade r set r.ativo = false where r.id = :id");
		query.setParameter("id", id);
		query.executeUpdate();		
	}

	@SuppressWarnings("unchecked")
	@Override
	public RamoAtividade pesquisarById(Integer id) {
		Query query = this.entityManager.createQuery("SELECT r FROM RamoAtividade r where r.id = :id");
		query.setParameter("id", id);
		List<RamoAtividade> lista = query.getResultList();
		return lista.size() == 1 ? lista.get(0) : null;
	}
}
