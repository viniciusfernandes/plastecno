package br.com.plastecno.service.impl;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

import br.com.plastecno.service.EstoqueService;
import br.com.plastecno.service.PedidoService;
import br.com.plastecno.service.constante.SituacaoPedido;
import br.com.plastecno.service.entity.ItemEstoque;
import br.com.plastecno.service.entity.ItemPedido;
import br.com.plastecno.service.entity.Pedido;

@Stateless
public class EstoqueServiceImpl implements EstoqueService {
	@EJB
	private PedidoService pedidoService;

	private ItemEstoque geraritemEstoque(ItemPedido itemPedido) {

		ItemEstoque itemEstoque = new ItemEstoque();

		itemEstoque.setComprimento(itemPedido.getComprimento());
		itemEstoque.setDescricaoPeca(itemPedido.getDescricaoPeca());
		itemEstoque.setFormaMaterial(itemPedido.getFormaMaterial());
		itemEstoque.setMaterial(itemPedido.getMaterial());
		itemEstoque.setMedidaExterna(itemPedido.getMedidaExterna());
		itemEstoque.setMedidaInterna(itemPedido.getMedidaInterna());
		itemEstoque.setQuantidade(itemPedido.getQuantidade());
		return itemEstoque;
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public void inserirItemEstoque(Integer idItemPedido) {
		ItemPedido itemPedido = pedidoService.pesquisarItemPedido(idItemPedido);
		Pedido pedido = itemPedido.getPedido();
		long qtdePendente = pedidoService.pesquisarTotalItemPendente(pedido.getId());
		if (qtdePendente <= 1) {
			pedido.setSituacaoPedido(SituacaoPedido.COMPRA_RECEBIDA);
		}
		itemPedido.setRecebido(true);
		ItemEstoque itemEstoque = geraritemEstoque(itemPedido);

	}
}
