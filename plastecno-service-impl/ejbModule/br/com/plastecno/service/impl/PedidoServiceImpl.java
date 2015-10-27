package br.com.plastecno.service.impl;

import java.util.ArrayList;
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
import javax.persistence.PersistenceException;
import javax.persistence.Query;

import br.com.plastecno.service.ClienteService;
import br.com.plastecno.service.ComissaoService;
import br.com.plastecno.service.EmailService;
import br.com.plastecno.service.EstoqueService;
import br.com.plastecno.service.LogradouroService;
import br.com.plastecno.service.MaterialService;
import br.com.plastecno.service.PedidoService;
import br.com.plastecno.service.RepresentadaService;
import br.com.plastecno.service.TransportadoraService;
import br.com.plastecno.service.UsuarioService;
import br.com.plastecno.service.constante.FinalidadePedido;
import br.com.plastecno.service.constante.SituacaoPedido;
import br.com.plastecno.service.constante.TipoApresentacaoIPI;
import br.com.plastecno.service.constante.TipoEntrega;
import br.com.plastecno.service.constante.TipoPedido;
import br.com.plastecno.service.dao.ItemPedidoDAO;
import br.com.plastecno.service.dao.PedidoDAO;
import br.com.plastecno.service.entity.Cliente;
import br.com.plastecno.service.entity.Comissao;
import br.com.plastecno.service.entity.Contato;
import br.com.plastecno.service.entity.ItemPedido;
import br.com.plastecno.service.entity.Logradouro;
import br.com.plastecno.service.entity.Pedido;
import br.com.plastecno.service.entity.Representada;
import br.com.plastecno.service.entity.Usuario;
import br.com.plastecno.service.exception.BusinessException;
import br.com.plastecno.service.exception.NotificacaoException;
import br.com.plastecno.service.impl.anotation.REVIEW;
import br.com.plastecno.service.impl.calculo.CalculadoraPreco;
import br.com.plastecno.service.impl.mensagem.email.GeradorPedidoEmail;
import br.com.plastecno.service.impl.mensagem.email.TipoMensagemPedido;
import br.com.plastecno.service.impl.util.QueryUtil;
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
	private RepresentadaService representadaService;

	@EJB
	private TransportadoraService transportadoraService;

	@EJB
	private UsuarioService usuarioService;

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
	public void alterarQuantidadeRecepcionada(Integer idItemPedido, Integer quantidadeRecepcionada)
			throws BusinessException {
		if (quantidadeRecepcionada == null) {
			return;
		}

		SituacaoPedido situacaoPedido = pesquisarSituacaoPedidoByIdItemPedido(idItemPedido);
		if (!SituacaoPedido.COMPRA_AGUARDANDO_RECEBIMENTO.equals(situacaoPedido)) {
			throw new BusinessException("Não é possível alterar a quantidade recepcionada pois a situacao do pedido é \""
					+ situacaoPedido.getDescricao() + "\"");
		}

		Integer quantidadeItem = itemPedidoDAO.pesquisarQuantidadeItemPedido(idItemPedido);
		if (quantidadeItem == null) {
			throw new BusinessException("O item de pedido de código " + idItemPedido + " pesquisado não existe no sistema");
		}

		if (quantidadeItem < quantidadeRecepcionada) {
			Integer idPedido = pesquisarIdPedidoByIdItemPedido(idItemPedido);
			Integer sequencialItem = itemPedidoDAO.pesquisarSequencialItemPedido(idItemPedido);
			throw new BusinessException(
					"Não é possível recepcionar uma quantidade maior do que foi comprado para o item No. " + sequencialItem
							+ " do pedido No. " + idPedido);
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

	@SuppressWarnings("unchecked")
	private void alterarSequencialItemPedido(Integer idPedido, Integer sequencial) {
		if (sequencial != null && sequencial > 0) {

			List<Object[]> resultados = entityManager
					.createQuery("select i.id, i.sequencial from ItemPedido i where i.pedido.id = :idPedido")
					.setParameter("idPedido", idPedido).getResultList();
			Integer novaSeq = null;
			Integer id = null;
			for (Object[] array : resultados) {
				id = (Integer) array[0];
				novaSeq = (Integer) array[1];
				entityManager
						.createQuery(
								"update ItemPedido i set i.sequencial = :novaSeq where i.id = :id and i.sequencial >= :sequencial")
						.setParameter("novaSeq", --novaSeq).setParameter("id", id).setParameter("sequencial", sequencial)
						.executeUpdate();
			}

		}
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

	private void calcularComissaoVenda(Pedido pedido) throws BusinessException {
		if (!pedido.isVenda()) {
			return;
		}

		if (pedido.isRepresentacao() && pedido.getAliquotaComissao() == null) {
			throw new BusinessException("Não existe comissão configurada para o pedido pedido No. " + pedido.getId()
					+ ". Veja as configurações da representada \"" + pedido.getRepresentada().getNomeFantasia() + "\"");
		}

		List<ItemPedido> listaItem = pesquisarItemPedidoByIdPedido(pedido.getId());
		Comissao comissaoVenda = null;
		Double valorComissionado = null;
		Double valorComissionadoRepresentacao = null;
		Double precoItem = null;

		for (ItemPedido itemPedido : listaItem) {
			if (pedido.isRevenda()) {
				comissaoVenda = comissaoService.pesquisarComissaoVigenteProduto(itemPedido.getMaterial().getId(), itemPedido
						.getFormaMaterial().indexOf());

				// Caso nao exista comissao configurada para o material, devemos
				// utilizar a comissao configurada para o vendedor.
				if (comissaoVenda == null) {
					comissaoVenda = comissaoService.pesquisarComissaoVigenteVendedor(pedido.getVendedor().getId());
				}

			} else if (pedido.isRepresentacao()) {
				comissaoVenda = comissaoService.pesquisarComissaoVigenteVendedor(pedido.getVendedor().getId());
			}

			// Nos calculos do preco de venda do item nao pode haver o IPI de venda.
			precoItem = itemPedido.calcularPrecoItem();

			if (pedido.isRevenda() && comissaoVenda != null && comissaoVenda.getAliquotaRevenda() != null) {
				valorComissionado = precoItem * comissaoVenda.getAliquotaRevenda();
			} else if (pedido.isRepresentacao() && comissaoVenda != null && comissaoVenda.getAliquotaRepresentacao() != null) {
				valorComissionado = precoItem * comissaoVenda.getAliquotaRepresentacao();
				valorComissionadoRepresentacao = precoItem * (pedido.getRepresentada().getComissao());
			} else {
				Usuario vendedor = usuarioService.pesquisarUsuarioResumidoById(pedido.getVendedor().getId());
				throw new BusinessException(
						"Não existe comissão configurada para o vendedor \""
								+ vendedor.getNomeCompleto()
								+ "\". Problema para calular a comissão do item No. "
								+ itemPedido.getSequencial()
								+ " do pedido No. "
								+ pedido.getId()
								+ ". Também pode não existir comissão padrão configurada para o material desse item, verifique as configurações do sistema.");
			}

			itemPedido.setValorComissionado(valorComissionado);
			itemPedido.setValorComissionadoRepresentacao(valorComissionadoRepresentacao);
			itemPedidoDAO.alterar(itemPedido);
		}
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public Double calcularValorPedido(Integer idPedido) throws BusinessException {
		try {
			return pedidoDAO.pesquisarQuantidadePrecoUnidade(idPedido);
		} catch (PersistenceException e) {
			throw new BusinessException("Falha no calculo do valor da unidade do item do pedido " + idPedido
					+ ". Provavelmente o valor do item esta estourando os limites do sistema.");
		}
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public Double calcularValorPedidoIPI(Integer idPedido) throws BusinessException {
		try {
			return pedidoDAO.pesquisarQuantidadePrecoUnidadeIPI(idPedido);
		} catch (PersistenceException e) {
			throw new BusinessException("Falha no calculo do valor IPI do item do pedido " + idPedido
					+ ". Provavelmente o valor do item esta estourando os limites do sistema.");
		}
	}

	@Override
	public void cancelarPedido(Integer idPedido) throws BusinessException {
		if (idPedido == null) {
			throw new BusinessException("Não é possível cancelar o pedido pois ele não existe no sistema");
		}
		TipoPedido tipoPedido = pedidoDAO.pesquisarTipoPedidoById(idPedido);
		// Essas condicoes serao analisadas quando um pedido for cancelado a partir
		// de um "refazer do pedido".
		if (TipoPedido.COMPRA.equals(tipoPedido)) {
			// estoqueService.devolverItemCompradoEstoqueByIdPedido(idPedido);
		} else if (TipoPedido.REVENDA.equals(tipoPedido)) {
			estoqueService.cancelarReservaEstoqueByIdPedido(idPedido);
		}
		pedidoDAO.cancelar(idPedido);
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
		pedidoCompra.setDataInclusao(new Date());
		pedidoCompra.setFinalidadePedido(FinalidadePedido.REVENDA);
		pedidoCompra.setProprietario(comprador);
		pedidoCompra.setRepresentada(fornecedor);
		pedidoCompra.setSituacaoPedido(SituacaoPedido.COMPRA_ANDAMENTO);
		pedidoCompra.setTipoPedido(TipoPedido.COMPRA);
		pedidoCompra.setTipoEntrega(TipoEntrega.CIF);

		pedidoCompra = inserir(pedidoCompra);
		ItemPedido itemCadastrado = null;
		ItemPedido itemClone = null;
		boolean incluiAlgumItem = false;
		for (Integer idItemPedido : listaIdItemPedido) {
			itemCadastrado = pesquisarItemPedido(idItemPedido);
			if (itemCadastrado == null) {
				continue;
			}
			itemClone = itemCadastrado.clone();
			itemClone.setPedido(pedidoCompra);
			itemClone.setQuantidade(itemCadastrado.getQuantidadeEncomendada());
			itemClone.setQuantidadeReservada(0);

			try {
				inserirItemPedido(pedidoCompra.getId(), itemClone);
				if (!incluiAlgumItem) {
					incluiAlgumItem = true;
				}
			} catch (BusinessException e) {
				throw new BusinessException("Não foi possível cadastrar uma nova encomenda pois houve falha no item No. "
						+ itemCadastrado.getSequencial() + " do pedido No. " + itemCadastrado.getPedido().getId()
						+ ". Possível problema: " + e.getMensagemEmpilhada());
			}
			itemCadastrado.setEncomendado(true);
			inserirItemPedido(itemCadastrado);
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

	@REVIEW(data = "26/02/2015", descricao = "Esse metodo nao esta muito claro quando tratamos as condicoes dos pedidos de compra. Atualmente tipo nulo vem do controller no caso em que o pedido NAO EH COMPRA")
	private void definirTipoPedido(Pedido pedido) {
		// Aqui os pedidos de venda/revenda podem nao ter sido configurados,
		// portanto, faremos uma consulta pelo nome da representada para decidir, ja
		// que os pedidos de compra sempre serao configurados antes de inserir.
		if (pedido.getTipoPedido() == null) {
			if (representadaService.isRevendedor(pedido.getRepresentada().getId())) {
				pedido.setTipoPedido(TipoPedido.REVENDA);
			} else {
				pedido.setTipoPedido(TipoPedido.REPRESENTACAO);
			}
		}
	}

	/**
	 * Aqui estamos exigindo que sempre tenhamos uma nova transacao pois se um
	 * pedido tiver problemas para ser enviado para o empacotamento, isso nao deve
	 * interferir no empacotamento dos outros pedidos.
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
	 * pedido tiver problemas para ser enviado para o empacotamento, isso nao deve
	 * interferir no empacotamento dos outros pedidos.
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

	private void enviarOrcamento(Pedido pedido, byte[] arquivoAnexado) throws BusinessException {

		if (StringUtils.isEmpty(pedido.getContato().getEmail())) {
			throw new BusinessException("Email do contato é obrigatório para envio do orçamento");
		}
		try {
			emailService.enviar(new GeradorPedidoEmail(pedido, arquivoAnexado).gerarMensagem(TipoMensagemPedido.ORCAMENTO));
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
	public void enviarPedido(Integer idPedido, byte[] arquivoAnexado) throws BusinessException {

		final Pedido pedido = pesquisarPedidoById(idPedido);

		if (pedido == null) {
			throw new BusinessException("Pedido não exite no sistema");
		}

		/*
		 * Devemos sempre usar a lista do cliente pois o cliente pode ter alterado
		 * os dados de logradouro
		 */
		pedido.addLogradouro(clienteService.pesquisarLogradouro(pedido.getCliente().getId()));
		pedido.setDataEnvio(new Date());

		validarEnvio(pedido);

		if (pedido.isOrcamento()) {
			enviarOrcamento(pedido, arquivoAnexado);
		} else {
			calcularComissaoVenda(pedido);
			enviarVenda(pedido, arquivoAnexado);
		}
		if (pedido.isCompra()) {
			pedido.setSituacaoPedido(SituacaoPedido.COMPRA_AGUARDANDO_RECEBIMENTO);
		}
		pedidoDAO.alterar(pedido);
	}

	@Override
	public boolean enviarRevendaAguardandoEncomendaEmpacotamento(Integer idPedido) throws BusinessException {
		boolean empacotamentoOk = estoqueService.reservarItemPedido(idPedido);
		if (!empacotamentoOk) {
			alterarItemAguardandoCompraByIdPedido(idPedido);
		}
		return empacotamentoOk;
	}

	private void enviarVenda(Pedido pedido, byte[] arquivoAnexado) throws BusinessException {
		this.validarEnvioVenda(pedido);

		if (pedido.isRevenda()) {
			estoqueService.reservarItemPedido(pedido.getId());
		}

		logradouroService.validarListaLogradouroPreenchida(pedido.getListaLogradouro());
		// Aqui estamos tratando o caso em que a situacao do pedido nao foi definida
		// na reserva dos itens do pedido, pois la o pedido entre em pendecia de
		// reserva.
		if (SituacaoPedido.DIGITACAO.equals(pedido.getSituacaoPedido())) {
			pedido.setSituacaoPedido(SituacaoPedido.ENVIADO);
		}

		try {
			GeradorPedidoEmail gerador = new GeradorPedidoEmail(pedido, arquivoAnexado);
			emailService.enviar(gerador.gerarMensagem(TipoMensagemPedido.VENDA));

			if (pedido.isClienteNotificadoVenda()) {
				emailService.enviar(gerador.gerarMensagem(TipoMensagemPedido.VENDA_CLIENTE));
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

	private Integer gerarSequencialItemPedido(Integer idPedido) {
		Integer seq = pedidoDAO.pesquisarMaxSequenciaItemPedido(idPedido);
		return seq == null ? 1 : ++seq;
	}

	@PostConstruct
	public void init() {
		pedidoDAO = new PedidoDAO(entityManager);
		itemPedidoDAO = new ItemPedidoDAO(entityManager);
	}

	/*
	 * Esse metodo retorna um pedido pois, apos a inclusao de um novo pedido,
	 * configuramos a data de inclusao como sendo a data atual, e essa informacao
	 * deve ser retornada para o componente chamador.
	 */
	@Override
	public Pedido inserir(Pedido pedido) throws BusinessException {
		if (SituacaoPedido.CANCELADO.equals(pedido.getSituacaoPedido())) {
			throw new InformacaoInvalidaException("Pedido ja foi cancelado e nao pode ser alterado");
		}

		definirTipoPedido(pedido);

		ValidadorInformacao.validar(pedido);

		final Integer idPedido = pedido.getId();
		final boolean isPedidoNovo = idPedido == null;
		/*
		 * Estamos proibindo que qualquer vendedor cadastre um NOVO pedido para um
		 * cliente que nao esteja associado em sua carteira de clientes.
		 */
		if (isPedidoNovo && pedido.isVenda()
				&& !this.usuarioService.isVendaPermitida(pedido.getCliente().getId(), pedido.getVendedor().getId())) {

			Cliente cliente = this.clienteService.pesquisarById(pedido.getCliente().getId());
			Usuario proprietario = this.usuarioService.pesquisarById(pedido.getProprietario().getId());
			throw new BusinessException("Não é possível incluir o pedido pois o cliente "
					+ (cliente != null ? cliente.getNomeCompleto() : pedido.getCliente().getId())
					+ " não esta associado ao vendedor "
					+ (proprietario != null ? proprietario.getNome() + " - " + proprietario.getEmail() : pedido.getCliente()
							.getId()));
		}

		if (pedido.isVenda()) {

			// Efetuando o vinculo entre o vendedor e o pedido pois o vendedor eh
			// obrigatorio pois agora eh possivel que um outro vendedor com o perfil
			// de administrador faca cadastro de pedidos em nome de outro. Por isso
			// estamos ajustando o vendedor correto.
			Usuario vendedor = usuarioService.pesquisarVendedorByIdCliente(pedido.getCliente().getId());
			if (vendedor == null) {
				String nomeCliente = clienteService.pesquisarNomeFantasia(pedido.getCliente().getId());
				throw new BusinessException("Não existe vendedor associado ao cliente " + nomeCliente);
			}
			pedido.setVendedor(vendedor);
			pedido.setAliquotaComissao(representadaService.pesquisarComissaoRepresentada(pedido.getRepresentada().getId()));
		}

		final Date dataEntrega = DateUtils.gerarDataSemHorario(pedido.getDataEntrega());
		if (SituacaoPedido.DIGITACAO.equals(pedido.getSituacaoPedido()) && dataEntrega != null
				&& DateUtils.isAnteriorDataAtual(dataEntrega)) {
			throw new InformacaoInvalidaException("Data de entrega deve ser posterior a data atual");
		}

		if (TipoEntrega.CIF_TRANS.equals(pedido.getTipoEntrega()) && pedido.getTransportadoraRedespacho() == null) {
			throw new BusinessException("A transportadora de redespacho é obrigatória para o tipo de entrega "
					+ TipoEntrega.CIF_TRANS.getDescricao());
		}
		/*
		 * Devemos sempre pesquisar pois o cliente pode ter alterado os dados de
		 * logradouro
		 */
		pedido.addLogradouro(this.clienteService.pesquisarLogradouro(pedido.getCliente().getId()));

		if (isPedidoNovo) {
			pedido.setDataInclusao(new Date());
			if (!pedido.isCompraAndamento()) {
				pedido.setSituacaoPedido(SituacaoPedido.DIGITACAO);
			}
			pedido = pedidoDAO.inserir(pedido);

		} else {
			// recuperando as informacoes do sistema que nao devem ser alteradas
			// na edicao do pedido.
			pedido.setDataInclusao(this.pesquisarDataInclusao(idPedido));
			pedido.setDataEnvio(this.pesquisarDataEnvio(idPedido));
			pedido.setValorPedido(this.pesquisarValorPedido(idPedido));
			pedido.setValorPedidoIPI(this.pesquisarValorPedidoIPI(idPedido));
			pedido = pedidoDAO.alterar(pedido);
		}

		return pedido;
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public Integer inserirItemPedido(Integer idPedido, ItemPedido itemPedido) throws BusinessException {
		// configurando o material para efetuar o calculo usando o peso
		// especifico
		if (itemPedido.getMaterial() != null) {
			itemPedido.setMaterial(this.materialService.pesquisarById(itemPedido.getMaterial().getId()));
		}

		if (itemPedido.isPeca() && itemPedido.isVendaKilo()) {
			throw new BusinessException("Não é possível vender uma peca por kilo");
		}

		if (itemPedido.isPeca() && StringUtils.isEmpty(itemPedido.getDescricaoPeca())) {
			throw new BusinessException("Descrição da peca do item do pedido é obrigatório");
		}

		itemPedido.configurarMedidaInterna();

		// Temos que recuperar o pedido pois os valores totais do pedido serao
		// alterados sempre com a inclusao de item. Esses valores serao utilizados
		// para facilitar a exibicao dos valores do pedido para o usuario.
		final Pedido pedido = this.pesquisarPedidoById(idPedido);
		itemPedido.setPedido(pedido);
		/*
		 * Atualizando o valor de cada unidade do item que podera ser usado
		 * posteriormente em relatorios, alem disso, eh pbrigatorio para inclusao do
		 * item no sistema
		 */
		itemPedido.setPrecoUnidade(CalculadoraPreco.calcularPorUnidade(itemPedido));

		/*
		 * Caso o ipi seja nulo, isso indica que o usuario nao digitou o valor entao
		 * 
		 * utilizaremos os valores definidos para as formas dos materiais, que eh o
		 * default do sistema. Esse preenchimento foi realizado pois agora temos que
		 * incluir essa informacao do pedido.html que sera enviado para o cliente.
		 */
		Double aliquotaIPI = itemPedido.getAliquotaIPI();
		final boolean ipiPreenchido = aliquotaIPI != null;
		final TipoApresentacaoIPI tipoApresentacaoIPI = pesquisarTipoApresentacaoIPI(itemPedido);
		final boolean ipiObrigatorio = TipoApresentacaoIPI.SEMPRE.equals(tipoApresentacaoIPI);
		final boolean ipiImportado = TipoApresentacaoIPI.OCASIONAL.equals(tipoApresentacaoIPI)
				&& materialService.isMaterialImportado(itemPedido.getMaterial().getId());

		if (ipiPreenchido && aliquotaIPI > 0 && TipoApresentacaoIPI.NUNCA.equals(tipoApresentacaoIPI)) {
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
		 * podera fazer referencias ao item no campo de observacao, por exemplo: o
		 * item 1 deve ter acabamento, etc.
		 */
		if (itemPedido.isNovo()) {
			itemPedido.setSequencial(gerarSequencialItemPedido(idPedido));
		}

		if (itemPedido.isNovo()) {
			itemPedidoDAO.inserir(itemPedido);
		} else {
			itemPedido = itemPedidoDAO.alterar(itemPedido);
		}

		ValidadorInformacao.validar(itemPedido);
		/*
		 * Devemos sempre atualizar o valor do pedido mesmo em caso de excecao de
		 * validacoes, caso contrario teremos um valor nulo na base de dados.
		 */
		pedido.setValorPedido(this.calcularValorPedido(idPedido));
		pedido.setValorPedidoIPI(this.calcularValorPedidoIPI(idPedido));

		return itemPedido.getId();
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public Integer inserirItemPedido(ItemPedido itemPedido) throws BusinessException {
		Integer idPedido = pedidoDAO.pesquisarIdPedidoByIdItemPedido(itemPedido.getId());
		if (idPedido == null) {
			throw new BusinessException("Não existe pedido cadastrado para o item "
					+ (itemPedido.isPeca() ? itemPedido.getDescricaoPeca() : itemPedido.getDescricao()));
		}
		return inserirItemPedido(idPedido, itemPedido);
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
				this.entityManager.createQuery("select p.situacaoPedido from Pedido p where p.id = :idPedido").setParameter(
						"idPedido", idPedido), SituacaoPedido.class, null);

		return SituacaoPedido.ENVIADO.equals(situacao);

	}

	@Override
	public PaginacaoWrapper<Pedido> paginarPedido(Integer idCliente, boolean isCompra, Integer indiceRegistroInicial,
			Integer numeroMaximoRegistros) {
		return paginarPedido(idCliente, null, isCompra, indiceRegistroInicial, numeroMaximoRegistros);
	}

	@Override
	public PaginacaoWrapper<Pedido> paginarPedido(Integer idCliente, Integer idVendedor, boolean isCompra,
			Integer indiceRegistroInicial, Integer numeroMaximoRegistros) {
		List<Pedido> listaPedido = null;
		if (usuarioService.isVendaPermitida(idCliente, idVendedor)) {
			listaPedido = pesquisarByIdCliente(idCliente, isCompra, indiceRegistroInicial, numeroMaximoRegistros);
		} else {
			listaPedido = new ArrayList<Pedido>();
		}
		return new PaginacaoWrapper<Pedido>(this.pesquisarTotalPedidoByIdCliente(idCliente, idVendedor, isCompra),
				listaPedido);
	}

	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	@Override
	public double pesquisarAliquotaICMSRepresentadaByIdItemPedido(Integer idItemPedido) {
		return itemPedidoDAO.pesquisarAliquotaICMSRepresentadaByIdItemPedido(idItemPedido);
	}

	@Override
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
	public List<Pedido> pesquisarByIdCliente(Integer idCliente, boolean isCompra, Integer indiceRegistroInicial,
			Integer numeroMaximoRegistros) {
		return this.pesquisarPedidoByIdClienteByIdVendedor(idCliente, null, isCompra, indiceRegistroInicial,
				numeroMaximoRegistros);
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public List<Pedido> pesquisarByIdCliente(Integer idCliente, Integer indiceRegistroInicial,
			Integer numeroMaximoRegistros) {
		return pesquisarByIdCliente(idCliente, false, indiceRegistroInicial, numeroMaximoRegistros);
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public double pesquisarComissaoRepresentadaByIdPedido(Integer idPedido) {
		Double comissao = pedidoDAO.pesquisarComissaoRepresentadaByIdPedido(idPedido);
		return comissao == null ? 0 : comissao;
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public List<ItemPedido> pesquisarCompraAguardandoRecebimento(Integer idRepresentada, Periodo periodo) {
		if (periodo != null) {
			return itemPedidoDAO.pesquisarCompraAguardandoRecebimento(idRepresentada, periodo.getInicio(), periodo.getFim());
		}
		return itemPedidoDAO.pesquisarCompraAguardandoRecebimento(idRepresentada, null, null);
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public Pedido pesquisarCompraById(Integer id) {

		if (id == null) {
			return null;
		}
		return this.pedidoDAO.pesquisarById(id, true);
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public List<Pedido> pesquisarCompraByPeriodoEComprador(Periodo periodo, Integer idComprador) throws BusinessException {
		return pesquisarPedidoEnviadoByPeriodoEProprietario(false, periodo, idComprador, true);
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public Date pesquisarDataEnvio(Integer idPedido) {
		return pedidoDAO.pesquisarDataEnvioById(idPedido);
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public Date pesquisarDataInclusao(Integer idPedido) {
		return pedidoDAO.pesquisarDataInclusaoById(idPedido);
	}

	@Override
	@SuppressWarnings("unchecked")
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public List<Pedido> pesquisarEntregaVendaByPeriodo(Periodo periodo) {
		StringBuilder select = new StringBuilder();
		select
				.append("select new Pedido(p.id, p.dataEntrega, p.valorPedido, p.cliente.nomeFantasia, p.cliente.razaoSocial, p.representada.nomeFantasia) ");
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
				.append("from Pedido p where p.situacaoPedido in :situacoes and ").append(" p.dataEnvio >= :dataInicio and ")
				.append(" p.dataEnvio <= :dataFim and ").append("p.tipoPedido != :tipoPedido ");

		if (idRepresentada != null) {
			select.append("and p.representada.id = :idRepresentada ");
		}
		select.append("order by p.dataEnvio desc ");

		Query query = this.entityManager.createQuery(select.toString())
				.setParameter("situacoes", pesquisarSituacaoVendaEfetivada()).setParameter("dataInicio", periodo.getInicio())
				.setParameter("dataFim", periodo.getFim()).setParameter("tipoPedido", TipoPedido.COMPRA);

		if (idRepresentada != null) {
			query.setParameter("idRepresentada", idRepresentada);
		}
		return query.getResultList();
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public List<Pedido> pesquisarEnviadosByPeriodoEVendedor(Periodo periodo, Integer idVendedor) throws BusinessException {
		return this.pesquisarVendaByPeriodoEVendedor(true, periodo, idVendedor);
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public List<Integer> pesquisarIdItemPedidoByIdPedido(Integer idPedido) {
		if (idPedido == null) {
			return new ArrayList<Integer>(1);
		}
		return itemPedidoDAO.pesquisarIdItemPedidoByIdPedido(idPedido);
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
				this.entityManager.createQuery("select v.id from Pedido p inner join p.proprietario v where p.id = :idPedido ")
						.setParameter("idPedido", idPedido), Integer.class, null);
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
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public ItemPedido pesquisarItemPedido(Integer idItemPedido) {
		ItemPedido itemPedido = pedidoDAO.pesquisarItemPedido(idItemPedido);
		if (itemPedido != null) {
			Double[] valorPedido = pesquisarValorPedidoByItemPedido(idItemPedido);
			itemPedido.setValorPedido(valorPedido[0]);
			itemPedido.setValorPedidoIPI(valorPedido[1]);
		}
		return itemPedido;
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
	public List<ItemPedido> pesquisarItemPedidoByIdPedido(Integer idPedido) {
		return pedidoDAO.pesquisarItemPedidoByIdPedido(idPedido);
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public List<ItemPedido> pesquisarItemPedidoCompradoResumidoByPeriodo(Periodo periodo) {
		return pesquisarValoresItemPedidoResumidoByPeriodo(periodo, pesquisarSituacaoCompraEfetivada(), TipoPedido.COMPRA);
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
	public List<ItemPedido> pesquisarItemPedidoRepresentacaoByPeriodo(Periodo periodo) {
		return pesquisarValoresItemPedidoResumidoByPeriodo(periodo, pesquisarSituacaoVendaEfetivada(),
				TipoPedido.REPRESENTACAO);
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public List<ItemPedido> pesquisarItemPedidoRevendaByPeriodo(Periodo periodo) {
		return pesquisarValoresItemPedidoResumidoByPeriodo(periodo, pesquisarSituacaoVendaEfetivada(), TipoPedido.REVENDA);
	}

	@Override
	public List<ItemPedido> pesquisarItemPedidoVendaByPeriodo(Periodo periodo, Integer idVendedor) {
		if (idVendedor == null) {
			return new ArrayList<ItemPedido>();
		}

		return itemPedidoDAO.pesquisarItemPedidoVendaComissionadaByPeriodo(periodo, idVendedor,
				pesquisarSituacaoVendaEfetivada());
	}

	@Override
	public List<ItemPedido> pesquisarItemPedidoVendaResumidaByPeriodo(Periodo periodo) {
		return itemPedidoDAO
				.pesquisarItemPedidoVendaComissionadaByPeriodo(periodo, null, pesquisarSituacaoVendaEfetivada());
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public List<Logradouro> pesquisarLogradouro(Integer idPedido) {
		return pedidoDAO.pesquisarLogradouro(idPedido);
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
	public List<Pedido> pesquisarPedidoByIdCliente(Integer idCliente, Integer indiceRegistroInicial,
			Integer numeroMaximoRegistros) {

		if (idCliente == null) {
			return Collections.emptyList();
		}
		return pedidoDAO.pesquisarPedidoByIdCliente(idCliente, indiceRegistroInicial, numeroMaximoRegistros);
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public List<Pedido> pesquisarPedidoByIdClienteByIdVendedor(Integer idCliente, Integer idVendedor, boolean isCompra,
			Integer indiceRegistroInicial, Integer numeroMaximoRegistros) {

		if (idCliente == null) {
			return Collections.emptyList();
		}
		return this.pedidoDAO.pesquisarPedidoByIdClienteByIdVendedor(idCliente, idVendedor, isCompra,
				indiceRegistroInicial, numeroMaximoRegistros);
	}

	@Override
	@SuppressWarnings("unchecked")
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public List<Pedido> pesquisarPedidoCompraByPeriodo(Periodo periodo) {
		StringBuilder select = new StringBuilder();
		select
				.append("select new Pedido(p.id, p.tipoPedido, p.dataEntrega, p.valorPedido, p.cliente.nomeFantasia, p.cliente.razaoSocial, p.representada.nomeFantasia) ");
		select.append("from Pedido p ");
		select.append("where p.tipoPedido = :tipoPedido and ");
		select.append("p.dataEnvio >= :dataInicio and ");
		select.append("p.dataEnvio <= :dataFim and ");
		select.append("p.situacaoPedido in :situacoes ");
		select.append("order by p.dataEntrega, p.id, p.representada.nomeFantasia, p.cliente.nomeFantasia ");

		return this.entityManager.createQuery(select.toString()).setParameter("dataInicio", periodo.getInicio())
				.setParameter("dataFim", periodo.getFim()).setParameter("situacoes", pesquisarSituacaoCompraEfetivada())
				.setParameter("tipoPedido", TipoPedido.COMPRA).getResultList();
	}

	@SuppressWarnings("unchecked")
	private List<Pedido> pesquisarPedidoEnviadoByPeriodoEProprietario(boolean orcamento, Periodo periodo,
			Integer idProprietario, boolean isCompra) throws BusinessException {
		if (idProprietario == null) {
			throw new BusinessException("O ID do vendedor é obrigatório");
		}

		StringBuilder select = new StringBuilder();
		select
				.append(
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
				.setParameter("dataFim", periodo.getFim()).setParameter("tipoPedido", TipoPedido.COMPRA).getResultList();
	}

	@Override
	@SuppressWarnings("unchecked")
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public List<Pedido> pesquisarPedidoVendaByPeriodo(Periodo periodo) {
		StringBuilder select = new StringBuilder();
		select
				.append("select new Pedido(p.id, p.dataEntrega, p.valorPedido, p.cliente.nomeFantasia, p.cliente.razaoSocial, p.representada.nomeFantasia) ");
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
	public Representada pesquisarRepresentadaResumidaByIdPedido(Integer idPedido) {
		return pedidoDAO.pesquisarRepresentadaResumidaByIdPedido(idPedido);
	}

	@Override
	public List<SituacaoPedido> pesquisarSituacaoCompraEfetivada() {
		return pedidoDAO.pesquisarSituacaoCompraEfetivada();
	}

	@Override
	public SituacaoPedido pesquisarSituacaoPedidoById(Integer idPedido) {
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

	private TipoApresentacaoIPI pesquisarTipoApresentacaoIPI(ItemPedido itemPedido) throws BusinessException {
		if (itemPedido.getPedido() == null || itemPedido.getPedido().getId() == null) {
			throw new BusinessException(
					"Não é possível verificar a obrigatoriedade do IPI pois pedido ainda não existe no sistema");
		}

		if (itemPedido.getMaterial() == null) {
			throw new BusinessException("Não é possível verificar a obrigatoriedade do IPI pois o item não possui material");
		}

		final Integer idRepresentada = pedidoDAO.pesquisarIdRepresentadaByIdPedido(itemPedido.getPedido().getId());
		return representadaService.pesquisarTipoApresentacaoIPI(idRepresentada);
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
	public Long pesquisarTotalPedidoByIdCliente(Integer idCliente, Integer idVendedor, boolean isCompra) {
		if (idCliente == null) {
			return 0L;
		}

		StringBuilder select = new StringBuilder("select count(p.id) from Pedido p where p.cliente.id = :idCliente ");
		if (idVendedor != null) {
			select.append("and p.proprietario.id = :idVendedor ");
		}

		if (isCompra) {
			select.append("and p.tipoPedido = :tipoPedido ");
		} else {
			select.append("and p.tipoPedido != :tipoPedido ");
		}

		Query query = this.entityManager.createQuery(select.toString());
		query.setParameter("idCliente", idCliente).setParameter("tipoPedido", TipoPedido.COMPRA);
		if (idVendedor != null) {
			query.setParameter("idVendedor", idVendedor);
		}

		return QueryUtil.gerarRegistroUnico(query, Long.class, null);
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public Long pesquisarTotalPedidoVendaByIdCliente(Integer idCliente) {
		return this.pesquisarTotalPedidoByIdCliente(idCliente, null, false);
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public List<TotalizacaoPedidoWrapper> pesquisarTotalPedidoVendaResumidaByPeriodo(Periodo periodo) {
		return pedidoDAO.pesquisarValorTotalPedidoByPeriodo(periodo.getInicio(), periodo.getFim(), false);
	}

	private List<ItemPedido> pesquisarValoresItemPedidoResumidoByPeriodo(Periodo periodo,
			List<SituacaoPedido> listaSituacao, TipoPedido tipoPedido) {
		StringBuilder select = new StringBuilder();
		select
				.append("select new ItemPedido(i.precoUnidade, i.quantidade, i.aliquotaIPI, i.aliquotaICMS, i.valorComissionado, i.pedido.aliquotaComissao) from ItemPedido i ");
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

		if (id == null) {
			return null;
		}
		return this.pedidoDAO.pesquisarById(id, false);
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
		ItemPedido itemPedido = pesquisarItemPedido(idItemPedido);
		itemPedido.setQuantidadeReservada(0);
		itemPedido.setEncomendado(false);
		inserirItemPedido(itemPedido);
	}

	@Override
	public Integer refazerPedido(Integer idPedido) throws BusinessException {
		Pedido pedido = pesquisarPedidoById(idPedido);
		Pedido pedidoClone = null;
		try {
			pedidoClone = pedido.clone();
		} catch (CloneNotSupportedException e) {
			throw new BusinessException("Falha no processo de copia do pedido No. " + idPedido, e);
		}

		pedidoClone.setId(null);
		pedidoClone.setDataEntrega(new Date());
		pedidoClone.setListaLogradouro(null);
		pedidoClone = this.inserir(pedidoClone);

		List<ItemPedido> listaItemPedido = pesquisarItemPedidoByIdPedido(idPedido);
		ItemPedido itemPedidoClone = null;
		for (ItemPedido itemPedido : listaItemPedido) {
			try {
				itemPedidoClone = itemPedido.clone();
				inserirItemPedido(pedidoClone.getId(), itemPedidoClone);
			} catch (IllegalStateException e) {
				throw new BusinessException("Falha no processo de copia do item No. " + itemPedido.getId() + " do pedido No. "
						+ idPedido, e);
			}
		}

		// Ao final da clonaem do pedido precisamos cancelar o antigo para que
		// esse nao aparece nos faturamentos da empresa.
		cancelarPedido(pedido.getId());
		return pedidoClone.getId();
	}

	@Override
	public Pedido removerItemPedido(Integer idItemPedido) throws BusinessException {
		ItemPedido itemPedido = null;
		try {
			itemPedido = pesquisarItemPedido(idItemPedido);

			if (itemPedido == null) {
				return null;
			}
			Pedido pedido = itemPedido.getPedido();

			alterarSequencialItemPedido(pedido.getId(), itemPedido.getSequencial());

			itemPedidoDAO.remover(itemPedido);

			// Efetuando novamente o calculo pois na remocao o valor do pedido
			// deve ser atualizado
			pedido.setValorPedido(this.calcularValorPedido(pedido.getId()));
			pedido.setValorPedidoIPI(this.calcularValorPedidoIPI(pedido.getId()));

			if (pedido.isCompraEfetuada() && pesquisarTotalItemPedido(pedido.getId()) <= 0L) {
				pedido.setSituacaoPedido(SituacaoPedido.CANCELADO);
			}
			return pedido;
		} catch (NonUniqueResultException e) {
			throw new BusinessException("Não foi possivel remover o item pois foi encontrato mais de um item para o codigo "
					+ idItemPedido);
		} catch (NoResultException e) {
			throw new BusinessException("Não foi possivel remover o item pois não existe item com o codigo " + idItemPedido);
		}

	}

	private void validarEnvio(Pedido pedido) throws BusinessException {

		if (!pedido.isOrcamento()) {
			clienteService.validarListaLogradouroPreenchida(pedido.getCliente());
		}

		final BusinessException exception = new BusinessException();
		if (SituacaoPedido.CANCELADO.equals(pedido.getSituacaoPedido())
				|| SituacaoPedido.ENVIADO.equals(pedido.getSituacaoPedido())) {
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

	private void verificarMaterialAssociadoFornecedor(Integer idRepresentadaFornecedora, Set<Integer> listaIdItemPedido)
			throws BusinessException {
		Integer idMaterial = null;
		for (Integer idItemPedido : listaIdItemPedido) {

			idMaterial = itemPedidoDAO.pesquisarIdMeterialByIdItemPedido(idItemPedido);
			if (!materialService.isMaterialAssociadoRepresentada(idMaterial, idRepresentadaFornecedora)) {
				Integer idPedido = pesquisarIdPedidoByIdItemPedido(idItemPedido);
				Integer sequencial = itemPedidoDAO.pesquisarSequencialItemPedido(idItemPedido);
				String nomeFantasia = representadaService.pesquisarNomeFantasiaById(idRepresentadaFornecedora);
				throw new BusinessException("Não é possível encomendar o item No. " + sequencial + " do pedido No. " + idPedido
						+ " pois o fornecedor \"" + nomeFantasia + "\" não trabalha com o material do item");
			}
		}
	}
}
