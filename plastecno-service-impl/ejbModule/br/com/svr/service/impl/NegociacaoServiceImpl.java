package br.com.svr.service.impl;

import java.util.Comparator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

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
import br.com.svr.service.dao.crm.IndiceConversaoDAO;
import br.com.svr.service.dao.crm.NegociacaoDAO;
import br.com.svr.service.entity.Pedido;
import br.com.svr.service.entity.crm.IndiceConversao;
import br.com.svr.service.entity.crm.Negociacao;
import br.com.svr.service.exception.BusinessException;
import br.com.svr.service.impl.util.QueryUtil;
import br.com.svr.service.validacao.ValidadorInformacao;
import br.com.svr.service.wrapper.GrupoWrapper;
import br.com.svr.service.wrapper.RelatorioWrapper;

@Stateless
public class NegociacaoServiceImpl implements NegociacaoService {
	@PersistenceContext(name = "svr")
	private EntityManager entityManager;

	private IndiceConversaoDAO indiceConversaoDAO;
	private Logger log = Logger.getLogger(this.getClass().getName());
	private NegociacaoDAO negociacaoDAO;

	@EJB
	private PedidoService pedidoService;

	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public Integer aceitarNegocicacaoByIdNegociacao(Integer idNegociacao) throws BusinessException {
		negociacaoDAO.alterarSituacaoNegociacao(idNegociacao, SituacaoNegociacao.ACEITO);
		Integer idOrcamento = negociacaoDAO.pesquisarIdPedidoByIdNegociacao(idNegociacao);
		return pedidoService.aceitarOrcamento(idOrcamento);
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public Integer aceitarNegocicacaoByIdOrcamento(Integer idOrcamento) throws BusinessException {
		return aceitarNegocicacaoByIdNegociacao(negociacaoDAO.pesquisarIdNegociacaoByIdOrcamento(idOrcamento));
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public void alterarCategoria(Integer idNegociacao, CategoriaNegociacao categoriaNegociacao)
			throws BusinessException {
		if (idNegociacao == null) {
			throw new BusinessException("O ID da negocia��o n�o pode ser nulo para a altera��o de categoria.");
		}
		if (categoriaNegociacao == null) {
			throw new BusinessException("A categoria da negocia��o n�o pode ser nulo para a altera��o de categoria.");
		}
		negociacaoDAO.alterarCategoria(idNegociacao, categoriaNegociacao);
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public void alterarNegociacaoAbertaIndiceConversaoValorByIdCliente(Integer idCliente, Double indice) {
		negociacaoDAO.alterarIndiceConversaoValorByIdCliente(idCliente, indice, SituacaoNegociacao.ABERTO);
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
		Negociacao n = pesquisarById(idNegociacao);
		n.setTipoNaoFechamento(tipoNaoFechamento);
		n.setSituacaoNegociacao(SituacaoNegociacao.CANCELADO);
		negociacaoDAO.alterar(n);

		// negociacaoDAO.alterarSituacaoNegociacao(idNegociacao,
		// SituacaoNegociacao.CANCELADO);
		// negociacaoDAO.alterarTipoNaoFechamento(idNegociacao,
		// tipoNaoFechamento);
		Integer idOrc = negociacaoDAO.pesquisarIdPedidoByIdNegociacao(idNegociacao);
		pedidoService.cancelarOrcamento(idOrc);
		return idOrc;
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public void gerarIndiceConversaoCliente() throws BusinessException {
		List<Integer> idsCliente = entityManager.createQuery("select c.id from Cliente c", Integer.class)
				.getResultList();

		if (idsCliente == null || idsCliente.size() <= 0) {
			return;
		}
		Object[] valPed = null;
		Object[] valOrc = null;

		IndiceConversao idx = null;
		Integer idPed = null;
		for (Integer idCli : idsCliente) {
			valPed = entityManager
					.createQuery(
							"select count(p.id), sum(p.valorPedidoIPI) from Pedido p where p.cliente.id =:idCliente and p.situacaoPedido in (:listaSituacao) and p.id >14990",
							Object[].class).setParameter("listaSituacao", SituacaoPedido.getListaVendaEfetivada())
					.setParameter("idCliente", idCli).getSingleResult();
			if (valPed == null) {
				continue;
			}
			valOrc = entityManager
					.createQuery(
							"select count(p.id), sum(p.valorPedidoIPI) from Pedido p where p.cliente.id =:idCliente and p.situacaoPedido in (:listaSituacao) and p.id >14990",
							Object[].class).setParameter("listaSituacao", SituacaoPedido.getListaOrcamentoEfetivado())
					.setParameter("idCliente", idCli).getSingleResult();

			idx = new IndiceConversao();
			idx.setValorVendas(valPed[1] == null ? 0 : (double) valPed[1]);
			idx.setQuantidadeVendas(valPed[0] == null ? 0 : (long) valPed[0]);

			idx.setValorOrcamentos(valOrc[1] == null ? 0 : (double) valOrc[1]);
			idx.setQuantidadeOrcamentos(valOrc[0] == null ? 0 : (long) valOrc[0]);

			idx.calcularIndice();

			idx.setIdCliente(idCli);
			try {
				entityManager.persist(idx);
				entityManager.flush();
			} catch (Exception e) {
				log.log(Level.SEVERE, "Falha no calculo do indice de conversao do pedido " + idPed);
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
						"select o.id, o.cliente.id, o.proprietario.id from Pedido o where o.id>=14900 and o.situacaoPedido in (:listaSituacoes)",
						Object[].class).setParameter("listaSituacoes", SituacaoPedido.getListaOrcamentoAberto())
				.getResultList();
		for (Object[] ids : listaIdOrc) {
			inserirNegociacao((Integer) ids[0], (Integer) ids[1], (Integer) ids[2]);
		}
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public RelatorioWrapper<CategoriaNegociacao, Negociacao> gerarRelatorioNegociacao(Integer idVendedor) {
		RelatorioWrapper<CategoriaNegociacao, Negociacao> rel = new RelatorioWrapper<CategoriaNegociacao, Negociacao>(
				"RElat�rio de Negocia��es");
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
				if (n2.getIdOrcamento() != null && n1.getIdOrcamento() == null) {
					return 1;
				}
				if (n2.getIdOrcamento() == null) {
					return -1;
				}
				return n2.getIdOrcamento().compareTo(n1.getIdOrcamento());
			}
		});
		return rel;
	}

	@PostConstruct
	public void init() {
		negociacaoDAO = new NegociacaoDAO(entityManager);
		indiceConversaoDAO = new IndiceConversaoDAO(entityManager);
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public Integer inserirNegociacao(Integer idOrcamento, Integer idCliente, Integer idVendedor)
			throws BusinessException {
		if (idOrcamento == null) {
			throw new BusinessException("� necess�rio um ID de or�amento para a inclus�o de uma negocia��o");
		}

		if (idCliente == null) {
			idCliente = pedidoService.pesquisarIdClienteByIdPedido(idOrcamento);
		}

		Object[] dados = pedidoService.pesquisarIdNomeClienteNomeContatoValor(idOrcamento);
		Double idxConvValor = negociacaoDAO.pesquisarIndiceConversaoValorByIdCliente(idCliente);

		Negociacao n = pesquisarNegociacaoByIdOrcamento(idOrcamento);
		if (n == null) {
			n = new Negociacao();
			// ESSES dados devem ser imutaveis apos a criacao da negociacao.
			n.setCategoriaNegociacao(CategoriaNegociacao.PROPOSTA_CLIENTE);
			n.setIdCliente(idCliente);
			n.setOrcamento(new Pedido(idOrcamento));
			n.setSituacaoNegociacao(SituacaoNegociacao.ABERTO);
			n.setTipoNaoFechamento(TipoNaoFechamento.OK);
		}
		n.setNomeCliente((String) dados[1]);
		n.setIndiceConversaoValor(idxConvValor);
		n.setNomeContato(dados[3] == null ? null : (String) dados[3]);
		n.setTelefoneContato(dados[4] == null ? null : ((dados[2] == null ? "" : dados[2]) + "-" + dados[4]));

		// Aqui eh possivel que outro vendedor realize uma negociacao iniciado
		// por outro vendedor no caso da ausencia do mesmo.
		n.setIdVendedor(idVendedor);
		try {
			ValidadorInformacao.validar(n);
		} catch (BusinessException e) {
			e.printStackTrace();
		}
		return negociacaoDAO.inserir(n).getId();
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public Negociacao pesquisarById(Integer idNegociacao) {
		return negociacaoDAO.pesquisarById(idNegociacao);
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public IndiceConversao pesquisarIndiceConversaoByIdCliente(Integer idCliente) {
		return negociacaoDAO.pesquisarIndiceByIdCliente(idCliente);
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public List<Negociacao> pesquisarNegociacaoAbertaByIdVendedor(Integer idVendedor) {
		return negociacaoDAO.pesquisarNegociacaoAbertaByIdVendedor(idVendedor);
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public Negociacao pesquisarNegociacaoByIdOrcamento(Integer idOrcamento) {
		return negociacaoDAO.pesquisarNegociacaoByIdOrcamento(idOrcamento);
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public void recalcularIndiceConversao(Integer idPedido, Integer idOrcamento) throws BusinessException {
		if (idOrcamento == null) {
			return;
		}
		if (pedidoService.isPedidoCancelado(idOrcamento)) {
			throw new BusinessException(
					"N�o � poss�vel efetuar o recalculo do �ndice de conves�o para o Or�amento No. " + idOrcamento
							+ " pois ele n�o esta em aberto.");
		}
		// Nao vamos incluir o frete pois isso eh um custo que pode nao refletir
		// a capacidade de compra do cliente.
		Double valOrc = pedidoService.pesquisarValorPedidoIPI(idOrcamento);
		Double valPed = pedidoService.pesquisarValorPedidoIPI(idPedido);
		if (valOrc == null || valPed == null || valOrc == 0d || valPed == 0d) {
			return;
		}
		Integer idCliente = pedidoService.pesquisarIdClienteByIdPedido(idOrcamento);

		IndiceConversao idx = negociacaoDAO.pesquisarIndiceByIdCliente(idCliente);
		if (idx == null) {
			idx = new IndiceConversao();
		}

		idx.setQuantidadeVendas(idx.getQuantidadeVendas() + 1);
		idx.setValorVendas(idx.getValorVendas() + valPed);

		idx.setQuantidadeOrcamentos(idx.getQuantidadeOrcamentos() + 1);
		idx.setValorOrcamentos(idx.getValorOrcamentos() + valOrc);

		idx.setIdCliente(idCliente);
		idx.calcularIndice();
		indiceConversaoDAO.alterar(idx);

		alterarNegociacaoAbertaIndiceConversaoValorByIdCliente(idCliente, idx.getIndiceValor());
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public void removerNegociacaoByIdOrcamento(Integer idOrcamento) {
		negociacaoDAO.removerNegociacaoByIdOrcamento(idOrcamento);
	}
}
