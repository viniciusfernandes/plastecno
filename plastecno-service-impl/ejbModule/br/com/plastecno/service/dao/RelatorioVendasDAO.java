package br.com.plastecno.service.dao;

import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import br.com.plastecno.service.constante.SituacaoPedido;

public class RelatorioVendasDAO extends GenericDAO<Object> {
	
	public RelatorioVendasDAO(EntityManager entityManager) {
		super(entityManager);
	}

	@SuppressWarnings("unchecked")
	public List<Object[]> pesquisarVendas(Date dataInicio, Date dataFim) {
		StringBuilder select = new StringBuilder();
		select.append("select v.nome, r.nomeFantasia, sum(p.valorPedido) from Pedido p ");
		select.append("inner join p.representada r ");
		select.append("inner join p.proprietario v ");
		select.append("where p.dataEnvio >= :dataInicio and p.dataEnvio <= :dataFim ");
		select.append("and p.situacaoPedido = :situacaoEnviado ");
		select.append("group by v.nome, r.nomeFantasia ");
		select.append("order by v.nome ");
		
		Query query = this.entityManager.createQuery(select.toString());
		query.setParameter("dataInicio", dataInicio);
		query.setParameter("dataFim", dataFim);
		query.setParameter("situacaoEnviado", SituacaoPedido.ENVIADO);
		
		return query.getResultList();
	}
	
	@SuppressWarnings("unchecked")
	public List<Object[]> pesquisarVendas(Integer anoReferencia) {
		StringBuilder select = new StringBuilder();
		select.append("select r.nome_fantasia as representada , p.data_envio, sum(p.valor_pedido) as total_vendido from tb_pedido p ");
		select.append("inner join tb_representada r on r.id = p.id_representada ");
		select.append("group by date_trunc( 'month', p.data_envio), p.data_envio, r.nome_fantasia ");
		select.append("order by r.nome_fantasia , p.data_envio ");

		Query query = this.entityManager.createNativeQuery(select.toString());
		return query.getResultList();
	}
	
}
