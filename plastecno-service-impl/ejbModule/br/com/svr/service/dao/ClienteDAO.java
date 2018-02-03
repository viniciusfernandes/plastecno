package br.com.svr.service.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import br.com.svr.service.constante.TipoCliente;
import br.com.svr.service.constante.TipoLogradouro;
import br.com.svr.service.entity.Cliente;
import br.com.svr.service.entity.ContatoCliente;
import br.com.svr.service.entity.LogradouroCliente;
import br.com.svr.service.impl.util.QueryUtil;

public class ClienteDAO extends GenericDAO<Cliente> {

	public ClienteDAO(EntityManager entityManager) {
		super(entityManager);
	}

	public void alterarTipoCliente(Integer idCliente, TipoCliente tipoCliente) {
		super.alterarPropriedade(Cliente.class, idCliente, "tipoCliente", tipoCliente);
	}

	public boolean isEmailExistente(Integer idCliente, String email) {
		Query query = null;
		if (idCliente == null) {
			query = this.entityManager.createQuery("select count(r.id) from Cliente r where r.email = :email ");
			query.setParameter("email", email);
		} else {
			query = this.entityManager
					.createQuery("select count(r.id) from Cliente  r where r.id != :id AND r.email = :email ");
			query.setParameter("email", email);
			query.setParameter("id", idCliente);
		}

		return QueryUtil.gerarRegistroUnico(query, Long.class, 0L) > 1;
	}

	public boolean isRevendedorExistente(Integer id) {
		return isEntidadeExistente(Cliente.class, id, "tipoCliente", TipoCliente.REVENDEDOR);
	}

	public Cliente pesquisarById(Integer id) {
		return super.pesquisarById(Cliente.class, id);
	}

	@SuppressWarnings("unchecked")
	public List<Cliente> pesquisarByNomeFantasia(String nomeFantasia) {
		StringBuilder select = new StringBuilder().append("select c from Cliente c ").append(
				"where c.nomeFantasia like :nomeFantasia");
		return this.entityManager.createQuery(select.toString()).setParameter("nomeFantasia", nomeFantasia)
				.getResultList();

	}

	public Cliente pesquisarClienteResumidoByCnpj(String cnpj) {
		return QueryUtil
				.gerarRegistroUnico(
						entityManager
								.createQuery(
										"select new Cliente(c.id, c.nomeFantasia, c.razaoSocial, c.cnpj, c.cpf, c.inscricaoEstadual, c.email) from Cliente c where c.cnpj = :cnpj")
								.setParameter("cnpj", cnpj), Cliente.class, null);
	}

	public Cliente pesquisarClienteResumidoById(Integer idCliente) {
		return QueryUtil
				.gerarRegistroUnico(
						entityManager
								.createQuery(
										"select new Cliente(c.id, c.nomeFantasia, c.razaoSocial) from Cliente c where c.id = :idCliente")
								.setParameter("idCliente", idCliente), Cliente.class, null);
	}

	public Cliente pesquisarClienteResumidoDocumentoById(Integer idCliente) {
		return QueryUtil
				.gerarRegistroUnico(
						entityManager
								.createQuery(
										"select new Cliente(c.id, c.nomeFantasia, c.razaoSocial, c.cnpj, c.cpf, c.inscricaoEstadual, c.email) from Cliente c where c.id = :idCliente")
								.setParameter("idCliente", idCliente), Cliente.class, null);
	}

	public ContatoCliente pesquisarContatoByIdContato(Integer idContato) {
		return QueryUtil
				.gerarRegistroUnico(
						entityManager
								.createQuery(
										"select new ContatoCliente (cc.ddd, cc.dddSecundario, cc.ddi, cc.departamento, cc.email, cc.fax, cc.cliente.id, cc.nome, cc.ramal, cc.telefone, cc.telefoneSecundario) from ContatoCliente cc where cc.id = :idContato")
								.setParameter("idContato", idContato), ContatoCliente.class, null);
	}

	public ContatoCliente pesquisarContatoPrincipalResumidoByIdCliente(Integer idCliente) {
		return QueryUtil
				.gerarRegistroUnico(
						entityManager
								.createQuery(
										"select new ContatoCliente(c.ddd, c.ddi, c.email, c.nome, c.telefone) from ContatoCliente c where  c.id = (select max(c1.id) from ContatoCliente c1 where c1.cliente.id = :idCliente )")
								.setParameter("idCliente", idCliente), ContatoCliente.class, null);
	}

	public List<ContatoCliente> pesquisarContatoResumidoByNomeFantasia(Integer idCliente, String nome) {
		return entityManager
				.createQuery(
						"select new ContatoCliente (cc.id, cc.nome) from ContatoCliente cc where cc.cliente.id = :idCliente and cc.nome like :nome ",
						ContatoCliente.class).setParameter("idCliente", idCliente)
				.setParameter("nome", "%" + nome + "%").getResultList();
	}

	public Integer pesquisarIdVendedorByIdCliente(Integer idCliente) {
		return QueryUtil.gerarRegistroUnico(
				entityManager.createQuery("select c.vendedor.id from Cliente c where c.id =:id ").setParameter("id",
						idCliente), Integer.class, null);
	}

	@SuppressWarnings("unchecked")
	public List<LogradouroCliente> pesquisarLogradouroById(Integer idCliente) {
		return entityManager.createQuery("select l from LogradouroCliente l where l.cliente.id = :idCliente")
				.setParameter("idCliente", idCliente).getResultList();
	}

	public List<LogradouroCliente> pesquisarLogradouroFaturamentoById(Integer idCliente) {
		StringBuilder select = new StringBuilder().append("select l from Cliente c ").append(
				"inner join c.listaLogradouro l where c.id = :idCliente and l.tipoLogradouro = :tipoLogradouro ");
		return entityManager.createQuery(select.toString(), LogradouroCliente.class)
				.setParameter("idCliente", idCliente).setParameter("tipoLogradouro", TipoLogradouro.FATURAMENTO)
				.getResultList();
	}

	public String pesquisarNomeFantasia(Integer idCliente) {
		return super.pesquisarCampoById(Cliente.class, idCliente, "nomeFantasia", String.class);
	}

	public Cliente pesquisarRevendedor() {
		return QueryUtil
				.gerarRegistroUnico(
						entityManager
								.createQuery(
										"select new Cliente(c.id, c.nomeFantasia, c.razaoSocial) from Cliente c where c.tipoCliente = :tipoCliente")
								.setParameter("tipoCliente", TipoCliente.REVENDEDOR), Cliente.class, null);
	}
}
