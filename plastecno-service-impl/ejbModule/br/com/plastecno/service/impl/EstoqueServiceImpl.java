package br.com.plastecno.service.impl;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

import br.com.plastecno.service.EstoqueService;
import br.com.plastecno.service.PedidoService;
import br.com.plastecno.service.entity.ItemPedido;

@Stateless
public class EstoqueServiceImpl implements EstoqueService {
	@EJB
	private PedidoService pedidoService;

	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public void inserirItemEstoque(Integer idItemPedido) {
		ItemPedido itemPedido = pedidoService.pesquisarItemPedido(idItemPedido);
		itemPedido.setRecebido(true);

	}

	public void pesquisarQuantidadeItemPendentes(Integer idPedido) {

	}
}
