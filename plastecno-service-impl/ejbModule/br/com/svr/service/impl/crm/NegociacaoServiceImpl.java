package br.com.svr.service.impl.crm;

import javax.annotation.PostConstruct;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import br.com.svr.service.constante.crm.CategoriaNegociacao;
import br.com.svr.service.constante.crm.SituacaoNegociacao;
import br.com.svr.service.crm.NegociacaoService;
import br.com.svr.service.dao.crm.NegociacaoDAO;
import br.com.svr.service.entity.Pedido;
import br.com.svr.service.entity.Usuario;
import br.com.svr.service.entity.crm.Negociacao;
import br.com.svr.service.exception.BusinessException;

public class NegociacaoServiceImpl implements NegociacaoService {
	@PersistenceContext(name = "svr")
	private EntityManager entityManager;

	private NegociacaoDAO negociacaoDAO;

	@PostConstruct
	public void init() {
		negociacaoDAO = new NegociacaoDAO(entityManager);
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public Integer inserirNegociacao(Integer idOrcamento, Integer idVendedor) throws BusinessException {
		if (idOrcamento == null) {
			throw new BusinessException("É necessário um ID de orçamento para a inclusão de uma negociação");
		}

		Negociacao n = new Negociacao();
		n.setCategoriaNegociacao(CategoriaNegociacao.PROPOSTA_CLIENTE);
		n.setOrcamento(new Pedido(idOrcamento));
		n.setSituacaoNegociacao(SituacaoNegociacao.ABERTO);

		// Aqui eh possivel que outro vendedor realize uma negociacao iniciado
		// por outro vendedor no caso da ausencia do mesmo.
		n.setVendedor(new Usuario(idVendedor));
		return negociacaoDAO.inserir(n).getId();
	}
	

	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public Negociacao pesquisarById(Integer idNegociacao){
		return negociacaoDAO.pesquisarById(idNegociacao);
	}

}
