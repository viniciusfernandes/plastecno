package br.com.plastecno.service.dao;

import java.util.List;

import javax.persistence.EntityManager;

import br.com.plastecno.service.entity.ItemReservado;

public class ItemReservadoDAO extends GenericDAO<ItemReservado> {

	public ItemReservadoDAO(EntityManager entityManager) {
		super(entityManager);
	}

	@SuppressWarnings("unchecked")
	public List<ItemReservado> pesquisarItemReservadoByIdPedido(Integer idPedido) {
		return entityManager
				.createQuery(
						"select new ItemReservado(i.id, i.itemPedido.id, i.itemPedido.quantidade, i.itemEstoque.id, i.itemEstoque.quantidade) from ItemReservado i where i.itemPedido.pedido.id = :idPedido")
				.setParameter("idPedido", idPedido).getResultList();
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
