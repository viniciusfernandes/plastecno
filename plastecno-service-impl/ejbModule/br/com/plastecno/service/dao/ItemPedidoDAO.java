package br.com.plastecno.service.dao;

import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import br.com.plastecno.service.constante.SituacaoPedido;
import br.com.plastecno.service.constante.TipoPedido;
import br.com.plastecno.service.entity.ItemPedido;

public class ItemPedidoDAO extends GenericDAO<ItemPedido> {

	public ItemPedidoDAO(EntityManager entityManager) {
		super(entityManager);
	}

	@SuppressWarnings("unchecked")
	public List<ItemPedido> pesquisarCompraPendenteRecebimento(Integer idRepresentada, Date dataInicial, Date dataFinal) {
		StringBuilder select = new StringBuilder();
		select.append("select i from ItemPedido i ");
		select.append("where i.pedido.tipoPedido = :tipoPedido ");
		select.append("and i.recebido = false ");
		select.append("and i.pedido.situacaoPedido = :situacaoPedido ");

		if (dataInicial != null) {
			select.append("and i.pedido.dataEnvio >= :dataInicial ");
		}

		if (dataFinal != null) {
			select.append("and i.pedido.dataEnvio <= :dataFinal ");
		}

		if (idRepresentada != null) {
			select.append("and i.pedido.representada.id = :idRepresentada ");
		}

		select.append("order by i.pedido.dataEnvio asc ");

		Query query = this.entityManager.createQuery(select.toString());
		query.setParameter("tipoPedido", TipoPedido.COMPRA);
		query.setParameter("situacaoPedido", SituacaoPedido.COMPRA_PENDENTE_RECEBIMENTO);

		if (dataInicial != null) {
			query.setParameter("dataInicial", dataInicial);
		}

		if (dataFinal != null) {
			query.setParameter("dataFinal", dataFinal);
		}

		if (idRepresentada != null) {
			query.setParameter("idRepresentada", idRepresentada);
		}

		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	public List<ItemPedido> pesquisarItemEncomenda(Integer idCliente, Date dataInicial, Date dataFinal) {
		StringBuilder select = new StringBuilder();
		select.append("select i from ItemPedido i ");
		select.append("where i.pedido.situacaoPedido = :situacaoPedido and i.pedido.tipoPedido = :tipoPedido ");
		select.append("and i.encomendado = false and i.quantidade > i.quantidadeReservada ");

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
		query.setParameter("tipoPedido", TipoPedido.REVENDA);
		query.setParameter("situacaoPedido", SituacaoPedido.REVENDA_PENDENTE_ENCOMENDA);

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

	@SuppressWarnings("unchecked")
	public List<ItemPedido> pesquisarItemPedidoEmpacotamento(Integer idCliente, Date dataInicial, Date dataFinal) {
		StringBuilder select = new StringBuilder();
		select
				.append("select i from ItemPedido i where i.pedido.situacaoPedido = :situacaoPedido and i.quantidadeReservada > 0 ");

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

	@SuppressWarnings("unchecked")
	public List<ItemPedido> pesquisarItemPedidoEncomendado(Integer idCliente, Date dataInicial, Date dataFinal) {
		StringBuilder select = new StringBuilder();
		select.append("select i from ItemPedido i ");
		select.append("where i.pedido.situacaoPedido = :situacaoPedido and i.pedido.tipoPedido = :tipoPedido ");
		select.append("and i.encomendado = true and i.quantidade > i.quantidadeReservada ");

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
		query.setParameter("tipoPedido", TipoPedido.REVENDA);
		query.setParameter("situacaoPedido", SituacaoPedido.REVENDA_ENCOMENDADA);

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

	public Integer pesquisarQuantidadeItemPedido(Integer idItemPedido) {
		return pesquisarCampoById(ItemPedido.class, idItemPedido, "quantidade", Integer.class);
	}
}
