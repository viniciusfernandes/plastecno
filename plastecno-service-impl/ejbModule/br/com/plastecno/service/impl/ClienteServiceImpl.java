package br.com.plastecno.service.impl;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import br.com.plastecno.service.ClienteService;
import br.com.plastecno.service.ConfiguracaoSistemaService;
import br.com.plastecno.service.ContatoService;
import br.com.plastecno.service.EnderecamentoService;
import br.com.plastecno.service.LogradouroService;
import br.com.plastecno.service.UsuarioService;
import br.com.plastecno.service.constante.ParametroConfiguracaoSistema;
import br.com.plastecno.service.constante.TipoCliente;
import br.com.plastecno.service.constante.TipoLogradouro;
import br.com.plastecno.service.dao.ClienteDAO;
import br.com.plastecno.service.entity.Cliente;
import br.com.plastecno.service.entity.ComentarioCliente;
import br.com.plastecno.service.entity.ContatoCliente;
import br.com.plastecno.service.entity.LogradouroCliente;
import br.com.plastecno.service.entity.Transportadora;
import br.com.plastecno.service.entity.Usuario;
import br.com.plastecno.service.exception.BusinessException;
import br.com.plastecno.service.impl.anotation.REVIEW;
import br.com.plastecno.service.impl.util.QueryUtil;
import br.com.plastecno.service.validacao.exception.InformacaoInvalidaException;
import br.com.plastecno.service.wrapper.PaginacaoWrapper;
import br.com.plastecno.util.StringUtils;
import br.com.plastecno.validacao.ValidadorInformacao;

@Stateless
public class ClienteServiceImpl implements ClienteService {
	private ClienteDAO clienteDAO;
	@EJB
	private ConfiguracaoSistemaService configuracaoSistemaService;

	@EJB
	private ContatoService contatoService;

	@EJB
	private EnderecamentoService enderecamentoService;

	@PersistenceContext(unitName = "plastecno")
	private EntityManager entityManager;

	@EJB
	private LogradouroService logradouroService;

	@EJB
	private UsuarioService usuarioService;

	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public Cliente alterarRevendedor(Cliente cliente) throws BusinessException {
		Cliente revendedor = pesquisarRevendedor();
		if (revendedor != null) {
			clienteDAO.alterarTipoCliente(revendedor.getId(), TipoCliente.NORMAL);
		}
		cliente.setTipoCliente(TipoCliente.REVENDEDOR);
		return inserir(cliente);
	}

	@Override
	public Integer contactarCliente(Integer id) {
		Query query = this.entityManager
				.createQuery("update Cliente c set c.dataUltimoContato = :dataUltimoContato  where c.id = :id");
		query.setParameter("id", id);
		query.setParameter("dataUltimoContato", new Date());
		return query.executeUpdate();
	}

	@SuppressWarnings("unchecked")
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	private List<Cliente> executarSelectClienteContato(StringBuilder select, boolean formatarContato) {
		List<Object[]> resultado = entityManager.createNativeQuery(select.toString()).getResultList();

		if (resultado == null) {
			return new ArrayList<Cliente>();
		}

		Cliente c = null;
		ContatoCliente co = null;
		Set<Integer> idList = new HashSet<Integer>();
		List<Cliente> lCli = new ArrayList<Cliente>();
		StringBuilder formCont = new StringBuilder();
		for (Object[] o : resultado) {
			if (o[0] == null || idList.contains(o[0])) {
				continue;
			}
			idList.add((Integer) o[0]);

			c = new Cliente();

			c.setId((Integer) o[0]);
			c.setNomeFantasia((String) (o[3] == null ? "" : o[3]));
			c.setNomeVendedor((String) (o[1] == null ? "SEM VENDEDOR" : (o[1] + " " + (o[2] == null ? "" : o[2]))));

			co = new ContatoCliente();
			co.setNome((String) (o[4] == null ? "" : o[4]));
			co.setDdi((String) (o[5] == null ? "" : o[5]));
			co.setDdd((String) (o[6] == null ? "" : o[6]));
			co.setTelefone((String) (o[7] == null ? "" : o[7]));
			co.setRamal((String) (o[8] == null ? "" : o[8]));
			co.setFax((String) (o[9] == null ? "" : o[9]));

			if (formatarContato) {
				formCont.append(co.getNome());
				if (StringUtils.isNotEmpty(co.getEmail())) {
					formCont.append(" - ").append(co.getEmail());
				}

				if (StringUtils.isNotEmpty(co.getTelefone())) {
					formCont.append(" - ").append(co.getTelefoneFormatado());
				}
				c.setContatoFormatado(formCont.toString());
				formCont.delete(0, formCont.length());
			}

			c.addContato(co);
			lCli.add(c);
		}
		return lCli;
	}

	private Query gerarQueryPesquisa(Cliente filtro, StringBuilder select) {
		Query query = this.entityManager.createQuery(select.toString());
		if (StringUtils.isNotEmpty(filtro.getNomeFantasia())) {
			query.setParameter("nomeFantasia", "%" + filtro.getNomeFantasia() + "%");
		}

		if (StringUtils.isNotEmpty(filtro.getEmail())) {
			query.setParameter("email", "%" + filtro.getEmail() + "%");
		}

		if (StringUtils.isNotEmpty(filtro.getCpf())) {
			query.setParameter("cpf", "%" + filtro.getCpf() + "%");
		} else if (StringUtils.isNotEmpty(filtro.getCnpj())) {
			query.setParameter("cnpj", "%" + filtro.getCnpj() + "%");
		}
		return query;
	}

	private void gerarRestricaoPesquisa(Cliente filtro, StringBuilder select) {
		StringBuilder restricao = new StringBuilder();
		if (StringUtils.isNotEmpty(filtro.getNomeFantasia())) {
			restricao.append("u.nomeFantasia LIKE :nomeFantasia AND ");
		}

		if (StringUtils.isNotEmpty(filtro.getEmail())) {
			restricao.append("u.email LIKE :email AND ");
		}

		if (StringUtils.isNotEmpty(filtro.getCpf())) {
			restricao.append("u.cpf LIKE :cpf AND ");
		} else if (StringUtils.isNotEmpty(filtro.getCnpj())) {
			restricao.append("u.cnpj LIKE :cnpj AND ");
		}

		if (restricao.length() > 0) {
			select.append(" WHERE ").append(restricao);
			select.delete(select.lastIndexOf("AND"), select.length() - 1);
		}
	}

	private StringBuilder gerarSelectRelatorioClienteContato() {
		StringBuilder s = new StringBuilder();
		s.append("select c.id, u.nome u_nome, u.sobrenome, c.nome_fantasia, ct.nome, ct.ddi_1, ct.ddd_1, ct.telefone_1, ct.ramal_1, ct.fax_1   from vendas.tb_cliente c  ");
		s.append("left join vendas.tb_contato_cliente cc on cc.id_cliente = c.id ");
		s.append("left join vendas.tb_contato ct on ct.id = cc.id ");
		s.append("left join vendas.tb_usuario u on u.id = c.id_vendedor ");
		return s;
	}

	@PostConstruct
	public void init() {
		clienteDAO = new ClienteDAO(entityManager);
	}

	@Override
	public Cliente inserir(Cliente cliente) throws BusinessException {
		// Os revendedores nao deve estar associados a nenhum vendedor.
		if (cliente.isRevendedor()) {
			cliente.setVendedor(null);
		} else if (!cliente.isRevendedor() && cliente.getVendedor() == null) {
			throw new BusinessException("Vendedor do cliente é obrigatório");
		}

		if (cliente.getTipoCliente() == null) {
			cliente.setTipoCliente(TipoCliente.NORMAL);
		}
		ValidadorInformacao.validar(cliente);

		if (isNomeFantasiaExistente(cliente.getId(), cliente.getNomeFantasia())) {
			throw new BusinessException("O nome fantasia enviado ja foi cadastrado para outro cliente");
		}

		validarDocumentosPreenchidos(cliente);
		validarRevendedorExistente(cliente);
		inserirEndereco(cliente);
		return cliente.getId() == null ? clienteDAO.inserir(cliente) : clienteDAO.alterar(cliente);
	}

	@Override
	public void inserirComentario(Integer idProprietario, Integer idCliente, String comentario)
			throws BusinessException {
		Cliente cliente = pesquisarById(idCliente);
		Usuario proprietario = usuarioService.pesquisarById(idProprietario);
		inserirComentario(proprietario, cliente, comentario);
	}

	@Override
	public void inserirComentario(Integer idCliente, String comentario) throws BusinessException {
		if (StringUtils.isEmpty(comentario)) {
			return;
		}
		Cliente cliente = pesquisarById(idCliente);
		Usuario vendedor = usuarioService.pesquisarVendedorByIdCliente(idCliente);
		inserirComentario(vendedor, cliente, comentario);
	}

	private void inserirComentario(Usuario proprietario, Cliente cliente, String comentario) throws BusinessException {
		ComentarioCliente comentarioCliente = new ComentarioCliente();
		comentarioCliente.setCliente(cliente);
		comentarioCliente.setVendedor(proprietario);
		comentarioCliente.setDataInclusao(new Date());
		comentarioCliente.setConteudo(comentario);

		ValidadorInformacao.validar(comentarioCliente);
		entityManager.persist(comentarioCliente);

	}

	private void inserirEndereco(Cliente cliente) throws BusinessException {
		if (cliente.isListaLogradouroPreenchida()) {
			for (LogradouroCliente logradouro : cliente.getListaLogradouro()) {
				logradouro.addEndereco(this.enderecamentoService.inserir(logradouro.recuperarEndereco()));
			}
		}
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public boolean isCNPJExistente(Integer idCliente, String cnpj) {
		return clienteDAO.isEntidadeExistente(Cliente.class, idCliente, "cnpj", cnpj);
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public boolean isCPFExistente(Integer idCliente, String cpf) {
		return clienteDAO.isEntidadeExistente(Cliente.class, idCliente, "cpf", cpf);
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public boolean isEmailExistente(Integer idCliente, String email) {
		return clienteDAO.isEmailExistente(idCliente, email);
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public boolean isInscricaoEstadualExistente(Integer idCliente, String inscricaoEstadual) {
		return clienteDAO.isEntidadeExistente(Cliente.class, idCliente, "inscricaoEstadual", inscricaoEstadual);
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public boolean isNomeFantasiaExistente(Integer id, String nomeFantasia) {
		return clienteDAO.isEntidadeExistente(Cliente.class, id, "nomeFantasia", nomeFantasia);
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public PaginacaoWrapper<Cliente> paginarCliente(Cliente filtro, boolean carregarVendedor,
			Integer indiceRegistroInicial, Integer numeroMaximoRegistros) {
		return new PaginacaoWrapper<Cliente>(this.pesquisarTotalRegistros(filtro), this.pesquisarBy(filtro, true,
				indiceRegistroInicial, numeroMaximoRegistros));
	}

	@SuppressWarnings("unchecked")
	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public List<Cliente> pesquisarBy(Cliente filtro, boolean carregarVendedor, Integer indiceRegistroInicial,
			Integer numeroMaximoRegistros) {
		if (filtro == null) {
			return Collections.emptyList();
		}
		StringBuilder select = null;
		if (carregarVendedor) {
			select = new StringBuilder("SELECT u FROM Cliente u left join fetch u.vendedor ");
		} else {
			select = new StringBuilder("SELECT u FROM Cliente u ");
		}

		this.gerarRestricaoPesquisa(filtro, select);
		select.append("order by u.nomeFantasia ");

		Query query = this.gerarQueryPesquisa(filtro, select);
		if (indiceRegistroInicial != null && indiceRegistroInicial >= 0 && numeroMaximoRegistros != null
				&& numeroMaximoRegistros >= 0) {
			query.setFirstResult(indiceRegistroInicial).setMaxResults(numeroMaximoRegistros);
		}
		return query.getResultList();
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public List<Cliente> pesquisarBy(Cliente filtro, Integer indiceRegistroInicial, Integer numeroMaximoRegistros) {
		return this.pesquisarBy(filtro, false, indiceRegistroInicial, numeroMaximoRegistros);
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	@REVIEW(descricao = "Esse metodo esta sendo chamado na tela de pedidos e estamos retornando mais informacao do que o necessario.")
	public Cliente pesquisarById(Integer id) {
		return clienteDAO.pesquisarById(id);
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public List<Cliente> pesquisarByIdVendedor(Integer idVendedor, boolean isPesquisaClienteInativo)
			throws BusinessException {
		return isPesquisaClienteInativo ? pesquisarInativosByIdVendedor(idVendedor)
				: pesquisarClienteContatoByIdVendedor(idVendedor);
	}

	@SuppressWarnings("unchecked")
	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public List<Cliente> pesquisarByNomeFantasia(String nomeFantasia) {
		Query query = this.entityManager
				.createQuery("select new Cliente(c.id, c.nomeFantasia) from Cliente c where c.nomeFantasia like :nomeFantasia order by c.nomeFantasia ");
		query.setParameter("nomeFantasia", "%" + nomeFantasia + "%");
		return query.getResultList();
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public List<Cliente> pesquisarClienteByIdRamoAtividade(Integer idRamoAtividade) {
		if (idRamoAtividade == null) {
			return Collections.emptyList();
		}

		StringBuilder s = gerarSelectRelatorioClienteContato();
		s.append("where c.id_ramo_atividade = ").append(idRamoAtividade);
		s.append(" order by u.nome, c.nome_fantasia ");

		return executarSelectClienteContato(s, true);
	}

	@Override
	@SuppressWarnings("unchecked")
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public List<Cliente> pesquisarClienteByIdRegiao(Integer idRegiao) throws BusinessException {

		if (idRegiao == null) {
			throw new BusinessException("Escolha uma região para gerar o relatório de clientes.");
		}

		final List<Integer> listaIdBairro = this.entityManager
				.createQuery("select b.id from Regiao r inner join r.listaBairro b where r.id = :idRegiao")
				.setParameter("idRegiao", idRegiao).getResultList();

		if (listaIdBairro.isEmpty()) {
			return Collections.EMPTY_LIST;
		}

		StringBuilder select = new StringBuilder();
		select.append("select c from Cliente c ")
				// o contato deve ser exibido no relatorio e usamos um let join
				// pois um cliente pode nao ter contatos
				.append("left join fetch c.listaContato lc ").append("inner join fetch c.listaLogradouro l ")
				.append("where l.tipoLogradouro = :tipoLogradouro and l.endereco.bairro.id in (:listaIdBairro) ")
				.append("order by c.nomeFantasia");

		return this.entityManager.createQuery(select.toString()).setParameter("listaIdBairro", listaIdBairro)
				.setParameter("tipoLogradouro", TipoLogradouro.FATURAMENTO).getResultList();
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public List<Cliente> pesquisarClienteContatoByIdVendedor(Integer idVendedor) {
		StringBuilder s = gerarSelectRelatorioClienteContato();
		if (idVendedor != null) {
			s.append("where c.id_vendedor = ").append(idVendedor);
		}
		s.append(" order by c.nome_fantasia ");
		return executarSelectClienteContato(s, true);
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	@REVIEW(descricao = "Esse metodo esta sendo chamado na tela de pedidos e estamos retornando mais informacao do que o necessario.")
	public Cliente pesquisarClienteEContatoById(Integer idCliente) {
		return QueryUtil.gerarRegistroUnico(
				entityManager.createQuery(
						"select c from Cliente c left join fetch c.listaContato where c.id = :idCliente").setParameter(
						"idCliente", idCliente), Cliente.class, null);
	}

	@REVIEW(descricao = "Acredito que nesse servico nao precisamos carregar todos os logradouros e os contatos.")
	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public Cliente pesquisarClienteResumidoByCnpj(String cnpj) {
		Cliente c = clienteDAO.pesquisarClienteResumidoByCnpj(cnpj);
		if (c != null) {
			c.addLogradouro(pesquisarLogradouro(c.getId()));
			c.addContato(pesquisarContato(c.getId()));
			// Estamos anulando oconteudo desses campos para evitar
			// lazyloadException que estava acontecedendo em algumas requisicoes
			// ajax.
			c.setVendedor(null);
			c.setListaRedespacho(null);
		}
		return c;
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public Cliente pesquisarClienteResumidoById(Integer idCliente) {
		return clienteDAO.pesquisarClienteResumidoById(idCliente);
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public Cliente pesquisarClienteResumidoLogradouroById(Integer idCliente) {
		Cliente c = clienteDAO.pesquisarClienteResumidoLogradouroById(idCliente);
		if (c != null) {
			c.addLogradouro(pesquisarLogradouro(idCliente));
		}
		return c;
	}

	@Override
	@SuppressWarnings("unchecked")
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public List<Cliente> pesquisarClientesAssociados(Integer idVendedor) {
		return this.entityManager
				.createQuery(
						"select new Cliente(c.id, c.nomeFantasia) from Cliente c where c.vendedor.id = :idVendedor order by c.nomeFantasia asc")
				.setParameter("idVendedor", idVendedor).getResultList();

	}

	@Override
	@SuppressWarnings("unchecked")
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public List<Cliente> pesquisarClientesById(List<Integer> listaIdCliente) {
		return this.entityManager.createQuery("select c from Cliente c where c.id in (:listaIdCliente)")
				.setParameter("listaIdCliente", listaIdCliente).getResultList();
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<Cliente> pesquisarClientesDesassociados() {
		return this.entityManager
				.createQuery(
						"select new Cliente(c.id, c.nomeFantasia) from Cliente c where c.vendedor = null order by c.nomeFantasia asc")
				.getResultList();

	}

	@Override
	@SuppressWarnings("unchecked")
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public List<ComentarioCliente> pesquisarComentarioByIdCliente(Integer idCliente) {
		return (List<ComentarioCliente>) entityManager
				.createQuery(
						"select new ComentarioCliente (c.dataInclusao, c.conteudo, v.nome, v.sobrenome) from ComentarioCliente c "
								+ " inner join c.vendedor v where c.cliente.id = :idCliente order by c.dataInclusao desc")
				.setParameter("idCliente", idCliente).getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public List<ContatoCliente> pesquisarContato(Integer idCliente) {
		return this.entityManager
				.createQuery("select l from Cliente c inner join c.listaContato l where c.id = :idCliente ")
				.setParameter("idCliente", idCliente).getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public List<Cliente> pesquisarInativosByIdVendedor(Integer idVendedor) throws BusinessException {
		final String PARAMETRO = this.configuracaoSistemaService
				.pesquisar(ParametroConfiguracaoSistema.DIAS_INATIVIDADE_CLIENTE);

		if (PARAMETRO == null) {
			throw new BusinessException(
					"Não configurado o parametro de dias de inatividade para pesquisa de clientes inativos");
		}

		final Integer DIAS_INATIVIDADE = Integer.parseInt(PARAMETRO);
		final Calendar dataInicioInatividade = Calendar.getInstance();
		dataInicioInatividade.add(Calendar.DAY_OF_YEAR, -DIAS_INATIVIDADE);

		final StringBuilder select = new StringBuilder();
		select.append("select c from Pedido p inner join p.cliente c ");
		select.append("left join fetch c.listaContato ");

		select.append("where p.dataEnvio <= :dataInicioInatividade ");

		if (idVendedor != null) {
			select.append(" and c.vendedor.id = :idVendedor ");
		}
		select.append("order by c.dataUltimoContato, c.nomeFantasia ");

		Query query = this.entityManager.createQuery(select.toString());
		if (idVendedor != null) {
			query.setParameter("idVendedor", idVendedor);
		}
		query.setParameter("dataInicioInatividade", dataInicioInatividade.getTime());
		return query.getResultList();
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public List<LogradouroCliente> pesquisarLogradouro(Integer idCliente) {
		return clienteDAO.pesquisarLogradouroById(idCliente);
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public LogradouroCliente pesquisarLogradouroById(Integer idLogradouro) {
		return this.logradouroService.pesquisarById(idLogradouro, LogradouroCliente.class);
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public LogradouroCliente pesquisarLogradouroFaturamentoById(Integer idCliente) {
		List<LogradouroCliente> l = clienteDAO.pesquisarLogradouroFaturamentoById(idCliente);
		return l.isEmpty() ? null : l.get(0);
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public String pesquisarNomeFantasia(Integer idCliente) {
		return clienteDAO.pesquisarNomeFantasia(idCliente);
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public Cliente pesquisarRevendedor() {
		return clienteDAO.pesquisarRevendedor();
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public Long pesquisarTotalRegistros(Cliente filtro) {
		if (filtro == null) {
			return 0L;
		}

		final StringBuilder select = new StringBuilder("SELECT count(u.id) FROM Cliente u ");
		this.gerarRestricaoPesquisa(filtro, select);
		Query query = this.gerarQueryPesquisa(filtro, select);
		return QueryUtil.gerarRegistroUnico(query, Long.class, null);
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	@SuppressWarnings("unchecked")
	public List<Transportadora> pesquisarTransportadorasDesassociadas(Integer idCliente) {
		List<Transportadora> listaTransportadora = this.pesquisarTransportadorasRedespacho(idCliente);
		Query query = null;
		if (!listaTransportadora.isEmpty()) {
			query = this.entityManager
					.createQuery("select new Transportadora(t.id, t.nomeFantasia) from Transportadora t where t not in (:listaTransportadora) and t.ativo = true order by t.nomeFantasia asc");
			query.setParameter("listaTransportadora", listaTransportadora);
		} else {
			query = this.entityManager
					.createQuery("select new Transportadora(t.id, t.nomeFantasia) from Transportadora t order by t.nomeFantasia asc");
		}
		return query.getResultList();
	}

	@Override
	@SuppressWarnings("unchecked")
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public List<Transportadora> pesquisarTransportadorasRedespacho(Integer idCliente) {
		return this.entityManager
				.createQuery(
						"select new Transportadora(t.id, t.nomeFantasia) from Cliente c inner join c.listaRedespacho t where c.id = :idCliente and t.ativo = true order by t.nomeFantasia asc")
				.setParameter("idCliente", idCliente).getResultList();
	}

	@Override
	public void removerLogradouro(Integer idLogradouro) {
		final LogradouroCliente logradouroCliente = this.pesquisarLogradouroById(idLogradouro);
		logradouroCliente.setCancelado(true);
		this.entityManager.merge(logradouroCliente);
	}

	@Override
	public void removerLogradourosAusentes(Integer idCliente, Collection<LogradouroCliente> listaLogradouro) {
		final List<LogradouroCliente> listaLogradouroCliente = logradouroService.pesquisarAusentes(idCliente,
				listaLogradouro, LogradouroCliente.class);
		for (LogradouroCliente logradouroCliente : listaLogradouroCliente) {
			logradouroCliente.setCancelado(true);
			this.entityManager.merge(logradouroCliente);
		}
	}

	private void validarDocumentosPreenchidos(Cliente cliente) throws InformacaoInvalidaException {

		if (this.isEmailExistente(cliente.getId(), cliente.getEmail())) {
			throw new InformacaoInvalidaException("Email enviado ja foi cadastrado para outro cliente");
		}

		if (this.isCPFExistente(cliente.getId(), cliente.getCpf())) {
			throw new InformacaoInvalidaException("CPF enviado ja foi cadastrado para outro cliente");
		}

		if (this.isCNPJExistente(cliente.getId(), cliente.getCnpj())) {
			throw new InformacaoInvalidaException("CNPJ enviado ja foi cadastrado para outro cliente");
		}

		if (this.isInscricaoEstadualExistente(cliente.getId(), cliente.getInscricaoEstadual())) {
			throw new InformacaoInvalidaException("Inscrição Estadual enviado já foi cadastrado para outro cliente");
		}
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public void validarListaLogradouroPreenchida(Cliente cliente) throws BusinessException {
		logradouroService.validarListaLogradouroPreenchida(cliente.getListaLogradouro());
	}

	private void validarRevendedorExistente(Cliente cliente) throws BusinessException {
		if (cliente.isRevendedor() && clienteDAO.isRevendedorExistente(cliente.getId())) {
			Cliente revendedor = clienteDAO.pesquisarRevendedor();
			throw new BusinessException("Não é possível mais de um cliente revendedor cadastrado. O cliente \""
					+ revendedor.getNomeFantasia() + "\" já esta cadastrado como revendedor.");
		}
	}
}
