package br.com.plastecno.service.impl;

import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Stateless;
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
import br.com.plastecno.service.constante.TipoLogradouro;
import br.com.plastecno.service.dao.GenericDAO;
import br.com.plastecno.service.entity.Cliente;
import br.com.plastecno.service.entity.ComentarioCliente;
import br.com.plastecno.service.entity.ContatoCliente;
import br.com.plastecno.service.entity.LogradouroCliente;
import br.com.plastecno.service.entity.Transportadora;
import br.com.plastecno.service.entity.Usuario;
import br.com.plastecno.service.exception.BusinessException;
import br.com.plastecno.service.impl.util.QueryUtil;
import br.com.plastecno.service.validacao.exception.InformacaoInvalidaException;
import br.com.plastecno.service.wrapper.PaginacaoWrapper;
import br.com.plastecno.util.StringUtils;
import br.com.plastecno.validacao.ValidadorInformacao;

@Stateless
public class ClienteServiceImpl implements ClienteService {
	
	@PersistenceContext(unitName="plastecno")
	private EntityManager entityManager;
	
	@EJB
	private LogradouroService logradouroService;
	
	@EJB
	private ConfiguracaoSistemaService configuracaoSistemaService;
	
	@EJB
	private ContatoService contatoService;
	
	@EJB
	private EnderecamentoService enderecamentoService;
	
	@EJB
	private UsuarioService usuarioService;
	
	private GenericDAO genericDAO;
	
	@PostConstruct
	public void init() {
		this.genericDAO = new GenericDAO(entityManager);
	}
	
	@Override
	public PaginacaoWrapper<Cliente> paginarCliente(Cliente filtro, boolean carregarVendedor, Integer indiceRegistroInicial,  Integer numeroMaximoRegistros) {
		return new PaginacaoWrapper<Cliente>(
				this.pesquisarTotalRegistros(filtro),
				this.pesquisarBy(filtro, true, indiceRegistroInicial, numeroMaximoRegistros));
	} 
	
	@Override
	@SuppressWarnings("unchecked")
	public List<Cliente> pesquisarClienteByIdRegiao(Integer idRegiao) throws BusinessException{
		
		if (idRegiao == null) {
			throw new BusinessException("Escolha uma regi�o para gerar o relat�rio de clientes.");
		} 
		
		final List<Integer> listaIdBairro = 
				this.entityManager.createQuery("select b.id from Regiao r inner join r.listaBairro b where r.id = :idRegiao")
				.setParameter("idRegiao", idRegiao).getResultList();
		
		if (listaIdBairro.isEmpty()) {
			return Collections.EMPTY_LIST;
		}
		
		StringBuilder select = new StringBuilder();
		select.append("select c from Cliente c ")
		// o contato deve ser exibido no relatorio e usamos um let join pois um cliente pode nao ter contatos
		.append("left join fetch c.listaContato lc ")
		.append("inner join fetch c.listaLogradouro l ")
		.append("where l.tipoLogradouro = :tipoLogradouro and l.endereco.bairro.id in (:listaIdBairro) ");
		
		return this.entityManager.createQuery(select.toString())
				.setParameter("listaIdBairro", listaIdBairro).setParameter("tipoLogradouro", TipoLogradouro.FATURAMENTO)
				.getResultList();
	}
	
	@Override
	public List<Cliente> pesquisarByIdVendedor (Integer idVendedor, boolean isPesquisaClienteInativo) throws BusinessException {
		return isPesquisaClienteInativo ? this.pesquisarInativosByIdVendedor(idVendedor) : 
			this.pesquisarByIdVendedor(idVendedor); 
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<Cliente> pesquisarInativosByIdVendedor (Integer idVendedor) throws BusinessException {
		final String PARAMETRO = 
				this.configuracaoSistemaService.pesquisar(ParametroConfiguracaoSistema.DIAS_INATIVIDADE_CLIENTE);
		
		if (PARAMETRO == null) {
			throw new BusinessException("N�o configurado o parametro de dias de inatividade para pesquisa de clientes inativos");
		}
		
		final Integer DIAS_INATIVIDADE = Integer.parseInt(PARAMETRO);
		final Calendar dataInicioInatividade = Calendar.getInstance();
		dataInicioInatividade.add(Calendar.DAY_OF_YEAR, - DIAS_INATIVIDADE);
		
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
	
	@SuppressWarnings("unchecked")
	@Override
	public List<Cliente> pesquisarByIdVendedor (Integer idVendedor) {
		StringBuilder select =new StringBuilder("select c from Cliente c left join fetch c.listaContato ");
		if (idVendedor != null) {
			select.append("where c.vendedor.id = :idVendedor ");	
		}
		select.append(" order by c.dataUltimoContato, c.nomeFantasia ");
		
		Query query = this.entityManager.createQuery(select.toString());
		if (idVendedor != null) {
			query.setParameter("idVendedor", idVendedor);	
		}
		
		return query.getResultList();
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public List<Cliente> pesquisarClientesAssociados(Integer idVendedor) {
		return this.entityManager.createQuery("select new Cliente(c.id, c.nomeFantasia) from Cliente c where c.vendedor.id = :idVendedor order by c.nomeFantasia asc")
				.setParameter("idVendedor", idVendedor).getResultList();
		
	}
	
	@Override
	public boolean isNomeFantasiaExistente(Integer id, String nomeFantasia) {
		return this.genericDAO.isEntidadeExistente(Cliente.class, id, "nomeFantasia", nomeFantasia);
	}
	
	@Override
	public boolean isClienteProspectado(Integer idCliente) {
		Query query = this.entityManager.createQuery("select c.prospeccaoFinalizada from Cliente c where c.id = :idCliente")
				.setParameter("idCliente", idCliente);
		return QueryUtil.gerarRegistroUnico(query, Boolean.class, false);
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public List<Cliente> pesquisarClientesDesassociados() {
		return this.entityManager.createQuery("select new Cliente(c.id, c.nomeFantasia) from Cliente c where c.vendedor = null order by c.nomeFantasia asc")
				.getResultList();
		
	}
	
	@Override
	public Cliente inserir (Cliente cliente) throws BusinessException {
		/* 
		 * Verificando se foi configurado a prospeccao do cliente, caso nao foi, 
		 * pegaremos a configuracao ja existente no sistema 
		 */
		if (cliente.getProspeccaoFinalizada() == null) {
			cliente.setProspeccaoFinalizada(this.isClienteProspectado(cliente.getId()));			
		}
		ValidadorInformacao.validar(cliente);
		
		if(isNomeFantasiaExistente(cliente.getId(), cliente.getNomeFantasia())) {
			throw new BusinessException("O nome fantasia enviado ja foi cadastrado para outro cliente");
		}
		
		validarDocumentosPreenchidos(cliente);
		validarListaLogradouroPreenchida(cliente);
		inserirEndereco(cliente);
		return this.entityManager.merge(cliente);
	}
	
	@Override
	public void removerLogradourosAusentes (Integer idCliente, Collection<LogradouroCliente> listaLogradouro) {
		final List<LogradouroCliente> listaLogradouroCliente = 
				logradouroService.pesquisarAusentes(idCliente, listaLogradouro, LogradouroCliente.class);
		for (LogradouroCliente logradouroCliente : listaLogradouroCliente) {
			logradouroCliente.setCancelado(true);
			this.entityManager.merge(logradouroCliente);
		}
	}
	
	@Override
	public void removerLogradouro (Integer idLogradouro) {
		final LogradouroCliente logradouroCliente = this.pesquisarLogradouroById(idLogradouro);
		logradouroCliente.setCancelado(true);
		this.entityManager.merge(logradouroCliente);
	}
	
	@Override
	public boolean isCNPJExistente(Integer idCliente, String cnpj) {
		return this.genericDAO.isEntidadeExistente(Cliente.class, idCliente, "cnpj", cnpj);
	}
	
	@Override
	public boolean isCPFExistente(Integer idCliente, String cpf) {
		return this.genericDAO.isEntidadeExistente(Cliente.class, idCliente, "cpf", cpf);
	}
	
	@Override
	public boolean isInscricaoEstadualExistente(Integer idCliente, String inscricaoEstadual) {
		return this.genericDAO.isEntidadeExistente(Cliente.class, idCliente, "inscricaoEstadual", inscricaoEstadual);
	}
	
	@Override
	public Integer contactarCliente (Integer id) {
		Query query = this.entityManager.createQuery("update Cliente c set c.dataUltimoContato = :dataUltimoContato  where c.id = :id");
		query.setParameter("id", id);
		query.setParameter("dataUltimoContato", new Date());		
		return query.executeUpdate();
	}
	
	@Override
	public List<Cliente> pesquisarBy(Cliente filtro, Integer indiceRegistroInicial,  Integer numeroMaximoRegistros) {
		return this.pesquisarBy(filtro, false, indiceRegistroInicial, numeroMaximoRegistros);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<Cliente> pesquisarBy(Cliente filtro, boolean carregarVendedor, Integer indiceRegistroInicial,  Integer numeroMaximoRegistros) {
		if (filtro == null){
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
		if(indiceRegistroInicial != null && indiceRegistroInicial >= 0 && numeroMaximoRegistros != null && numeroMaximoRegistros >= 0) {
			query.setFirstResult(indiceRegistroInicial).setMaxResults(numeroMaximoRegistros);
		}
		return query.getResultList();
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<Cliente> pesquisarByRamoAtividade(Integer idRamoAtividade) {
		if (idRamoAtividade == null){
			return Collections.emptyList();
		}
		StringBuilder select = new StringBuilder("SELECT u FROM Cliente u ")
		.append("left join fetch u.listaContato l ")
		.append("left join fetch u.vendedor ")
		.append("where u.ramoAtividade.id = :idRamoAtividade ");
		
		select.append("order by u.vendedor.nome, u.nomeFantasia ");
		
		return this.entityManager.createQuery(select.toString())
				.setParameter("idRamoAtividade", idRamoAtividade).getResultList();
	}
	
	@Override
	public Cliente pesquisarById(Integer id) {
		return QueryUtil.gerarRegistroUnico(this.entityManager.createQuery("select c from Cliente c where c.id = :id")
				.setParameter("id", id), Cliente.class, null);
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public List<Cliente> pesquisarClientesById(List<Integer> listaIdCliente) {
		return this.entityManager.createQuery("select c from Cliente c where c.id in (:listaIdCliente)")
				.setParameter("listaIdCliente", listaIdCliente).getResultList();
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<LogradouroCliente> pesquisarLogradouro (Integer idCliente) {
		StringBuilder select = new StringBuilder()
		.append("select l from Cliente c ")
		.append("inner join c.listaLogradouro l where c.id = :idCliente ")
		.append(" and l.cancelado = false ");
		
		return this.entityManager.createQuery(select.toString())
			.setParameter("idCliente", idCliente).getResultList();
	}
	
	@Override
	public LogradouroCliente pesquisarLogradouroById (Integer idLogradouro) {
		return this.logradouroService.pesquisarById(idLogradouro, LogradouroCliente.class);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<ContatoCliente> pesquisarContato (Integer idCliente) {
		return this.entityManager.createQuery("select l from Cliente c inner join c.listaContato l where c.id = :idCliente ")
				.setParameter("idCliente", idCliente).getResultList();
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public List<Transportadora> pesquisarTransportadorasAssociadas(Integer idCliente) {
		return this.entityManager.createQuery(
				"select new Transportadora(t.id, t.nomeFantasia) from Cliente c inner join c.listaRedespacho t where c.id = :idCliente and t.ativo = true order by t.nomeFantasia asc")
				.setParameter("idCliente", idCliente).getResultList();
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public List<Transportadora> pesquisarTransportadorasDesassociadas(Integer idCliente) {
		List<Transportadora> listaTransportadora = this.pesquisarTransportadorasAssociadas(idCliente);
		Query query = null;
		if (!listaTransportadora.isEmpty()) {
			query = this.entityManager.createQuery(
					"select new Transportadora(t.id, t.nomeFantasia) from Transportadora t where t not in (:listaTransportadora) and t.ativo = true by t.nomeFantasia asc");
			query.setParameter("listaTransportadora", listaTransportadora);
		} else {
			query = this.entityManager.createQuery("select new Transportadora(t.id, t.nomeFantasia) from Transportadora t by t.nomeFantasia asc");
		}
		return query.getResultList();
	}
	
	@Override
	public boolean isClienteAtivo(Integer idCliente) {
		return true;
	}
	
	@Override
	public boolean isEmailExistente(Integer idCliente, String email) {
		Query query = null;
		if (idCliente == null) {
			query = this.entityManager.createQuery("select count(r.id) from Cliente r where r.email = :email ");
			query.setParameter("email", email);
		} else {
			query = this.entityManager.createQuery("select count(r.id) from Cliente  r where r.id != :id AND r.email = :email ");
			query.setParameter("email", email);
			query.setParameter("id", idCliente);	
		}
		
		return QueryUtil.gerarRegistroUnico(query, Long.class, 0L) > 1;
	}
	
	@Override
	public Long pesquisarTotalRegistros(Cliente filtro) {
		if (filtro == null){
			return 0L;
		}
		
		final StringBuilder select = new StringBuilder("SELECT count(u.id) FROM Cliente u ");
		this.gerarRestricaoPesquisa(filtro, select);
		Query query = this.gerarQueryPesquisa(filtro, select);
		return QueryUtil.gerarRegistroUnico(query, Long.class, null);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<Cliente> pesquisarByNomeFantasia(String nomeFantasia) {
		Query query = this.entityManager.createQuery(
				"select new Cliente(c.id, c.nomeFantasia) from Cliente c where c.nomeFantasia like :nomeFantasia order by c.nomeFantasia ");
		query.setParameter("nomeFantasia", "%"+nomeFantasia+"%");
		return query.getResultList();
	}
	
	private Query gerarQueryPesquisa (Cliente filtro, StringBuilder select) {
		Query query = this.entityManager.createQuery(select.toString());
		if (StringUtils.isNotEmpty(filtro.getNomeFantasia())) {
			query.setParameter("nomeFantasia", "%"+filtro.getNomeFantasia()+"%");
		}
		
		if (StringUtils.isNotEmpty(filtro.getEmail())) {
			query.setParameter("email", "%"+filtro.getEmail()+"%");
		}
		
		if (StringUtils.isNotEmpty(filtro.getCpf())) {
			query.setParameter("cpf", "%"+filtro.getCpf()+"%");
		} else if (StringUtils.isNotEmpty(filtro.getCnpj())) {
			query.setParameter("cnpj", "%"+filtro.getCnpj()+"%");
		}
		return query;
	}
	
	private void gerarRestricaoPesquisa (Cliente filtro, StringBuilder select) {
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
		
		if(restricao.length() > 0) {
			
			select.append(" WHERE ").append(restricao);
			select.delete(select.lastIndexOf("AND"), select.length()-1);
		}
	}
	
	private void validarListaLogradouroPreenchida(Cliente cliente) throws BusinessException {
		//logradouroService.validarLogradouro(cliente.getListaLogradouro());
		
		if (cliente.isProspectado()) {
			logradouroService.verificarListaLogradouroObrigatorio(cliente.getListaLogradouro());
		}
	}
	
	private void validarDocumentosPreenchidos (Cliente cliente) throws InformacaoInvalidaException {
		
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
			throw new InformacaoInvalidaException("Inscri��o Estadual enviado j� foi cadastrado para outro cliente"); 
		}
	}
	
	private void inserirEndereco(Cliente cliente) throws BusinessException {
		if (cliente.isListaLogradouroPreenchida()) {
			for (LogradouroCliente logradouro : cliente.getListaLogradouro()) {
				logradouro.addEndereco(
						this.enderecamentoService.inserir(logradouro.recuperarEndereco()));
			}
		}
	}

	@Override
	public void inserirComentario(Integer idCliente, String comentario)
			throws BusinessException {
		Cliente cliente = pesquisarById(idCliente);
		Usuario vendedor = usuarioService.pesquisarVendedorByIdCliente(idCliente);

		ComentarioCliente comentarioCliente = new ComentarioCliente();
		comentarioCliente.setCliente(cliente);
		comentarioCliente.setVendedor(vendedor);
		comentarioCliente.setDataInclusao(new Date());
		comentarioCliente.setConteudo(comentario);

		ValidadorInformacao.validar(comentarioCliente);
		entityManager.persist(comentarioCliente);
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<ComentarioCliente> pesquisarComentarioByIdCliente(
			Integer idCliente) {
		return (List<ComentarioCliente>) entityManager
				.createQuery(
						"select new ComentarioCliente (c.dataInclusao, c.conteudo, v.nome, v.sobrenome) from ComentarioCliente c "
						+" inner join c.vendedor v where c.cliente.id = :idCliente order by c.dataInclusao desc"
						)
				.setParameter("idCliente", idCliente).getResultList();
	}
}
