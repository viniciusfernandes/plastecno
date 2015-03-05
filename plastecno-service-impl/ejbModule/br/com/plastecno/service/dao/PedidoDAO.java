package br.com.plastecno.service.dao;

import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import br.com.plastecno.service.constante.SituacaoPedido;
import br.com.plastecno.service.constante.TipoPedido;
import br.com.plastecno.service.entity.Cliente;
import br.com.plastecno.service.entity.ItemPedido;
import br.com.plastecno.service.entity.Logradouro;
import br.com.plastecno.service.entity.Pedido;
import br.com.plastecno.service.impl.util.QueryUtil;
import br.com.plastecno.util.StringUtils;

public class PedidoDAO extends GenericDAO<Pedido> {
	public PedidoDAO(EntityManager entityManager) {
		super(entityManager);
	}

	public void atualizarSituacaoPedidoById(Integer idPedido, SituacaoPedido situacaoPedido) {
		this.entityManager.createQuery("update Pedido p set p.situacaoPedido = :situacao where p.id = :idPedido")
				.setParameter("situacao", situacaoPedido).setParameter("idPedido", idPedido).executeUpdate();
	}

	public void cancelar(Integer idPedido) {
		atualizarSituacaoPedidoById(idPedido, SituacaoPedido.CANCELADO);
	}

	private Query gerarQueryPesquisa(Pedido filtro, StringBuilder select) {
		Query query = this.entityManager.createQuery(select.toString());
		final Cliente cliente = filtro.getCliente();
		if (cliente != null && StringUtils.isNotEmpty(cliente.getNomeFantasia())) {
			query.setParameter("nomeFantasia", "%" + cliente.getNomeFantasia() + "%");
		}

		if (cliente != null && StringUtils.isNotEmpty(cliente.getEmail())) {
			query.setParameter("email", "%" + cliente.getEmail() + "%");
		}

		if (cliente != null && StringUtils.isNotEmpty(cliente.getCpf())) {
			query.setParameter("cpf", "%" + cliente.getCpf() + "%");
		} else if (cliente != null && StringUtils.isNotEmpty(cliente.getCnpj())) {
			query.setParameter("cnpj", "%" + cliente.getCnpj() + "%");
		}
		return query;
	}

	private void gerarRestricaoPesquisa(Pedido filtro, StringBuilder select) {
		StringBuilder restricao = new StringBuilder();
		final Cliente cliente = filtro.getCliente();

		if (cliente != null && StringUtils.isNotEmpty(cliente.getNomeFantasia())) {
			restricao.append("p.cliente.nomeFantasia LIKE :nomeFantasia AND ");
		}

		if (cliente != null && StringUtils.isNotEmpty(cliente.getEmail())) {
			restricao.append("p.cliente.email LIKE :email AND ");
		}

		if (cliente != null && StringUtils.isNotEmpty(cliente.getCpf())) {
			restricao.append("p.cliente.cpf LIKE :cpf AND ");
		} else if (cliente != null && StringUtils.isNotEmpty(cliente.getCnpj())) {
			restricao.append("p.cliente.cnpj LIKE :cnpj AND ");
		}

		if (restricao.length() > 0) {
			select.append(" WHERE ").append(restricao);
			select.delete(select.lastIndexOf("AND"), select.length() - 1);
		}
	}

	public List<Pedido> pesquisarBy(Pedido filtro, Integer indiceRegistroInicial, Integer numeroMaximoRegistros) {
		StringBuilder select = null;
		select = new StringBuilder("select p from Pedido p ");

		this.gerarRestricaoPesquisa(filtro, select);
		Query query = this.gerarQueryPesquisa(filtro, select);

		return QueryUtil.paginar(query, indiceRegistroInicial, numeroMaximoRegistros);
	}

	public Pedido pesquisarById(Integer idPedido) {
		return super.pesquisarById(Pedido.class, idPedido);
	}

	public Pedido pesquisarById(Integer idPedido, boolean isCompra) {
		StringBuilder select = new StringBuilder();
		select.append("select p from Pedido p ");
		select.append("join fetch p.proprietario ");
		select.append("left join fetch p.transportadora ");
		select.append("left join fetch p.transportadoraRedespacho ");
		select.append("join fetch p.representada ");
		select.append("join fetch p.contato ");
		select.append("where p.id = :idPedido ");

		if (isCompra) {
			select.append("and p.tipoPedido = :tipoPedido ");
		} else {
			select.append("and p.tipoPedido != :tipoPedido ");
		}

		Query query = this.entityManager.createQuery(select.toString());
		query.setParameter("idPedido", idPedido).setParameter("tipoPedido", TipoPedido.COMPRA);

		return QueryUtil.gerarRegistroUnico(query, Pedido.class, null);
	}

	@SuppressWarnings("unchecked")
	public List<ItemPedido> pesquisarCompraPendenteRecebimento() {
		StringBuilder select = new StringBuilder();
		select.append("select i from ItemPedido i ");
		select.append("where i.pedido.tipoPedido = :tipoPedido ");
		select.append("and i.pedido.situacaoPedido = :situacaoPedido ");
		select.append("order by i.sequencial asc ");

		Query query = this.entityManager.createQuery(select.toString());
		query.setParameter("tipoPedido", TipoPedido.COMPRA);
		query.setParameter("situacaoPedido", SituacaoPedido.COMPRA_PENDENTE_RECEBIMENTO);
		return query.getResultList();
	}

	public Date pesquisarDataEnvioById(Integer idPedido) {
		StringBuilder select = new StringBuilder();
		select.append("select p.dataEnvio from Pedido p where p.id = :id");
		Query query = this.entityManager.createQuery(select.toString());
		query.setParameter("id", idPedido);

		return QueryUtil.gerarRegistroUnico(query, Date.class, null);
	}

	public Date pesquisarDataInclusaoById(Integer idPedido) {
		StringBuilder select = new StringBuilder();
		select.append("select p.dataInclusao from Pedido p where p.id = :id");
		Query query = this.entityManager.createQuery(select.toString());
		query.setParameter("id", idPedido);

		return QueryUtil.gerarRegistroUnico(query, Date.class, null);
	}

	public Integer pesquisarIdPedidoByIdItemPedido(Integer idItemPedido) {
		return QueryUtil.gerarRegistroUnico(
				entityManager.createQuery("select p.id from ItemPedido i inner join i.pedido p where i.id = :idItemPedido")
						.setParameter("idItemPedido", idItemPedido), Integer.class, null);
	}

	@SuppressWarnings("unchecked")
	public List<Integer> pesquisarIdPedidoBySituacaoPedido(SituacaoPedido situacaoPedido) {
		return entityManager.createQuery("select p.id from Pedido p where p.situacaoPedido = :situacaoPedido ")
				.setParameter("situacaoPedido", situacaoPedido).getResultList();
	}

	public Integer pesquisarIdRepresentadaByIdPedido(Integer idPedido) {
		final String select = "select r.id from Pedido p inner join p.representada r where p.id = :idPedido";
		return QueryUtil.gerarRegistroUnico(this.entityManager.createQuery(select).setParameter("idPedido", idPedido),
				Integer.class, null);
	}

	public ItemPedido pesquisarItemPedido(Integer idItemPedido) {
		Query query = this.entityManager.createQuery("select i from ItemPedido i where i.id = :idItemPedido");
		query.setParameter("idItemPedido", idItemPedido);
		return QueryUtil.gerarRegistroUnico(query, ItemPedido.class, null);
	}

	@SuppressWarnings("unchecked")
	public List<ItemPedido> pesquisarItemPedidoByIdPedido(Integer idPedido) {
		Query query = this.entityManager
				.createQuery("select i from ItemPedido i where i.pedido.id = :idPedido order by i.sequencial asc ");
		query.setParameter("idPedido", idPedido);
		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	public List<Logradouro> pesquisarLogradouro(Integer idPedido) {
		return this.entityManager
				.createQuery("select l from Pedido p inner join p.listaLogradouro l where p.id = :idPedido")
				.setParameter("idPedido", idPedido).getResultList();
	}

	public Integer pesquisarMaxSequenciaItemPedido(Integer idPedido) {
		return (Integer) entityManager
				.createQuery("select max(i.sequencial) from ItemPedido i where i.pedido.id = :idPedido")
				.setParameter("idPedido", idPedido).getSingleResult();

	}

	public List<Pedido> pesquisarPedidoByIdCliente(Integer idCliente, Integer indiceRegistroInicial,
			Integer numeroMaximoRegistros) {
		return pesquisarPedidoByIdClienteByIdVendedor(idCliente, null, false, indiceRegistroInicial, numeroMaximoRegistros);
	}

	public List<Pedido> pesquisarPedidoByIdClienteByIdVendedor(Integer idCliente, Integer idProprietario,
			boolean isCompra, Integer indiceRegistroInicial, Integer numeroMaximoRegistros) {
		StringBuilder select = new StringBuilder(
				"select p from Pedido p left join fetch p.proprietario where p.cliente.id = :idCliente ");
		if (idProprietario != null) {
			select.append(" and p.proprietario.id = :idVendedor ");
		}

		if (isCompra) {
			select.append(" and p.tipoPedido = :tipoPedido ");
		} else {
			select.append(" and p.tipoPedido != :tipoPedido ");
		}
		select.append(" order by p.dataInclusao desc, p.cliente.nomeFantasia ");

		Query query = this.entityManager.createQuery(select.toString());
		query.setParameter("idCliente", idCliente);
		if (idProprietario != null) {
			query.setParameter("idVendedor", idProprietario);
		}
		query.setParameter("tipoPedido", TipoPedido.COMPRA);
		return QueryUtil.paginar(query, indiceRegistroInicial, numeroMaximoRegistros);
	}

	public Double pesquisarQuantidadePrecoUnidade(Integer idPedido) {
		return QueryUtil.gerarRegistroUnico(
				this.entityManager.createQuery(
						"select SUM(i.quantidade * i.precoUnidade) from ItemPedido i where i.pedido.id = :idPedido ").setParameter(
						"idPedido", idPedido), Double.class, 0d);
	}

	public Double pesquisarQuantidadePrecoUnidadeIPI(Integer idPedido) {
		return QueryUtil.gerarRegistroUnico(
				this.entityManager.createQuery(
						"select SUM(i.quantidade * i.precoUnidadeIPI) from ItemPedido i where i.pedido.id = :idPedido ")
						.setParameter("idPedido", idPedido), Double.class, 0d);
	}

	public SituacaoPedido pesquisarSituacaoPedidoById(Integer idPedido) {
		return pesquisarCampoById(Pedido.class, idPedido, "situacaoPedido", SituacaoPedido.class);
	}

	public SituacaoPedido pesquisarSituacaoPedidoByIdItemPedido(Integer idItemPedido) {
		return QueryUtil.gerarRegistroUnico(
				entityManager.createQuery(
						"select p.situacaoPedido from ItemPedido i inner join i.pedido p where i.id = :idItemPedido").setParameter(
						"idItemPedido", idItemPedido), SituacaoPedido.class, null);
	}

	public TipoPedido pesquisarTipoPedidoById(Integer idPedido) {
		return pesquisarCampoById(Pedido.class, idPedido, "tipoPedido", TipoPedido.class);
	}

	public SituacaoPedido pesquisarTipoPedidoByIdItemPedido(Integer idItemPedido) {
		return QueryUtil.gerarRegistroUnico(
				entityManager.createQuery(
						"select p.tipoPedido from ItemPedido i inner join i.pedido p where i.id = :idItemPedido").setParameter(
						"idItemPedido", idItemPedido), SituacaoPedido.class, null);
	}

	public Long pesquisarTotalItemPedido(Integer idPedido) {
		return (Long) this.entityManager.createQuery("select count(i.id) from ItemPedido i where i.pedido.id = :idPedido ")
				.setParameter("idPedido", idPedido).getSingleResult();
	}

	public Long pesquisarTotalItemPedido(Integer idPedido, Boolean isItemPendente) {
		StringBuilder select = new StringBuilder();
		select.append("select count(i.id) from ItemPedido i where i.pedido.id = :idPedido ");
		if (isItemPendente != null && !isItemPendente.booleanValue()) {
			select.append("and i.recebido = false");
		}
		return (Long) this.entityManager.createQuery(select.toString()).setParameter("idPedido", idPedido)
				.getSingleResult();
	}

	public long pesquisarTotalItensPedido(Integer idPedido) {
		StringBuilder select = new StringBuilder();
		select.append("select count(i.id) from ItemPedido i inner join i.pedido p where p.id = :idPedido");
		Query query = entityManager.createQuery(select.toString()).setParameter("idPedido", idPedido);

		return QueryUtil.gerarRegistroUnico(query, Long.class, 0L);
	}

	@SuppressWarnings("unchecked")
	public List<Object[]> pesquisarTotalPedidoByPeriodo(Date dataInicio, Date dataFim, boolean isCompra) {
		StringBuilder select = new StringBuilder();
		select.append("select v.nome, r.nomeFantasia, sum(p.valorPedido) from Pedido p ");
		select.append("inner join p.representada r ");
		select.append("inner join p.proprietario v ");
		select.append("where p.dataEnvio >= :dataInicio and p.dataEnvio <= :dataFim ");

		if (isCompra) {
			select.append("and (p.situacaoPedido = :situacaoPedido1 or p.situacaoPedido = :situacaoPedido2) ");
			select.append("and p.tipoPedido = :tipoPedido ");
		} else {
			select.append("and p.situacaoPedido = :situacaoPedido ");
			select.append("and p.tipoPedido != :tipoPedido ");
		}

		select.append("group by v.nome, r.nomeFantasia ");
		select.append("order by v.nome ");

		Query query = this.entityManager.createQuery(select.toString());
		query.setParameter("dataInicio", dataInicio);
		query.setParameter("dataFim", dataFim);
		query.setParameter("tipoPedido", TipoPedido.COMPRA);
		if (isCompra) {
			query.setParameter("situacaoPedido1", SituacaoPedido.COMPRA_PENDENTE_RECEBIMENTO);
			query.setParameter("situacaoPedido2", SituacaoPedido.COMPRA_RECEBIDA);
		} else {
			query.setParameter("situacaoPedido", SituacaoPedido.ENVIADO);
		}
		return query.getResultList();
	}

	public Double pesquisarValorPedido(Integer idPedido) {
		StringBuilder select = new StringBuilder();
		select.append("select i.valorPedido from Pedido i where i.id = :idPedido ");
		Query query = this.entityManager.createQuery(select.toString());
		query.setParameter("idPedido", idPedido);
		return QueryUtil.gerarRegistroUnico(query, Double.class, 0d);
	}

	public Double pesquisarValorPedidoIPI(Integer idPedido) {
		StringBuilder select = new StringBuilder();
		select.append("select i.valorPedidoIPI from Pedido i where i.id = :idPedido ");
		Query query = this.entityManager.createQuery(select.toString());
		query.setParameter("idPedido", idPedido);
		return QueryUtil.gerarRegistroUnico(query, Double.class, 0d);
	}
}
