package br.com.plastecno.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import br.com.plastecno.service.ContatoService;
import br.com.plastecno.service.LogradouroService;
import br.com.plastecno.service.TransportadoraService;
import br.com.plastecno.service.dao.TransportadoraDAO;
import br.com.plastecno.service.entity.ContatoTransportadora;
import br.com.plastecno.service.entity.LogradouroTransportadora;
import br.com.plastecno.service.entity.Transportadora;
import br.com.plastecno.service.exception.BusinessException;
import br.com.plastecno.service.impl.util.QueryUtil;
import br.com.plastecno.service.wrapper.PaginacaoWrapper;
import br.com.plastecno.util.StringUtils;
import br.com.plastecno.validacao.ValidadorInformacao;

@Stateless
public class TransportadoraServiceImpl implements TransportadoraService {

	@EJB
	private ContatoService contatoService;

	@PersistenceContext(unitName = "plastecno")
	private EntityManager entityManager;

	@EJB
	private LogradouroService logradouroService;

	private TransportadoraDAO transportadoraDAO;

	@Override
	public Integer desativar(Integer id) {
		Query query = this.entityManager.createQuery("update Transportadora r set r.ativo = false where r.id = :id");
		query.setParameter("id", id);
		return query.executeUpdate();
	}

	private Query gerarQueryPesquisa(Transportadora filtro, StringBuilder select) {
		Query query = this.entityManager.createQuery(select.toString());

		if (StringUtils.isNotEmpty(filtro.getNomeFantasia())) {
			query.setParameter("nomeFantasia", "%" + filtro.getNomeFantasia() + "%");
		}

		if (StringUtils.isNotEmpty(filtro.getRazaoSocial())) {
			query.setParameter("razaoSocial", "%" + filtro.getRazaoSocial() + "%");
		}

		if (StringUtils.isNotEmpty(filtro.getCnpj())) {
			query.setParameter("cnpj", "%" + filtro.getCnpj() + "%");
		}
		return query;
	}

	private void gerarRestricaoPesquisa(Transportadora filtro, Boolean apenasAtivos, StringBuilder select) {
		StringBuilder restricao = new StringBuilder();

		if (StringUtils.isNotEmpty(filtro.getNomeFantasia())) {
			restricao.append("t.nomeFantasia LIKE :nomeFantasia AND ");
		}

		if (StringUtils.isNotEmpty(filtro.getRazaoSocial())) {
			restricao.append("t.razaoSocial LIKE :razaoSocial AND ");
		}

		if (StringUtils.isNotEmpty(filtro.getCnpj())) {
			restricao.append("t.cnpj LIKE :cnpj AND ");
		}

		if (restricao.length() > 0) {
			select.append(" WHERE ").append(restricao);
			select.delete(select.lastIndexOf("AND"), select.length() - 1);
		}
	}

	@PostConstruct
	public void init() {
		transportadoraDAO = new TransportadoraDAO(entityManager);
	}

	@Override
	public Integer inserir(Transportadora transportadora) throws BusinessException {
		ValidadorInformacao.validar(transportadora);

		if (isNomeFantasiaExistente(transportadora.getId(), transportadora.getNomeFantasia())) {
			throw new BusinessException("O nome fantasia enviado ja foi cadastrado para outra transportadora");
		}

		if (isCNPJExistente(transportadora.getId(), transportadora.getCnpj())) {
			throw new BusinessException("CNPJ enviado ja foi cadastrado para outra transportadora");
		}
		logradouroService.inserirEnderecoBaseCEP(transportadora.getLogradouro());
		return transportadoraDAO.alterar(transportadora).getId();
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public boolean isCNPJExistente(Integer idTransportadora, String cnpj) {
		return transportadoraDAO.isEntidadeExistente(Transportadora.class, idTransportadora, "cnpj", cnpj);
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public boolean isNomeFantasiaExistente(Integer idTransportadora, String nomeFantasia) {
		return transportadoraDAO.isEntidadeExistente(Transportadora.class, idTransportadora, "nomeFantasia",
				nomeFantasia);
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public PaginacaoWrapper<Transportadora> paginarTransportadora(Transportadora filtro, Boolean apenasAtivos,
			Integer indiceRegistroInicial, Integer numeroMaximoRegistros) {
		return new PaginacaoWrapper<Transportadora>(this.pesquisarTotalRegistros(filtro, apenasAtivos),
				this.pesquisarBy(filtro, apenasAtivos, indiceRegistroInicial, numeroMaximoRegistros));
	}

	@SuppressWarnings("unchecked")
	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public List<Transportadora> pesquisar() {
		return this.entityManager.createQuery("select t from Transportadora t order by t.nomeFantasia ")
				.getResultList();
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public List<Transportadora> pesquisarBy(Transportadora filtro, Boolean apenasAtivos, Integer indiceRegistroInicial,
			Integer numeroMaximoRegistros) {
		if (filtro == null) {
			return Collections.emptyList();
		}

		StringBuilder select = new StringBuilder("SELECT t FROM br.com.plastecno.service.entity.Transportadora t ");
		this.gerarRestricaoPesquisa(filtro, apenasAtivos, select);
		select.append(" order by t.nomeFantasia ");

		Query query = this.gerarQueryPesquisa(filtro, select);
		return QueryUtil.paginar(query, indiceRegistroInicial, numeroMaximoRegistros);
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public Transportadora pesquisarByCnpj(String cnpj) {
		if (cnpj == null || cnpj.isEmpty()) {
			return null;
		}
		return transportadoraDAO.pesquisarByCNPJ(cnpj);
	}

	@SuppressWarnings("unchecked")
	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public List<Transportadora> pesquisarById(List<Integer> listaId) {
		return this.entityManager
				.createQuery("select m from Transportadora m where m.id in (:listaId) order by m.nomeFantasia asc")
				.setParameter("listaId", listaId).getResultList();
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public List<Transportadora> pesquisarByNomeFantasia(String nomeFantasia) {
		if (nomeFantasia == null || nomeFantasia.isEmpty()) {
			return new ArrayList<Transportadora>();
		}
		return transportadoraDAO.pesquisarByNomeFantasia(nomeFantasia);
	}

	@Override
	@SuppressWarnings("unchecked")
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public List<ContatoTransportadora> pesquisarContato(Integer id) {
		return (List<ContatoTransportadora>) this.contatoService.pesquisar(id, ContatoTransportadora.class);
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public LogradouroTransportadora pesquisarLogradorouro(Integer id) {
		StringBuilder select = new StringBuilder("select t.logradouro from Transportadora t  ");
		select.append(" INNER JOIN t.logradouro where t.id = :id ");

		Query query = this.entityManager.createQuery(select.toString());
		query.setParameter("id", id);
		return QueryUtil.gerarRegistroUnico(query, LogradouroTransportadora.class, null);
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public Long pesquisarTotalRegistros(Transportadora filtro, Boolean apenasAtivos) {
		if (filtro == null) {
			return 0L;
		}

		final StringBuilder select = new StringBuilder("SELECT count(t.id) FROM Transportadora t ");
		this.gerarRestricaoPesquisa(filtro, apenasAtivos, select);
		Query query = this.gerarQueryPesquisa(filtro, select);
		return QueryUtil.gerarRegistroUnico(query, Long.class, null);
	}

	@Override
	@SuppressWarnings("unchecked")
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public List<Transportadora> pesquisarTransportadoraByIdCliente(Integer idCliente) {
		return this.entityManager
				.createQuery(
						"select new Transportadora(t.id, t.nomeFantasia) from Cliente c inner join c.listaRedespacho t where c.id = :idCliente order by t.nomeFantasia asc ")
				.setParameter("idCliente", idCliente).getResultList();
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public Transportadora pesquisarTransportadoraLogradouroById(Integer idTransportadora) {
		return transportadoraDAO.pesquisarTransportadoraLogradouroById(idTransportadora);
	}
}
