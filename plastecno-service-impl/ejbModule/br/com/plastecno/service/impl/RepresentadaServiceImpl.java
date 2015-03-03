package br.com.plastecno.service.impl;

import java.util.Collections;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import br.com.plastecno.service.ContatoService;
import br.com.plastecno.service.LogradouroService;
import br.com.plastecno.service.RepresentadaService;
import br.com.plastecno.service.constante.TipoApresentacaoIPI;
import br.com.plastecno.service.constante.TipoPedido;
import br.com.plastecno.service.constante.TipoRelacionamento;
import br.com.plastecno.service.dao.RepresentadaDAO;
import br.com.plastecno.service.entity.ContatoRepresentada;
import br.com.plastecno.service.entity.Logradouro;
import br.com.plastecno.service.entity.Representada;
import br.com.plastecno.service.exception.BusinessException;
import br.com.plastecno.service.impl.util.QueryUtil;
import br.com.plastecno.util.StringUtils;
import br.com.plastecno.validacao.ValidadorInformacao;

@Stateless
public class RepresentadaServiceImpl implements RepresentadaService {

	@EJB
	private ContatoService contatoService;

	@PersistenceContext(unitName = "plastecno")
	private EntityManager entityManager;

	@EJB
	private LogradouroService logradouroService;

	private RepresentadaDAO representadaDAO;

	@Override
	public Integer desativar(Integer id) {
		Query query = this.entityManager.createQuery("update Representada r set r.ativo = false where r.id = :id");
		query.setParameter("id", id);
		return query.executeUpdate();
	}

	private Query gerarQueryPesquisa(Representada filtro, StringBuilder select) {
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

	private void gerarRestricaoPesquisa(Representada filtro, Boolean apenasAtivos, StringBuilder select) {
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

		if (apenasAtivos != null && Boolean.TRUE.equals(apenasAtivos)) {
			restricao.append("t.ativo = true AND ");
		} else if (apenasAtivos != null && Boolean.FALSE.equals(apenasAtivos)) {
			restricao.append("t.ativo = false AND ");
		}

		if (restricao.length() > 0) {
			select.append(" WHERE ");
			select.append(restricao);
			select.delete(select.lastIndexOf("AND"), select.length() - 1);
		}
	}

	@PostConstruct
	public void init() {
		this.representadaDAO = new RepresentadaDAO(this.entityManager);
	}

	@Override
	public Integer inserir(final Representada representada) throws BusinessException {
		ValidadorInformacao.validar(representada);

		if (isNomeFantasiaExistente(representada.getId(), representada.getNomeFantasia())) {
			throw new BusinessException("O nome fantasia enviado ja foi cadastrado para outra representada");
		}

		if (isCNPJExistente(representada.getId(), representada.getCnpj())) {
			throw new BusinessException("CNPJ enviado ja foi cadastrado para outra representada");
		}

		if (representada.isRevendedor() && isRevendedorExistente(representada.getId())) {
			Representada revendedor = pesquisarRevendedor();
			throw new BusinessException("J� existe um revendedor cadastrado no sistema. Remova o revendedor \""
					+ revendedor.getNomeFantasia() + "\" para em seguida cadastrar um outro.");
		}

		representada.setLogradouro(this.logradouroService.inserir(representada.getLogradouro()));

		if (representada.getId() == null) {
			return representadaDAO.inserir(representada).getId();
		} else {
			return representadaDAO.alterar(representada).getId();
		}
	}

	@Override
	public Boolean isCalculoIPIHabilitado(Integer idRepresentada) {
		return !TipoApresentacaoIPI.NUNCA.equals(representadaDAO.pesquisarTipoApresentacaoIPI(idRepresentada));
	}

	@Override
	public boolean isCNPJExistente(Integer id, String cnpj) {
		return this.representadaDAO.isEntidadeExistente(Representada.class, id, "cnpj", cnpj);
	}

	@Override
	public boolean isNomeFantasiaExistente(Integer id, String nomeFantasia) {
		return this.representadaDAO.isEntidadeExistente(Representada.class, id, "nomeFantasia", nomeFantasia);
	}

	@Override
	public boolean isRevendedor(Integer idRepresentada) {
		return TipoRelacionamento.REVENDA.equals(representadaDAO.pesquisarTipoRelacionamento(idRepresentada));
	}

	private boolean isRevendedorExistente(Integer id) {
		return this.representadaDAO.isEntidadeExistente(Representada.class, id, "tipoRelacionamento",
				TipoRelacionamento.REVENDA);
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<Representada> pesquisarBy(Representada filtro, Boolean apenasAtivos, Integer indiceRegistroInicial,
			Integer numeroMaximoRegistros) {
		if (filtro == null) {
			return Collections.emptyList();
		}

		StringBuilder select = new StringBuilder("SELECT t FROM Representada t ");
		this.gerarRestricaoPesquisa(filtro, apenasAtivos, select);
		select.append(" order by t.nomeFantasia ");

		Query query = this.gerarQueryPesquisa(filtro, select);

		if (indiceRegistroInicial != null && indiceRegistroInicial >= 0 && numeroMaximoRegistros != null
				&& numeroMaximoRegistros >= 0) {
			query.setFirstResult(indiceRegistroInicial).setMaxResults(numeroMaximoRegistros);
		}
		return query.getResultList();
	}

	@Override
	public Representada pesquisarById(Integer id) {
		return representadaDAO.pesquisarById(id);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Representada> pesquisarById(List<Integer> listaIdRepresentada) {
		return this.entityManager.createQuery("select m from Representada m where m.id in (:listaIdRepresentada) ")
				.setParameter("listaIdRepresentada", listaIdRepresentada).getResultList();
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<ContatoRepresentada> pesquisarContato(Integer id) {
		return (List<ContatoRepresentada>) this.contatoService.pesquisar(id, ContatoRepresentada.class);
	}

	@Override
	public List<Representada> pesquisarFornecedor(Boolean ativo) {
		return representadaDAO.pesquisarRepresentadaExcluindoRelacionamento(ativo, TipoRelacionamento.REPRESENTACAO);
	}

	@Override
	public List<Representada> pesquisarFornecedorAtivo() {
		return pesquisarFornecedor(true);
	}

	@Override
	public Logradouro pesquisarLogradorouro(Integer id) {
		StringBuilder select = new StringBuilder("select t.logradouro from Representada t  ");
		select.append(" INNER JOIN t.logradouro where t.id = :id ");

		Query query = this.entityManager.createQuery(select.toString());
		query.setParameter("id", id);
		return QueryUtil.gerarRegistroUnico(query, Logradouro.class, null);
	}

	@Override
	public List<Representada> pesquisarRepresentada() {
		return this.pesquisarRepresentada(null);
	}

	@Override
	public List<Representada> pesquisarRepresentada(Boolean ativo) {
		return representadaDAO.pesquisarRepresentadaExcluindoRelacionamento(ativo, TipoRelacionamento.FORNECIMENTO);
	}

	@Override
	public List<Representada> pesquisarRepresentadaAtivo() {
		return this.pesquisarRepresentada(true);
	}

	@Override
	public List<Representada> pesquisarRepresentadaAtivoByTipoPedido(TipoPedido tipoPedido) {
		if (TipoPedido.COMPRA.equals(tipoPedido)) {
			return pesquisarFornecedorAtivo();
		}
		return pesquisarRepresentadaAtivo();
	}

	@Override
	public Representada pesquisarRevendedor() {
		return representadaDAO.pesquisarRevendedor();
	}

	@Override
	public TipoApresentacaoIPI pesquisarTipoApresentacaoIPI(Integer idRepresentada) {
		Representada representada = pesquisarById(idRepresentada);
		return representada != null ? representada.getTipoApresentacaoIPI() : null;
	}

	@Override
	public Long pesquisarTotalRegistros(Representada filtro, Boolean apenasAtivos) {
		if (filtro == null) {
			return 0L;
		}

		final StringBuilder select = new StringBuilder("SELECT count(t.id) FROM Representada t ");
		this.gerarRestricaoPesquisa(filtro, apenasAtivos, select);
		Query query = this.gerarQueryPesquisa(filtro, select);
		return QueryUtil.gerarRegistroUnico(query, Long.class, null);
	}

}
