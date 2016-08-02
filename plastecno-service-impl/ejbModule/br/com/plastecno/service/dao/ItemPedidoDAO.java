package br.com.plastecno.service.dao;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import br.com.plastecno.service.constante.SituacaoPedido;
import br.com.plastecno.service.constante.TipoPedido;
import br.com.plastecno.service.entity.ItemPedido;
import br.com.plastecno.service.impl.util.QueryUtil;
import br.com.plastecno.service.wrapper.Periodo;

public class ItemPedidoDAO extends GenericDAO<ItemPedido> {

	public ItemPedidoDAO(EntityManager entityManager) {
		super(entityManager);
	}

	public void alterarComissao(Integer idItemPedido, Double valorComissao) {
		alterarPropriedade(ItemPedido.class, idItemPedido, "comissao", valorComissao);
	}

	public void alterarQuantidadeRecepcionada(Integer idItemPedido, Integer quantidadeRecepcionada) {
		entityManager
				.createQuery(
						"update ItemPedido i set i.quantidadeRecepcionada = :quantidadeRecepcionada where i.id = :idItemPedido")
				.setParameter("idItemPedido", idItemPedido).setParameter("quantidadeRecepcionada", quantidadeRecepcionada)
				.executeUpdate();
	}

	private StringBuilder gerarConstrutorItemPedidoComDataEntrega() {
		return new StringBuilder(
				"select new ItemPedido(i.id, i.sequencial, i.pedido.id, i.pedido.proprietario.nome, i.quantidade, i.quantidadeRecepcionada, i.quantidadeReservada, i.precoUnidade, i.pedido.representada.nomeFantasia, i.pedido.dataEntrega, i.formaMaterial, i.material.sigla, i.material.descricao, i.descricaoPeca, i.medidaExterna, i.medidaInterna, i.comprimento)  from ItemPedido i ");
	}

	private StringBuilder gerarConstrutorItemPedidoIdPedidoCompraEVenda() {
		return new StringBuilder(
				"select new ItemPedido(i.id, i.sequencial, i.pedido.id, i.idPedidoCompra, i.idPedidoVenda, i.pedido.proprietario.nome, i.quantidade, i.quantidadeRecepcionada, i.quantidadeReservada, i.precoUnidade, i.pedido.representada.nomeFantasia, i.pedido.dataEntrega, i.formaMaterial, i.material.sigla, i.material.descricao, i.descricaoPeca, i.medidaExterna, i.medidaInterna, i.comprimento)  from ItemPedido i ");
	}

	public void inserirComissao(Integer idItemPedido, Double valorComissao) {
		super.alterarPropriedade(ItemPedido.class, idItemPedido, "comissao", valorComissao);
	}

	public Integer inserirNcmItemAguardandoMaterialAssociadoItemCompra(Integer idItemPedidoCompra, String ncm) {
		return entityManager
				.createQuery(
						"update ItemPedido iVenda set iVenda.ncm =:ncm where iVenda.pedido.id in (select iCompra.idPedidoVenda from ItemPedido iCompra where iCompra.id = :idItemPedidoCompra and iCompra.material.id = iVenda.material.id and iCompra.formaMaterial = iVenda.formaMaterial) ")
				.setParameter("idItemPedidoCompra", idItemPedidoCompra).setParameter("ncm", ncm).executeUpdate();
	}

	private void inserirParametroPesquisaItemVendido(Query query, ItemPedido itemVendido) {
		if (itemVendido != null && itemVendido.contemMaterial()) {
			query.setParameter("formaMaterial", itemVendido.getFormaMaterial()).setParameter("idMaterial",
					itemVendido.getMaterial().getId());
			if (itemVendido.getMedidaExterna() != null) {
				query.setParameter("medidaExterna", itemVendido.getMedidaExterna());
			}

			if (itemVendido.getMedidaInterna() != null) {
				query.setParameter("medidaInterna", itemVendido.getMedidaInterna());
			}

			if (itemVendido.getComprimento() != null) {
				query.setParameter("comprimento", itemVendido.getComprimento());
			}
		}
	}

	private void inserirPesquisaItemVendido(StringBuilder select, ItemPedido itemVendido) {
		if (itemVendido != null && itemVendido.contemMaterial()) {
			select.append("and i.formaMaterial =:formaMaterial and i.material.id =:idMaterial ");
			if (itemVendido.getMedidaExterna() != null) {
				select.append("and i.medidaExterna =:medidaExterna ");
			}

			if (itemVendido.getMedidaInterna() != null) {
				select.append("and i.medidaInterna =:medidaInterna ");
			}

			if (itemVendido.getComprimento() != null) {
				select.append("and i.comprimento =:comprimento ");
			}
		}
	}

	public double pesquisarAliquotaIPIByIdItemPedido(Integer idItemPedido) {
		if (idItemPedido == null) {
			return 0;
		}

		Double ipi = QueryUtil.gerarRegistroUnico(
				entityManager.createQuery("select i.aliquotaIPI from ItemPedido i where i.id = :idItemPedido").setParameter(
						"idItemPedido", idItemPedido), Double.class, 0d);
		return ipi == null ? 0 : ipi;
	}

	@SuppressWarnings("unchecked")
	public List<ItemPedido> pesquisarCompraAguardandoRecebimento(Integer idRepresentada, Date dataInicial, Date dataFinal) {
		StringBuilder select = gerarConstrutorItemPedidoIdPedidoCompraEVenda();
		select.append("where i.pedido.tipoPedido = :tipoPedido ");
		select.append("and (i.quantidade != i.quantidadeRecepcionada or i.quantidadeRecepcionada =null)");
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

		select.append("order by i.pedido.dataEntrega asc ");

		Query query = this.entityManager.createQuery(select.toString());
		query.setParameter("tipoPedido", TipoPedido.COMPRA);
		query.setParameter("situacaoPedido", SituacaoPedido.COMPRA_AGUARDANDO_RECEBIMENTO);

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

	public List<Integer> pesquisarIdItemPedidoByIdPedido(Integer idPedido) {
		return entityManager.createQuery("select i.id from ItemPedido i where i.pedido.id = :idPedido", Integer.class)
				.setParameter("idPedido", idPedido).getResultList();
	}

	public Object[] pesquisarIdMaterialFormaMaterialItemPedido(Integer idItemPedido) {
		return QueryUtil.gerarRegistroUnico(
				entityManager.createQuery("select i.material.id, i.formaMaterial from ItemPedido i where i.id = :idItemPedido")
						.setParameter("idItemPedido", idItemPedido), Object[].class, new Object[] {});
	}

	public Integer pesquisarIdMeterialByIdItemPedido(Integer idItemPedido) {
		return QueryUtil.gerarRegistroUnico(
				this.entityManager.createQuery("select i.material.id from ItemPedido i where i.id = :idItemPedido")
						.setParameter("idItemPedido", idItemPedido), Integer.class, null);
	}

	public List<Integer> pesquisarIdPedidoAssociadoByIdPedidoOrigem(Integer idPedidoOrigem, boolean isCompra) {
		StringBuilder select = new StringBuilder("select distinct ");
		if (isCompra) {
			select.append("i.idPedidoVenda ");
		} else {
			select.append("i.idPedidoCompra ");
		}
		select.append("from ItemPedido i where i.pedido.id = :idPedidoOrigem and ");

		if (isCompra) {
			select.append("i.idPedidoVenda != null ");
		} else {
			select.append("i.idPedidoCompra != null ");
		}

		if (isCompra) {
			select.append("order by i.idPedidoVenda desc ");
		} else {
			select.append("order by i.idPedidoCompra desc ");
		}

		return entityManager.createQuery(select.toString(), Integer.class).setParameter("idPedidoOrigem", idPedidoOrigem)
				.getResultList();
	}

	public Object[] pesquisarIdPedidoCompraEVenda(Integer idItemPedido) {
		return entityManager
				.createQuery("select i.idPedidoCompra, i.idPedidoVenda from ItemPedido i where i.id = :idItemPedido",
						Object[].class).setParameter("idItemPedido", idItemPedido).getSingleResult();
	}

	@SuppressWarnings("unchecked")
	public List<ItemPedido> pesquisarItemAguardandoCompra(Integer idCliente, Date dataInicial, Date dataFinal) {
		StringBuilder select = gerarConstrutorItemPedidoComDataEntrega();

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

		select.append("order by i.pedido.dataEntrega asc ");

		Query query = this.entityManager.createQuery(select.toString());
		query.setParameter("tipoPedido", TipoPedido.REVENDA);
		query.setParameter("situacaoPedido", SituacaoPedido.ITEM_AGUARDANDO_COMPRA);

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
	public List<ItemPedido> pesquisarItemAguardandoMaterial(Integer idFornecedor, Date dataInicial, Date dataFinal) {
		StringBuilder select = gerarConstrutorItemPedidoIdPedidoCompraEVenda();

		select.append("where i.pedido.tipoPedido = :tipoPedido ");
		select.append("and i.pedido.situacaoPedido = :situacaoPedido ");
		select.append("and i.encomendado = true and i.quantidadeReservada < i.quantidade ");

		if (dataInicial != null) {
			select.append("and i.pedido.dataEnvio >= :dataInicial ");
		}

		if (dataFinal != null) {
			select.append("and i.pedido.dataEnvio <= :dataFinal ");
		}

		if (idFornecedor != null) {
			select
					.append("and i.idPedidoCompra in ( select p.id from Pedido p where p.tipoPedido = :tipoPedidoCompra and p.representada.id = :idFornecedor ");
			// Essa condicao foi incluida apenas para melhorar o filtro do resultado
			// dos pedidos de compra e nao tem relacao direta com o negocio.
			if (dataInicial != null) {
				select.append("and (p.dataEnvio is null or p.dataEnvio >= :dataInicial) ");
			}
			select.append(" ) ");
		}

		select.append("order by i.pedido.dataEntrega asc ");

		Query query = this.entityManager.createQuery(select.toString());
		query.setParameter("tipoPedido", TipoPedido.REVENDA);
		query.setParameter("situacaoPedido", SituacaoPedido.ITEM_AGUARDANDO_MATERIAL);

		if (dataInicial != null) {
			query.setParameter("dataInicial", dataInicial);
		}

		if (dataFinal != null) {
			query.setParameter("dataFinal", dataFinal);
		}

		if (idFornecedor != null) {
			query.setParameter("tipoPedidoCompra", TipoPedido.COMPRA).setParameter("idFornecedor", idFornecedor);
		}

		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	public List<ItemPedido> pesquisarItemPedidoAguardandoEmpacotamento(Integer idCliente) {
		StringBuilder select = gerarConstrutorItemPedidoComDataEntrega();

		select.append("where i.pedido.situacaoPedido = :situacaoPedido and i.quantidadeReservada > 0 ");

		if (idCliente != null) {
			select.append("and i.pedido.cliente.id = :idCliente ");
		}

		select.append("order by i.pedido.id asc ");

		Query query = this.entityManager.createQuery(select.toString());
		query.setParameter("situacaoPedido", SituacaoPedido.REVENDA_AGUARDANDO_EMPACOTAMENTO);

		if (idCliente != null) {
			query.setParameter("idCliente", idCliente);
		}

		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	public List<ItemPedido> pesquisarItemPedidoAguardandoMaterial(Integer idCliente, Date dataInicial, Date dataFinal) {
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
		query.setParameter("situacaoPedido", SituacaoPedido.ITEM_AGUARDANDO_MATERIAL);

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

	public List<ItemPedido> pesquisarItemPedidoByIdClienteIdVendedorIdFornecedor(Integer idCliente,
			Integer idProprietario, Integer idFornecedor, boolean isCompra, Integer indiceRegistroInicial,
			Integer numeroMaximoRegistros, ItemPedido itemVendido) {

		// Tivemos que particionar a consulta em 2 etapas pois a paginacao deve ser
		// feita na consulta de pedidos, mas estava sendo realizada na consulta de
		// itens de pedidos, isto eh, retornavamos apenas 10 itens por consulta
		// quando devemos na verdade retornar 10 pedidos por consulta.
		StringBuilder selectPedido = new StringBuilder();
		selectPedido.append("select p.id from Pedido p where p.cliente.id = :idCliente ");

		if (idProprietario != null) {
			selectPedido.append(" and p.proprietario.id = :idVendedor ");
		}

		if (idFornecedor != null) {
			selectPedido.append(" and p.representada.id = :idFornecedor ");
		}

		if (isCompra) {
			selectPedido.append(" and p.tipoPedido = :tipoPedido ");
		} else {
			selectPedido.append(" and p.tipoPedido != :tipoPedido ");
		}

		selectPedido.append("order by p.id desc ");

		Query query = this.entityManager.createQuery(selectPedido.toString());
		query.setParameter("idCliente", idCliente);

		if (idProprietario != null) {
			query.setParameter("idVendedor", idProprietario);
		}

		if (idFornecedor != null) {
			query.setParameter("idFornecedor", idFornecedor);
		}

		query.setParameter("tipoPedido", TipoPedido.COMPRA);

		List<Object[]> listaIdPedido = QueryUtil.paginar(query, indiceRegistroInicial, numeroMaximoRegistros);
		if (listaIdPedido.isEmpty()) {
			return new ArrayList<ItemPedido>();
		}

		StringBuilder selectItem = new StringBuilder();

		selectItem
				.append("select new ItemPedido(i.pedido.id, i.pedido.numeroPedidoCliente, i.pedido.situacaoPedido, i.pedido.dataEnvio, i.pedido.tipoPedido, i.pedido.representada.nomeFantasia, i.id, i.sequencial, i.quantidade, i.precoUnidade, i.formaMaterial, ");

		selectItem
				.append("i.material.sigla, i.material.descricao, i.descricaoPeca, i.medidaExterna, i.medidaInterna, i.comprimento, i.tipoVenda, i.precoVenda, i.aliquotaIPI, i.aliquotaICMS) from ItemPedido i ");
		selectItem.append("where i.pedido.id in (:listaIdPedido) ");

		inserirPesquisaItemVendido(selectItem, itemVendido);

		selectItem.append("order by i.pedido.dataEnvio asc ");

		TypedQuery<ItemPedido> queryItem = entityManager.createQuery(selectItem.toString(), ItemPedido.class).setParameter(
				"listaIdPedido", Arrays.asList(listaIdPedido.toArray(new Integer[] {})));

		inserirParametroPesquisaItemVendido(queryItem, itemVendido);

		return queryItem.getResultList();
	}

	public List<ItemPedido> pesquisarItemPedidoVendaComissionadaByPeriodo(Periodo periodo, Integer idVendedor,
			List<SituacaoPedido> listaSituacao) {
		StringBuilder select = new StringBuilder(
				"select new ItemPedido(i.id, i.sequencial, i.pedido.id, i.pedido.proprietario.id, i.pedido.proprietario.nome, i.pedido.proprietario.sobrenome, i.precoUnidade, i.precoCusto, i.quantidade, i.valorComissionado, i.formaMaterial, i.material.sigla, i.material.descricao, i.descricaoPeca, i.medidaExterna, i.medidaInterna, i.comprimento) ");
		select.append("from ItemPedido i ");
		select.append("where i.pedido.tipoPedido != :tipoPedido and ");
		select.append("i.pedido.dataEnvio >= :dataInicio and ");
		select.append("i.pedido.dataEnvio <= :dataFim and ");
		select.append("i.pedido.situacaoPedido in (:situacoes) ");
		if (idVendedor != null) {
			select.append("and i.pedido.proprietario.id = :idVendedor ");
		}
		select.append("order by i.pedido.dataEnvio ");

		TypedQuery<ItemPedido> query = this.entityManager.createQuery(select.toString(), ItemPedido.class)
				.setParameter("dataInicio", periodo.getInicio()).setParameter("dataFim", periodo.getFim())
				.setParameter("situacoes", listaSituacao).setParameter("tipoPedido", TipoPedido.COMPRA);

		if (idVendedor != null) {
			query.setParameter("idVendedor", idVendedor);
		}
		return query.getResultList();
	}

	public Integer pesquisarQuantidadeItemPedido(Integer idItemPedido) {
		return pesquisarCampoById(ItemPedido.class, idItemPedido, "quantidade", Integer.class);
	}

	public Integer pesquisarQuantidadeRecepcionadaItemPedido(Integer idItemPedido) {
		return pesquisarCampoById(ItemPedido.class, idItemPedido, "quantidadeRecepcionada", Integer.class);
	}

	public Integer pesquisarSequencialItemPedido(Integer idItemPedido) {
		return pesquisarCampoById(ItemPedido.class, idItemPedido, "sequencial", Integer.class);
	}

	public Long pesquisarTotalItemRevendaNaoEncomendado(Integer idPedido) {
		return QueryUtil.gerarRegistroUnico(
				this.entityManager.createQuery(
						"select count(i.id) from ItemPedido i where  i.encomendado = false and i.pedido.id = :idPedido")
						.setParameter("idPedido", idPedido), Long.class, null);
	}

	public Long pesquisarTotalPedidoByIdClienteIdVendedorIdFornecedor(Integer idCliente, Integer idVendedor,
			Integer idFornecedor, boolean isCompra, ItemPedido itemVendido) {
		StringBuilder select = new StringBuilder("select count(p.id) from Pedido p where p.cliente.id = :idCliente ");
		if (idVendedor != null) {
			select.append("and p.proprietario.id = :idVendedor ");
		}

		if (idFornecedor != null) {
			select.append("and p.representada.id = :idFornecedor ");
		}

		if (isCompra) {
			select.append("and p.tipoPedido = :tipoPedido ");
		} else {
			select.append("and p.tipoPedido != :tipoPedido ");
		}

		inserirPesquisaItemVendido(select, itemVendido);

		Query query = this.entityManager.createQuery(select.toString());
		query.setParameter("idCliente", idCliente).setParameter("tipoPedido", TipoPedido.COMPRA);

		if (idVendedor != null) {
			query.setParameter("idVendedor", idVendedor);
		}

		if (idFornecedor != null) {
			query.setParameter("idFornecedor", idFornecedor);
		}

		inserirParametroPesquisaItemVendido(query, itemVendido);

		return QueryUtil.gerarRegistroUnico(query, Long.class, null);
	}

	public Double[] pesquisarValorPedidoByItemPedido(Integer idItemPedido) {
		Query query = this.entityManager
				.createQuery("select i.pedido.valorPedido, i.pedido.valorPedidoIPI  from ItemPedido i where i.id = :idItemPedido");
		query.setParameter("idItemPedido", idItemPedido);
		Object[] valores = QueryUtil.gerarRegistroUnico(query, Object[].class, new Object[] { 0d, 0d });
		return new Double[] { (Double) valores[0], (Double) valores[1] };
	}
}
