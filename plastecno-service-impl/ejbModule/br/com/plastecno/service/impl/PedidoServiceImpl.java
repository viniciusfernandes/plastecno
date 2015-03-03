package br.com.plastecno.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

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
import br.com.plastecno.util.DateUtils;
import br.com.plastecno.util.StringUtils;
import br.com.plastecno.validacao.ValidadorInformacao;

@Stateless
public class PedidoServiceImpl implements PedidoService {

	@EJB
	private ClienteService clienteService;

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
			estoqueService.devolverItemCompradoEstoqueByIdPedido(idPedido);
		}
		if (TipoPedido.REVENDA.equals(pedidoDAO.pesquisarTipoPedidoById(idPedido))) {
			estoqueService.cancelarReservaEstoqueByIdPedido(idPedido);
		}
		pedidoDAO.cancelar(idPedido);
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public boolean contemItemPedido(Integer idPedido) {
		return this.pesquisarTotalItemPedido(idPedido) > 0;
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

	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public Integer encomendarItemPedido(Integer idComprador, Integer idRepresentadaFornecedora,
			List<Integer> listaIdItemPedido) throws BusinessException {
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
		Usuario comprador = usuarioService.pesquisarById(idComprador);

		Contato contato = new Contato();
		contato.setNome(comprador.getNome());
		contato.setEmail(comprador.getEmail());

		Pedido pedido = new Pedido();
		pedido.setCliente(clienteService.pesquisarRevendedor());
		pedido.setComprador(comprador);
		pedido.setContato(contato);
		pedido.setDataInclusao(new Date());
		pedido.setFinalidadePedido(FinalidadePedido.REVENDA);
		pedido.setProprietario(comprador);
		pedido.setRepresentada(fornecedor);
		pedido.setSituacaoPedido(SituacaoPedido.ENCOMENDA);
		pedido.setTipoPedido(TipoPedido.COMPRA);

		pedido = inserir(pedido);
		ItemPedido itemCadastrado = null;
		ItemPedido itemClone = null;
		for (Integer idItemPedido : listaIdItemPedido) {
			itemCadastrado = pesquisarItemPedido(idItemPedido);
			if (itemCadastrado == null) {
				continue;
			}
			itemClone = itemCadastrado.clone();
			itemClone.setPedido(pedido);
			try {
				inserirItemPedido(pedido.getId(), itemClone);
			} catch (BusinessException e) {
				throw new BusinessException("Não foi possível cadastrar uma nova encomenda pois houve falha no item No. "
						+ itemCadastrado.getSequencial() + " do pedido No. " + itemCadastrado.getPedido().getId()
						+ ". Possível problema: " + e.getMensagemEmpilhada());
			}
			itemCadastrado.setEncomendado(true);
			inserirItemPedido(itemCadastrado);
		}
		return pedido.getId();
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
			enviarVenda(pedido, arquivoAnexado);
		}
		if (pedido.isCompra()) {
			pedido.setSituacaoPedido(SituacaoPedido.COMPRA_PENDENTE_RECEBIMENTO);
		}

		pedidoDAO.alterar(pedido);
	}

	private void enviarVenda(Pedido pedido, byte[] arquivoAnexado) throws BusinessException {
		this.validarEnvioVenda(pedido);

		if (pedido.isRevenda()) {
			estoqueService.reservarItemPedido(pedido.getId());
		}

		logradouroService.verificarListaLogradouroObrigatorio(pedido.getListaLogradouro());
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
		if (isPedidoNovo
				&& !this.usuarioService.isVendaPermitida(pedido.getCliente().getId(), pedido.getVendedor().getId())) {

			Cliente cliente = this.clienteService.pesquisarById(pedido.getCliente().getId());
			Usuario proprietario = this.usuarioService.pesquisarById(pedido.getProprietario().getId());
			throw new BusinessException("Não é possível incluir o pedido pois o cliente "
					+ (cliente != null ? cliente.getNomeCompleto() : pedido.getCliente().getId())
					+ " não esta associado ao vendedor "
					+ (proprietario != null ? proprietario.getNome() + " - " + proprietario.getEmail() : pedido.getCliente()
							.getId()));
		}

		final Date dataEntrega = DateUtils.gerarDataSemHorario(pedido.getDataEntrega());
		if (dataEntrega != null && DateUtils.isAnteriorDataAtual(dataEntrega)) {
			throw new InformacaoInvalidaException("Data de entrega deve ser posterior a data atual");
		}

		if (TipoEntrega.CIF_TRANS.equals(pedido.getTipoEntrega()) && pedido.getTransportadoraRedespacho() == null) {
			throw new BusinessException("A transportadora de redespacho é obrigatória para o tipo de entrega "
					+ TipoEntrega.CIF_TRANS.getDescricao());
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

		}
		/*
		 * Devemos sempre pesquisar pois o cliente pode ter alterado os dados de
		 * logradouro
		 */
		pedido.addLogradouro(this.clienteService.pesquisarLogradouro(pedido.getCliente().getId()));

		if (isPedidoNovo) {
			pedido.setDataInclusao(new Date());
			if (!pedido.isEncomenda()) {
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

		if (itemPedido.isMedidaExternaIgualInterna()) {
			itemPedido.setMedidaInterna(itemPedido.getMedidaExterna());
		}

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
		final boolean ipiPositivo = ipiPreenchido && aliquotaIPI > 0d;
		final TipoApresentacaoIPI tipoApresentacaoIPI = pesquisarTipoApresentacaoIPI(itemPedido);
		final boolean materialImportado = materialService.isMaterialImportado(itemPedido.getMaterial().getId());
		final boolean ipiNecessario = TipoApresentacaoIPI.SEMPRE.equals(tipoApresentacaoIPI)
				|| (!ipiPreenchido && TipoApresentacaoIPI.OCASIONAL.equals(tipoApresentacaoIPI) && materialImportado);

		if (ipiPositivo && TipoApresentacaoIPI.NUNCA.equals(tipoApresentacaoIPI)) {
			throw new BusinessException(
					"Remova o valor do IPI do item pois representada escolhida não apresenta cáculo de IPI.");
		} else if (!ipiPositivo && ipiNecessario) {
			itemPedido.setAliquotaIPI(itemPedido.getFormaMaterial().getIpi());
		}

		// No caso em que nao exista a cobranca de IPI os precos serao iguais
		final Double precoUnidadeIPI = ipiNecessario ? CalculadoraPreco.calcularPorUnidadeIPI(itemPedido) : itemPedido
				.getPrecoUnidade();

		itemPedido.setPrecoUnidadeIPI(precoUnidadeIPI);

		/*
		 * O valor sequencial sera utilizado para que a representada identifique
		 * rapidamento qual eh o item que deve ser customizado, assim o vendedor
		 * podera fazer referencias ao item no campo de observacao, por exemplo: o
		 * item 1 deve ter acabamento, etc.
		 */
		if (itemPedido.isNovo()) {
			itemPedido.setSequencial(gerarSequencialItemPedido(idPedido));
		}

		ValidadorInformacao.validar(itemPedido);
		if (itemPedido.isNovo()) {
			itemPedidoDAO.inserir(itemPedido);
		} else {
			itemPedido = itemPedidoDAO.alterar(itemPedido);
		}
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
		return inserirItemPedido(idPedido, itemPedido);
	}

	@Override
	public boolean isCalculoIPIHabilitado(Integer idPedido) {
		Integer idRepresentada = pesquisarIdRepresentadaByIdPedido(idPedido);
		return representadaService.isCalculoIPIHabilitado(idRepresentada);
	}

	@Override
	public boolean isClienteProspectado(Integer idPedido) {
		return QueryUtil.gerarRegistroUnico(
				this.entityManager.createQuery(
						"select c.prospeccaoFinalizada from Pedido p inner join p.cliente c where p.id = :idPedido").setParameter(
						"idPedido", idPedido), Boolean.class, false);
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
		return new PaginacaoWrapper<Pedido>(this.pesquisarTotalRegistros(idCliente, idVendedor), listaPedido);
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
	public List<ItemPedido> pesquisarCompraPendenteRecebimento(Integer idRepresentada, Periodo periodo) {
		return itemPedidoDAO.pesquisarCompraPendenteRecebimento(idRepresentada, periodo.getInicio(), periodo.getFim());
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
	@REVIEW(data = "19/02/2015", descricao = "Redefinir o conteudo retornado pela query para gerar apenas a informacao necessaria")
	public List<Pedido> pesquisarEnviadosByPeriodo(Periodo periodo) {
		StringBuilder select = new StringBuilder();
		select.append("select p from Pedido p join fetch p.representada ");
		select.append("where p.tipoPedido != :tipoPedido and ");
		select.append(" p.dataEntrega >= :dataInicio and ");
		select.append("p.dataEntrega <= :dataFim and ");
		select.append("p.situacaoPedido = :situacaoPedido ");
		select.append("order by p.dataEntrega, p.representada.nomeFantasia, p.cliente.nomeFantasia ");

		return this.entityManager.createQuery(select.toString()).setParameter("dataInicio", periodo.getInicio())
				.setParameter("dataFim", periodo.getFim()).setParameter("situacaoPedido", SituacaoPedido.ENVIADO)
				.setParameter("tipoPedido", TipoPedido.COMPRA).getResultList();
	}

	@Override
	@SuppressWarnings("unchecked")
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	@REVIEW(data = "19/02/2015", descricao = "Redefinir o conteudo retornado pela query para gerar apenas a informacao necessaria")
	public List<Pedido> pesquisarEnviadosByPeriodoERepresentada(Periodo periodo, Integer idRepresentada) {
		StringBuilder select = new StringBuilder()
				.append("select p from Pedido p where p.situacaoPedido = :situacaoPedido and ")
				.append(" p.dataEnvio >= :dataInicio and ").append(" p.dataEnvio <= :dataFim and ")
				.append("p.tipoPedido != :tipoPedido ");

		if (idRepresentada != null) {
			select.append("and p.representada.id = :idRepresentada ");
		}
		select.append("order by p.dataEnvio desc ");

		Query query = this.entityManager.createQuery(select.toString())
				.setParameter("situacaoPedido", SituacaoPedido.ENVIADO).setParameter("dataInicio", periodo.getInicio())
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
	public Integer pesquisarIdPedidoByIdItemPedido(Integer idItemPedido) {
		return pedidoDAO.pesquisarIdPedidoByIdItemPedido(idItemPedido);
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
				this.entityManager.createQuery("select v.id from Pedido p inner join p.proprietario v where p.id = idPedido ")
						.setParameter("idPedido", idPedido), Integer.class, null);
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public List<ItemPedido> pesquisarItemEncomenda(Integer idCliente, Periodo periodo) {
		return itemPedidoDAO.pesquisarItemEncomenda(idCliente, periodo.getInicio(), periodo.getFim());
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public ItemPedido pesquisarItemPedido(Integer idItemPedido) {
		return pedidoDAO.pesquisarItemPedido(idItemPedido);
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public List<ItemPedido> pesquisarItemPedidoByIdPedido(Integer idPedido) {
		return pedidoDAO.pesquisarItemPedidoByIdPedido(idPedido);
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

	@SuppressWarnings("unchecked")
	private List<Pedido> pesquisarPedidoEnviadoByPeriodoEProprietario(boolean orcamento, Periodo periodo,
			Integer idProprietario, boolean isCompra) throws BusinessException {
		if (idProprietario == null) {
			throw new BusinessException("O ID do vendedor é obrigatório");
		}

		StringBuilder select = new StringBuilder()
				.append("select p from Pedido p join fetch p.representada where p.situacaoPedido = :situacaoPedido and ")
				.append("p.proprietario.id = :idProprietario and ").append(" p.dataEnvio >= :dataInicio and ")
				.append(" p.dataEnvio <= :dataFim ");

		if (isCompra) {
			select.append(" and p.tipoPedido = :tipoPedido ");
		} else {
			select.append(" and p.tipoPedido != :tipoPedido ");
		}
		select.append("order by p.dataEnvio desc ");
		SituacaoPedido situacaoPedido = null;

		if (isCompra) {
			situacaoPedido = SituacaoPedido.COMPRA_PENDENTE_RECEBIMENTO;
		} else if (!isCompra && orcamento) {
			situacaoPedido = SituacaoPedido.ORCAMENTO;
		} else if (!isCompra && !orcamento) {
			situacaoPedido = SituacaoPedido.ENVIADO;
		}
		return this.entityManager.createQuery(select.toString()).setParameter("situacaoPedido", situacaoPedido)
				.setParameter("idProprietario", idProprietario).setParameter("dataInicio", periodo.getInicio())
				.setParameter("dataFim", periodo.getFim()).setParameter("tipoPedido", TipoPedido.COMPRA).getResultList();
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
	public List<ItemPedido> pesquisarRevendaEmpacotamento() {
		return pesquisarRevendaEmpacotamento(null, null);
	}

	@Override
	public List<ItemPedido> pesquisarRevendaEmpacotamento(Integer idCliente, Periodo periodo) {
		return itemPedidoDAO.pesquisarItemPedidoEmpacotamento(idCliente, periodo.getInicio(), periodo.getFim());
	}

	@Override
	public SituacaoPedido pesquisarSituacaoPedidoById(Integer idPedido) {
		return pedidoDAO.pesquisarSituacaoPedidoById(idPedido);
	}

	@Override
	public SituacaoPedido pesquisarSituacaoPedidoByIdItemPedido(Integer idItemPedido) {
		return pedidoDAO.pesquisarSituacaoPedidoByIdItemPedido(idItemPedido);
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
	public List<Object[]> pesquisarTotalCompraResumidaByPeriodo(Periodo periodo) {
		return pedidoDAO.pesquisarTotalPedidoByPeriodo(periodo.getInicio(), periodo.getFim(), true);
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public Long pesquisarTotalItemPedido(Integer idPedido) {
		return pedidoDAO.pesquisarTotalItemPedido(idPedido);
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public long pesquisarTotalItemPendente(Integer idPedido) {
		return pedidoDAO.pesquisarTotalItemPedido(idPedido, false);
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public Long pesquisarTotalRegistros(Integer idCliente) {
		return this.pesquisarTotalRegistros(idCliente, null);
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public Long pesquisarTotalRegistros(Integer idCliente, Integer idVendedor) {
		if (idCliente == null) {
			return 0L;
		}

		StringBuilder select = new StringBuilder("select count(p.id) from Pedido p where p.cliente.id = :idCliente ");
		if (idVendedor != null) {
			select.append(" and p.proprietario.id = :idVendedor ");
		}

		Query query = this.entityManager.createQuery(select.toString());
		query.setParameter("idCliente", idCliente);
		if (idVendedor != null) {
			query.setParameter("idVendedor", idVendedor);
		}

		return QueryUtil.gerarRegistroUnico(query, Long.class, null);
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public List<Object[]> pesquisarTotalVendaResumidaByPeriodo(Periodo periodo) {
		return pedidoDAO.pesquisarTotalPedidoByPeriodo(periodo.getInicio(), periodo.getFim(), false);
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public Double pesquisarValorPedido(Integer idPedido) {
		final Double valor = pedidoDAO.pesquisarValorPedido(idPedido);
		return valor == null ? 0D : valor;
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public Double pesquisarValorPedidoIPI(Integer idPedido) {
		final Double valor = pedidoDAO.pesquisarValorPedidoIPI(idPedido);
		return valor == null ? 0D : valor;
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
		if (!pedido.getCliente().isProspectado()) {
			throw new BusinessException("Não é possível enviar pedido para clientes em não propspectado");
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
}
