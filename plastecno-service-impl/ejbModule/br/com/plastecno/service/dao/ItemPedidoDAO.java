package br.com.plastecno.service.dao;

import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import br.com.plastecno.service.constante.SituacaoPedido;
import br.com.plastecno.service.entity.ItemPedido;

public class ItemPedidoDAO extends GenericDAO<ItemPedido> {

	public ItemPedidoDAO(EntityManager entityManager) {
		super(entityManager);
	}

	@SuppressWarnings("unchecked")
	public List<ItemPedido> pesquisarItemPedidoEmpacotamento(Integer idCliente, Date dataInicial, Date dataFinal) {
		StringBuilder select = new StringBuilder();
		select.append("select i from ItemPedido i where i.pedido.situacaoPedido = :situacaoPedido ");

		if (dataInicial != null) {
			select.append("and i.pedido.dataEnvio >= :dataInicial ");
		}

		if (dataFinal != null) {
			select.append("and i.pedido.dataEnvio <= :dataFinal ");
		}

		if (idCliente != null) {
			select.append("and i.pedido.cliente.id = :idCliente ");
		}

		select.append("order by i.pedido.dataEnvio asc ");

		Query query = this.entityManager.createQuery(select.toString());
		query.setParameter("situacaoPedido", SituacaoPedido.EMPACOTAMENTO);

		if (dataInicial != null) {
			query.setParameter("dataInicial", dataInicial);
		}

		if (dataFinal != null) {
			query.setParameter("dataFinal", dataFinal);
		}

		if (idCliente != null) {
			query.setParameter("idCliente", idCliente);
		}

		return query.getResultList();
	}
}
