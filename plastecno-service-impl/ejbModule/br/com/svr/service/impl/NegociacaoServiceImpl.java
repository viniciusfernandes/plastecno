package br.com.svr.service.impl;

import java.util.Comparator;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import br.com.svr.service.NegociacaoService;
import br.com.svr.service.PedidoService;
import br.com.svr.service.constante.SituacaoPedido;
import br.com.svr.service.constante.crm.CategoriaNegociacao;
import br.com.svr.service.constante.crm.SituacaoNegociacao;
import br.com.svr.service.constante.crm.TipoNaoFechamento;
import br.com.svr.service.dao.crm.NegociacaoDAO;
import br.com.svr.service.entity.crm.IndiceConversao;
import br.com.svr.service.entity.crm.Negociacao;
import br.com.svr.service.exception.BusinessException;
import br.com.svr.service.impl.util.QueryUtil;
import br.com.svr.service.wrapper.GrupoWrapper;
import br.com.svr.service.wrapper.RelatorioWrapper;
import br.com.svr.validacao.ValidadorInformacao;

@Stateless
public class NegociacaoServiceImpl implements NegociacaoService {
	@PersistenceContext(name = "svr")
	private EntityManager entityManager;

	private NegociacaoDAO negociacaoDAO;
	@EJB
	private PedidoService pedidoService;

	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public Integer aceitarNegocicacao(Integer idNegociacao) throws BusinessException {
		negociacaoDAO.alterarSituacaoNegociacao(idNegociacao, SituacaoNegociacao.ACEITO);
		return pedidoService.aceitarOrcamento(negociacaoDAO.pesquisarIdPedidoByIdNegociacao(idNegociacao));
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public void alterarCategoria(Integer idNegociacao, CategoriaNegociacao categoriaNegociacao)
			throws BusinessException {
		if (idNegociacao == null) {
			throw new BusinessException("O ID da negociação não pode ser nulo para a alteração de categoria.");
		}
		if (categoriaNegociacao == null) {
			throw new BusinessException("A categoria da negociação não pode ser nulo para a alteração de categoria.");
		}
		negociacaoDAO.alterarCategoria(idNegociacao, categoriaNegociacao);
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public double calcularValorCategoriaNegociacaoAberta(Integer idVendedor, CategoriaNegociacao categoria) {
		return negociacaoDAO.calcularValorCategoriaNegociacaoAberta(idVendedor, categoria);
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public Integer cancelarNegocicacao(Integer idNegociacao, TipoNaoFechamento tipoNaoFechamento)
			throws BusinessException {
		negociacaoDAO.alterarSituacaoNegociacao(idNegociacao, SituacaoNegociacao.CANCELADO);
		negociacaoDAO.alterarTipoNaoFechamento(idNegociacao, tipoNaoFechamento);
		Integer idOrc = negociacaoDAO.pesquisarIdPedidoByIdNegociacao(idNegociacao);
		pedidoService.cancelarOrcamento(idOrc);
		return idOrc;
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public void gerarIndiceConversaoCliente() throws BusinessException {
		List<Integer> lIdCliente = entityManager.createQuery("select c.id from Cliente c", Integer.class)
				.getResultList();
		List<Object[]> lIdPed = null;
		Double vOrc = null;
		Double vPed = null;
		double idxValor = 0;
		int qtdePed = 0;
		IndiceConversao indConv = null;
		for (Integer idCli : lIdCliente) {
			lIdPed = entityManager
					.createQuery(
							"select p.id, p.idOrcamento, p.valorPedido from Pedido p where p.idOrcamento != null and p.cliente.id =:idCliente",
							Object[].class).setParameter("idCliente", idCli).getResultList();
			qtdePed = lIdPed.size();

			if (qtdePed <= 0) {
				continue;
			}
			idxValor = 0;
			for (Object[] ids : lIdPed) {
				vPed = (Double) ids[2];
				vOrc = entityManager.createQuery("select p.valorPedido from Pedido p where p.id =:idOrc", Double.class)
						.setParameter("idOrc", ids[1]).getSingleResult();
				if (vOrc == null || vPed == null || vOrc == 0d || vPed == 0d) {
					continue;
				}
				idxValor += vOrc / vPed;
			}

			idxValor /= qtdePed;
			indConv = new IndiceConversao();
			indConv.setIndiceValor(idxValor);
			indConv.setIdCliente(idCli);
			try {
				entityManager.persist(indConv);
				entityManager.flush();
			} catch (Exception e) {
				System.out.println("idx=" + idxValor + " valPed=" + vPed + " valOrc=" + vOrc);
				return;
			}
		}
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public void gerarNegociacaoInicial() throws BusinessException {
		boolean contemNeg = QueryUtil.gerarRegistroUnico(
				entityManager.createQuery("select max(n.id) from Negociacao n"), Integer.class, null) != null;
		if (contemNeg) {
			return;
		}
		List<Object[]> listaIdOrc = entityManager
				.createQuery(
						"select o.id, o.proprietario.id from Pedido o where o.situacaoPedido in (:listaOrcamentoAberto)",
						Object[].class).setParameter("listaOrcamentoAberto", SituacaoPedido.getListaOrcamentoAberto())
				.getResultList();
		for (Object[] ids : listaIdOrc) {
			inserirNegociacao((Integer) ids[0], (Integer) ids[1]);
		}
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public RelatorioWrapper<CategoriaNegociacao, Negociacao> gerarRelatorioNegociacao(Integer idVendedor) {
		RelatorioWrapper<CategoriaNegociacao, Negociacao> rel = new RelatorioWrapper<CategoriaNegociacao, Negociacao>(
				"RElatório de Negociações");
		List<Negociacao> lNeg = pesquisarNegociacaoAbertaByIdVendedor(idVendedor);
		for (Negociacao n : lNeg) {
			rel.addGrupo(n.getCategoriaNegociacao(), n);
		}

		// Totalizando o valor das negociacao de cada categoria.
		double tot = 0d;
		for (GrupoWrapper<CategoriaNegociacao, Negociacao> g : rel.getListaGrupo()) {
			tot = 0d;
			for (Negociacao n : g.getListaElemento()) {
				tot += n.getValor();
			}
			g.setPropriedade("valorTotal", tot);
		}

		// Adicionando os grupos que o usuario nao tem negociacao para que todos
		// eles aparecem no relatorio.
		GrupoWrapper<CategoriaNegociacao, Negociacao> gr = null;
		for (CategoriaNegociacao c : CategoriaNegociacao.values()) {
			gr = rel.getGrupo(c);
			if (gr == null) {
				gr = rel.addGrupo(c, null);
				gr.setPropriedade("valorTotal", 0d);
			}
		}

		rel.sortGrupo(new Comparator<GrupoWrapper<CategoriaNegociacao, Negociacao>>() {

			@Override
			public int compare(GrupoWrapper<CategoriaNegociacao, Negociacao> g1,
					GrupoWrapper<CategoriaNegociacao, Negociacao> g2) {

				return g1.getId().getOrdem().compareTo(g2.getId().getOrdem());
			}
		});
		rel.sortElementoByGrupo(new Comparator<Negociacao>() {

			@Override
			public int compare(Negociacao n1, Negociacao n2) {
				return n1.getId().compareTo(n2.getId());
			}
		});
		return rel;
	}

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
		Object[] dados = pedidoService.pesquisarNomeClienteNomeContatoValor(idOrcamento);
		Negociacao n = new Negociacao();
		n.setCategoriaNegociacao(CategoriaNegociacao.PROPOSTA_CLIENTE);
		n.setIdOrcamento(idOrcamento);
		n.setNomeCliente((String) dados[0]);
		n.setNomeContato(dados[2] == null ? null : (String) dados[2]);
		n.setSituacaoNegociacao(SituacaoNegociacao.ABERTO);
		n.setTelefoneContato(dados[3] == null ? null : ((dados[1] == null ? "" : dados[1]) + "-" + dados[3]));
		n.setValor(dados[4] == null ? 0d : (double) dados[4]);
		n.setTipoNaoFechamento(TipoNaoFechamento.OK);

		// Aqui eh possivel que outro vendedor realize uma negociacao iniciado
		// por outro vendedor no caso da ausencia do mesmo.
		n.setIdVendedor(idVendedor);

		ValidadorInformacao.validar(n);
		return negociacaoDAO.inserir(n).getId();
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public Negociacao pesquisarById(Integer idNegociacao) {
		return negociacaoDAO.pesquisarById(idNegociacao);
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public List<Negociacao> pesquisarNegociacaoAbertaByIdVendedor(Integer idVendedor) {
		return negociacaoDAO.pesquisarNegociacaoAbertaByIdVendedor(idVendedor);
	}
}
