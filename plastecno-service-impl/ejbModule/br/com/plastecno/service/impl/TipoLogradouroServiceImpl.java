package br.com.plastecno.service.impl;

import java.util.Arrays;
import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import br.com.plastecno.service.TipoLogradouroService;
import br.com.plastecno.service.constante.TipoLogradouro;

@Stateless
public class TipoLogradouroServiceImpl implements TipoLogradouroService {
	@PersistenceContext(unitName="plastecno")
	private EntityManager entityManager;
		
	@SuppressWarnings("unchecked")
	@Override
	public TipoLogradouro pesquisarByDescricao(String descricao) {		
		StringBuilder select = new StringBuilder("select t from br.com.plastecno.service.entity.TipoLogradouro t where t.descricao =:descricao ");
		Query query = this.entityManager.createQuery(select.toString());
		query.setParameter("descricao", descricao);
		List<TipoLogradouro> lista = query.getResultList();
		return lista.size() == 1 ? lista.get(0) : null;
	}

	@Override
	public List<TipoLogradouro> pesquisar() {
		return Arrays.asList(TipoLogradouro.values());
	}

	@Override
	public TipoLogradouro pesquisarById(String id) {
		return TipoLogradouro.valueOf(id);
	}
}
