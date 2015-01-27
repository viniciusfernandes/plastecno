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
import javax.persistence.Query;

import br.com.plastecno.service.ClienteService;
import br.com.plastecno.service.EmailService;
import br.com.plastecno.service.LogradouroService;
import br.com.plastecno.service.MaterialService;
import br.com.plastecno.service.PedidoService;
import br.com.plastecno.service.RepresentadaService;
import br.com.plastecno.service.TransportadoraService;
import br.com.plastecno.service.UsuarioService;
import br.com.plastecno.service.constante.SituacaoPedido;
import br.com.plastecno.service.constante.TipoEntrega;
import br.com.plastecno.service.constante.TipoPedido;
import br.com.plastecno.service.dao.ItemPedidoDAO;
import br.com.plastecno.service.dao.PedidoDAO;
import br.com.plastecno.service.entity.Cliente;
import br.com.plastecno.service.entity.ItemPedido;
import br.com.plastecno.service.entity.Logradouro;
import br.com.plastecno.service.entity.Pedido;
import br.com.plastecno.service.entity.Usuario;
import br.com.plastecno.service.exception.BusinessException;
import br.com.plastecno.service.exception.NotificacaoException;
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

	private ItemPedidoDAO itemPedidoDAO;

	@EJB
	private LogradouroService logradouroService;

	@EJB
	private RepresentadaService representadaService;

	@EJB
	private MaterialService materialService;

	private PedidoDAO pedidoDAO;

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
	public Double calcularValorPedido(Integer idPedido) throws BusinessException {
		return pedidoDAO.pesquisarQuantidadePrecoUnidade(idPedido);
	}

	@Override
	public Double calcularValorPedidoIPI(Integer idPedido) throws BusinessException {
		return pedidoDAO.pesquisarQuantidadePrecoUnidadeIPI(idPedido);
	}

	@Override
	public void cancelar(Integer idPedido) throws BusinessException {
		if (idPedido == null) {
			throw new BusinessException("Não é possível cancelar o pedido pois ele não existe no sistema");
		}
		pedidoDAO.cancelar(idPedido);
	}

	@Override
	public boolean contemItemPedido(Integer idPedido) {
		return this.pesquisarTotalItemPedido(idPedido) > 0;
	}

	private void definirTipoPedido(Pedido pedido) {
		// Aqui os pedidos de venda/revenda podem nao ter sido configurados,
		// portanto, faremos uma consulta pelo nome da representada para decidir, ja
		// que os pedidos de compra sempre serao configurados antes de inserir.
		if (pedido.getTipoPedido() == null) {
			if (representadaService.isRevendedor(pedido.getRepresentada().getNomeFantasia())) {
				pedido.setTipoPedido(TipoPedido.REVENDA);
			} else {
				pedido.setTipoPedido(TipoPedido.REPRESENTACAO);
			}
		}
	}

	@Override
	public void enviar(Integer idPedido, byte[] arquivoAnexado) throws BusinessException {

		final Pedido pedido = pesquisarById(idPedido);

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

	private void enviarVenda(Pedido pedido, byte[] arquivoAnexado) throws BusinessException {
		this.validarEnvioVenda(pedido);
		logradouroService.verificarListaLogradouroObrigatorio(pedido.getListaLogradouro());
		pedido.setSituacaoPedido(SituacaoPedido.ENVIADO);
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
			Usuario vendedor = this.usuarioService.pesquisarById(pedido.getVendedor().getId());
			throw new BusinessException("Não é possível incluir o pedido pois o cliente "
					+ (cliente != null ? cliente.getNomeCompleto() : pedido.getCliente().getId())
					+ " não esta associado ao vendedor "
					+ (vendedor != null ? vendedor.getNome() + " - " + vendedor.getEmail() : pedido.getCliente().getId()));
		}

		final Date dataEntrega = DateUtils.gerarDataSemHorario(pedido.getDataEntrega());
		if (dataEntrega != null && DateUtils.isAnteriorDataAtual(dataEntrega)) {
			throw new InformacaoInvalidaException("Data de entrega deve ser posterior a data atual");
		}

		if (TipoEntrega.CIF_TRANS.equals(pedido.getTipoEntrega()) && pedido.getTransportadoraRedespacho() == null) {
			throw new BusinessException("A transportadora de redespacho é obrigatória para o tipo de entrega "
					+ TipoEntrega.CIF_TRANS.getDescricao());
		}

		// Efetuando o vinculo entre o vendedor e o pedido pois o vendedor eh
		// obrigatorio pois agora eh possivel que um outro vendedor com o perfil de
		// administrador faca cadastro de pedidos em nome de outro. Por isso estamos
		// ajustando o vendedor correto.
		Usuario vendedor = usuarioService.pesquisarVendedorByIdCliente(pedido.getCliente().getId());
		if (vendedor == null) {
			throw new BusinessException("Nao existe vendedor associado ao cliente " + pedido.getCliente().getNomeCompleto());
		}
		pedido.setVendedor(vendedor);

		/*
		 * Devemos sempre pesquisar pois o cliente pode ter alterado os dados de
		 * logradouro
		 */
		pedido.addLogradouro(this.clienteService.pesquisarLogradouro(pedido.getCliente().getId()));

		if (isPedidoNovo) {
			pedido.setDataInclusao(new Date());
			pedido.setSituacaoPedido(SituacaoPedido.DIGITACAO);
			pedidoDAO.inserir(pedido);

		} else {
			// recuperando as informacoes do sistema que nao devem ser alteradas
			// na edicao do pedido.
			pedido.setDataInclusao(this.pesquisarDataInclusao(idPedido));
			pedido.setDataEnvio(this.pesquisarDataEnvio(idPedido));
			pedido.setValorPedido(this.pesquisarValorPedido(idPedido));
			pedido.setValorPedidoIPI(this.pesquisarValorPedidoIPI(idPedido));
			pedidoDAO.alterar(pedido);
		}

		return pedido;
	}

	@Override
	public Integer inserirItemPedido(Integer idPedido, ItemPedido itemPedido) throws BusinessException {
		return inserirItemPedido(idPedido, itemPedido, null);
	}

	@Override
	public Integer inserirItemPedido(Integer idPedido, ItemPedido itemPedido, Double aliquotaIPI)
			throws BusinessException {
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

		final Pedido pedido = this.pesquisarById(idPedido);
		itemPedido.setPedido(pedido);
		/*
		 * Atualizando o valor de cada unidade do item que podera ser usado
		 * posteriormente em relatorios, alem disso, eh pbrigatorio para inclusao do
		 * item no sistema
		 */
		itemPedido.setPrecoUnidade(CalculadoraPreco.calcularPorUnidade(itemPedido));

		// No caso em que nao exista a cobranca de IPI os precos serao iguais
		final Double precoUnidadeIPI = isCalculoIPIObrigatorio(itemPedido) ? CalculadoraPreco.calcularPorUnidadeIPI(
				itemPedido, aliquotaIPI) : itemPedido.getPrecoUnidade();

		itemPedido.setPrecoUnidadeIPI(precoUnidadeIPI);

		/*
		 * Caso o ipi seja nulo, isso indica que o usuario nao digitou o valor entao
		 * utilizaremos os valores definidos para as formas dos materiais, que eh o
		 * default do sistema. Esse preenchimento foi realizado pois agora temos que
		 * incluir essa informacao do pedido.html que sera enviado para o cliente.
		 */

		if (aliquotaIPI == null) {
			aliquotaIPI = itemPedido.getFormaMaterial().getIpi();
		}
		itemPedido.setAliquotaIPI(aliquotaIPI);

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
		itemPedido = itemPedidoDAO.alterar(itemPedido);

		/*
		 * Devemos sempre atualizar o valor do pedido mesmo em caso de excecao de
		 * validacoes, caso contrario teremos um valor nulo na base de dados.
		 */
		pedido.setValorPedido(this.calcularValorPedido(idPedido));
		pedido.setValorPedidoIPI(this.calcularValorPedidoIPI(idPedido));

		return itemPedido.getId();
	}

	private boolean isCalculoIPIObrigatorio(ItemPedido itemPedido) throws BusinessException {
		if (itemPedido.getPedido() == null || itemPedido.getPedido().getId() == null) {
			throw new BusinessException(
					"Não é possível verificar a obrigatoriedade do IPI pois pedido ainda não existe no sistema");
		}

		if (itemPedido.getMaterial() == null) {
			throw new BusinessException("Não é possível verificar a obrigatoriedade do IPI pois o item não possui material");
		}

		final Integer idRepresentada = pedidoDAO.pesquisarIdRepresentadaByIdPedido(itemPedido.getPedido().getId());
		return this.materialService.isCalculoIPIObrigatorio(itemPedido.getMaterial().getId(), idRepresentada);
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
	public PaginacaoWrapper<Pedido> paginarPedido(Integer idCliente, boolean isCompra,
			Integer indiceRegistroInicial, Integer numeroMaximoRegistros) {
		return paginarPedido(idCliente, null, isCompra, indiceRegistroInicial, numeroMaximoRegistros);
	}

	@Override
	public PaginacaoWrapper<Pedido> paginarPedido(Integer idCliente, Integer idVendedor,boolean isCompra,
			Integer indiceRegistroInicial, Integer numeroMaximoRegistros) {
		List<Pedido> listaPedido = null;
		if (usuarioService.isVendaPermitida(idCliente, idVendedor)) {
			listaPedido = pesquisarByIdCliente(idCliente, indiceRegistroInicial, numeroMaximoRegistros);
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
	public Pedido pesquisarById(Integer id) {

		if (id == null) {
			return null;
		}
		return this.pedidoDAO.pesquisarById(id);
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
		return this.pesquisarPedidoByIdClienteByIdVendedor(idCliente, null, indiceRegistroInicial, numeroMaximoRegistros);
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public List<Pedido> pesquisarPedidoByIdClienteByIdVendedor(Integer idCliente, Integer idVendedor,
			boolean isCompra, Integer indiceRegistroInicial, Integer numeroMaximoRegistros) {

		if (idCliente == null) {
			return Collections.emptyList();
		}
		return this.pedidoDAO.pesquisarPedidoByIdClienteByIdVendedor(idCliente, idVendedor, isCompra,indiceRegistroInicial,
				numeroMaximoRegistros);
	}

	@Override
	@SuppressWarnings("unchecked")
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public List<Pedido> pesquisarByPeriodoEVendedor(boolean orcamento, Periodo periodo, Integer idVendedor)
			throws BusinessException {
		if (idVendedor == null) {
			throw new BusinessException("O ID do vendedor é obrigatório");
		}

		StringBuilder select = new StringBuilder()
				.append("select p from Pedido p join fetch p.representada where p.situacaoPedido = :situacaoPedido and ")
				.append("p.vendedor.id = :idVendedor and ").append(" p.dataEnvio >= :dataInicio and ")
				.append(" p.dataEnvio <= :dataFim ").append("order by p.dataEnvio desc ");

		return this.entityManager.createQuery(select.toString())
				.setParameter("situacaoPedido", orcamento ? SituacaoPedido.ORCAMENTO : SituacaoPedido.ENVIADO)
				.setParameter("idVendedor", idVendedor).setParameter("dataInicio", periodo.getInicio())
				.setParameter("dataFim", periodo.getFim()).getResultList();
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public List<Pedido> pesquisarCompraPendente(Integer idRepresentada, Periodo periodo) {
		return pedidoDAO.pesquisarCompraPendenteByPeriodo(idRepresentada, periodo);
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
	public List<Pedido> pesquisarEnviadosByPeriodo(Periodo periodo) {
		StringBuilder select = new StringBuilder();
		select.append("select p from Pedido p join fetch p.representada ");
		select.append("where p.dataEntrega >= :dataInicio and ");
		select.append("p.dataEntrega <= :dataFim and ");
		select.append("p.situacaoPedido = :situacaoPedido ");
		select.append("order by p.dataEntrega, p.representada.nomeFantasia, p.cliente.nomeFantasia ");
		return this.entityManager.createQuery(select.toString()).setParameter("dataInicio", periodo.getInicio())
				.setParameter("dataFim", periodo.getFim()).setParameter("situacaoPedido", SituacaoPedido.ENVIADO)
				.getResultList();
	}

	@Override
	@SuppressWarnings("unchecked")
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public List<Pedido> pesquisarEnviadosByPeriodoERepresentada(Periodo periodo, Integer idRepresentada) {
		StringBuilder select = new StringBuilder()
				.append("select p from Pedido p where p.situacaoPedido = :situacaoPedido and ")
				.append("p.representada.id = :idRepresentada and ").append(" p.dataEnvio >= :dataInicio and ")
				.append(" p.dataEnvio <= :dataFim ").append("order by p.dataEnvio desc ");

		return this.entityManager.createQuery(select.toString()).setParameter("situacaoPedido", SituacaoPedido.ENVIADO)
				.setParameter("idRepresentada", idRepresentada).setParameter("dataInicio", periodo.getInicio())
				.setParameter("dataFim", periodo.getFim()).getResultList();
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public List<Pedido> pesquisarEnviadosByPeriodoEVendedor(Periodo periodo, Integer idVendedor) throws BusinessException {
		return this.pesquisarByPeriodoEVendedor(true, periodo, idVendedor);
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public Integer pesquisarIdVendedorByIdPedido(Integer idPedido) {
		if (idPedido == null) {
			return null;
		}
		return QueryUtil.gerarRegistroUnico(
				this.entityManager.createQuery("select v.id from Pedido p inner join p.vendedor v where p.id = idPedido ")
						.setParameter("idPedido", idPedido), Integer.class, null);
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public ItemPedido pesquisarItemPedido(Integer idItemPedido) {
		Query query = this.entityManager.createQuery("select i from ItemPedido i where i.id = :idItemPedido");
		query.setParameter("idItemPedido", idItemPedido);
		return QueryUtil.gerarRegistroUnico(query, ItemPedido.class, null);
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
	public List<Pedido> pesquisarPedidoByIdCliente(Integer idCliente, Integer indiceRegistroInicial,
			Integer numeroMaximoRegistros) {

		if (idCliente == null) {
			return Collections.emptyList();
		}
		return pedidoDAO.pesquisarPedidoByIdCliente(idCliente, indiceRegistroInicial, numeroMaximoRegistros);
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public List<Pedido> pesquisarPedidoByIdClienteByIdVendedor(Integer idCliente, Integer idVendedor,
			Integer indiceRegistroInicial, Integer numeroMaximoRegistros) {

		if (idCliente == null) {
			return Collections.emptyList();
		}
		return pedidoDAO.pesquisarPedidoByIdClienteByIdVendedor(idCliente, idVendedor, indiceRegistroInicial,
				numeroMaximoRegistros);
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public Long pesquisarTotalItemPedido(Integer idPedido) {
		return pedidoDAO.pesquisarTotalItemPedido(idPedido);
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
			select.append(" and p.vendedor.id = :idVendedor ");
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
	public Usuario pesquisarVendedor(Integer idPedido) {
		StringBuilder select = new StringBuilder();
		select.append("select p.vendedor from Pedido p where p.id = :id");
		Query query = this.entityManager.createQuery(select.toString());
		query.setParameter("id", idPedido);

		return QueryUtil.gerarRegistroUnico(query, Usuario.class, null);
	}

	@Override
	public Integer refazerPedido(Integer idPedido) throws BusinessException {
		Pedido pedido = this.pesquisarById(idPedido);
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
			} catch (CloneNotSupportedException e) {
				throw new BusinessException("Falha no processo de copia do item No. " + itemPedido.getId() + " do pedido No. "
						+ idPedido, e);
			}
		}

		// Ao final da clonaem do pedido precisamos cancelar o antigo para que
		// esse nao aparece nos faturamentos da empresa.
		cancelar(pedido.getId());
		return pedidoClone.getId();
	}

	@Override
	public Pedido removerItemPedido(Integer idItemPedido) throws BusinessException {
		ItemPedido itemPedido = null;
		try {
			itemPedido = (ItemPedido) this.entityManager
					.createQuery("select i from ItemPedido i join fetch i.pedido where i.id = :idItemPedido")
					.setParameter("idItemPedido", idItemPedido).getSingleResult();

			Pedido pedido = itemPedido.getPedido();
			alterarSequencialItemPedido(pedido.getId(), itemPedido.getSequencial());

			this.entityManager.remove(itemPedido);

			// Efetuando novamente o calculo pois na remocao o valor do pedido
			// deve ser atualizado
			pedido.setValorPedido(this.calcularValorPedido(pedido.getId()));
			pedido.setValorPedidoIPI(this.calcularValorPedidoIPI(pedido.getId()));
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
