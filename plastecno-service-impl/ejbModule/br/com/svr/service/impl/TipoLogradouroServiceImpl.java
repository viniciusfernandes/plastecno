package br.com.svr.service.impl;

import java.util.Arrays;
import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import br.com.svr.service.TipoLogradouroService;
import br.com.svr.service.constante.TipoLogradouro;

@Stateless
public class TipoLogradouroServiceImpl implements TipoLogradouroService {
	@PersistenceContext(unitName="svr")
	private EntityManager entityManager;
		
	@Override
	public List<TipoLogradouro> pesquisar() {
		return Arrays.asList(TipoLogradouro.values());
	}

	@SuppressWarnings("unchecked")
	@Override
	public TipoLogradouro pesquisarByDescricao(String descricao) {		
		StringBuilder select = new StringBuilder("select t from br.com.svr.service.entity.TipoLogradouro t where t.descricao =:descricao ");
		Query query = this.entityManager.createQuery(select.toString());
		query.setParameter("descricao", descricao);
		List<TipoLogradouro> lista = query.getResultList();
		return lista.size() == 1 ? lista.get(0) : null;
	}

	@Override
	public TipoLogradouro pesquisarById(String id) {
		return TipoLogradouro.valueOf(id);
	}
}
