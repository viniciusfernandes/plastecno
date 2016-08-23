package br.com.plastecno.service.dao;

import javax.persistence.EntityManager;

import br.com.plastecno.service.entity.PedidoNFe;
import br.com.plastecno.service.impl.util.QueryUtil;

public class PedidoNFeDAO extends GenericDAO<PedidoNFe> {
	public PedidoNFeDAO(EntityManager entityManager) {
		super(entityManager);
	}

	public String pesquisarXMLNFeByIdPedido(Integer idPedido) {
		return QueryUtil.gerarRegistroUnico(
				entityManager.createQuery("select p.xmlNFe from PedidoNFe p where p.idPedido = :idPedido")
						.setParameter("idPedido", idPedido), String.class, null);
	}
}
