package br.com.plastecno.service.impl;

import java.util.Collections;
import java.util.List;

import javax.ejb.Stateless;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import br.com.plastecno.service.PerfilAcessoService;
import br.com.plastecno.service.entity.PerfilAcesso;

@Stateless
@TransactionManagement(TransactionManagementType.CONTAINER)
public class PerfilAcessoServiceImpl implements PerfilAcessoService {
	@PersistenceContext(unitName="plastecno")
	private EntityManager entityManager;

	@SuppressWarnings("unchecked")
	@Override
	public List<PerfilAcesso> pesquisar() {
		return this.entityManager.createQuery("SELECT p FROM PerfilAcesso p order by p.descricao ").getResultList();
	}

	@Override
	public PerfilAcesso pesquisarById(Integer id) {
		return this.entityManager.find(PerfilAcesso.class, id);
	}	

	@Override
	@SuppressWarnings("unchecked")
	public List<PerfilAcesso> pesquisarById(List<Integer> listaIdPerfilAcesso) {
		
		if (listaIdPerfilAcesso == null || listaIdPerfilAcesso.isEmpty()) {
			return Collections.EMPTY_LIST;
		} else {
			Query query = this.entityManager.createQuery("SELECT p FROM PerfilAcesso p where p.id in (:listaIdPerfilAcesso) order by p.descricao ");
			query.setParameter("listaIdPerfilAcesso", listaIdPerfilAcesso);
			return query.getResultList();
		}
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public List<PerfilAcesso> pesquisarComplementaresById(List<Integer> listaIdPerfilAcesso) {
		Query query = null;
		if (listaIdPerfilAcesso == null || listaIdPerfilAcesso.isEmpty()) {
			query = this.entityManager.createQuery("SELECT p FROM PerfilAcesso p order by p.descricao ");
		} else {
			query = this.entityManager.createQuery("SELECT p FROM PerfilAcesso p where p.id not in (:listaIdPerfilAcesso) order by p.descricao ");
			query.setParameter("listaIdPerfilAcesso", listaIdPerfilAcesso);
			
		}
		return query.getResultList();
	}
}
