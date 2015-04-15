package br.com.plastecno.service.impl;

import java.util.Collections;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import br.com.plastecno.service.MaterialService;
import br.com.plastecno.service.RepresentadaService;
import br.com.plastecno.service.dao.MaterialDAO;
import br.com.plastecno.service.entity.Material;
import br.com.plastecno.service.entity.Representada;
import br.com.plastecno.service.exception.BusinessException;
import br.com.plastecno.service.impl.util.QueryUtil;
import br.com.plastecno.service.wrapper.PaginacaoWrapper;
import br.com.plastecno.util.StringUtils;
import br.com.plastecno.validacao.ValidadorInformacao;

@Stateless
@TransactionManagement(TransactionManagementType.CONTAINER)
public class MaterialServiceImpl implements MaterialService {

	@PersistenceContext(unitName = "plastecno")
	private EntityManager entityManager;

	@EJB
	private RepresentadaService representadaService;
	private MaterialDAO materialDAO;

	@Override
	public void desativar(Integer id) {
		materialDAO.desativar(id);
	}

	private Query gerarQueryPesquisa(Material filtro, StringBuilder select) {
		Query query = this.entityManager.createQuery(select.toString());
		if (StringUtils.isNotEmpty(filtro.getSigla())) {
			query.setParameter("sigla", "%" + filtro.getSigla() + "%");
		}
		return query;
	}

	private void gerarRestricaoPesquisa(Material filtro, Boolean apenasAtivos, StringBuilder select) {

		StringBuilder restricao = new StringBuilder();
		if (StringUtils.isNotEmpty(filtro.getSigla())) {
			restricao.append("r.sigla LIKE :sigla AND ");
		}

		if (apenasAtivos != null && Boolean.TRUE.equals(apenasAtivos)) {
			restricao.append("r.ativo = true AND ");
		} else if (apenasAtivos != null && Boolean.FALSE.equals(apenasAtivos)) {
			restricao.append("r.ativo = false AND ");
		}

		if (restricao.length() > 0) {
			select.append(" WHERE ");
			select.append(restricao);
			select.delete(select.lastIndexOf("AND"), select.length() - 1);
		}
	}

	@PostConstruct
	public void init() {
		this.materialDAO = new MaterialDAO(entityManager);
	}

	@Override
	public Integer inserir(Material material) throws BusinessException {
		return this.inserir(material, null);
	}

	@Override
	public Integer inserir(Material material, List<Integer> listaIdRepresentadaAssociada) throws BusinessException {

		// Adicionando as novas representadas
		if (listaIdRepresentadaAssociada != null && !listaIdRepresentadaAssociada.isEmpty()) {

			// Cogido para remover as representadas que estao associadas
			material.addRepresentada(this.pesquisarRepresentadasAssociadas(material.getId()));
			material.clearListaRepresentada();

			for (Integer idRepresentada : listaIdRepresentadaAssociada) {
				material.addRepresentada(this.representadaService.pesquisarById(idRepresentada));
			}
		}

		ValidadorInformacao.validar(material);

		if (material.getId() == null
				&& (material.getListaRepresentada() == null || material.getListaRepresentada().isEmpty())) {
			throw new BusinessException("Material deve ser associado ao menos a 1 representada");
		}

		if (this.isMaterialExistente(material.getSigla(), material.getId())) {
			throw new BusinessException("Material já existente com a sigla " + material.getSigla());
		}
		// Realizando o merge das associacoes das representadas
		return material.getId() != null ? materialDAO.alterar(material).getId() : materialDAO.inserir(material).getId();
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public boolean isMaterialAssociadoRepresentada(Integer idMaterial, Integer idRepresentada) {
		return materialDAO.isMaterialAssociadoRepresentada(idMaterial, idRepresentada);
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public boolean isMaterialExistente(Integer idMaterial) {
		return pesquisarById(idMaterial) != null;
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public boolean isMaterialExistente(String sigla, Integer idMaterial) {
		return materialDAO.isEntidadeExistente(Material.class, idMaterial, "sigla", sigla);
	}

	@Override
	public boolean isMaterialImportado(Integer idMaterial) {
		return materialDAO.isMaterialImportado(idMaterial);
	}

	public PaginacaoWrapper<Material> paginarMaterial(Material filtro, Boolean apenasAtivos,
			Integer indiceRegistroInicial, Integer numeroMaximoRegistros) {

		return new PaginacaoWrapper<Material>(this.pesquisarTotalRegistros(filtro, apenasAtivos), this.pesquisarBy(filtro,
				apenasAtivos, indiceRegistroInicial, numeroMaximoRegistros));

	}

	@Override
	public List<Material> pesquisarBy(Material filtro, Boolean apenasAtivos, Integer indiceRegistroInicial,
			Integer numeroMaximoRegistros) {

		if (filtro == null) {
			return Collections.emptyList();
		}

		StringBuilder select = new StringBuilder("SELECT r FROM Material r ");
		this.gerarRestricaoPesquisa(filtro, null, select);
		select.append(" order by r.sigla ");

		Query query = this.gerarQueryPesquisa(filtro, select);
		return QueryUtil.paginar(query, indiceRegistroInicial, numeroMaximoRegistros);
	}

	@Override
	public Material pesquisarById(Integer id) {
		return materialDAO.pesquisarById(id);
	}

	@Override
	public List<Material> pesquisarBySigla(String sigla) {
		return materialDAO.pesquisarBySigla(sigla);
	}

	@Override
	public List<Material> pesquisarBySigla(String sigla, Integer idRepresentada) {
		return materialDAO.pesquisarBySigla(sigla, idRepresentada);
	}

	@Override
	public List<Material> pesquisarMaterialAtivoBySigla(String sigla, Integer idRepresentada) {
		return materialDAO.pesquisarBySigla(sigla, idRepresentada, true);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Representada> pesquisarRepresentadasAssociadas(Integer idMaterial) {
		Query query = this.entityManager
				.createQuery("select new Representada(r.id, r.nomeFantasia) from Material m , IN (m.listaRepresentada) r where  m.id = :id order by r.nomeFantasia asc");
		query.setParameter("id", idMaterial);
		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Representada> pesquisarRepresentadasNaoAssociadas(Integer idMaterial) {
		List<Representada> listaRepresentada = this.pesquisarRepresentadasAssociadas(idMaterial);
		Query query = null;
		if (!listaRepresentada.isEmpty()) {
			query = this.entityManager
					.createQuery("select new Representada(r.id, r.nomeFantasia) from Representada r where  r not in (:listaRepresentada) order by r.nomeFantasia asc");
			query.setParameter("listaRepresentada", listaRepresentada);
		} else {
			query = this.entityManager.createQuery("select r from Representada r");
		}

		return query.getResultList();
	}

	@Override
	public Long pesquisarTotalRegistros(Material filtro, Boolean apenasAtivos) {
		if (filtro == null) {
			return 0L;
		}

		final StringBuilder select = new StringBuilder("SELECT count(r.id) FROM Material r ");
		this.gerarRestricaoPesquisa(filtro, apenasAtivos, select);
		Query query = this.gerarQueryPesquisa(filtro, select);
		return QueryUtil.gerarRegistroUnico(query, Long.class, null);
	}
}
