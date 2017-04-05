package br.com.plastecno.service.dao;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import br.com.plastecno.service.constante.SituacaoPedido;
import br.com.plastecno.service.constante.TipoPedido;
import br.com.plastecno.service.entity.Cliente;
import br.com.plastecno.service.entity.ItemPedido;
import br.com.plastecno.service.entity.Logradouro;
import br.com.plastecno.service.entity.Pedido;
import br.com.plastecno.service.entity.Representada;
import br.com.plastecno.service.entity.Transportadora;
import br.com.plastecno.service.impl.anotation.REVIEW;
import br.com.plastecno.service.impl.util.QueryUtil;
import br.com.plastecno.service.wrapper.TotalizacaoPedidoWrapper;
import br.com.plastecno.util.StringUtils;

public class PedidoDAO extends GenericDAO<Pedido> {
	public PedidoDAO(EntityManager entityManager) {
		super(entityManager);
	}

	public void alterarSituacaoPedidoById(Integer idPedido, SituacaoPedido situacaoPedido) {
		this.entityManager.createQuery("update Pedido p set p.situacaoPedido = :situacao where p.id = :idPedido")
				.setParameter("situacao", situacaoPedido).setParameter("idPedido", idPedido).executeUpdate();
	}

	public void cancelar(Integer idPedido) {
		alterarSituacaoPedidoById(idPedido, SituacaoPedido.CANCELADO);
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

	public void inserirDadosNotaFiscal(Pedido pedido) {
		entityManager
				.createQuery(
						"update Pedido p set p.numeroNF =:numeroNF, p.dataEmissaoNF =:dataEmissaoNF, p.dataVencimentoNF =:dataVencimentoNF, p.valorParcelaNF =:valorParcelaNF, p.valorTotalNF =:valorTotalNF, p.numeroColeta =:numeroColeta, p.numeroVolumes =:numeroVolumes where p.id =:id")
				.setParameter("numeroNF", pedido.getNumeroNF())
				.setParameter("dataEmissaoNF", pedido.getDataEmissaoNF())
				.setParameter("dataVencimentoNF", pedido.getDataVencimentoNF())
				.setParameter("valorParcelaNF", pedido.getValorParcelaNF())
				.setParameter("valorTotalNF", pedido.getValorTotalNF())
				.setParameter("numeroColeta", pedido.getNumeroColeta())
				.setParameter("numeroVolumes", pedido.getNumeroVolumes()).setParameter("id", pedido.getId())
				.executeUpdate();
	}

	public void inserirDataEmissaoNFe(Integer idPedido, Date dataEmissaoNFe) {
		this.entityManager.createQuery("update Pedido p set p.dataEmissaoNF= :dataEmissaoNFe where p.id = :idPedido")
				.setParameter("dataEmissaoNFe", dataEmissaoNFe).setParameter("idPedido", idPedido).executeUpdate();

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

	public Cliente pesquisarClienteByIdPedido(Integer idPedido) {
		return QueryUtil.gerarRegistroUnico(
				entityManager.createQuery("select p.cliente from Pedido p where p.id = :idPedido").setParameter(
						"idPedido", idPedido), Cliente.class, null);

	}

	public Cliente pesquisarClienteResumidoByIdPedido(Integer idPedido) {
		return QueryUtil
				.gerarRegistroUnico(
						entityManager
								.createQuery(
										"select new Cliente(p.cliente.id, p.cliente.nomeFantasia, p.cliente.razaoSocial, p.cliente.cnpj, p.cliente.cpf, p.cliente.inscricaoEstadual, p.cliente.inscricaoSUFRAMA, p.cliente.email) from Pedido p where p.id = :idPedido")
								.setParameter("idPedido", idPedido), Cliente.class, null);
	}

	public Double pesquisarComissaoRepresentadaByIdPedido(Integer idPedido) {
		return QueryUtil.gerarRegistroUnico(
				this.entityManager.createQuery("select p.representada.comissao from Pedido p where p.id = :idPedido")
						.setParameter("idPedido", idPedido), Double.class, 0d);
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
		query.setParameter("situacaoPedido", SituacaoPedido.COMPRA_AGUARDANDO_RECEBIMENTO);
		return query.getResultList();
	}

	public Pedido pesquisarDadosNotaFiscalByIdItemPedido(Integer idItemPedido) {
		return QueryUtil
				.gerarRegistroUnico(
						entityManager
								.createQuery(
										"select new Pedido(i.pedido.id, i.pedido.numeroNF, i.pedido.dataEmissaoNF, i.pedido.dataVencimentoNF, i.pedido.valorParcelaNF, i.pedido.valorTotalNF, i.pedido.numeroColeta, i.pedido.numeroVolumes) from ItemPedido i where i.id = :idItemPedido")
								.setParameter("idItemPedido", idItemPedido), Pedido.class, null);
	}

	public Date pesquisarDataEmissaoNFe(Integer idPedido) {
		return QueryUtil.gerarRegistroUnico(
				entityManager.createQuery("select p.dataEmissaoNF from Pedido p where p.id = :idPedido").setParameter(
						"idPedido", idPedido), Date.class, null);
	}

	public Date pesquisarDataEnvioById(Integer idPedido) {
		StringBuilder select = new StringBuilder();
		select.append("select p.dataEnvio from Pedido p where p.id = :id");
		Query query = this.entityManager.createQuery(select.toString());
		query.setParameter("id", idPedido);

		return QueryUtil.gerarRegistroUnico(query, Date.class, null);
	}

	public String pesquisarFormaPagamentoByIdPedido(Integer idPedido) {
		return QueryUtil.gerarRegistroUnico(
				entityManager.createQuery("select p.formaPagamento from Pedido p where p.id = :idPedido").setParameter(
						"idPedido", idPedido), String.class, null);
	}

	public Integer pesquisarIdPedidoByIdItemPedido(Integer idItemPedido) {
		return QueryUtil.gerarRegistroUnico(
				entityManager.createQuery(
						"select p.id from ItemPedido i inner join i.pedido p where i.id = :idItemPedido").setParameter(
						"idItemPedido", idItemPedido), Integer.class, null);
	}

	@SuppressWarnings("unchecked")
	public List<Integer> pesquisarIdPedidoByIdItemPedido(List<Integer> listaIdItemPedido) {
		return QueryUtil.gerarRegistroUnico(
				entityManager.createQuery(
						"select p.id from ItemPedido i inner join i.pedido p where i.id IN (:listaIdItemPedido)")
						.setParameter("listaIdItemPedido", listaIdItemPedido), List.class, null);
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

	public ItemPedido pesquisarItemPedidoById(Integer idItemPedido) {
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
		return pesquisarPedidoByIdClienteIdVendedorIdFornecedor(idCliente, null, null, false, indiceRegistroInicial,
				numeroMaximoRegistros);
	}

	@REVIEW(descricao = "Eh provavel que este metodo esteja retornando mais informacao do que o necessario e deve ser refatorado")
	public List<Pedido> pesquisarPedidoByIdClienteIdVendedorIdFornecedor(Integer idCliente, Integer idProprietario,
			Integer idFornecedor, boolean isCompra, Integer indiceRegistroInicial, Integer numeroMaximoRegistros) {
		StringBuilder select = new StringBuilder(
				"select p from Pedido p join fetch p.representada left join fetch p.proprietario where p.cliente.id = :idCliente ");
		if (idProprietario != null) {
			select.append(" and p.proprietario.id = :idVendedor ");
		}

		if (idFornecedor != null) {
			select.append(" and p.representada.id = :idFornecedor ");
		}

		if (isCompra) {
			select.append(" and p.tipoPedido = :tipoPedido ");
		} else {
			select.append(" and p.tipoPedido != :tipoPedido ");
		}
		select.append(" order by p.dataEnvio desc, p.id desc, p.cliente.nomeFantasia ");

		Query query = this.entityManager.createQuery(select.toString());
		query.setParameter("idCliente", idCliente);

		if (idProprietario != null) {
			query.setParameter("idVendedor", idProprietario);
		}

		if (idFornecedor != null) {
			query.setParameter("idFornecedor", idFornecedor);
		}

		query.setParameter("tipoPedido", TipoPedido.COMPRA);
		return QueryUtil.paginar(query, indiceRegistroInicial, numeroMaximoRegistros);
	}

	public Pedido pesquisarPedidoByIdItemPedido(Integer idItemPedido) {
		return QueryUtil.gerarRegistroUnico(
				entityManager.createQuery("select i.pedido from ItemPedido i where i.id = :idItemPedido").setParameter(
						"idItemPedido", idItemPedido), Pedido.class, null);
	}

	public Pedido pesquisarPedidoResumidoCalculoComissao(Integer idPedido) {
		return QueryUtil
				.gerarRegistroUnico(
						entityManager
								.createQuery(
										"select new Pedido(p.representada.comissao, p.id, p.proprietario.id, p.finalidadePedido, p.tipoPedido) from Pedido p where p.id =:idPedido")
								.setParameter("idPedido", idPedido), Pedido.class, null);
	}

	public Double pesquisarQuantidadePrecoUnidade(Integer idPedido) {
		return QueryUtil.gerarRegistroUnico(
				this.entityManager.createQuery(
						"select SUM(i.quantidade * i.precoUnidade) from ItemPedido i where i.pedido.id = :idPedido ")
						.setParameter("idPedido", idPedido), Double.class, 0d);
	}

	public Double pesquisarQuantidadePrecoUnidadeIPI(Integer idPedido) {
		return QueryUtil
				.gerarRegistroUnico(
						this.entityManager
								.createQuery(
										"select SUM(i.quantidade * i.precoUnidadeIPI) from ItemPedido i where i.pedido.id = :idPedido ")
								.setParameter("idPedido", idPedido), Double.class, 0d);
	}

	public Representada pesquisarRepresentadaResumidaByIdPedido(Integer idPedido) {
		final String select = "select new Representada(r.id, r.nomeFantasia) from Pedido p inner join p.representada r where p.id = :idPedido";
		return QueryUtil.gerarRegistroUnico(this.entityManager.createQuery(select).setParameter("idPedido", idPedido),
				Representada.class, null);
	}

	public List<SituacaoPedido> pesquisarSituacaoCompraEfetivada() {
		List<SituacaoPedido> situacoes = new ArrayList<SituacaoPedido>();
		situacoes.add(SituacaoPedido.COMPRA_AGUARDANDO_RECEBIMENTO);
		situacoes.add(SituacaoPedido.COMPRA_RECEBIDA);
		return situacoes;
	}

	public SituacaoPedido pesquisarSituacaoPedidoById(Integer idPedido) {
		return pesquisarCampoById(Pedido.class, idPedido, "situacaoPedido", SituacaoPedido.class);
	}

	public SituacaoPedido pesquisarSituacaoPedidoByIdItemPedido(Integer idItemPedido) {
		return QueryUtil.gerarRegistroUnico(
				entityManager.createQuery(
						"select p.situacaoPedido from ItemPedido i inner join i.pedido p where i.id = :idItemPedido")
						.setParameter("idItemPedido", idItemPedido), SituacaoPedido.class, null);
	}

	public List<SituacaoPedido> pesquisarSituacaoRevendaEfetivada() {
		List<SituacaoPedido> situacoes = pesquisarSituacaoVendaEfetivada();
		situacoes.remove(SituacaoPedido.ENVIADO);
		return situacoes;
	}

	public List<SituacaoPedido> pesquisarSituacaoVendaEfetivada() {
		List<SituacaoPedido> situacoes = new ArrayList<SituacaoPedido>();
		situacoes.add(SituacaoPedido.ITEM_AGUARDANDO_MATERIAL);
		situacoes.add(SituacaoPedido.ITEM_AGUARDANDO_COMPRA);
		situacoes.add(SituacaoPedido.EMPACOTADO);
		situacoes.add(SituacaoPedido.REVENDA_AGUARDANDO_EMPACOTAMENTO);
		situacoes.add(SituacaoPedido.ENVIADO);
		return situacoes;
	}

	public Object[] pesquisarTelefoneContatoByIdPedido(Integer idPedido) {
		return QueryUtil.gerarRegistroUnico(
				entityManager.createQuery(
						"select p.contato.ddd, p.contato.telefone from Pedido p where p.id = :idPedido").setParameter(
						"idPedido", idPedido), Object[].class, new Object[] {});
	}

	public TipoPedido pesquisarTipoPedidoById(Integer idPedido) {
		return pesquisarCampoById(Pedido.class, idPedido, "tipoPedido", TipoPedido.class);
	}

	public SituacaoPedido pesquisarTipoPedidoByIdItemPedido(Integer idItemPedido) {
		return QueryUtil.gerarRegistroUnico(
				entityManager.createQuery(
						"select p.tipoPedido from ItemPedido i inner join i.pedido p where i.id = :idItemPedido")
						.setParameter("idItemPedido", idItemPedido), SituacaoPedido.class, null);
	}

	public Long pesquisarTotalItemPedido(Integer idPedido) {
		return pesquisarTotalItemPedido(idPedido, false);
	}

	public Long pesquisarTotalItemPedido(Integer idPedido, boolean apenasNaoRecebido) {
		StringBuilder select = new StringBuilder();
		select.append("select count(i.id) from ItemPedido i where i.pedido.id = :idPedido ");
		if (apenasNaoRecebido) {
			select.append(" and (i.quantidade != i.quantidadeRecepcionada or i.quantidadeRecepcionada = null)");
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

	public Transportadora pesquisarTransportadoraByIdPedido(Integer idPedido) {
		return QueryUtil
				.gerarRegistroUnico(
						entityManager
								.createQuery(
										"select p.transportadora from Pedido p join fetch p.transportadora.logradouro where p.id = :idPedido")
								.setParameter("idPedido", idPedido), Transportadora.class, null);
	}

	public Double pesquisarValorPedido(Integer idPedido) {
		return QueryUtil.gerarRegistroUnico(
				entityManager.createQuery("select i.valorPedido from Pedido i where i.id = :idPedido").setParameter(
						"idPedido", idPedido), Double.class, 0d);
	}

	public Double pesquisarValorPedidoIPI(Integer idPedido) {
		StringBuilder select = new StringBuilder();
		select.append("select i.valorPedidoIPI from Pedido i where i.id = :idPedido ");
		Query query = this.entityManager.createQuery(select.toString());
		query.setParameter("idPedido", idPedido);
		return QueryUtil.gerarRegistroUnico(query, Double.class, 0d);
	}

	@SuppressWarnings("unchecked")
	public List<TotalizacaoPedidoWrapper> pesquisarValorTotalPedidoByPeriodo(Date dataInicio, Date dataFim,
			boolean isCompra) {
		StringBuilder select = new StringBuilder();
		// select
		// .append("select new TotalizacaoPedidoWrapper(v.id, v.nome, v.sobrenome, r.nomeFantasia, sum(p.valorPedido), sum(p.valorPedidoIPI)) ");

		select.append("select v.id, v.nome, v.sobrenome, r.id, r.nomeFantasia, sum(p.valorPedido), sum(p.valorPedidoIPI) ");
		select.append("from Pedido p ");
		select.append("inner join p.representada r ");
		select.append("inner join p.proprietario v ");
		select.append("where p.dataEnvio >= :dataInicio and p.dataEnvio <= :dataFim ");

		select.append("and p.situacaoPedido IN (:situacoes) ");

		if (isCompra) {
			select.append("and p.tipoPedido = :tipoPedido ");
		} else {
			select.append("and p.tipoPedido != :tipoPedido ");
		}

		select.append("group by v.id, v.nome, r.id, r.nomeFantasia ");
		select.append("order by v.nome, r.nomeFantasia ");

		Query query = this.entityManager.createQuery(select.toString());
		query.setParameter("dataInicio", dataInicio);
		query.setParameter("dataFim", dataFim);
		query.setParameter("tipoPedido", TipoPedido.COMPRA);

		if (isCompra) {
			query.setParameter("situacoes", pesquisarSituacaoCompraEfetivada());
		} else {
			query.setParameter("situacoes", pesquisarSituacaoVendaEfetivada());
		}

		List<TotalizacaoPedidoWrapper> lista = new ArrayList<TotalizacaoPedidoWrapper>();
		List<Object[]> resultados = query.getResultList();

		TotalizacaoPedidoWrapper totalizacao = null;
		for (Object[] o : resultados) {
			totalizacao = new TotalizacaoPedidoWrapper();
			totalizacao.setIdProprietario((Integer) o[0]);
			totalizacao.setNomeProprietario((String) o[1] + " " + (String) o[2]);
			totalizacao.setIdRepresentada((Integer) o[3]);
			totalizacao.setNomeFantasiaRepresentada((String) o[4]);
			totalizacao.setValorTotal((Double) o[5]);
			totalizacao.setValorTotalIPI((Double) o[6]);
			lista.add(totalizacao);
		}
		return lista;

	}

	public List<Object[]> pesquisarValorVendaClienteByPeriodo(Date dataInicial, Date dataFinal, Integer idCliente,
			boolean isOrcamento) {
		StringBuilder select = new StringBuilder();
		select.append("select count(p.id), sum(p.valorPedido), p.cliente.nomeFantasia from Pedido p ");
		select.append("where ");
		if (idCliente != null) {
			select.append("p.cliente.id = :idCliente and ");
		}

		if (dataInicial != null) {
			select.append("p.dataEnvio >= :dataInicial and ");
		}

		if (dataFinal != null) {
			select.append("p.dataEnvio <= :dataFinal and ");
		}

		if (isOrcamento) {
			select.append("p.situacaoPedido = :situacaoPedido ");
		} else {
			select.append("p.situacaoPedido in (:situacoes) ");
		}

		select.append("group BY p.cliente.nomeFantasia ORDER by count(p.id) desc ");
		TypedQuery<Object[]> query = entityManager.createQuery(select.toString(), Object[].class);
		if (idCliente != null) {
			query.setParameter("idCliente", idCliente);
		}

		if (dataInicial != null) {
			query.setParameter("dataInicial", dataInicial);
		}

		if (dataFinal != null) {
			query.setParameter("dataFinal", dataFinal);
		}

		if (isOrcamento) {
			query.setParameter("situacaoPedido", SituacaoPedido.ORCAMENTO);
		} else {
			query.setParameter("situacoes", pesquisarSituacaoVendaEfetivada());
		}

		return query.getResultList();
	}
}
