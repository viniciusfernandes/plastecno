package br.com.plastecno.service.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import br.com.plastecno.service.entity.Cliente;
import br.com.plastecno.service.entity.Pedido;
import br.com.plastecno.service.impl.util.QueryUtil;
import br.com.plastecno.util.StringUtils;

public class PedidoDAO extends GenericDAO {

    public PedidoDAO(EntityManager entityManager) {
        super(entityManager);
    }

    public Pedido pesquisarById(Integer idPedido) {
        StringBuilder select = new StringBuilder();
        select.append("select p from Pedido p ");
        select.append("join fetch p.vendedor ");
        select.append("left join fetch p.transportadora ");
        select.append("left join fetch p.transportadoraRedespacho ");
        select.append("join fetch p.representada ");
        select.append("join fetch p.contato ");
        select.append("where p.id = :idPedido");
        
        Query query = this.entityManager.createQuery(select.toString());
        query.setParameter("idPedido", idPedido);
        return QueryUtil.gerarRegistroUnico(query, Pedido.class, null);
    }

    public List<Pedido> pesquisarByIdClienteByIdVendedor(Integer idCliente, Integer idVendedor,
            Integer indiceRegistroInicial, Integer numeroMaximoRegistros) {
        StringBuilder select = new StringBuilder(
                "select p from Pedido p left join fetch p.vendedor where p.cliente.id = :idCliente ");
        if (idVendedor != null) {
            select.append(" and p.vendedor.id = :idVendedor ");
        }

        select.append(" order by p.dataInclusao desc, p.cliente.nomeFantasia ");

        Query query = this.entityManager.createQuery(select.toString());
        query.setParameter("idCliente", idCliente);
        if (idVendedor != null) {
            query.setParameter("idVendedor", idVendedor);
        }

        return QueryUtil.paginar(query, indiceRegistroInicial, numeroMaximoRegistros);
    }

    public List<Pedido> pesquisarBy(Pedido filtro, Integer indiceRegistroInicial, Integer numeroMaximoRegistros) {
        StringBuilder select = null;
        select = new StringBuilder("select p from Pedido p ");

        this.gerarRestricaoPesquisa(filtro, select);
        Query query = this.gerarQueryPesquisa(filtro, select);

        return QueryUtil.paginar(query, indiceRegistroInicial, numeroMaximoRegistros);
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
}
