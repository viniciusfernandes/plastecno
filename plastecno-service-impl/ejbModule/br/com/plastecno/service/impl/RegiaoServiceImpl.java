package br.com.plastecno.service.impl;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.Stateless;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import br.com.plastecno.service.RegiaoService;
import br.com.plastecno.service.dao.GenericDAO;
import br.com.plastecno.service.entity.Bairro;
import br.com.plastecno.service.entity.Regiao;
import br.com.plastecno.service.exception.BusinessException;
import br.com.plastecno.service.impl.util.QueryUtil;
import br.com.plastecno.service.wrapper.PaginacaoWrapper;
import br.com.plastecno.util.StringUtils;
import br.com.plastecno.validacao.ValidadorInformacao;

@Stateless
@TransactionManagement(TransactionManagementType.CONTAINER)
public class RegiaoServiceImpl implements RegiaoService {

	@PersistenceContext(unitName = "plastecno")
	private EntityManager entityManager;

	private GenericDAO genericDAO;

	@PostConstruct
	public void init() {
		this.genericDAO = new GenericDAO(this.entityManager);
	}

	@Override
	public Integer inserir(Regiao regiao, List<Integer> listaIdBairroAssociado) throws BusinessException {
		if (listaIdBairroAssociado != null) {
			Bairro bairro = null;
			for (Integer idBairro : listaIdBairroAssociado) {
				if (!regiao.contemBairro(idBairro)) {
					bairro = new Bairro();
					bairro.setId(idBairro);
					regiao.addBairro(bairro);
				}
			}
		}
		ValidadorInformacao.validar(regiao);
		if (this.isNomeRegiaoExistente(regiao.getId(), regiao.getNome())) {
			throw new BusinessException("Região já existente com o nome " + regiao.getNome());
		}

		return this.entityManager.merge(regiao).getId();
	}

	@Override
	public boolean isNomeRegiaoExistente(Integer idRegiao, String nomeRegiao) {
		return this.genericDAO.isEntidadeExistente(Regiao.class, idRegiao, "nome", nomeRegiao);
	}

	@Override
	public PaginacaoWrapper<Regiao> paginarRegiao(Regiao filtro, Integer indiceRegistroInicial,
			Integer numeroMaximoRegistros) {
		return new PaginacaoWrapper<Regiao>(this.pesquisatTotalRegistros(filtro), this.pesquisarBy(filtro,
				indiceRegistroInicial, numeroMaximoRegistros));
	}

	@Override
	public List<Regiao> pesquisarBy(Regiao filtro) {
		return this.pesquisarBy(filtro, null, null);
	}

	@Override
	public List<Regiao> pesquisarBy(Regiao filtro, Integer indiceRegistroInicial, Integer numeroMaximoRegistros) {
		StringBuilder select = new StringBuilder("select r from Regiao r ");

		if (!StringUtils.isEmpty(filtro.getNome())) {
			select.append("where r.nome like :nome ");
		}

		Query query = this.entityManager.createQuery(select.toString());
		if (!StringUtils.isEmpty(filtro.getNome())) {
			query.setParameter("nome", "%" + filtro.getNome() + "%");
		}
		select.append(" order by r.nome ");
		return QueryUtil.paginar(query, indiceRegistroInicial, numeroMaximoRegistros);
	}

	@Override
	public Regiao pesquisarById(Integer id) {
		return QueryUtil.gerarRegistroUnico(
				this.entityManager.createQuery("select r from Regiao r join fetch r.listaBairro where r.id = :id")
						.setParameter("id", id), Regiao.class, null);
	}

	@Override
	public Long pesquisatTotalRegistros(Regiao filtro) {
		StringBuilder select = new StringBuilder("select count(r.id) from Regiao r ");

		if (!StringUtils.isEmpty(filtro.getNome())) {
			select.append("where r.nome like :nome ");
		}

		Query query = this.entityManager.createQuery(select.toString());
		if (!StringUtils.isEmpty(filtro.getNome())) {
			query.setParameter("nome", "%" + filtro.getNome() + "%");
		}
		return QueryUtil.gerarRegistroUnico(query, Long.class, 0L);
	}

	@Override
	public void remover(Integer id) {
		this.entityManager.remove(this.pesquisarById(id));
	}
}
