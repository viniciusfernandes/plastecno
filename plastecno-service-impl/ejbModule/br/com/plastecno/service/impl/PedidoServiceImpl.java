package br.com.plastecno.service.impl;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import br.com.plastecno.service.ClienteService;
import br.com.plastecno.service.ComissaoService;
import br.com.plastecno.service.EmailService;
import br.com.plastecno.service.EstoqueService;
import br.com.plastecno.service.LogradouroService;
import br.com.plastecno.service.MaterialService;
import br.com.plastecno.service.PedidoService;
import br.com.plastecno.service.RamoAtividadeService;
import br.com.plastecno.service.RepresentadaService;
import br.com.plastecno.service.TransportadoraService;
import br.com.plastecno.service.UsuarioService;
import br.com.plastecno.service.calculo.exception.AlgoritmoCalculoException;
import br.com.plastecno.service.constante.SituacaoPedido;
import br.com.plastecno.service.constante.TipoApresentacaoIPI;
import br.com.plastecno.service.constante.TipoEntrega;
import br.com.plastecno.service.constante.TipoFinalidadePedido;
import br.com.plastecno.service.constante.TipoLogradouro;
import br.com.plastecno.service.constante.TipoPedido;
import br.com.plastecno.service.dao.ItemPedidoDAO;
import br.com.plastecno.service.dao.PedidoDAO;
import br.com.plastecno.service.entity.Cliente;
import br.com.plastecno.service.entity.Comissao;
import br.com.plastecno.service.entity.Contato;
import br.com.plastecno.service.entity.ContatoCliente;
import br.com.plastecno.service.entity.ItemPedido;
import br.com.plastecno.service.entity.LogradouroCliente;
import br.com.plastecno.service.entity.LogradouroPedido;
import br.com.plastecno.service.entity.Pedido;
import br.com.plastecno.service.entity.Representada;
import br.com.plastecno.service.entity.Transportadora;
import br.com.plastecno.service.entity.Usuario;
import br.com.plastecno.service.exception.BusinessException;
import br.com.plastecno.service.exception.NotificacaoException;
import br.com.plastecno.service.impl.anotation.REVIEW;
import br.com.plastecno.service.impl.anotation.TODO;
import br.com.plastecno.service.impl.calculo.CalculadoraItem;
import br.com.plastecno.service.impl.calculo.CalculadoraPreco;
import br.com.plastecno.service.impl.mensagem.email.GeradorPedidoEmail;
import br.com.plastecno.service.impl.mensagem.email.TipoMensagemPedido;
import br.com.plastecno.service.impl.util.QueryUtil;
import br.com.plastecno.service.mensagem.email.AnexoEmail;
import br.com.plastecno.service.validacao.exception.InformacaoInvalidaException;
import br.com.plastecno.service.wrapper.PaginacaoWrapper;
import br.com.plastecno.service.wrapper.Periodo;
import br.com.plastecno.service.wrapper.TotalizacaoPedidoWrapper;
import br.com.plastecno.util.DateUtils;
import br.com.plastecno.util.NumeroUtils;
import br.com.plastecno.util.StringUtils;
import br.com.plastecno.validacao.ValidadorInformacao;

@Stateless
public class PedidoServiceImpl implements PedidoService {

	@EJB
	private ClienteService clienteService;

	@EJB
	private ComissaoService comissaoService;

	@EJB
	private EmailService emailService;
	@PersistenceContext(name = "plastecno")
	private EntityManager entityManager;

	@EJB
	private EstoqueService estoqueService;

	private ItemPedidoDAO itemPedidoDAO;

	@EJB
	private LogradouroService logradouroService;

	@EJB
	private MaterialService materialService;

	private PedidoDAO pedidoDAO;

	@EJB
	private RamoAtividadeService ramoAtividadeService;

	@EJB
	private RepresentadaService representadaService;

	@EJB
	private TransportadoraService transportadoraService;

	@EJB
	private UsuarioService usuarioService;

	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	@Override
	public Integer aceitarOrcamento(Integer idOrcamento) throws BusinessException {
		SituacaoPedido situacaoPedido = pesquisarSituacaoPedidoById(idOrcamento);
		if (!SituacaoPedido.ORCAMENTO.equals(situacaoPedido)
				&& !SituacaoPedido.ORCAMENTO_DIGITACAO.equals(situacaoPedido)) {
			throw new BusinessException(
					"Apenas os orçamentos em digitação ou enviados podem ser aceitos para um novo pedido.");
		}

		Integer idPedido = copiarPedido(idOrcamento, true);

		alterarSituacaoPedidoByIdPedido(idOrcamento, SituacaoPedido.ORCAMENTO_ACEITO);

		// O pedido deve ir para digitacao
		alterarSituacaoPedidoByIdPedido(idPedido, SituacaoPedido.DIGITACAO);

		// Amarrando o pedido ao orcamento para o aceite. Isso sera utilizado em
		// relatorios dos orcamento que viraram pedidos.
		pedidoDAO.alterarIdOrcamentoByIdPedido(idPedido, idOrcamento);
		return idPedido;
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public void alterarItemAguardandoCompraByIdPedido(Integer idPedido) {
		pedidoDAO.alterarSituacaoPedidoById(idPedido, SituacaoPedido.ITEM_AGUARDANDO_COMPRA);
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public void alterarItemAguardandoMaterialByIdPedido(Integer idPedido) {
		pedidoDAO.alterarSituacaoPedidoById(idPedido, SituacaoPedido.ITEM_AGUARDANDO_MATERIAL);
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	@REVIEW(descricao = "Aqui estamos realizando um update diretamente na base e temos um problema de roolback quando estourada uma excecao de negocios")
	public void alterarQuantidadeRecepcionada(Integer idItemPedido, Integer quantidadeRecepcionada)
			throws BusinessException {
		if (quantidadeRecepcionada == null) {
			return;
		}

		if (quantidadeRecepcionada < 0) {
			quantidadeRecepcionada = 0;
		}

		Integer qtdeItem = pesquisarQuantidadeItemPedido(idItemPedido);
		if (qtdeItem == null) {
			throw new BusinessException("O item de pedido de código " + idItemPedido
					+ " pesquisado não existe no sistema");
		}

		if (qtdeItem < quantidadeRecepcionada) {
			Integer idPedido = pesquisarIdPedidoByIdItemPedido(idItemPedido);
			Integer sequencialItem = itemPedidoDAO.pesquisarSequencialItemPedido(idItemPedido);
			throw new BusinessException(
					"Não é possível recepcionar uma quantidade maior do que foi comprado para o item No. "
							+ sequencialItem + " do pedido No. " + idPedido);
		} else if (qtdeItem > quantidadeRecepcionada) {
			alterarSituacaoPedidoByIdItemPedido(idItemPedido, SituacaoPedido.COMPRA_AGUARDANDO_RECEBIMENTO);
		}

		SituacaoPedido situacaoPedido = pesquisarSituacaoPedidoByIdItemPedido(idItemPedido);
		if (!SituacaoPedido.COMPRA_AGUARDANDO_RECEBIMENTO.equals(situacaoPedido)) {
			throw new BusinessException(
					"Não é possível alterar a quantidade recepcionada pois a situacao do pedido é \""
							+ situacaoPedido.getDescricao() + "\"");
		}
		itemPedidoDAO.alterarQuantidadeRecepcionada(idItemPedido, quantidadeRecepcionada);
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public void alterarQuantidadeReservadaByIdItemPedido(Integer idItemPedido) {
		entityManager.createQuery("update ItemPedido i set i.quantidadeReservada = 0 where i.id=:id")
				.setParameter("id", idItemPedido).executeUpdate();
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public void alterarRevendaAguardandoMaterialByIdItem(Integer idItemPedido) {
		alterarSituacaoPedidoByIdItemPedido(idItemPedido, SituacaoPedido.ITEM_AGUARDANDO_MATERIAL);
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public void alterarSituacaoPedidoByIdItemPedido(Integer idItemPedido, SituacaoPedido situacaoPedido) {
		Integer idPedido = pesquisarIdPedidoByIdItemPedido(idItemPedido);
		pedidoDAO.alterarSituacaoPedidoById(idPedido, situacaoPedido);
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public void alterarSituacaoPedidoByIdPedido(Integer idPedido, SituacaoPedido situacaoPedido) {
		pedidoDAO.alterarSituacaoPedidoById(idPedido, situacaoPedido);
	}

	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	private void associarLogradouroCliente(Pedido pedido) throws BusinessException {
		if (pedido == null || pedido.getCliente() == null) {
			return;
		}
		List<LogradouroCliente> lLog = clienteService.pesquisarLogradouroCliente(pedido.getCliente().getId());
		if (lLog == null) {
			return;
		}
		// Antes de associar um logradouro novo devemos remover os antigos
		pedidoDAO.removerLogradouroPedido(pedido.getId());
		LogradouroPedido lPed = null;
		for (LogradouroCliente lCli : lLog) {
			lPed = new LogradouroPedido(pedido, lCli);
			pedido.addLogradouro(logradouroService.inserir(lPed));
		}
	}

	@TODO(descricao = "Esse metodo existe apenas para manter a consitencia do valor do pedido no relatorio de pedidos vendidos. Acredito que deve ser removido fututamente e a query do relatorio sera melhorada")
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	private double[] atualizarValoresPedido(Integer idPedido) {
		double[] valores = pedidoDAO.pesquisarValoresPedido(idPedido);
		if (valores.length <= 0) {
			pedidoDAO.alterarValorPedido(idPedido, 0d, 0d);
			return new double[] {};
		}

		double vFrete = pedidoDAO.pesquisarValorFreteByIdPedido(idPedido);
		// O custo do frete deve ser incorporado no pedido e repassado para o
		// cliente.
		valores[0] += vFrete;
		valores[1] += vFrete;
		pedidoDAO.alterarValorPedido(idPedido, valores[0], valores[1]);
		return valores;
	}

	private void calcularComissaoVenda(Pedido pedido, ItemPedido... listaItem) throws BusinessException {

		if (pedido == null || !pedido.isVenda() || !isCalculoComissaoPermitida(pedido.getFinalidadePedido())) {
			return;
		}

		Comissao comissaoVenda = null;
		Double aliqComissao = null;
		Double aliqRepresentada = null;
		Double valorComissionado = null;
		Double valorComissionadoRepresentada = null;
		Double precoItem = null;

		for (ItemPedido itemPedido : listaItem) {

			// Aqui estamos priorizando a aliquota que foi inputada pelo
			// usuario. Usado em casos de vendas especiais, caso contrario sera
			// usada a comissao cadastrada no sistema. Essa situacao eh
			// utilizada apenas na revenda.
			if (pedido.isRevenda() && itemPedido.contemAliquotaComissao()) {
				comissaoVenda = new Comissao();
				comissaoVenda.setAliquotaRevenda(itemPedido.getAliquotaComissao());
				comissaoVenda.setAliquotaRepresentacao(itemPedido.getAliquotaComissao());
			} else if (pedido.isRevenda() && !itemPedido.contemAliquotaComissao()) {
				// A comissao cadastrada para o material tem prioridade a
				// comissao configurada para o vendedor.
				comissaoVenda = comissaoService.pesquisarComissaoVigenteProduto(itemPedido.getMaterial().getId(),
						itemPedido.getFormaMaterial().indexOf());

				// Caso nao exista comissao configurada para o material devemos
				// utilizar a comissao configurada para o vendedor.
				if (comissaoVenda == null) {
					comissaoVenda = comissaoService.pesquisarComissaoVigenteVendedor(pedido.getIdVendedor());
				}

			} // O caso de venda por representacao tem uma comissao diferente da
				// revenda.
			else if (pedido.isRepresentacao()) {
				comissaoVenda = comissaoService.pesquisarComissaoVigenteVendedor(pedido.getIdVendedor());
			}

			// Nos calculos do preco de venda do item nao pode haver o IPI de
			// venda.
			precoItem = itemPedido.calcularPrecoItem();

			if (comissaoVenda != null && comissaoVenda.getAliquotaRevenda() != null && pedido.isRevenda()) {
				aliqComissao = comissaoVenda.getAliquotaRevenda();
				valorComissionado = precoItem * aliqComissao;
			} else if (comissaoVenda != null && comissaoVenda.getAliquotaRepresentacao() != null
					&& pedido.isRepresentacao()) {
				aliqComissao = comissaoVenda.getAliquotaRepresentacao();
				aliqRepresentada = pedido.getAliquotaComissaoRepresentada();

				valorComissionado = precoItem * aliqComissao;
				valorComissionadoRepresentada = precoItem * (aliqRepresentada == null ? 0 : aliqRepresentada);
			} else {
				Usuario vendedor = usuarioService.pesquisarUsuarioResumidoById(pedido.getIdVendedor());
				throw new BusinessException(
						"Não existe comissão configurada para o vendedor \""
								+ vendedor.getNomeCompleto()
								+ "\". Problema para calular a comissão do item No. "
								+ itemPedido.getSequencial()
								+ " do pedido No. "
								+ pedido.getId()
								+ ". Também pode não existir comissão padrão configurada para o material desse item, verifique as configurações do sistema.");
			}
			itemPedido.setAliquotaComissao(aliqComissao);
			itemPedido.setAliquotaComissaoRepresentada(aliqRepresentada);
			itemPedido.setValorComissionado(valorComissionado);
			itemPedido.setValorComissionadoRepresentada(valorComissionadoRepresentada);
			itemPedidoDAO.alterar(itemPedido);
		}
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public List<Date> calcularDataPagamento(Integer idPedido) {
		return calcularDataPagamento(idPedido, null);
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public List<Date> calcularDataPagamento(Integer idPedido, Date dataInicial) {
		List<Date> lista = new ArrayList<Date>();
		String formaPagamento = pedidoDAO.pesquisarFormaPagamentoByIdPedido(idPedido);
		if (formaPagamento == null) {
			return lista;
		}

		// Pedidos com pagamento a vista ou antecipado nao deve ter boletos, por
		// isso nao tera datas de pagamentos
		String[] dias = formaPagamento.split("\\D+");
		if (dias.length == 0) {
			return lista;
		}

		if (dataInicial == null) {
			dataInicial = new Date();
		}

		Calendar cal = Calendar.getInstance();
		cal.setTime(dataInicial);
		Integer diaCorrido = null;
		for (String dia : dias) {
			try {
				diaCorrido = Integer.parseInt(dia);
			} catch (NumberFormatException e) {
				continue;
			}
			cal.add(Calendar.DAY_OF_MONTH, diaCorrido);
			lista.add(cal.getTime());

			// Retornando a data atual para somar os outros dias corridos e
			// evitar criar outros objetos Calendar.
			cal.add(Calendar.DAY_OF_MONTH, -diaCorrido);
		}
		return lista;
	}

	private void calcularPeso(ItemPedido itemPedido) throws AlgoritmoCalculoException {
		// O sistema nao consegue calcular o peso das pecas
		if (itemPedido.isPeca()) {
			return;
		}
		itemPedido.setPeso(calcularPesoItemPedido(itemPedido));
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public Double calcularPesoItemPedido(ItemPedido itemPedido) throws AlgoritmoCalculoException {
		// O sistema nao consegue calcular o peso de pecas
		if (itemPedido.isPeca()) {
			return null;
		}
		if (itemPedido.contemMaterial() && itemPedido.getMaterial().getPesoEspecifico() == null) {
			Double pEspecifico = materialService.pesquisarPesoEspecificoById(itemPedido.getMaterial().getId());
			itemPedido.getMaterial().setPesoEspecifico(pEspecifico);
		}
		return CalculadoraItem.calcularKilo(itemPedido);
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public Double[] calcularValorFreteUnidadeByIdPedido(Integer idPedido) {
		Double[] val = itemPedidoDAO.pesquisarValorFreteUnidadeByIdPedido(idPedido);
		if (val[1] != 0d) {
			val[1] = val[0] / val[1];
		}
		return val;
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public void cancelarOrcamento(Integer idOrcamento) throws BusinessException {
		if (idOrcamento == null) {
			throw new BusinessException("Não é possível cancelar o orçamento pois ele não existe no sistema");
		}

		Integer idPedido = pedidoDAO.pesquisarIdPedidoByIdOrcamento(idOrcamento);
		if (idPedido != null) {
			throw new BusinessException("O orçamento No. " + idOrcamento + " esta associado ao pedido No. " + idPedido
					+ " e não pode ser cancelado.");
		}
		alterarSituacaoPedidoByIdPedido(idOrcamento, SituacaoPedido.ORCAMENTO_CANCELADO);
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public void cancelarPedido(Integer idPedido) throws BusinessException {
		if (idPedido == null) {
			throw new BusinessException("Não é possível cancelar o pedido pois ele não existe no sistema");
		}
		TipoPedido tipoPedido = pedidoDAO.pesquisarTipoPedidoById(idPedido);
		// Essas condicoes serao analisadas quando um pedido for cancelado a
		// partir
		// de um "refazer do pedido".
		if (TipoPedido.COMPRA.equals(tipoPedido)) {
			// estoqueService.devolverItemCompradoEstoqueByIdPedido(idPedido);
		} else if (TipoPedido.REVENDA.equals(tipoPedido)) {
			estoqueService.cancelarReservaEstoqueByIdPedido(idPedido);
		}
		alterarSituacaoPedidoByIdPedido(idPedido, SituacaoPedido.CANCELADO);
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public Integer comprarItemPedido(Integer idComprador, Integer idRepresentadaFornecedora,
			Set<Integer> listaIdItemPedido) throws BusinessException {
		if (listaIdItemPedido == null || listaIdItemPedido.isEmpty()) {
			throw new BusinessException("A lista de itens de pedido para encomendar não pode estar vazia");
		}
		Representada fornecedor = representadaService.pesquisarById(idRepresentadaFornecedora);
		if (fornecedor == null) {
			throw new BusinessException("Fornecedor é obrigatório para realizar a encomenda");
		}

		if (!fornecedor.isFornecedor()) {
			throw new BusinessException("A fornecedor \"" + fornecedor.getNomeFantasia()
					+ "\" escolhido não esta cadastrado como fornecedor");
		}

		verificarMaterialAssociadoFornecedor(idRepresentadaFornecedora, listaIdItemPedido);

		Cliente revendedor = clienteService.pesquisarRevendedor();
		if (revendedor == null) {
			throw new BusinessException(
					"Para efetuar uma encomenda é necessário cadastrar um cliente como revendedor no sistema");
		}

		Usuario comprador = usuarioService.pesquisarById(idComprador);

		Contato contato = new Contato();
		contato.setNome(comprador.getNome());
		contato.setEmail(comprador.getEmail());

		Pedido pedidoCompra = new Pedido();
		pedidoCompra.setCliente(clienteService.pesquisarRevendedor());
		pedidoCompra.setComprador(comprador);
		pedidoCompra.setContato(contato);
		pedidoCompra.setFinalidadePedido(TipoFinalidadePedido.REVENDA);
		pedidoCompra.setProprietario(comprador);
		pedidoCompra.setRepresentada(fornecedor);
		pedidoCompra.setSituacaoPedido(SituacaoPedido.DIGITACAO);
		pedidoCompra.setTipoPedido(TipoPedido.COMPRA);
		pedidoCompra.setTipoEntrega(TipoEntrega.CIF);

		pedidoCompra = inserirPedido(pedidoCompra);
		ItemPedido itemCadastrado = null;
		ItemPedido itemClone = null;
		boolean incluiAlgumItem = false;
		for (Integer idItemPedido : listaIdItemPedido) {
			// Precisamos recuperar o item por completo para clonagem e criacao
			// de um
			// pedido de comprar contendo as mesmas informacoes.
			itemCadastrado = pesquisarItemPedidoById(idItemPedido);
			if (itemCadastrado == null) {
				continue;
			}
			itemClone = itemCadastrado.clone();
			itemClone.setPedido(pedidoCompra);
			itemClone.setQuantidade(itemCadastrado.getQuantidadeEncomendada());
			itemClone.setQuantidadeReservada(0);
			itemClone.setIdPedidoVenda(pedidoDAO.pesquisarIdPedidoByIdItemPedido(idItemPedido));

			try {
				inserirItemPedido(pedidoCompra.getId(), itemClone);
				if (!incluiAlgumItem) {
					incluiAlgumItem = true;
				}
			} catch (BusinessException e) {
				throw new BusinessException(
						"Não foi possível cadastrar uma nova encomenda pois houve falha no item No. "
								+ itemCadastrado.getSequencial() + " do pedido No. "
								+ itemCadastrado.getPedido().getId() + ". Possível problema: "
								+ e.getMensagemEmpilhada());
			}

			itemCadastrado.setEncomendado(true);
			itemCadastrado.setIdPedidoCompra(pedidoCompra.getId());
			// A execucao desse metodo esta garantindo que o pedido que
			// aguardava
			// compra agora ira para aguardando material.
			// inserirItemPedido(itemCadastrado);
			itemPedidoDAO.alterar(itemCadastrado);

			if (!contemPedidoItemRevendaAguardandoEncomenda(idItemPedido)) {
				alterarRevendaAguardandoMaterialByIdItem(itemCadastrado.getId());
				alterarItemAguardandoMaterialByIdPedido(itemCadastrado.getPedido().getId());
			}

		}
		if (!incluiAlgumItem) {
			pedidoDAO.remover(pedidoCompra);
		}
		return pedidoCompra.getId();
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public boolean contemFornecedorDistintoByIdItem(List<Integer> listaIdItem) {
		// Apenas um registro deve ser retornado, o que indica apenas um
		// fornecedor
		return itemPedidoDAO.pesquisarTotalFornecedorDistintoByIdItem(listaIdItem).size() > 1;
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public boolean contemItemPedido(Integer idPedido) {
		return this.pesquisarTotalItemPedido(idPedido) > 0;
	}

	public boolean contemPedidoItemRevendaAguardandoEncomenda(Integer idItemPedido) {
		return pesquisarTotalItemRevendaAguardandoEncomenda(idItemPedido) > 0;
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public boolean contemQuantidadeNaoRecepcionadaItemPedido(Integer idItemPedido) {
		return pesquisarQuantidadeNaoRecepcionadaItemPedido(idItemPedido) > 0;
	}

	@Override
	public Integer copiarPedido(Integer idPedido, boolean isOrcamento) throws BusinessException {
		Pedido pedido = pesquisarPedidoById(idPedido);
		Pedido pClone = null;
		try {
			pClone = pedido.clone();
		} catch (CloneNotSupportedException e) {
			throw new BusinessException("Falha no processo de copia do pedido No. " + idPedido, e);
		}

		// Configurando a data de entrega para um dia posterior
		Calendar c = Calendar.getInstance();
		c.setTime(new Date());
		c.add(Calendar.DAY_OF_MONTH, 1);

		pClone.setId(null);
		pClone.setDataEntrega(c.getTime());
		pClone.setDataEnvio(null);
		pClone.setDataEmissaoNF(null);
		pClone.setDataVencimentoNF(null);
		pClone.setValorParcelaNF(null);
		pClone.setValorTotalNF(null);
		pClone.setListaLogradouro(null);
		pClone.setSituacaoPedido(isOrcamento ? SituacaoPedido.ORCAMENTO_DIGITACAO : SituacaoPedido.DIGITACAO);
		pClone = inserirPedido(pClone);

		List<ItemPedido> listaItemPedido = pesquisarItemPedidoByIdPedido(idPedido);
		ItemPedido iClone = null;
		for (ItemPedido itemPedido : listaItemPedido) {
			try {
				iClone = itemPedido.clone();
				iClone.setIdPedidoCompra(null);
				iClone.setIdPedidoVenda(null);
				iClone.setQuantidadeRecepcionada(null);
				iClone.setQuantidadeReservada(null);
				iClone.setEncomendado(false);
				inserirItemPedido(pClone.getId(), iClone);
			} catch (IllegalStateException e) {
				throw new BusinessException("Falha no processo de copia do item No. " + itemPedido.getId()
						+ " do pedido No. " + idPedido, e);
			}
		}
		return pClone.getId();
	}

	@REVIEW(data = "26/02/2015", descricao = "Esse metodo nao esta muito claro quando tratamos as condicoes dos pedidos de compra. Atualmente tipo nulo vem do controller no caso em que o pedido NAO EH COMPRA")
	private void definirTipoPedido(Pedido pedido) {
		if (pedido == null) {
			return;
		}

		// Aqui os pedidos de venda/revenda podem nao ter sido configurados,
		// portanto, faremos uma consulta pelo nome da representada para
		// decidir, ja
		// que os pedidos de compra sempre serao configurados antes de inserir.
		if (pedido.getTipoPedido() == null) {
			if (pedido.getRepresentada() != null && representadaService.isRevendedor(pedido.getRepresentada().getId())) {
				pedido.setTipoPedido(TipoPedido.REVENDA);
			} else {
				pedido.setTipoPedido(TipoPedido.REPRESENTACAO);
			}
		}
	}

	/**
	 * Aqui estamos exigindo que sempre tenhamos uma nova transacao pois se um
	 * pedido tiver problemas para ser enviado para o empacotamento, isso nao
	 * deve interferir no empacotamento dos outros pedidos.
	 */
	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public boolean empacotarItemAguardandoCompra(Integer idPedido) throws BusinessException {
		boolean empacotamentoOk = estoqueService.reservarItemPedido(idPedido);
		if (!empacotamentoOk) {
			alterarItemAguardandoCompraByIdPedido(idPedido);
		}
		return empacotamentoOk;
	}

	/**
	 * Aqui estamos exigindo que sempre tenhamos uma nova transacao pois se um
	 * pedido tiver problemas para ser enviado para o empacotamento, isso nao
	 * deve interferir no empacotamento dos outros pedidos.
	 */
	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public boolean empacotarItemAguardandoMaterial(Integer idPedido) throws BusinessException {
		boolean empacotamentoOk = estoqueService.reservarItemPedido(idPedido);
		if (!empacotamentoOk) {
			alterarItemAguardandoMaterialByIdPedido(idPedido);
		}
		return empacotamentoOk;
	}

	@Override
	public boolean empacotarPedidoAguardandoCompra(Integer idPedido) throws BusinessException {
		boolean empacotamentoOk = estoqueService.reservarItemPedido(idPedido);
		if (!empacotamentoOk) {
			alterarItemAguardandoCompraByIdPedido(idPedido);
		}
		return empacotamentoOk;
	}

	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	private void enviarCompra(Pedido pedido, AnexoEmail pdfPedido, AnexoEmail... anexos) throws BusinessException {
		try {
			emailService.enviar(GeradorPedidoEmail.gerarMensagem(pedido, TipoMensagemPedido.COMPRA, pdfPedido, anexos));
		} catch (NotificacaoException e) {
			StringBuilder mensagem = new StringBuilder();
			mensagem.append("Falha no envio do pedido de compra No. ").append(pedido.getId()).append(" do comprador ")
					.append(pedido.getComprador().getNomeCompleto()).append(" para o cliente ")
					.append(pedido.getCliente().getNomeCompleto())
					.append(" e contato feito por " + pedido.getContato().getNome());

			e.addMensagem(e.getListaMensagem());
			throw e;
		}
	}

	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	private void enviarOrcamento(Pedido pedido, AnexoEmail pdfPedido, AnexoEmail... anexos) throws BusinessException {
		Contato contato = pedido.getContato();
		if (StringUtils.isEmpty(contato.getEmail())) {
			throw new BusinessException("Email do contato é obrigatório para envio do orçamento");
		}

		if (StringUtils.isEmpty(contato.getDdd())) {
			throw new BusinessException("DDD do telefone do contato é obrigatório para envio do orçamento");
		}

		if (StringUtils.isEmpty(contato.getTelefone())) {
			throw new BusinessException("O telefone do contato é obrigatório para envio do orçamento");
		}

		try {
			// O caso do email alternativo eh para direcionar o email de
			// orcamento para o proprio vendedor, pois eles costumam a enviar o
			// PDF do orcamento para o contato do cliente de outra forma.
			TipoMensagemPedido tipo = pedido.isClienteNotificadoVenda() ? TipoMensagemPedido.ORCAMENTO
					: TipoMensagemPedido.ORCAMENTO_ALTERNATIVO;
			emailService.enviar(GeradorPedidoEmail.gerarMensagem(pedido, tipo, pdfPedido, anexos));
		} catch (NotificacaoException e) {
			StringBuilder mensagem = new StringBuilder();
			mensagem.append("Falha no envio do orçamento No. ").append(pedido.getId()).append(" do vendedor ")
					.append(pedido.getVendedor().getNomeCompleto()).append(" para o cliente ")
					.append(pedido.getCliente().getNomeCompleto())
					.append(" e contato feito por " + pedido.getContato().getNome());

			e.addMensagem(e.getListaMensagem());
			throw e;
		}
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public void enviarPedido(Integer idPedido, AnexoEmail pdfPedido) throws BusinessException {
		enviarPedido(idPedido, pdfPedido, (AnexoEmail[]) null);
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public void enviarPedido(Integer idPedido, AnexoEmail pdfPedido, AnexoEmail... anexos) throws BusinessException {

		final Pedido pedido = pesquisarPedidoById(idPedido);

		if (pedido == null) {
			throw new BusinessException("Pedido/Orçamento não exite no sistema");
		}

		if (pedido.isCancelado()) {
			throw new BusinessException("Pedido/Orçamento foi cancelado e não pode ser enviado.");
		}

		// A data de emissao nao pode ser alterada pois dara conflito no calculo
		// de comissao de vendas
		if (pedido.getDataEnvio() == null) {
			pedido.setDataEnvio(new Date());
		}

		validarEnvio(pedido);

		if (pedido.isOrcamento()) {
			enviarOrcamento(pedido, pdfPedido, anexos);
		} else if (pedido.isVenda()) {
			enviarVenda(pedido, pdfPedido, anexos);
		} else if (pedido.isCompra()) {
			enviarCompra(pedido, pdfPedido, anexos);
		}

		if (pedido.isCompra()) {
			pedido.setSituacaoPedido(SituacaoPedido.COMPRA_AGUARDANDO_RECEBIMENTO);
		} else {
			// Aqui estamos tratando o caso em que a situacao do pedido nao foi
			// definida
			// na reserva dos itens do pedido, pois la o pedido entre em
			// pendecia de
			// reserva.
			if (!pedido.isOrcamento() && SituacaoPedido.DIGITACAO.equals(pedido.getSituacaoPedido())) {
				pedido.setSituacaoPedido(SituacaoPedido.ENVIADO);
			} else if (pedido.isOrcamento()) {
				pedido.setSituacaoPedido(SituacaoPedido.ORCAMENTO);
			}
		}
		pedidoDAO.alterar(pedido);
	}

	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	private void enviarVenda(Pedido pedido, AnexoEmail pdfPedido, AnexoEmail... anexos) throws BusinessException {
		this.validarEnvioVenda(pedido);

		if (pedido.isRevenda()) {
			estoqueService.reservarItemPedido(pedido.getId());
		}

		validarListaLogradouroPreenchida(pedido);
		try {
			emailService.enviar(GeradorPedidoEmail.gerarMensagem(pedido, TipoMensagemPedido.VENDA, pdfPedido, anexos));
			// Caso o contato tambem queira receber o email
			if (pedido.isClienteNotificadoVenda()) {
				emailService.enviar(GeradorPedidoEmail.gerarMensagem(pedido, TipoMensagemPedido.VENDA_CLIENTE,
						pdfPedido, anexos));
			}

		} catch (NotificacaoException e) {
			StringBuilder mensagem = new StringBuilder();
			mensagem.append("Falha no envio do pedido No. ").append(pedido.getId()).append(" do vendedor ")
					.append(pedido.getVendedor().getNomeCompleto()).append(" para a representada ")
					.append(pedido.getRepresentada().getNomeFantasia());

			e.addMensagem(e.getListaMensagem());
			throw e;
		}
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public Pedido gerarPedidoItemSelecionado(Integer idVendedor, boolean isCompra, boolean isOrcamento,
			List<Integer> listaIdItemSelecionado) throws BusinessException {
		if (listaIdItemSelecionado == null || listaIdItemSelecionado.isEmpty()) {
			throw new BusinessException("A lista de item selecionado deve ser preenchida para gerar um novo pedido.");
		}

		if (!itemPedidoDAO.verificarItemPedidoMesmoFornecedor(listaIdItemSelecionado)) {
			throw new BusinessException(
					"Os itens selecionados devem ser de pedidos efetuados para um mesmo fornecedor. Verifique os itens escolhidos.");
		}

		if (!itemPedidoDAO.verificarItemPedidoMesmoCliente(listaIdItemSelecionado)) {
			throw new BusinessException(
					"Os itens selecionados devem ser de pedidos efetuados para um mesmo cliente. Verifique os itens escolhidos.");
		}

		List<ItemPedido> listaItem = pesquisarItemPedidoById(listaIdItemSelecionado);
		if (listaItem == null || listaItem.isEmpty()) {
			throw new BusinessException("Os itens selecionados não existem no sistema.");
		}
		// Como todos os pedidos pertendem ao mesmo fornecedor, entao basta
		// espquisar pelo primeiro.
		Integer idRepres = itemPedidoDAO.pesquisarIdRepresentadaByIdItem(listaIdItemSelecionado.get(0));
		Integer idCli = itemPedidoDAO.pesquisarIdClienteByIdItem(listaIdItemSelecionado.get(0));

		TipoPedido tipo = null;
		if (isCompra) {
			tipo = TipoPedido.COMPRA;
		} else if (representadaService.isRevendedor(idRepres)) {
			tipo = TipoPedido.REPRESENTACAO;
		} else {
			tipo = TipoPedido.REVENDA;
		}

		Pedido p = new Pedido();
		p.setVendedor(new Usuario(idVendedor));
		p.setDataEntrega(DateUtils.gerarDataAmanha());
		p.setFormaPagamento("28 DDL");
		p.setRepresentada(new Representada(idRepres, null));
		p.setTipoPedido(tipo);
		p.setSituacaoPedido(isOrcamento ? SituacaoPedido.ORCAMENTO_DIGITACAO : SituacaoPedido.DIGITACAO);
		p.setCliente(new Cliente(idCli));
		p.setFinalidadePedido(TipoFinalidadePedido.INDUSTRIALIZACAO);

		Contato c = new Contato();
		c.setNome("PREENCHER CONTATO");
		p.setContato(c);

		p = inserirPedido(p);

		ItemPedido clone = null;
		for (ItemPedido item : listaItem) {
			clone = item.clone();
			inserirItemPedido(p.getId(), clone);
		}
		return p;
	}

	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	private Integer gerarSequencialItemPedido(Integer idPedido) {
		Integer seq = pedidoDAO.pesquisarMaxSequenciaItemPedido(idPedido);
		return seq == null ? 1 : ++seq;
	}

	@PostConstruct
	public void init() {
		pedidoDAO = new PedidoDAO(entityManager);
		itemPedidoDAO = new ItemPedidoDAO(entityManager);
	}

	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	private Pedido inserir(Pedido pedido) throws BusinessException {
		if (pedido.isCancelado()) {
			throw new InformacaoInvalidaException("Pedido/Orçamento ja foi cancelado e não pode ser alterado");
		}

		definirTipoPedido(pedido);

		ValidadorInformacao.validar(pedido);

		final Integer idPedido = pedido.getId();
		final boolean isPedidoNovo = idPedido == null;

		final boolean vendaPermitida = usuarioService.isVendaPermitida(pedido.getCliente().getId(), pedido
				.getVendedor().getId());
		/*
		 * Estamos proibindo que qualquer vendedor cadastre um NOVO pedido para
		 * um cliente que nao esteja associado em sua carteira de clientes.
		 */
		if (isPedidoNovo && pedido.isVenda() && !vendaPermitida) {

			Cliente cliente = clienteService.pesquisarById(pedido.getCliente().getId());
			Usuario proprietario = usuarioService.pesquisarById(pedido.getProprietario().getId());
			throw new BusinessException("Não é possível incluir o pedido pois o cliente "
					+ (cliente != null ? cliente.getNomeCompleto() : pedido.getCliente().getId())
					+ " não esta associado ao vendedor "
					+ (proprietario != null ? proprietario.getNome() + " - " + proprietario.getEmail() : pedido
							.getCliente().getId()));
		} else if (isPedidoNovo && pedido.isVenda() && vendaPermitida) {

			// Efetuando o vinculo entre o vendedor e o pedido pois o vendedor
			// eh
			// obrigatorio pois agora eh possivel que um outro vendedor com o
			// perfil
			// de administrador faca cadastro de pedidos em nome de outro. Por
			// isso
			// estamos ajustando o vendedor correto.
			Usuario vendedor = usuarioService.pesquisarVendedorResumidoByIdCliente(pedido.getCliente().getId());
			if (vendedor == null) {
				String nomeCliente = clienteService.pesquisarNomeFantasia(pedido.getCliente().getId());
				throw new BusinessException("Não existe vendedor associado ao cliente " + nomeCliente);
			}
			pedido.setVendedor(vendedor);
		}

		final Date dataEntrega = DateUtils.gerarDataSemHorario(pedido.getDataEntrega());
		if (SituacaoPedido.DIGITACAO.equals(pedido.getSituacaoPedido()) && dataEntrega != null
				&& DateUtils.isAnteriorDataAtual(dataEntrega)) {
			throw new InformacaoInvalidaException("Data de entrega deve ser posterior a data atual");
		}

		if (!pedido.isOrcamento() && TipoEntrega.CIF_TRANS.equals(pedido.getTipoEntrega())
				&& pedido.getTransportadoraRedespacho() == null) {
			throw new BusinessException("A transportadora de redespacho é obrigatória para o tipo de entrega "
					+ TipoEntrega.CIF_TRANS.getDescricao());
		}

		if (isPedidoNovo) {
			pedido = pedidoDAO.inserir(pedido);
		} else {
			// recuperando as informacoes do sistema que nao devem ser alteradas
			// na edicao do pedido.
			pedido.setDataEnvio(pesquisarDataEnvio(idPedido));
			pedido.setValorPedido(pesquisarValorPedido(idPedido));
			pedido.setValorPedidoIPI(pesquisarValorPedidoIPI(idPedido));
			pedido = pedidoDAO.alterar(pedido);
		}
		// Aqui estamos atualizando o valor do pedido pois pode haver um frete.
		atualizarValoresPedido(idPedido);

		// Devemos sempre associar ao pedido o logradouro do cliente.
		associarLogradouroCliente(pedido);

		return pedido;
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public void inserirDadosNotaFiscal(Pedido pedido) {
		if (pedido == null || pedido.getId() == null) {
			return;
		}
		pedidoDAO.inserirDadosNotaFiscal(pedido);
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public Integer inserirItemPedido(Integer idPedido, ItemPedido itemPedido) throws BusinessException {
		// configurando o material para efetuar o calculo usando o peso
		// especifico
		if (itemPedido.getMaterial() != null) {
			itemPedido.setMaterial(materialService.pesquisarById(itemPedido.getMaterial().getId()));
		}

		if (itemPedido.isPeca() && itemPedido.isVendaKilo()) {
			throw new BusinessException("Não é possível vender uma peca por kilo");
		}

		if (itemPedido.isPeca() && StringUtils.isEmpty(itemPedido.getDescricaoPeca())) {
			throw new BusinessException("Descrição da peca do item do pedido é obrigatório");
		}

		itemPedido.configurarMedidaInterna();

		// Aqui retornamos apenas as informacoes necessarias do pedida para a
		// inclusao do item para nao sobrecarregar o sistema.
		Pedido pedido = pedidoDAO.pesquisarPedidoResumidoCalculoComissao(idPedido);
		itemPedido.setPedido(pedido);
		/*
		 * Atualizando o valor de cada unidade do item que podera ser usado
		 * posteriormente em relatorios, alem disso, eh pbrigatorio para
		 * inclusao do item no sistema
		 */
		itemPedido.setPrecoUnidade(CalculadoraPreco.calcularPorUnidade(itemPedido));

		/*
		 * Caso o ipi seja nulo, isso indica que o usuario nao digitou o valor
		 * entao
		 * 
		 * utilizaremos os valores definidos para as formas dos materiais, que
		 * eh o default do sistema. Esse preenchimento foi realizado pois agora
		 * temos que incluir essa informacao do pedido.html que sera enviado
		 * para o cliente.
		 */
		Double aliquotaIPI = itemPedido.getAliquotaIPI();
		final boolean ipiPreenchido = aliquotaIPI != null;
		final TipoApresentacaoIPI tipoApresentacaoIPI = pesquisarTipoApresentacaoIPI(itemPedido);
		final boolean ipiObrigatorio = TipoApresentacaoIPI.SEMPRE.equals(tipoApresentacaoIPI);
		final boolean ipiImportado = TipoApresentacaoIPI.OCASIONAL.equals(tipoApresentacaoIPI)
				&& materialService.isMaterialImportado(itemPedido.getMaterial().getId());

		if (pedido.isVenda() && ipiPreenchido && aliquotaIPI > 0
				&& TipoApresentacaoIPI.NUNCA.equals(tipoApresentacaoIPI)) {
			throw new BusinessException(
					"Remova o valor do IPI do item pois representada escolhida não apresenta cáculo de IPI.");
		} else if (!ipiPreenchido && (ipiObrigatorio || ipiImportado)) {
			itemPedido.setAliquotaIPI(itemPedido.getFormaMaterial().getIpi());
		}

		// No caso em que nao exista a cobranca de IPI os precos serao iguais
		final Double precoUnidadeIPI = CalculadoraPreco.calcularPorUnidadeIPI(itemPedido);

		itemPedido.setPrecoUnidadeIPI(precoUnidadeIPI);
		itemPedido.setPrecoMinimo(NumeroUtils.arredondarValorMonetario(estoqueService
				.calcularPrecoMinimoItemEstoque(itemPedido)));
		itemPedido.setPrecoCusto(estoqueService.calcularPrecoCustoItemEstoque(itemPedido));

		/*
		 * O valor sequencial sera utilizado para que a representada identifique
		 * rapidamento qual eh o item que deve ser customizado, assim o vendedor
		 * podera fazer referencias ao item no campo de observacao, por exemplo:
		 * o item 1 deve ter acabamento, etc.
		 */
		if (itemPedido.isNovo()) {

			itemPedido.setSequencial(gerarSequencialItemPedido(idPedido));
		}

		if (itemPedido.isNovo()) {
			itemPedidoDAO.inserir(itemPedido);
		} else {
			if (pedido.isCompra()) {
				// Esse bloco de codigo foi criado para manter o historico dos
				// pedidos
				// de
				// compra que estao associados a um pedido de venda que nao
				// existe no
				// estoque. Pois ao editarmos um pedido de compra os dados do
				// idpedidovenda estavam desaparecendo.
				Object[] idPedidoCompraEVenda = itemPedidoDAO.pesquisarIdPedidoCompraEVenda(itemPedido.getId());
				itemPedido.setIdPedidoCompra((Integer) idPedidoCompraEVenda[0]);
				itemPedido.setIdPedidoVenda((Integer) idPedidoCompraEVenda[1]);
			}
			itemPedido = itemPedidoDAO.alterar(itemPedido);
		}

		ValidadorInformacao.validar(itemPedido);
		/*
		 * Devemos sempre atualizar o valor do pedido mesmo em caso de excecao
		 * de validacoes, caso contrario teremos um valor nulo na base de dados.
		 */
		atualizarValoresPedido(idPedido);

		// Aqui estamos calculando a comissao pois qualquer alteracao do item do
		// pedido deve refletir no relatorio de comissao. Mesmo que o pedido nao
		// tenha sido enviado a comissao sera calculada, mas so os pedidos
		// enviados aparecerao no relatorio de comissao.
		calcularComissaoVenda(pedido, itemPedido);

		// Aqui estamos calculando o peso do item para agilizar o preenchimento
		// do peso na geracao da NFe
		calcularPeso(itemPedido);
		return itemPedido.getId();
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public Integer inserirItemPedido(ItemPedido itemPedido) throws BusinessException {
		if (itemPedido == null || itemPedido.getId() == null) {
			return null;
		}
		Integer idPedido = pedidoDAO.pesquisarIdPedidoByIdItemPedido(itemPedido.getId());
		if (idPedido == null) {
			throw new BusinessException("Não existe pedido cadastrado para o item "
					+ (itemPedido.isPeca() ? itemPedido.getDescricaoPeca() : itemPedido.getDescricao()));
		}
		return inserirItemPedido(idPedido, itemPedido);
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public void inserirNcmItemAguardandoMaterialAssociadoByIdItemCompra(Integer idItemPedidoCompra, String ncm)
			throws BusinessException {
		if (idItemPedidoCompra == null || ncm == null || ncm.isEmpty()) {
			return;
		}
		try {
			itemPedidoDAO.inserirNcmItemAguardandoMaterialAssociadoItemCompra(idItemPedidoCompra, ncm);
		} catch (Exception e) {
			throw new BusinessException("Falha na inclusao do NCM nos item de revenda associados ao pedido No. "
					+ pesquisarIdPedidoByIdItemPedido(idItemPedidoCompra), e);
		}
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public Pedido inserirOrcamento(Pedido orcamento) throws BusinessException {
		if (orcamento == null) {
			return null;
		}

		if (orcamento.isNovo()) {
			orcamento.setSituacaoPedido(SituacaoPedido.ORCAMENTO_DIGITACAO);
		} else {
			// Garantindo a coerencia da situacao do pedido.
			orcamento.setSituacaoPedido(pesquisarSituacaoPedidoById(orcamento.getId()));
		}

		if (!orcamento.isOrcamento()) {
			throw new BusinessException("Esse pedido não é um orçamento e portanto não pode ser incluído dessa forma");
		}

		Cliente cliente = orcamento.getCliente();
		if (cliente == null) {
			throw new BusinessException(
					"O orçamento não contém cliente. O cliente deve ter ao menos um nome para a inclusão do orçamento.");
		}

		if (orcamento.isClienteNovo()) {
			cliente.addContato(new ContatoCliente(orcamento.getContato()));
			cliente.setRazaoSocial(cliente.getNomeFantasia());
			cliente.setVendedor(orcamento.getVendedor());
			cliente.setRamoAtividade(ramoAtividadeService.pesquisarRamoAtividadePadrao());

			orcamento.setCliente(clienteService.inserir(cliente));
		}
		return inserir(orcamento);
	}

	/*
	 * Esse metodo retorna um pedido pois, apos a inclusao de um novo pedido,
	 * configuramos a data de inclusao como sendo a data atual, e essa
	 * informacao deve ser retornada para o componente chamador.
	 */
	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public Pedido inserirPedido(Pedido pedido) throws BusinessException {
		if (pedido == null) {
			return null;
		}

		if (pedido.isOrcamento()) {
			return inserirOrcamento(pedido);
		}

		return inserir(pedido);
	}

	private boolean isCalculoComissaoPermitida(TipoFinalidadePedido tipoFinalidade) {
		return TipoFinalidadePedido.INDUSTRIALIZACAO.equals(tipoFinalidade)
				|| TipoFinalidadePedido.CONSUMO.equals(tipoFinalidade)
				|| TipoFinalidadePedido.REVENDA.equals(tipoFinalidade);
	}

	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	@Override
	public boolean isCalculoIPIHabilitado(Integer idPedido) {
		Integer idRepresentada = pesquisarIdRepresentadaByIdPedido(idPedido);
		return representadaService.isCalculoIPIHabilitado(idRepresentada);
	}

	@Override
	public boolean isPedidoEnviado(Integer idPedido) {
		SituacaoPedido situacao = QueryUtil.gerarRegistroUnico(
				this.entityManager.createQuery("select p.situacaoPedido from Pedido p where p.id = :idPedido")
						.setParameter("idPedido", idPedido), SituacaoPedido.class, null);

		return SituacaoPedido.ENVIADO.equals(situacao);

	}

	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	@Override
	public boolean isPedidoVendaExistente(Integer idPedido) {
		return pesquisarPedidoById(idPedido, false) != null;
	}

	@Override
	public PaginacaoWrapper<Pedido> paginarPedido(Integer idCliente, Integer idVendedor, Integer idFornecedor,
			boolean isCompra, Integer indiceRegistroInicial, Integer numeroMaximoRegistros, boolean isOrcamento) {
		List<Pedido> listaPedido = null;
		if (idVendedor == null || usuarioService.isVendaPermitida(idCliente, idVendedor)) {
			listaPedido = pesquisarByIdClienteIdFornecedor(idCliente, idFornecedor, isCompra, indiceRegistroInicial,
					numeroMaximoRegistros);
		} else {
			listaPedido = new ArrayList<Pedido>();
		}

		return new PaginacaoWrapper<Pedido>(pesquisarTotalPedidoByIdClienteIdVendedorIdFornecedor(idCliente,
				idVendedor, idFornecedor, isOrcamento, isCompra, null), listaPedido);
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public double pesquisarAliquotaIPIByIdItemPedido(Integer idItemPedido) {
		return itemPedidoDAO.pesquisarAliquotaIPIByIdItemPedido(idItemPedido);
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public List<Pedido> pesquisarBy(Pedido filtro, Integer indiceRegistroInicial, Integer numeroMaximoRegistros) {
		if (filtro == null) {
			return Collections.emptyList();
		}
		return pedidoDAO.pesquisarBy(filtro, indiceRegistroInicial, numeroMaximoRegistros);
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public List<Pedido> pesquisarByIdCliente(Integer idCliente) {
		return this.pesquisarByIdCliente(idCliente, null, null);
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public List<Pedido> pesquisarByIdCliente(Integer idCliente, Integer indiceRegistroInicial,
			Integer numeroMaximoRegistros) {
		return pesquisarByIdClienteIdFornecedor(idCliente, null, false, indiceRegistroInicial, numeroMaximoRegistros);
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public List<Pedido> pesquisarByIdClienteIdFornecedor(Integer idCliente, Integer idFornecedor, boolean isCompra,
			Integer indiceRegistroInicial, Integer numeroMaximoRegistros) {
		return this.pesquisarPedidoByIdClienteIdVendedorIdFornecedor(idCliente, null, idFornecedor, isCompra,
				indiceRegistroInicial, numeroMaximoRegistros);
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public List<ItemPedido> pesquisarCaracteristicaItemPedidoByNumeroItem(List<Integer> listaNumeroItem,
			Integer idPedido) {
		if (idPedido == null || listaNumeroItem == null || listaNumeroItem.isEmpty()) {
			return new ArrayList<ItemPedido>();
		}
		return itemPedidoDAO.pesquisarCaracteristicaItemPedidoByNumeroItem(listaNumeroItem, idPedido);
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public Cliente pesquisarClienteByIdPedido(Integer idPedido) {
		return pedidoDAO.pesquisarClienteByIdPedido(idPedido);
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public Cliente pesquisarClienteResumidoByIdPedido(Integer idPedido) {
		return pedidoDAO.pesquisarClienteResumidoByIdPedido(idPedido);
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public double pesquisarComissaoRepresentadaByIdPedido(Integer idPedido) {
		Double comissao = pedidoDAO.pesquisarComissaoRepresentadaByIdPedido(idPedido);
		return comissao == null ? 0 : comissao;
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public Pedido pesquisarCompraById(Integer id) {
		return pesquisarPedidoById(id, true);
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public List<Pedido> pesquisarCompraByPeriodoEComprador(Periodo periodo, Integer idComprador)
			throws BusinessException {
		return pesquisarPedidoEnviadoByPeriodoEProprietario(false, periodo, idComprador, true);
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public Pedido pesquisarDadosNotaFiscalByIdItemPedido(Integer idItemPedido) {
		return pedidoDAO.pesquisarDadosNotaFiscalByIdItemPedido(idItemPedido);
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public Date pesquisarDataEnvio(Integer idPedido) {
		return pedidoDAO.pesquisarDataEnvioById(idPedido);
	}

	@Override
	@SuppressWarnings("unchecked")
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public List<Pedido> pesquisarEntregaVendaByPeriodo(Periodo periodo) {
		StringBuilder select = new StringBuilder();
		select.append("select new Pedido(p.id, p.dataEntrega, p.valorPedido, p.cliente.nomeFantasia, p.cliente.razaoSocial, p.representada.nomeFantasia) ");
		select.append("from Pedido p ");
		select.append("where p.tipoPedido != :tipoPedido and ");
		select.append(" p.dataEntrega >= :dataInicio and ");
		select.append("p.dataEntrega <= :dataFim and ");
		select.append("p.situacaoPedido IN :situacoes ");
		select.append("order by p.dataEntrega, p.representada.nomeFantasia, p.cliente.nomeFantasia ");

		return this.entityManager.createQuery(select.toString()).setParameter("dataInicio", periodo.getInicio())
				.setParameter("dataFim", periodo.getFim()).setParameter("situacoes", pesquisarSituacaoVendaEfetivada())
				.setParameter("tipoPedido", TipoPedido.COMPRA).getResultList();
	}

	@Override
	@SuppressWarnings("unchecked")
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public List<Pedido> pesquisarEnviadosByPeriodoERepresentada(Periodo periodo, Integer idRepresentada) {
		StringBuilder select = new StringBuilder()

		.append("select new Pedido(p.id, p.dataEnvio, p.valorPedido, p.cliente.razaoSocial) ")
				.append("from Pedido p where p.situacaoPedido in :situacoes and ")
				.append(" p.dataEnvio >= :dataInicio and ").append(" p.dataEnvio <= :dataFim and ")
				.append("p.tipoPedido != :tipoPedido ");

		if (idRepresentada != null) {
			select.append("and p.representada.id = :idRepresentada ");
		}
		select.append("order by p.dataEnvio desc ");

		Query query = this.entityManager.createQuery(select.toString())
				.setParameter("situacoes", pesquisarSituacaoVendaEfetivada())
				.setParameter("dataInicio", periodo.getInicio()).setParameter("dataFim", periodo.getFim())
				.setParameter("tipoPedido", TipoPedido.COMPRA);

		if (idRepresentada != null) {
			query.setParameter("idRepresentada", idRepresentada);
		}
		return query.getResultList();
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public List<Pedido> pesquisarEnviadosByPeriodoEVendedor(Periodo periodo, Integer idVendedor)
			throws BusinessException {
		return this.pesquisarVendaByPeriodoEVendedor(true, periodo, idVendedor);
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public Integer pesquisarIdClienteByIdPedido(Integer idPedido) {
		return pedidoDAO.pesquisarIdClienteByIdPedido(idPedido);
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public List<Integer> pesquisarIdItemPedidoByIdPedido(Integer idPedido) {
		if (idPedido == null) {
			return new ArrayList<Integer>();
		}
		return itemPedidoDAO.pesquisarIdItemPedidoByIdPedido(idPedido);
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public Integer pesquisarIdItemPedidoByIdPedidoSequencial(Integer idPedido, Integer sequencial) {
		if (idPedido == null || sequencial == null) {
			return null;
		}
		return itemPedidoDAO.pesquisarIdItemPedidoByIdPedidoSequencial(idPedido, sequencial);
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public Object[] pesquisarIdMaterialFormaMaterialItemPedido(Integer idItemPedido) {
		return itemPedidoDAO.pesquisarIdMaterialFormaMaterialItemPedido(idItemPedido);
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public List<Integer> pesquisarIdPedidoAguardandoCompra() {
		return pedidoDAO.pesquisarIdPedidoBySituacaoPedido(SituacaoPedido.ITEM_AGUARDANDO_COMPRA);
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public List<Integer> pesquisarIdPedidoAguardandoEmpacotamento() {
		return pedidoDAO.pesquisarIdPedidoBySituacaoPedido(SituacaoPedido.REVENDA_AGUARDANDO_EMPACOTAMENTO);
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public List<Integer> pesquisarIdPedidoAguardandoMaterial() {
		return pedidoDAO.pesquisarIdPedidoBySituacaoPedido(SituacaoPedido.ITEM_AGUARDANDO_MATERIAL);
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public List<Integer> pesquisarIdPedidoAssociadoByIdPedidoOrigem(Integer idPedidoOrigem, boolean isCompra) {
		if (idPedidoOrigem == null) {
			return new ArrayList<Integer>();
		}
		return itemPedidoDAO.pesquisarIdPedidoAssociadoByIdPedidoOrigem(idPedidoOrigem, isCompra);
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public Integer pesquisarIdPedidoByIdItemPedido(Integer idItemPedido) {
		return pedidoDAO.pesquisarIdPedidoByIdItemPedido(idItemPedido);
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public List<Integer> pesquisarIdPedidoByIdItemPedido(List<Integer> listaIdItemPedido) {
		return pedidoDAO.pesquisarIdPedidoByIdItemPedido(listaIdItemPedido);
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public List<Integer> pesquisarIdPedidoItemAguardandoCompra() {
		return pedidoDAO.pesquisarIdPedidoBySituacaoPedido(SituacaoPedido.ITEM_AGUARDANDO_COMPRA);
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public Integer[] pesquisarIdPedidoQuantidadeSequencialByIdPedido(Integer idItem) {
		return itemPedidoDAO.pesquisarIdPedidoQuantidadeSequencialByIdPedido(idItem);
	}

	@Override
	public Integer pesquisarIdRepresentadaByIdPedido(Integer idPedido) {
		return pedidoDAO.pesquisarIdRepresentadaByIdPedido(idPedido);
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public Integer pesquisarIdVendedorByIdPedido(Integer idPedido) {
		if (idPedido == null) {
			return null;
		}
		return QueryUtil.gerarRegistroUnico(
				entityManager.createQuery(
						"select v.id from Pedido p inner join p.proprietario v where p.id = :idPedido ").setParameter(
						"idPedido", idPedido), Integer.class, null);
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public List<ItemPedido> pesquisarItemAguardandoCompra(Integer idCliente, Periodo periodo) {
		if (periodo != null) {
			return itemPedidoDAO.pesquisarItemAguardandoCompra(idCliente, periodo.getInicio(), periodo.getFim());
		}
		return itemPedidoDAO.pesquisarItemAguardandoCompra(idCliente, null, null);
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public List<ItemPedido> pesquisarItemAguardandoMaterial(Integer idRepresentada, Periodo periodo) {
		if (periodo != null) {
			return itemPedidoDAO.pesquisarItemAguardandoMaterial(idRepresentada, periodo.getInicio(), periodo.getFim());
		}
		return itemPedidoDAO.pesquisarItemAguardandoMaterial(idRepresentada, null, null);
	}

	@Override
	public List<ItemPedido> pesquisarItemPedidoAguardandoEmpacotamento() {
		return pesquisarItemPedidoAguardandoEmpacotamento(null);
	}

	@Override
	public List<ItemPedido> pesquisarItemPedidoAguardandoEmpacotamento(Integer idCliente) {
		return itemPedidoDAO.pesquisarItemPedidoAguardandoEmpacotamento(idCliente);
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public ItemPedido pesquisarItemPedidoById(Integer idItemPedido) {
		ItemPedido itemPedido = pedidoDAO.pesquisarItemPedidoById(idItemPedido);
		if (itemPedido != null) {
			Double[] valorPedido = pesquisarValorPedidoByItemPedido(idItemPedido);
			itemPedido.setValorPedido(valorPedido[0]);
			itemPedido.setValorPedidoIPI(valorPedido[1]);
			itemPedido.setValorTotalPedidoSemFrete(valorPedido[2]);
		}
		return itemPedido;
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public List<ItemPedido> pesquisarItemPedidoById(List<Integer> listaIdItem) {
		return itemPedidoDAO.pesquisarItemPedidoById(listaIdItem);
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public List<ItemPedido> pesquisarItemPedidoByIdClienteIdVendedorIdFornecedor(Integer idCliente, Integer idVendedor,
			Integer idFornecedor, boolean isOrcamento, boolean isCompra, Integer indiceRegistroInicial,
			Integer numeroMaximoRegistros, ItemPedido itemVendido) {

		if (idCliente == null) {
			return Collections.emptyList();
		}
		return itemPedidoDAO.pesquisarItemPedidoByIdClienteIdVendedorIdFornecedor(idCliente, idVendedor, idFornecedor,
				isOrcamento, isCompra, indiceRegistroInicial, numeroMaximoRegistros, itemVendido);
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public List<ItemPedido> pesquisarItemPedidoByIdPedido(Integer idPedido) {
		return pedidoDAO.pesquisarItemPedidoByIdPedido(idPedido);
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public List<ItemPedido> pesquisarItemPedidoCompraAguardandoRecepcao(Integer idRepresentada, Periodo periodo) {
		if (periodo != null) {
			return itemPedidoDAO.pesquisarItemPedidoCompraAguardandoRecepcao(idRepresentada, periodo.getInicio(),
					periodo.getFim());
		}
		return itemPedidoDAO.pesquisarItemPedidoCompraAguardandoRecepcao(idRepresentada, null, null);
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public List<ItemPedido> pesquisarItemPedidoCompradoResumidoByPeriodo(Periodo periodo) {
		return pesquisarValoresItemPedidoResumidoByPeriodo(periodo, pesquisarSituacaoCompraEfetivada(),
				TipoPedido.COMPRA);
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public List<ItemPedido> pesquisarItemPedidoCompraEfetivada(Integer idRepresentada, Periodo periodo) {
		if (periodo == null) {
			return itemPedidoDAO.pesquisarItemPedidoCompraEfetivada(idRepresentada, null, null);
		}
		return itemPedidoDAO.pesquisarItemPedidoCompraEfetivada(idRepresentada, periodo.getInicio(), periodo.getFim());
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public List<ItemPedido> pesquisarItemPedidoEncomendado() {
		return pesquisarItemPedidoEncomendado(null, null, null);
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public List<ItemPedido> pesquisarItemPedidoEncomendado(Integer idCliente, Date dataInicial, Date dataFinal) {
		return itemPedidoDAO.pesquisarItemPedidoAguardandoMaterial(idCliente, dataInicial, dataFinal);
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public ItemPedido pesquisarItemPedidoPagamento(Integer idItemPedido) {
		return itemPedidoDAO.pesquisarItemPedidoPagamento(idItemPedido);
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public List<ItemPedido> pesquisarItemPedidoRepresentacaoByPeriodo(Periodo periodo) {
		return pesquisarValoresItemPedidoResumidoByPeriodo(periodo, pesquisarSituacaoVendaEfetivada(),
				TipoPedido.REPRESENTACAO);
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public List<ItemPedido> pesquisarItemPedidoRevendaByPeriodo(Periodo periodo) {
		return pesquisarValoresItemPedidoResumidoByPeriodo(periodo, pesquisarSituacaoVendaEfetivada(),
				TipoPedido.REVENDA);
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public List<ItemPedido> pesquisarItemPedidoVendaByPeriodo(Periodo periodo, Integer idVendedor) {
		if (idVendedor == null) {
			return new ArrayList<ItemPedido>();
		}

		return itemPedidoDAO.pesquisarItemPedidoVendaComissionadaByPeriodo(periodo, idVendedor,
				pesquisarSituacaoVendaEfetivada());
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public List<ItemPedido> pesquisarItemPedidoVendaResumidaByPeriodo(Periodo periodo) {
		return itemPedidoDAO.pesquisarItemPedidoVendaComissionadaByPeriodo(periodo, null,
				pesquisarSituacaoVendaEfetivada());
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public List<LogradouroPedido> pesquisarLogradouro(Integer idPedido) {
		return pesquisarLogradouro(idPedido, null);
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public List<LogradouroPedido> pesquisarLogradouro(Integer idPedido, TipoLogradouro tipo) {
		return pedidoDAO.pesquisarLogradouro(idPedido, tipo);
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public String pesquisarNomeVendedorByIdPedido(Integer idPedido) {
		if (idPedido == null) {
			return null;
		}
		return QueryUtil.gerarRegistroUnico(
				this.entityManager.createQuery(
						"select v.nome from Pedido p inner join p.proprietario v where p.id = :idPedido ")
						.setParameter("idPedido", idPedido), String.class, null);
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public Pedido pesquisarPedidoById(Integer id) {

		if (id == null) {
			return null;
		}
		return this.pedidoDAO.pesquisarById(id);
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public Pedido pesquisarPedidoById(Integer idPedido, boolean isCompra) {
		if (idPedido == null) {
			return null;
		}
		return this.pedidoDAO.pesquisarById(idPedido, isCompra);
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public List<Pedido> pesquisarPedidoByIdCliente(Integer idCliente, Integer indiceRegistroInicial,
			Integer numeroMaximoRegistros) {

		if (idCliente == null) {
			return Collections.emptyList();
		}
		return pedidoDAO.pesquisarPedidoByIdCliente(idCliente, indiceRegistroInicial, numeroMaximoRegistros);
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public List<Pedido> pesquisarPedidoByIdClienteIdVendedorIdFornecedor(Integer idCliente, Integer idVendedor,
			Integer idFornecedor, boolean isCompra, Integer indiceRegistroInicial, Integer numeroMaximoRegistros) {

		if (idCliente == null) {
			return Collections.emptyList();
		}
		return this.pedidoDAO.pesquisarPedidoByIdClienteIdVendedorIdFornecedor(idCliente, idVendedor, idFornecedor,
				isCompra, indiceRegistroInicial, numeroMaximoRegistros);
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public Pedido pesquisarPedidoByIdItemPedido(Integer idItemPedido) {
		return pedidoDAO.pesquisarPedidoByIdItemPedido(idItemPedido);
	}

	@Override
	@SuppressWarnings("unchecked")
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public List<Pedido> pesquisarPedidoCompraByPeriodo(Periodo periodo) {
		StringBuilder select = new StringBuilder();
		select.append("select new Pedido(p.id, p.tipoPedido, p.dataEntrega, p.valorPedido, p.cliente.nomeFantasia, p.cliente.razaoSocial, p.representada.nomeFantasia) ");
		select.append("from Pedido p ");
		select.append("where p.tipoPedido = :tipoPedido and ");
		select.append("p.dataEnvio >= :dataInicio and ");
		select.append("p.dataEnvio <= :dataFim and ");
		select.append("p.situacaoPedido in :situacoes ");
		select.append("order by p.dataEntrega, p.id, p.representada.nomeFantasia, p.cliente.nomeFantasia ");

		return this.entityManager.createQuery(select.toString()).setParameter("dataInicio", periodo.getInicio())
				.setParameter("dataFim", periodo.getFim())
				.setParameter("situacoes", pesquisarSituacaoCompraEfetivada())
				.setParameter("tipoPedido", TipoPedido.COMPRA).getResultList();
	}

	@SuppressWarnings("unchecked")
	private List<Pedido> pesquisarPedidoEnviadoByPeriodoEProprietario(boolean orcamento, Periodo periodo,
			Integer idProprietario, boolean isCompra) throws BusinessException {
		if (idProprietario == null) {
			throw new BusinessException("O ID do vendedor é obrigatório");
		}

		StringBuilder select = new StringBuilder();
		select.append(
				"select new Pedido(p.id, p.tipoPedido, p.dataEntrega, p.dataEnvio, p.valorPedido, p.cliente.nomeFantasia, p.cliente.razaoSocial, p.representada.nomeFantasia) ")
				.append("from Pedido p ")

				.append("where p.situacaoPedido IN :situacoes and ").append("p.proprietario.id = :idProprietario and ")
				.append(" p.dataEnvio >= :dataInicio and ").append(" p.dataEnvio <= :dataFim ");

		if (isCompra) {
			select.append(" and p.tipoPedido = :tipoPedido ");
		} else {
			select.append(" and p.tipoPedido != :tipoPedido ");
		}
		select.append("order by p.dataEnvio desc ");
		List<SituacaoPedido> situacoes = new ArrayList<SituacaoPedido>();

		if (isCompra) {
			situacoes.addAll(pesquisarSituacaoCompraEfetivada());
		} else if (!isCompra && orcamento) {
			situacoes.add(SituacaoPedido.ORCAMENTO);
		} else if (!isCompra && !orcamento) {
			situacoes.addAll(pesquisarSituacaoVendaEfetivada());
		}
		return this.entityManager.createQuery(select.toString()).setParameter("situacoes", situacoes)
				.setParameter("idProprietario", idProprietario).setParameter("dataInicio", periodo.getInicio())
				.setParameter("dataFim", periodo.getFim()).setParameter("tipoPedido", TipoPedido.COMPRA)
				.getResultList();
	}

	@Override
	@SuppressWarnings("unchecked")
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public List<Pedido> pesquisarPedidoVendaByPeriodo(Periodo periodo) {
		StringBuilder select = new StringBuilder();
		select.append("select new Pedido(p.id, p.dataEntrega, p.valorPedido, p.cliente.nomeFantasia, p.cliente.razaoSocial, p.representada.nomeFantasia) ");
		select.append("from Pedido p ");
		select.append("where p.tipoPedido != :tipoPedido and ");
		select.append(" p.dataEnvio >= :dataInicio and ");
		select.append("p.dataEnvio <= :dataFim and ");
		select.append("p.situacaoPedido in (:situacoes) ");
		select.append("order by p.dataEntrega, p.id, p.representada.nomeFantasia, p.cliente.nomeFantasia ");

		return this.entityManager.createQuery(select.toString()).setParameter("dataInicio", periodo.getInicio())
				.setParameter("dataFim", periodo.getFim()).setParameter("situacoes", pesquisarSituacaoVendaEfetivada())
				.setParameter("tipoPedido", TipoPedido.COMPRA).getResultList();
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public Usuario pesquisarProprietario(Integer idPedido) {
		StringBuilder select = new StringBuilder();
		select.append("select p.proprietario from Pedido p where p.id = :id");
		Query query = this.entityManager.createQuery(select.toString());
		query.setParameter("id", idPedido);

		return QueryUtil.gerarRegistroUnico(query, Usuario.class, null);
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public int pesquisarQuantidadeItemPedido(Integer idItemPedido) {
		Integer q = itemPedidoDAO.pesquisarQuantidadeItemPedido(idItemPedido);
		return q == null ? 0 : q;
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public List<Integer[]> pesquisarQuantidadeItemPedidoByIdPedido(Integer idPedido) {
		return itemPedidoDAO.pesquisarQuantidadeItemPedidoByIdPedido(idPedido);
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public int pesquisarQuantidadeNaoRecepcionadaItemPedido(Integer idItemPedido) {
		return pesquisarQuantidadeItemPedido(idItemPedido) - pesquisarQuantidadeRecepcionadaItemPedido(idItemPedido);
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public int pesquisarQuantidadeRecepcionadaItemPedido(Integer idItemPedido) {
		Integer q = itemPedidoDAO.pesquisarQuantidadeRecepcionadaItemPedido(idItemPedido);
		return q == null ? 0 : q;
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public Integer pesquisarQuantidadeReservadaByIdItemPedido(Integer idItemPedido) {
		return itemPedidoDAO.pesquisarQuantidadeReservada(idItemPedido);
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public Representada pesquisarRepresentadaByIdPedido(Integer idPedido) {
		return pedidoDAO.pesquisarRepresentadaByIdPedido(idPedido);
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public Representada pesquisarRepresentadaIdPedido(Integer idPedido) {
		return representadaService.pesquisarById(pesquisarIdRepresentadaByIdPedido(idPedido));
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public Representada pesquisarRepresentadaResumidaByIdPedido(Integer idPedido) {
		return pedidoDAO.pesquisarRepresentadaResumidaByIdPedido(idPedido);
	}

	@Override
	public List<SituacaoPedido> pesquisarSituacaoCompraEfetivada() {
		return pedidoDAO.pesquisarSituacaoCompraEfetivada();
	}

	@Override
	public SituacaoPedido pesquisarSituacaoPedidoById(Integer idPedido) {
		if (idPedido == null) {
			return null;
		}
		return pedidoDAO.pesquisarSituacaoPedidoById(idPedido);
	}

	@Override
	public SituacaoPedido pesquisarSituacaoPedidoByIdItemPedido(Integer idItemPedido) {
		return pedidoDAO.pesquisarSituacaoPedidoByIdItemPedido(idItemPedido);
	}

	@Override
	public List<SituacaoPedido> pesquisarSituacaoRevendaEfetivada() {
		return pedidoDAO.pesquisarSituacaoRevendaEfetivada();
	}

	@Override
	public List<SituacaoPedido> pesquisarSituacaoVendaEfetivada() {
		return pedidoDAO.pesquisarSituacaoVendaEfetivada();
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public Object[] pesquisarTelefoneContatoByIdPedido(Integer idPedido) {
		return pedidoDAO.pesquisarTelefoneContatoByIdPedido((idPedido));
	}

	private TipoApresentacaoIPI pesquisarTipoApresentacaoIPI(ItemPedido itemPedido) throws BusinessException {
		if (itemPedido.getPedido() == null || itemPedido.getPedido().getId() == null) {
			throw new BusinessException(
					"Não é possível verificar a obrigatoriedade do IPI pois pedido ainda não existe no sistema");
		}

		if (itemPedido.getMaterial() == null) {
			throw new BusinessException(
					"Não é possível verificar a obrigatoriedade do IPI pois o item não possui material");
		}

		final Integer idRepresentada = pedidoDAO.pesquisarIdRepresentadaByIdPedido(itemPedido.getPedido().getId());
		return representadaService.pesquisarTipoApresentacaoIPI(idRepresentada);
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public TipoPedido pesquisarTipoPedidoByIdPedido(Integer idPedido) {
		return pedidoDAO.pesquisarTipoPedidoById(idPedido);
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public List<TotalizacaoPedidoWrapper> pesquisarTotalCompraResumidaByPeriodo(Periodo periodo) {
		return pedidoDAO.pesquisarValorTotalPedidoByPeriodo(periodo.getInicio(), periodo.getFim(), true);
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public long pesquisarTotalItemCompradoNaoRecebido(Integer idPedido) {
		return pedidoDAO.pesquisarTotalItemPedido(idPedido, true);
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public Long pesquisarTotalItemPedido(Integer idPedido) {
		return pedidoDAO.pesquisarTotalItemPedido(idPedido);
	}

	public Long pesquisarTotalItemRevendaAguardandoEncomenda(Integer idItemPedido) {
		Integer idPedido = pesquisarIdPedidoByIdItemPedido(idItemPedido);
		return itemPedidoDAO.pesquisarTotalItemRevendaNaoEncomendado(idPedido);
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public Long pesquisarTotalPedidoByIdClienteIdFornecedor(Integer idCliente, Integer idFornecedor,
			boolean isOrcamento, boolean isCompra) {
		return pesquisarTotalPedidoByIdClienteIdVendedorIdFornecedor(idCliente, null, idFornecedor, isOrcamento,
				isCompra, null);
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public Long pesquisarTotalPedidoByIdClienteIdVendedorIdFornecedor(Integer idCliente, Integer idVendedor,
			Integer idFornecedor, boolean isOrcamento, boolean isCompra, ItemPedido itemVendido) {
		if (idCliente == null) {
			return 0L;
		}

		return itemPedidoDAO.pesquisarTotalPedidoByIdClienteIdVendedorIdFornecedor(idCliente, idVendedor, idFornecedor,
				isOrcamento, isCompra, itemVendido);
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public Long pesquisarTotalPedidoVendaByIdClienteIdVendedorIdFornecedor(Integer idCliente) {
		return this.pesquisarTotalPedidoByIdClienteIdVendedorIdFornecedor(idCliente, null, null, false, false, null);
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public List<TotalizacaoPedidoWrapper> pesquisarTotalPedidoVendaResumidaByPeriodo(Periodo periodo) {
		return pedidoDAO.pesquisarValorTotalPedidoByPeriodo(periodo.getInicio(), periodo.getFim(), false);
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public Transportadora pesquisarTransportadoraByIdPedido(Integer idPedido) {
		return pedidoDAO.pesquisarTransportadoraByIdPedido(idPedido);
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public Transportadora pesquisarTransportadoraResumidaByIdPedido(Integer idPedido) {
		return pedidoDAO.pesquisarTransportadoraResumidaByIdPedido(idPedido);
	}

	private List<ItemPedido> pesquisarValoresItemPedidoResumidoByPeriodo(Periodo periodo,
			List<SituacaoPedido> listaSituacao, TipoPedido tipoPedido) {
		StringBuilder select = new StringBuilder();
		select.append("select new ItemPedido(i.precoUnidade, i.quantidade, i.aliquotaIPI, i.aliquotaICMS, i.valorComissionado, i.pedido.aliquotaComissao) from ItemPedido i ");
		select.append("where i.pedido.tipoPedido = :tipoPedido and ");
		select.append("i.pedido.dataEnvio >= :dataInicio and ");
		select.append("i.pedido.dataEnvio <= :dataFim and ");
		select.append("i.pedido.situacaoPedido in :situacoes ");

		return this.entityManager.createQuery(select.toString(), ItemPedido.class)
				.setParameter("dataInicio", periodo.getInicio()).setParameter("dataFim", periodo.getFim())
				.setParameter("situacoes", listaSituacao).setParameter("tipoPedido", tipoPedido).getResultList();
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public Double pesquisarValorFreteByIdPedido(Integer idPedido) {
		return pedidoDAO.pesquisarValorFreteByIdPedido(idPedido);
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public Double pesquisarValorFretePorItemByIdPedido(Integer idPedido) {
		long total = pesquisarTotalItemPedido(idPedido);
		if (0l == total) {
			return 0d;
		}
		Double vFrete = pesquisarValorFreteByIdPedido(idPedido);
		return vFrete / total;
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public Double pesquisarValorPedido(Integer idPedido) {
		final Double valor = pedidoDAO.pesquisarValorPedido(idPedido);
		return valor == null ? 0D : valor;
	}

	@Override
	public Double[] pesquisarValorPedidoByItemPedido(Integer idItemPedido) {
		return itemPedidoDAO.pesquisarValorPedidoByItemPedido(idItemPedido);
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public Double pesquisarValorPedidoIPI(Integer idPedido) {
		final Double valor = pedidoDAO.pesquisarValorPedidoIPI(idPedido);
		return valor == null ? 0D : valor;
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public List<TotalizacaoPedidoWrapper> pesquisarValorVendaClienteByPeriodo(Periodo periodo, Integer idCliente,
			boolean isOrcamento) {
		List<TotalizacaoPedidoWrapper> listaTotalizacao = new ArrayList<TotalizacaoPedidoWrapper>();
		List<Object[]> resultado = pedidoDAO.pesquisarValorVendaClienteByPeriodo(periodo.getInicio(), periodo.getFim(),
				idCliente, isOrcamento);
		for (Object[] o : resultado) {
			listaTotalizacao.add(new TotalizacaoPedidoWrapper((String) o[2], (Long) o[0], (Double) o[1]));
		}
		return listaTotalizacao;
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public Pedido pesquisarVendaById(Integer id) {
		return pesquisarPedidoById(id, false);
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public List<Pedido> pesquisarVendaByPeriodoEVendedor(boolean orcamento, Periodo periodo, Integer idVendedor)
			throws BusinessException {
		return pesquisarPedidoEnviadoByPeriodoEProprietario(orcamento, periodo, idVendedor, false);
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public Usuario pesquisarVendedorByIdItemPedido(Integer idItemPedido) {
		if (idItemPedido == null) {
			return null;
		}
		return QueryUtil
				.gerarRegistroUnico(
						this.entityManager
								.createQuery(
										"select new Usuario(v.id, v.nome, v.sobrenome) from ItemPedido i inner join i.pedido.proprietario v where i.id = :idItemPedido ")
								.setParameter("idItemPedido", idItemPedido), Usuario.class, null);
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public void reencomendarItemPedido(Integer idItemPedido) throws BusinessException {
		alterarSituacaoPedidoByIdItemPedido(idItemPedido, SituacaoPedido.ITEM_AGUARDANDO_COMPRA);
		ItemPedido itemPedido = pesquisarItemPedidoById(idItemPedido);
		itemPedido.setQuantidadeReservada(0);
		itemPedido.setEncomendado(false);
		inserirItemPedido(itemPedido);
	}

	@Override
	public Integer refazerPedido(Integer idPedido) throws BusinessException {
		Integer idClone = copiarPedido(idPedido, false);

		// Ao final da clonaem do pedido precisamos cancelar o antigo para que
		// esse nao aparece nos faturamentos da empresa.
		cancelarPedido(idPedido);
		return idClone;
	}

	@Override
	public Pedido removerItemPedido(Integer idItemPedido) throws BusinessException {
		if (idItemPedido == null) {
			return null;
		}

		Pedido pedido = pedidoDAO.pesquisarPedidoResumidoFinalidadeByIdItemPedido(idItemPedido);
		if (pedido == null) {
			return null;
		}

		try {
			// Aqui vamos remover os itens reservados de devolver as quantidades
			// reservadas do item do pedido para o estoque e zerar as
			// quantidades reservadas do item do pedido.
			estoqueService.devolverItemEstoque(idItemPedido);

			itemPedidoDAO.remover(new ItemPedido(idItemPedido));

			// Efetuando novamente o calculo pois na remocao o valor do pedido
			// deve ser atualizado
			atualizarValoresPedido(pedido.getId());

			if (pedido.isCompraEfetuada() && pesquisarTotalItemPedido(pedido.getId()) <= 0L) {
				pedido.setSituacaoPedido(SituacaoPedido.CANCELADO);
			}

			return pedido;
		} catch (NonUniqueResultException e) {
			throw new BusinessException(
					"Não foi possivel remover o item pois foi encontrato mais de um item para o codigo " + idItemPedido);
		} catch (NoResultException e) {
			throw new BusinessException("Não foi possivel remover o item pois não existe item com o codigo "
					+ idItemPedido);
		} catch (Exception e) {
			Integer seq = itemPedidoDAO.pesquisarSequencialItemPedido(idItemPedido);
			throw new IllegalStateException("Falha na remocao do item No. " + seq + " do pedido No. " + pedido.getId(),
					e);
		}

	}

	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public void removerLogradouroPedido(Integer idPedido) {
		pedidoDAO.removerLogradouroPedido(idPedido);
	}

	private void validarEnvio(Pedido pedido) throws BusinessException {

		if (!pedido.isOrcamento()) {
			clienteService.validarListaLogradouroPreenchida(pedido.getCliente());
		}

		final BusinessException exception = new BusinessException();
		if (SituacaoPedido.CANCELADO.equals(pedido.getSituacaoPedido())) {
			exception.addMensagem("Pedido cancelado ou enviado não pode ser enviado para as representadas");
		}

		if (!this.contemItemPedido(pedido.getId())) {
			exception.addMensagem("Pedido não contem itens para ser enviado");
		}

		if (exception.contemMensagem()) {
			throw exception;
		}
	}

	private void validarEnvioVenda(Pedido pedido) throws BusinessException {
		final BusinessException exception = new BusinessException();
		try {
			this.validarEnvio(pedido);
		} catch (BusinessException e) {
			exception.addMensagem(e.getListaMensagem());
		}

		if (StringUtils.isEmpty(pedido.getFormaPagamento())) {
			exception.addMensagem("Forma de pagamento é obrigatório");
		}

		if (pedido.getTipoEntrega() == null) {
			exception.addMensagem("Tipo de entrega é obrigatório");
		}

		final Date DATA_ENTREGA = pedido.getDataEntrega();
		if (DATA_ENTREGA == null) {
			exception.addMensagem("Data de entrega é obrigatória");
		}

		final Date DATA_ATUAL = new Date();
		if (DATA_ENTREGA != null && DATA_ENTREGA.compareTo(DATA_ATUAL) < 0) {
			exception.addMensagem("Data de entrega deve ser posterior a data atual");
		}

		if (exception.contemMensagem()) {
			throw exception;
		}
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public void validarListaLogradouroPreenchida(Pedido pedido) throws BusinessException {
		if (pedido == null) {
			return;
		}
		logradouroService.validarListaLogradouroPreenchida(pedido.getListaLogradouro());
	}

	private void verificarMaterialAssociadoFornecedor(Integer idRepresentadaFornecedora, Set<Integer> listaIdItemPedido)
			throws BusinessException {
		Integer idMaterial = null;
		for (Integer idItemPedido : listaIdItemPedido) {

			idMaterial = itemPedidoDAO.pesquisarIdMeterialByIdItemPedido(idItemPedido);
			if (!materialService.isMaterialAssociadoRepresentada(idMaterial, idRepresentadaFornecedora)) {
				Integer idPedido = pesquisarIdPedidoByIdItemPedido(idItemPedido);
				Integer sequencial = itemPedidoDAO.pesquisarSequencialItemPedido(idItemPedido);
				String nomeFantasia = representadaService.pesquisarNomeFantasiaById(idRepresentadaFornecedora);
				throw new BusinessException("Não é possível encomendar o item No. " + sequencial + " do pedido No. "
						+ idPedido + " pois o fornecedor \"" + nomeFantasia + "\" não trabalha com o material do item");
			}
		}
	}
}
