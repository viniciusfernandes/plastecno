package br.com.plastecno.service.dao;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import br.com.plastecno.service.entity.NFePedido;
import br.com.plastecno.service.impl.util.QueryUtil;

public class NFePedidoDAO extends GenericDAO<NFePedido> {
	public NFePedidoDAO(EntityManager entityManager) {
		super(entityManager);
	}

	public void inserirNFePedido(NFePedido p) {
		StringBuilder s = new StringBuilder();
		if (isEntidadeExistente(NFePedido.class, "numero", p.getNumero())) {
			s.append("update NFePedido p set p.serie = :serie, p.modelo = :modelo, p.xmlNFe = :xmlNFe, p.idPedido = :idPedido ");

			if (p.getNumeroTriangularizado() != null) {
				s.append(", p.numeroTriangularizado = :numeroTriangularizado ");
			}

			s.append("where p.numero =:numero ");

			Query q = entityManager.createQuery(s.toString()).setParameter("numero", p.getNumero())
					.setParameter("serie", p.getSerie()).setParameter("modelo", p.getModelo())
					.setParameter("xmlNFe", p.getXmlNFe()).setParameter("idPedido", p.getIdPedido());

			if (p.getNumeroTriangularizado() != null) {
				q.setParameter("numeroTriangularizado", p.getNumeroTriangularizado());
			}

			q.executeUpdate();

		} else {
			super.inserir(p);
		}
	}

	public Integer pesquisarIdPedidoByNumeroNFe(Integer numeroNFe) {
		return QueryUtil.gerarRegistroUnico(
				entityManager.createQuery("select p.idPedido from NFePedido p where p.numero = :numero").setParameter(
						"numero", numeroNFe), Integer.class, null);
	}

	public Integer pesquisarNumeroNFe(Integer idPedido, boolean isTriangulacao) {
		StringBuilder s = new StringBuilder();
		s.append("select p.numero from NFePedido p where p.idPedido = :idPedido and ");
		if (isTriangulacao) {
			s.append("numeroTriagularizado != null");
		} else {
			s.append("numeroTriagularizado == null");
		}
		return QueryUtil.gerarRegistroUnico(entityManager.createQuery(s.toString()).setParameter("idPedido", idPedido),
				Integer.class, null);
	}

	public String pesquisarXMLNFeByIdPedido(Integer idPedido) {
		// Como um pedido pode ter varias notas, pois os itens podem ser
		// emitidos em fracao, entao vamos retornar a nfe com o menor numero que
		// nao tenha sido a triangularizacao
		return QueryUtil
				.gerarRegistroUnico(
						entityManager
								.createQuery(
										"select p.xmlNFe from NFePedido p where p.numero = (select min(p2.numero) from NFePedido p2 where p2.idPedido =:idPedido and p2.numeroTriangularizado = null)")
								.setParameter("idPedido", idPedido), String.class, null);
	}

	public String pesquisarXMLNFeByNumero(Integer numero) {
		return QueryUtil.gerarRegistroUnico(
				entityManager.createQuery("select p.xmlNFe from NFePedido p where p.numero = :numero").setParameter(
						"numero", numero), String.class, null);
	}
}
