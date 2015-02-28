package br.com.plastecno.service.dao;

import javax.persistence.EntityManager;

import br.com.plastecno.service.entity.ItemReservado;

public class ItemReservadoDAO extends GenericDAO<ItemReservado> {

	public ItemReservadoDAO(EntityManager entityManager) {
		super(entityManager);
	}

	public Long pesquisarTotalItemPedidoReservado(Integer idPedido) {
		return (Long) entityManager
				.createQuery("select count(i.id) from ItemReservado i where i.itemPedido.pedido.id = :idPedido")
				.setParameter("idPedido", idPedido).getSingleResult();
	}

	public void removerByIdItemPedido(Integer idItemPedido) {
		entityManager.createQuery("delete from ItemReservado i where i.itemPedido.id =:idItemPedido")
				.setParameter("idItemPedido", idItemPedido).executeUpdate();
	}
}
