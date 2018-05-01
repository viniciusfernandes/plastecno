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

import br.com.svr.service.EstoqueService;
import br.com.svr.service.PedidoService;
import br.com.svr.service.RegistroEstoqueService;
import br.com.svr.service.constante.TipoOperacaoEstoque;
import br.com.svr.service.dao.RegistroEstoqueDAO;
import br.com.svr.service.entity.RegistroEstoque;

@Stateless
public class RegistroEstoqueServiceImpl implements RegistroEstoqueService {
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

	private void inserirRegistro(Integer idItemEstoque, Integer idItemPedido, Integer quantidade,
			TipoOperacaoEstoque tipoOperacao) {
		Integer idPed = pedidoService.pesquisarIdPedidoByIdItemPedido(idItemPedido);
		Integer qtdeEst = estoqueService.pesquisarQuantidadeByIdItemEstoque(idItemEstoque);

		// Estamos subtraindo/adicionando as quantidade pois nesse ponto
		// espera-se que o sistema ja tenha atualizado as quantidades do estoque
		// com os novos itens de compra/venda.
		qtdeEst = tipoOperacao.isEntrada() ? qtdeEst - quantidade : qtdeEst + quantidade;
		if (qtdeEst < 0) {
			qtdeEst = 0;
		}
		RegistroEstoque r = new RegistroEstoque();
		r.setDataOperacao(new Date());
		r.setIdItemEstoque(idItemEstoque);
		r.setIdItemPedido(idItemPedido);
		r.setIdPedido(idPed);
		r.setIdUsuario(null);
		r.setTipoOperacao(tipoOperacao);
		r.setQuantidadeRegistrada(quantidade);
		r.setQuantidadeAnterior(qtdeEst);

		registroEstoqueDAO.inserir(r);
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public void inserirRegistroEntradaDevolucaoItemVenda(Integer idItemEstoque, Integer idItemPedido, Integer quantidade) {
		inserirRegistro(idItemEstoque, idItemPedido, quantidade, TipoOperacaoEstoque.ENTRADA_DEVOLUCAO_VENDA);
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public void inserirRegistroEntradaItemCompra(Integer idItemEstoque, Integer idItemPedido, Integer quantidade) {
		inserirRegistro(idItemEstoque, idItemPedido, quantidade, TipoOperacaoEstoque.ENTRADA_PEDIDO_COMPRA);
	}
	
	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public void inserirRegistroSaidaItemVenda(Integer idItemEstoque, Integer idItemPedido, Integer quantidade) {
		inserirRegistro(idItemEstoque, idItemPedido, quantidade, TipoOperacaoEstoque.SAIDA_PEDIDO_VENDA);
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public List<RegistroEstoque> pesquisarRegistroByIdItemEstoque(Integer idItemEstoque) {
		return registroEstoqueDAO.pesquisarRegistroByIdItemEstoque(idItemEstoque);
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public List<RegistroEstoque> pesquisarRegistroByIdPedido(Integer idPedido) {
		return registroEstoqueDAO.pesquisarRegistroByIdPedido(idPedido);
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public List<RegistroEstoque> pesquisarRegistroEstoqueByIdItemPedido(Integer idItemPedido) {
		return registroEstoqueDAO.pesquisarRegistroEstoqueByIdItemPedido(idItemPedido);
	}
}
