package br.com.svr.service.impl;

import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import br.com.svr.service.ConfiguracaoSistemaService;
import br.com.svr.service.EstoqueService;
import br.com.svr.service.PedidoService;
import br.com.svr.service.RegistroEstoqueService;
import br.com.svr.service.constante.ParametroConfiguracaoSistema;
import br.com.svr.service.constante.TipoOperacaoEstoque;
import br.com.svr.service.dao.RegistroEstoqueDAO;
import br.com.svr.service.entity.RegistroEstoque;
import br.com.svr.service.exception.BusinessException;
import br.com.svr.service.wrapper.PaginacaoWrapper;
import br.com.svr.util.DateUtils;

@Stateless
public class RegistroEstoqueServiceImpl implements RegistroEstoqueService {
	@EJB
	private ConfiguracaoSistemaService configuracaoSistemaService;

	@PersistenceContext(name = "svr")
	private EntityManager entityManager;

	@EJB
	private EstoqueService estoqueService;

	@EJB
	private PedidoService pedidoService;

	private RegistroEstoqueDAO registroEstoqueDAO;

	@PostConstruct
	public void init() {
		registroEstoqueDAO = new RegistroEstoqueDAO(entityManager);
	}

	private void inserirRegistro(Integer idItemEstoque, Integer idItemPedido, Integer idUsuario, String nomeUsuario,
			Integer quantidadeAnterior, Integer quantidadeItem, Integer quantidadePosterior, Integer sequencialItem,
			TipoOperacaoEstoque tipoOperacao, Double valorAnterior, Double valorPosterior) {
		Integer idPed = pedidoService.pesquisarIdPedidoByIdItemPedido(idItemPedido);

		if (!tipoOperacao.isManual()) {
			// Nesse momento estamos o item do pedido ja foi incuido/excluido
			// automaticamente do
			// estoque, por isso estamos pesquisando a quantidade atual como
			// quantidade posterior.
			quantidadePosterior = estoqueService.pesquisarQuantidadeByIdItemEstoque(idItemEstoque);

			if (quantidadeItem == null) {
				quantidadeItem = 0;
			}
			// Estamos subtraindo/adicionando as quantidade pois nesse ponto
			// espera-se que o sistema ja tenha atualizado as quantidades do
			// estoque com os novos itens de compra/venda.
			quantidadeAnterior = tipoOperacao.isEntrada() ? quantidadePosterior - quantidadeItem : quantidadePosterior
					+ quantidadeItem;
			if (quantidadeAnterior < 0) {
				quantidadeAnterior = 0;
			}
		}

		RegistroEstoque r = new RegistroEstoque();
		r.setDataOperacao(new Date());
		r.setIdItemEstoque(idItemEstoque);
		r.setIdItemPedido(idItemPedido);
		r.setIdPedido(idPed);
		r.setIdUsuario(idUsuario);
		r.setNomeUsuario(nomeUsuario);
		r.setQuantidadeAnterior(quantidadeAnterior);
		r.setQuantidadePosterior(quantidadePosterior);
		r.setQuantidadeItem(quantidadeItem);
		r.setSequencialItemPedido(sequencialItem);
		r.setTipoOperacao(tipoOperacao);
		r.setValorAnterior(valorAnterior);
		r.setValorPosterior(valorPosterior);
		registroEstoqueDAO.inserir(r);
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public void inserirRegistroAlteracaoValorItemEstoque(Integer idItemEstoque, Integer idUsuario, String nomeUsuario,
			Double valorAnterior, Double valorPosterior) {
		inserirRegistro(idItemEstoque, null, idUsuario, nomeUsuario, null, null, null, null,
				TipoOperacaoEstoque.ALTERACAO_MANUAL_VALOR, valorAnterior, valorPosterior);
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public void inserirRegistroAlteracaoValorItemEstoque(Integer idItemEstoque, Integer idUsuario, String nomeUsuario,
			Integer quantidadeAnterior, Integer quantidadePosterior, Double valorAnterior, Double valorPosterior) {
		inserirRegistro(idItemEstoque, null, idUsuario, nomeUsuario, quantidadeAnterior, null, quantidadePosterior,
				null, TipoOperacaoEstoque.ALTERACAO_MANUAL_VALOR, valorAnterior, valorPosterior);
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public void inserirRegistroConfiguracaoItemEstoque(Integer idItemEstoque) {
		inserirRegistro(idItemEstoque, null, null, null, null, null, null, null,
				TipoOperacaoEstoque.ENTRADA_DEVOLUCAO_VENDA, null, null);
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public void inserirRegistroEntradaDevolucaoItemVenda(Integer idItemEstoque, Integer idItemPedido,
			Integer quantidade, Integer sequencialItem) {
		inserirRegistro(idItemEstoque, idItemPedido, sequencialItem, null, null, quantidade, null, sequencialItem,
				TipoOperacaoEstoque.ENTRADA_DEVOLUCAO_VENDA, null, null);
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public void inserirRegistroEntradaItemCompra(Integer idItemEstoque, Integer idItemPedido, Integer quantidade,
			Integer sequencialItem) {
		inserirRegistro(idItemEstoque, idItemPedido, null, null, null, quantidade, null, sequencialItem,
				TipoOperacaoEstoque.ENTRADA_PEDIDO_COMPRA, null, null);
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public void inserirRegistroSaidaItemVenda(Integer idItemEstoque, Integer idItemPedido, Integer quantidade,
			Integer sequencialItem) {
		inserirRegistro(idItemEstoque, idItemPedido, null, null, null, quantidade, null, sequencialItem,
				TipoOperacaoEstoque.SAIDA_PEDIDO_VENDA, null, null);
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public PaginacaoWrapper<RegistroEstoque> paginarRegistroByIdItemEstoque(Integer idItemEstoque,
			Integer indiceInicial, Integer numMaxRegistros) {
		return new PaginacaoWrapper<RegistroEstoque>(
				registroEstoqueDAO.pesquisarTotalRegistroByItemEstoque(idItemEstoque),
				pesquisarRegistroByIdItemEstoque(idItemEstoque, indiceInicial, numMaxRegistros));
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public List<RegistroEstoque> pesquisarRegistroByIdItemEstoque(Integer idItemEstoque) {
		return pesquisarRegistroByIdItemEstoque(idItemEstoque, null, null);
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public List<RegistroEstoque> pesquisarRegistroByIdItemEstoque(Integer idItemEstoque, Integer indiceInicial,
			Integer numeroMaxRegistros) {
		return registroEstoqueDAO.pesquisarRegistroByIdItemEstoque(idItemEstoque, indiceInicial, numeroMaxRegistros);
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public List<RegistroEstoque> pesquisarRegistroByIdItemPedido(Integer idItemPedido) {
		return registroEstoqueDAO.pesquisarRegistroEstoqueByIdItemPedido(idItemPedido);
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public List<RegistroEstoque> pesquisarRegistroByIdPedido(Integer idPedido) {
		return registroEstoqueDAO.pesquisarRegistroByIdPedido(idPedido);
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public void removerRegistroExpirado() throws BusinessException {
		Integer meses = null;
		try {
			meses=Integer.parseInt(configuracaoSistemaService
					.pesquisar(ParametroConfiguracaoSistema.EXPIRACAO_REGISTRO_ESTOQUE_MESES));
		} catch (Exception e) {
			throw new BusinessException("Nao foi possivel parsear o valor dos meses de expiracao dos registros de estoque", e);
		}
		Date dtExpiracao = DateUtils.fromNowMinusMonth(meses);
		registroEstoqueDAO.removerRegistroByDataLimite(dtExpiracao);
	}
}
