package br.com.plastecno.service.impl;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

import br.com.plastecno.service.EstoqueService;
import br.com.plastecno.service.PedidoService;
import br.com.plastecno.service.constante.SituacaoPedido;
import br.com.plastecno.service.entity.ItemPedido;
import br.com.plastecno.service.entity.Pedido;

@Stateless
public class EstoqueServiceImpl implements EstoqueService {
	@EJB
	private PedidoService pedidoService;

	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public void inserirItemEstoque(Integer idItemPedido) {
		ItemPedido item = pedidoService.pesquisarItemPedido(idItemPedido);
		Pedido pedido = item.getPedido();
		long qtdePendente = pedidoService.pesquisarTotalItemPendente(pedido.getId());
		if (qtdePendente <= 1) {
			pedido.setSituacaoPedido(SituacaoPedido.RECEBIDO);
		}
		item.setRecebido(true);
	}

}
