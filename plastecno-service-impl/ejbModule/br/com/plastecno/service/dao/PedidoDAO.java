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
import br.com.plastecno.service.wrapper.Periodo;
import br.com.plastecno.util.StringUtils;

public class PedidoDAO extends GenericDAO<Pedido> {

	public PedidoDAO(EntityManager entityManager) {
		super(entityManager);
	}

	public void cancelar(Integer idPedido) {
		this.entityManager.createQuery("update Pedido p set p.situacaoPedido = :situacao where p.id = :idPedido")
				.setParameter("situacao", SituacaoPedido.CANCELADO).setParameter("idPedido", idPedido).executeUpdate();
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
	public List<Pedido> pesquisarCompraPendenteByPeriodo(Integer idRepresentada, Periodo periodo) {
		StringBuilder select = new StringBuilder();
		select.append("select p from Pedido p join fetch p.representada ");
		select.append("where p.dataEntrega >= :dataInicio and ");
		select.append("p.dataEntrega <= :dataFim and ");
		select.append("p.situacaoPedido = :situacaoPedido and ");
		select.append("p.representada.id = :idRepresentada ");
		select.append("order by p.dataEntrega, p.representada.nomeFantasia, p.cliente.nomeFantasia ");
		return this.entityManager.createQuery(select.toString()).setParameter("dataInicio", periodo.getInicio())
				.setParameter("dataFim", periodo.getFim())
				.setParameter("situacaoPedido", SituacaoPedido.COMPRA_PENDENTE_RECEBIMENTO)
				.setParameter("idRepresentada", idRepresentada).getResultList();

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

	public Integer pesquisarIdRepresentadaByIdPedido(Integer idPedido) {
		final String select = "select r.id from Pedido p inner join p.representada r where p.id = :idPedido";
		return QueryUtil.gerarRegistroUnico(this.entityManager.createQuery(select).setParameter("idPedido", idPedido),
				Integer.class, null);
	}

	@SuppressWarnings("unchecked")
	public List<ItemPedido> pesquisarItemPedidoByIdPedido(Integer idPedido) {
		Query query = this.entityManager
				.createQuery("select i from ItemPedido i where i.pedido.id = :idPedido order by i.sequencial asc ");
		query.setParameter("idPedido", idPedido);
		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	public List<ItemPedido> pesquisarCompraPendenteRecebimento() {
		StringBuilder select = new StringBuilder();
		select.append("select i from ItemPedido i ");
		select.append("where i.pedido.tipoPedido = :tipoPedido ");
		select.append("and i.pedido.situacaoPedido = :situacaoPedido ");
		select.append("order by i.sequencial asc ");
		
		Query query = this.entityManager
				.createQuery(select.toString());
		query.setParameter("tipoPedido", TipoPedido.COMPRA);
		query.setParameter("situacaoPedido", SituacaoPedido.COMPRA_PENDENTE_RECEBIMENTO);
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

	public Long pesquisarTotalItemPedido(Integer idPedido) {
		return (Long) this.entityManager.createQuery("select count(i.id) from ItemPedido i where i.pedido.id = :idPedido ")
				.setParameter("idPedido", idPedido).getSingleResult();
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

	public List<Pedido> pesquisarPedidoByIdCliente(Integer idCliente, Integer indiceRegistroInicial,
			Integer numeroMaximoRegistros) {
		return pesquisarPedidoByIdClienteByIdVendedor(idCliente, null, false, indiceRegistroInicial, numeroMaximoRegistros);
	}

}
