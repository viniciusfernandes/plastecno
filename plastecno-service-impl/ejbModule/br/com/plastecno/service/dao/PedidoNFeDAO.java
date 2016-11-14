package br.com.plastecno.service.dao;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import br.com.plastecno.service.entity.PedidoNFe;
import br.com.plastecno.service.impl.util.QueryUtil;

public class PedidoNFeDAO extends GenericDAO<PedidoNFe> {
	public PedidoNFeDAO(EntityManager entityManager) {
		super(entityManager);
	}

	public void inserirPedidoNFe(PedidoNFe p) {
		StringBuilder s = new StringBuilder();
		if (isEntidadeExistente(PedidoNFe.class, "idPedido", p.getIdPedido())) {
			s.append("update PedidoNFe p set p.serie = :serie, p.modelo = :modelo ");
			if (p.getNumero() != null) {
				s.append(", p.numero = :numero ");
			}

			if (p.getXmlNFe() != null) {
				s.append(", p.xmlNFe = :xmlNFe ");
			}

			if (p.getNumeroTriangulacao() != null) {
				s.append(", p.numeroTriangulacao = :numeroTriangulacao ");
			}

			if (p.getXmlNFeTriangulacao() != null) {
				s.append(", p.xmlNFeTriangulacao = :xmlNFeTriangulacao ");
			}

			s.append("where p.idPedido =:idPedido");

			Query q = entityManager.createQuery(s.toString()).setParameter("serie", p.getSerie())
					.setParameter("modelo", p.getModelo()).setParameter("idPedido", p.getIdPedido());

			if (p.getNumero() != null) {
				q.setParameter("numero", p.getNumero());
			}

			if (p.getXmlNFe() != null) {
				q.setParameter("xmlNFe", p.getXmlNFe());
			}

			if (p.getNumeroTriangulacao() != null) {
				q.setParameter("numeroTriangulacao", p.getNumeroTriangulacao());
			}

			if (p.getXmlNFeTriangulacao() != null) {
				q.setParameter("xmlNFeTriangulacao", p.getXmlNFeTriangulacao());
			}

			q.executeUpdate();

		} else {
			super.inserir(p);
		}
	}

	public Integer pesquisarIdPedidoByNumeroNFe(Integer numeroNFe, boolean isTriangulacao) {
		StringBuilder s = new StringBuilder("select p.idPedido from PedidoNFe p where ");
		if (isTriangulacao) {
			s.append("p.numeroTriangulacao = :numeroTriangulacao");
		} else {
			s.append("p.numero = :numero");
		}
		Query q = entityManager.createQuery(s.toString());
		if (isTriangulacao) {
			q.setParameter("numeroTriangulacao", numeroNFe);
		} else {
			q.setParameter("numero", numeroNFe);
		}
		return QueryUtil.gerarRegistroUnico(q, Integer.class, null);
	}

	public Integer pesquisarNumeroNFe(Integer idPedido, boolean isTriangulacao) {
		StringBuilder s = new StringBuilder();
		if (isTriangulacao) {
			s.append("select p.numeroTriangulacao ");
		} else {
			s.append("select p.numero ");
		}
		s.append("from PedidoNFe p where p.idPedido = :idPedido ");
		return QueryUtil.gerarRegistroUnico(entityManager.createQuery(s.toString()).setParameter("idPedido", idPedido),
				Integer.class, null);
	}

	public String pesquisarXMLNFeByIdPedido(Integer idPedido, boolean isTriangulacao) {
		StringBuilder s = new StringBuilder();
		if (isTriangulacao) {
			s.append("select p.xmlNFeTriangulacao ");
		} else {
			s.append("select p.xmlNFe ");
		}
		s.append("from PedidoNFe p where p.idPedido = :idPedido ");
		return QueryUtil.gerarRegistroUnico(entityManager.createQuery(s.toString()).setParameter("idPedido", idPedido),
				String.class, null);
	}
}
