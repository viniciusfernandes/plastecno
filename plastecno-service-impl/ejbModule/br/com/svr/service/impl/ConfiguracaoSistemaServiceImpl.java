package br.com.svr.service.impl;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import br.com.svr.service.ConfiguracaoSistemaService;
import br.com.svr.service.constante.ParametroConfiguracaoSistema;
import br.com.svr.service.dao.ConfiguracaoSistemaDAO;

@Stateless
public class ConfiguracaoSistemaServiceImpl implements ConfiguracaoSistemaService {

	private ConfiguracaoSistemaDAO configuracaoSistemaDAO;

	@PersistenceContext(name = "svr")
	private EntityManager entityManager;

	@PostConstruct
	public void init() {
		this.configuracaoSistemaDAO = new ConfiguracaoSistemaDAO(entityManager);
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public String pesquisar(ParametroConfiguracaoSistema parametro) {
		return configuracaoSistemaDAO.pesquisar(parametro);
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public List<Object[]> pesquisarCFOP() {
		return configuracaoSistemaDAO.pesquisarCFOP();
	}
}
