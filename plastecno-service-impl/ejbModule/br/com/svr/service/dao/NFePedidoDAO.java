package br.com.svr.service.dao;

import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import br.com.svr.service.constante.TipoFinalidadePedido;
import br.com.svr.service.entity.NFePedido;
import br.com.svr.service.impl.util.QueryUtil;
import br.com.svr.service.nfe.constante.TipoNFe;
import br.com.svr.service.nfe.constante.TipoSituacaoNFe;

public class NFePedidoDAO extends GenericDAO<NFePedido> {

	public NFePedidoDAO(EntityManager entityManager) {
		super(entityManager);
	}

	public void inserirNFePedido(NFePedido p) {
		StringBuilder s = new StringBuilder();
		if (isEntidadeExistente(NFePedido.class, "numero", p.getNumero())) {
			s.append("update NFePedido p set p.serie = :serie, p.modelo = :modelo, p.xmlNFe = :xmlNFe, p.pedido.id = :idPedido, p.valor=:valor, p.valorICMS = :valorICMS ");

			if (p.getNumeroAssociado() != null) {
				s.append(", p.numeroAssociado = :numeroAssociado ");
			}

			s.append("where p.numero =:numero ");

			Query q = entityManager.createQuery(s.toString()).setParameter("numero", p.getNumero())
					.setParameter("serie", p.getSerie()).setParameter("modelo", p.getModelo())
					.setParameter("xmlNFe", p.getXmlNFe()).setParameter("idPedido", p.getIdPedido())
					.setParameter("valor", p.getValor()).setParameter("valorICMS", p.getValorICMS());

			if (p.getNumeroAssociado() != null) {
				q.setParameter("numeroAssociado", p.getNumeroAssociado());
			}

			q.executeUpdate();

		} else {
			super.inserir(p);
		}
	}

	public Integer pesquisarIdPedidoByNumeroNFe(Integer numeroNFe) {
		return QueryUtil.gerarRegistroUnico(
				entityManager.createQuery("select p.pedido.id from NFePedido p where :numeroNFe = p.numero ")
						.setParameter("numeroNFe", numeroNFe), Integer.class, null);
	}

	public List<NFePedido> pesquisarNFePedidoByPeriodo(Date dataInicial, Date dataFinal, TipoNFe tipoNFe,
			TipoSituacaoNFe tipoSituacaoNFe, List<TipoFinalidadePedido> listaFinalidadePed) {
		StringBuilder s = new StringBuilder();
		s.append("select new NFePedido(n.dataEmissao, n.pedido.id, n.nomeCliente, n.numero, n.valor, n.valorICMS) from NFePedido n where n.tipoNFe=:tipoNFe and n.tipoSituacaoNFe=:tipoSituacaoNFe and n.dataEmissao >=:dataInicial and n.dataEmissao <=:dataFinal ");
		if (listaFinalidadePed != null && !listaFinalidadePed.isEmpty()) {
			s.append("and n.pedido.finalidadePedido in (:listaFinalidadePed)");
		}

		TypedQuery<NFePedido> q = entityManager.createQuery(s.toString(), NFePedido.class)
				.setParameter("tipoNFe", tipoNFe).setParameter("tipoSituacaoNFe", tipoSituacaoNFe)
				.setParameter("dataInicial", dataInicial).setParameter("dataFinal", dataFinal);
		if (listaFinalidadePed != null && !listaFinalidadePed.isEmpty()) {
			q.setParameter("listaFinalidadePed", listaFinalidadePed);
		}
		return q.getResultList();
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

	public Object[] pesquisarNumeroSerieModeloNFe() {
		return QueryUtil
				.gerarRegistroUnico(
						entityManager
								.createQuery("select p.numero, p.serie, p.modelo, (select max(p2.numeroAssociado) from NFePedido p2) from NFePedido p where p.numero = (select max(p1.numero) from NFePedido p1 ) "),
						Object[].class, null);
	}

	public String pesquisarXMLNFeByIdPedido(Integer idPedido) {
		// Como um pedido pode ter varias notas, pois os itens podem ser
		// emitidos em fracao, entao vamos retornar a nfe com o menor numero que
		// nao tenha sido a triangularizacao
		return QueryUtil
				.gerarRegistroUnico(
						entityManager
								.createQuery(
										"select p.xmlNFe from NFePedido p where p.numero = (select min(p2.numero) from NFePedido p2 where p2.idPedido =:idPedido and p2.numeroAssociado = null)")
								.setParameter("idPedido", idPedido), String.class, null);
	}

	public String pesquisarXMLNFeByNumero(Integer numero) {
		return QueryUtil.gerarRegistroUnico(
				entityManager.createQuery("select p.xmlNFe from NFePedido p where p.numero = :numero").setParameter(
						"numero", numero), String.class, null);
	}

	public void removerNFePedido(Integer numeroNFe) {
		entityManager.createQuery("delete NFePedido n where n.numero = :numeroNFe")
				.setParameter("numeroNFe", numeroNFe).executeUpdate();
	}
}
