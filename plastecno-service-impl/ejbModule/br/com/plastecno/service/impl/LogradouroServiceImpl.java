package br.com.plastecno.service.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import br.com.plastecno.service.EnderecamentoService;
import br.com.plastecno.service.LogradouroService;
import br.com.plastecno.service.constante.TipoLogradouro;
import br.com.plastecno.service.dao.LogradouroDAO;
import br.com.plastecno.service.entity.Logradouro;
import br.com.plastecno.service.entity.LogradouroCliente;
import br.com.plastecno.service.entity.LogradouroEndereco;
import br.com.plastecno.service.entity.LogradouroPedido;
import br.com.plastecno.service.exception.BusinessException;
import br.com.plastecno.service.impl.util.QueryUtil;
import br.com.plastecno.service.validacao.exception.InformacaoInvalidaException;

@Stateless
public class LogradouroServiceImpl implements LogradouroService {
	@EJB
	private EnderecamentoService enderecamentoService;
	@PersistenceContext(unitName = "plastecno")
	private EntityManager entityManager;

	private LogradouroDAO logradouroDAO;

	@SuppressWarnings("unchecked")
	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public void importarLogradouro() {
		List<Object[]> ids = null;
		List<Object[]> logs = null;
		/*
		 * entityManager.createNativeQuery(
		 * "ALTER TABLE vendas.tb_logradouro_cliente drop constraint id_logradouro_cliente"
		 * ).executeUpdate();
		 * 
		 * ids = entityManager.createQuery(
		 * "select c.id, c.cliente.id from LogradouroCliente c"
		 * ).getResultList();
		 * 
		 * for (Object[] o : ids) { logs = entityManager .createQuery(
		 * "select l.endereco.cep, l.endereco.descricao, l.numero, l.complemento, l.endereco.bairro.descricao, l.endereco.cidade.descricao, l.endereco.cidade.uf, l.endereco.cidade.pais.descricao, l.codificado, l.tipoLogradouro from LogradouroEndereco l where l.id =:idlog "
		 * , Object[].class).setParameter("idlog", o[0]).getResultList();
		 * 
		 * for (Object[] log : logs) { entityManager .createNativeQuery(
		 * "update vendas.tb_logradouro_cliente set cep=:cep, endereco=:endereco, numero=:numero, complemento=:complemento, bairro=:bairro, cidade=:cidade, uf=:uf, pais=:pais, codificado=:codificado, id_tipo_logradouro=:tipo where id=:id"
		 * ) .setParameter("cep", log[0]).setParameter("endereco",
		 * log[1]).setParameter("numero", log[2]) .setParameter("complemento",
		 * log[3]).setParameter("bairro", log[4]) .setParameter("cidade",
		 * log[5]).setParameter("uf", log[6]).setParameter("pais", log[7])
		 * .setParameter("codificado", log[8]).setParameter("tipo",
		 * ((TipoLogradouro) log[9]).getCodigo()) .setParameter("id",
		 * o[0]).executeUpdate(); } }
		 * 
		 * ids =
		 * entityManager.createQuery("select p.id, p.cliente.id from Pedido p "
		 * ).getResultList(); for (Object[] o : ids) { logs = entityManager
		 * .createQuery(
		 * "select l.endereco.cep, l.endereco, l.numero, l.complemento, l.bairro, l.cidade, l.uf, l.pais, l.codificado, l.tipoLogradouro from LogradouroCliente l where l.cliente.id =:idCliente"
		 * , Object[].class).setParameter("idCliente", o[1]).getResultList();
		 * 
		 * for (Object[] log : logs) { entityManager .createNativeQuery(
		 * "insert into  vendas.tb_logradouro_pedido (id, id_pedido, cep, endereco, numero, complemento, bairro, cidade, uf, pais, codificado, id_tipo_logradouro) values (nextval('vendas.seq_logradouro_pedido_id'), :idPedido, :cep, :endereco, :numero , :complemento, :bairro, :cidade, :uf, :pais, :codificado, :tipo )"
		 * ) .setParameter("idPedido", o[0]).setParameter("cep",
		 * log[0]).setParameter("endereco", log[1]) .setParameter("numero",
		 * log[2]).setParameter("complemento", log[3]) .setParameter("bairro",
		 * log[4]).setParameter("cidade", log[5]).setParameter("uf", log[6])
		 * .setParameter("pais", log[7]).setParameter("codificado", log[8])
		 * .setParameter("tipo", ((TipoLogradouro)
		 * log[9]).getCodigo()).executeUpdate(); }
		 * 
		 * } ids = entityManager.createNativeQuery(
		 * "select id_logradouro, id from vendas.tb_representada r"
		 * ).getResultList(); for (Object[] o : ids) { logs = entityManager
		 * .createQuery(
		 * "select l.endereco.cep, l.endereco.descricao, l.numero, l.complemento, l.endereco.bairro.descricao, l.endereco.cidade.descricao, l.endereco.cidade.uf, l.endereco.cidade.pais.descricao, l.codificado, l.tipoLogradouro from LogradouroEndereco l where l.id =:idlog "
		 * , Object[].class).setParameter("idlog", o[0]).getResultList();
		 * 
		 * for (Object[] log : logs) { entityManager .createNativeQuery(
		 * "insert into  vendas.tb_logradouro_representada (id, cep, endereco, numero, complemento, bairro, cidade, uf, pais, codificado, id_tipo_logradouro) values (nextval('vendas.seq_logradouro_representada_id'), :cep, :endereco, :numero , :complemento, :bairro, :cidade, :uf, :pais, :codificado, :tipo )"
		 * ) .setParameter("cep", log[0]).setParameter("endereco",
		 * log[1]).setParameter("numero", log[2]) .setParameter("complemento",
		 * log[3]).setParameter("bairro", log[4]) .setParameter("cidade",
		 * log[5]).setParameter("uf", log[6]).setParameter("pais", log[7])
		 * .setParameter("codificado", log[8]).setParameter("tipo",
		 * ((TipoLogradouro) log[9]).getCodigo()) .executeUpdate();
		 * 
		 * entityManager .createNativeQuery(
		 * "update vendas.tb_representada set id_logradouro_representada = currval('vendas.seq_logradouro_representada_id') where id=:id_cliente"
		 * ) .setParameter("id_cliente", o[1]).executeUpdate(); } }
		 */
		ids = entityManager.createNativeQuery("select id_logradouro, id from vendas.tb_transportadora r")
				.getResultList();
		for (Object[] o : ids) {
			logs = entityManager
					.createQuery(
							"select l.endereco.cep, l.endereco.descricao, l.numero, l.complemento, l.endereco.bairro.descricao, l.endereco.cidade.descricao, l.endereco.cidade.uf, l.endereco.cidade.pais.descricao, l.codificado, l.tipoLogradouro from LogradouroEndereco l where l.id =:idlog ",
							Object[].class).setParameter("idlog", o[0]).getResultList();

			for (Object[] log : logs) {
				entityManager
						.createNativeQuery(
								"insert into  vendas.tb_logradouro_transportadora (id, cep, endereco, numero, complemento, bairro, cidade, uf, pais, codificado, id_tipo_logradouro) values (nextval('vendas.seq_logradouro_transportadora_id'), :cep, :endereco, :numero , :complemento, :bairro, :cidade, :uf, :pais, :codificado, :tipo )")
						.setParameter("cep", log[0]).setParameter("endereco", log[1]).setParameter("numero", log[2])
						.setParameter("complemento", log[3]).setParameter("bairro", log[4])
						.setParameter("cidade", log[5]).setParameter("uf", log[6]).setParameter("pais", log[7])
						.setParameter("codificado", log[8]).setParameter("tipo", ((TipoLogradouro) log[9]).getCodigo())
						.executeUpdate();

				entityManager
						.createNativeQuery(
								"update vendas.tb_transportadora set id_logradouro_transportadora = currval('vendas.seq_logradouro_transportadora_id') where id=:id_transportadora")
						.setParameter("id_transportadora", o[1]).executeUpdate();
			}
		}

		ids = entityManager.createNativeQuery("select id_logradouro, id from vendas.tb_contato r").getResultList();
		for (Object[] o : ids) {
			logs = entityManager
					.createQuery(
							"select l.endereco.cep, l.endereco.descricao, l.numero, l.complemento, l.endereco.bairro.descricao, l.endereco.cidade.descricao, l.endereco.cidade.uf, l.endereco.cidade.pais.descricao, l.codificado, l.tipoLogradouro from LogradouroEndereco l where l.id =:idlog ",
							Object[].class).setParameter("idlog", o[0]).getResultList();

			for (Object[] log : logs) {
				entityManager
						.createNativeQuery(
								"insert into  vendas.tb_logradouro_contato (id, cep, endereco, numero, complemento, bairro, cidade, uf, pais, codificado, id_tipo_logradouro) values (nextval('vendas.seq_logradouro_contato_id'), :cep, :endereco, :numero , :complemento, :bairro, :cidade, :uf, :pais, :codificado, :tipo )")
						.setParameter("cep", log[0]).setParameter("endereco", log[1]).setParameter("numero", log[2])
						.setParameter("complemento", log[3]).setParameter("bairro", log[4])
						.setParameter("cidade", log[5]).setParameter("uf", log[6]).setParameter("pais", log[7])
						.setParameter("codificado", log[8]).setParameter("tipo", ((TipoLogradouro) log[9]).getCodigo())
						.executeUpdate();

				entityManager
						.createNativeQuery(
								"update vendas.tb_contato set id_logradouro_contato = currval('vendas.seq_logradouro_representada_id') where id=:id_contato")
						.setParameter("id_contato", o[1]).executeUpdate();
			}
		}
		entityManager.createNativeQuery("ALTER TABLE vendas.tb_transportadora drop constraint id_logradouro")
				.executeUpdate();
		entityManager.createNativeQuery("ALTER TABLE vendas.tb_contato drop constraint id_logradouro").executeUpdate();
		entityManager.createNativeQuery("ALTER TABLE vendas.tb_transportadora constraint id_logradouro")
				.executeUpdate();
		entityManager.createNativeQuery("ALTER TABLE vendas.tb_contato drop id_logradouro").executeUpdate();
	}

	@PostConstruct
	public void init() {
		logradouroDAO = new LogradouroDAO(entityManager);
	}

	@Override
	public List<LogradouroCliente> inserir(List<LogradouroCliente> listaLogradouro) throws BusinessException {
		if (listaLogradouro == null) {
			return null;
		}

		if (listaLogradouro.isEmpty()) {
			return Collections.emptyList();
		}

		List<LogradouroCliente> lista = new ArrayList<>();
		for (LogradouroCliente logradouro : listaLogradouro) {
			lista.add(inserir(logradouro));
		}
		return lista;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T extends Logradouro> T inserir(T logradouro) throws BusinessException {
		if (logradouro == null) {
			return null;
		}
		return (T) logradouroDAO.alterar(logradouro);
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public void inserirEnderecoBaseCEP(Collection<Logradouro> lLogradouro) throws BusinessException {
		if (lLogradouro == null || lLogradouro.isEmpty()) {
			return;
		}

		// Vamos verificar os ceps ja existentes pois vamos inserir na tabela de
		// logradouro apenas os enderecos que nao existirem no sistema. Devemos
		// fazer esse filtro pois o usuario podera efetuar alteracoes no
		// endereco do cliente sem que reflita na tabela de logradouro, por
		// exemplo: atualizar o nome de rua de um determinado cep. A tabela de
		// logradouro eh utilizada apenas para consultar os ceps quando o
		// usuario efetuar uma pesquisa no campo de ceps.
		List<String> lCep = enderecamentoService.pesquisarCEPExistente(lLogradouro.stream().map(l -> l.getCep())
				.collect(Collectors.toSet()));

		if (!lCep.isEmpty()) {
			lLogradouro = lLogradouro.stream().filter(l -> !lCep.contains(l.getCep())).collect(Collectors.toList());
		}

		// Aqui estamos populando a tabela de CEP com os novos enderecos
		// inseridos pelo usuario
		for (Logradouro l : lLogradouro) {
			enderecamentoService.inserir(l.gerarEndereco());
		}
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public void inserirEnderecoBaseCEP(Logradouro logradouro) throws BusinessException {
		List<Logradouro> l = new ArrayList<Logradouro>();
		l.add(logradouro);
		inserirEnderecoBaseCEP(l);
	}

	@Override
	@SuppressWarnings("unchecked")
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public List<? extends LogradouroEndereco> pesquisar(Integer id, Class<? extends LogradouroEndereco> classe) {
		String nomeTipoLogradouro = classe.getSimpleName();
		StringBuilder select = new StringBuilder();
		select.append("select c from ").append(nomeTipoLogradouro).append(" c inner join c.")
				.append(nomeTipoLogradouro.replace("Logradouro", "").toLowerCase()).append(" l where l.id =:id");

		return entityManager.createQuery(select.toString()).setParameter("id", id).getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public <T extends LogradouroEndereco> List<T> pesquisarAusentes(Integer id, Collection<T> listaLogradouro,
			Class<T> classe) {
		String nomeTipoLogradouro = classe.getSimpleName();
		StringBuilder select = new StringBuilder();
		select.append("select l from ").append(nomeTipoLogradouro).append(" l where l.")
				.append(nomeTipoLogradouro.replace("Logradouro", "").toLowerCase()).append(".id =:id ");

		if (listaLogradouro != null && !listaLogradouro.isEmpty()) {
			select.append(" and l not in (:listaLogradouro) ");
		}

		final Query query = this.entityManager.createQuery(select.toString()).setParameter("id", id);

		if (listaLogradouro != null && !listaLogradouro.isEmpty()) {
			query.setParameter("listaLogradouro", listaLogradouro);
		}
		return query.getResultList();
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public String pesquisarCodigoIBGEByCEP(String cep) {
		if (cep == null) {
			return null;
		}
		return QueryUtil
				.gerarRegistroUnico(
						entityManager
								.createNativeQuery(
										"select c.cod_ibge from enderecamento.tb_cidade c inner join enderecamento.tb_endereco e on e.id_cidade = c.id_cidade and e.cep = :cep")
								.setParameter("cep", cep), String.class, null);
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public String pesquisarCodigoMunicipioByCep(String cep) {
		if (cep == null) {
			return null;
		}
		return logradouroDAO.pesquisarCodigoMunicipioByCep(cep);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * br.com.plastecno.service.LogradouroService#removerAusentes(java.lang.
	 * Integer, java.util.Collection, java.lang.Class)
	 */
	@Override
	public <T extends LogradouroEndereco> void removerAusentes(Integer id, Collection<T> listaLogradouro,
			Class<T> classe) {
		List<? extends LogradouroEndereco> listaLogradouroCadastrado = this.pesquisarAusentes(id, listaLogradouro,
				classe);
		for (LogradouroEndereco logradouro : listaLogradouroCadastrado) {
			this.entityManager.remove(logradouro);
		}

	}

	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public void removerLogradouro(Logradouro logradouro) {
		logradouroDAO.remover(logradouro);
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public void validarListaLogradouroPreenchida(List<LogradouroCliente> listaLogradouro) throws BusinessException {
		Set<TipoLogradouro> lLogAusente = new HashSet<TipoLogradouro>();
		lLogAusente.add(TipoLogradouro.COBRANCA);
		lLogAusente.add(TipoLogradouro.ENTREGA);
		lLogAusente.add(TipoLogradouro.FATURAMENTO);

		if (listaLogradouro != null && !listaLogradouro.isEmpty()) {
			listaLogradouro.forEach(t -> {
				lLogAusente.remove(t.getTipoLogradouro());
			});
		}

		if (lLogAusente.isEmpty()) {
			return;
		}

		List<String> listaMensagem = new ArrayList<String>();
		lLogAusente.forEach(t -> listaMensagem.add("� obrigatorio logradouro do tipo " + t));
		throw new InformacaoInvalidaException(listaMensagem);
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public void validarListaLogradouroPreenchidaXXX(List<LogradouroPedido> listaLogradouro) throws BusinessException {
		Set<TipoLogradouro> lLogAusente = new HashSet<TipoLogradouro>();
		lLogAusente.add(TipoLogradouro.COBRANCA);
		lLogAusente.add(TipoLogradouro.ENTREGA);
		lLogAusente.add(TipoLogradouro.FATURAMENTO);

		if (listaLogradouro != null && !listaLogradouro.isEmpty()) {
			listaLogradouro.forEach(t -> {
				lLogAusente.remove(t.getTipoLogradouro());
			});
		}

		if (lLogAusente.isEmpty()) {
			return;
		}

		List<String> listaMensagem = new ArrayList<String>();
		lLogAusente.forEach(t -> listaMensagem.add("� obrigatorio logradouro do tipo " + t));
		throw new InformacaoInvalidaException(listaMensagem);
	}

}
