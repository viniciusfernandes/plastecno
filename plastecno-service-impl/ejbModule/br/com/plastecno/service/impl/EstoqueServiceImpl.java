package br.com.plastecno.service.impl;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import br.com.plastecno.service.EstoqueService;
import br.com.plastecno.service.PedidoService;
import br.com.plastecno.service.constante.FormaMaterial;
import br.com.plastecno.service.constante.SituacaoPedido;
import br.com.plastecno.service.dao.ItemEstoqueDAO;
import br.com.plastecno.service.entity.ItemEstoque;
import br.com.plastecno.service.entity.ItemPedido;
import br.com.plastecno.service.entity.Material;
import br.com.plastecno.service.entity.Pedido;
import br.com.plastecno.service.exception.BusinessException;

@Stateless
public class EstoqueServiceImpl implements EstoqueService {
	@EJB
	private PedidoService pedidoService;

	@PersistenceContext(name = "plastecno")
	private EntityManager entityManager;
	private ItemEstoqueDAO itemEstoqueDAO;

	private void calcularValorMedio(ItemEstoque itemEstoque, ItemPedido itemPedido) {
		if (itemEstoque.getId() == null) {
			itemEstoque.setPrecoMedio(itemPedido.getPrecoUnidade());
		} else {
			final double valorEstoque = itemEstoque.getQuantidade() * itemEstoque.getPrecoMedio();
			final double valorItem = itemPedido.getQuantidade() * itemPedido.getPrecoUnidade();
			final double quantidadeTotal = itemEstoque.getQuantidade() + itemPedido.getQuantidade();
			final double precoMedio = (valorEstoque + valorItem) / quantidadeTotal;

			itemEstoque.setPrecoMedio(precoMedio);
			itemEstoque.setQuantidade((int) quantidadeTotal);
		}
	}

	private ItemEstoque gerarItemEstoque(ItemPedido itemPedido) {

		ItemEstoque itemEstoque = new ItemEstoque();

		itemEstoque.setComprimento(itemPedido.getComprimento());
		itemEstoque.setDescricaoPeca(itemPedido.getDescricaoPeca());
		itemEstoque.setFormaMaterial(itemPedido.getFormaMaterial());
		itemEstoque.setMaterial(itemPedido.getMaterial());
		itemEstoque.setMedidaExterna(itemPedido.getMedidaExterna());
		itemEstoque.setMedidaInterna(itemPedido.getMedidaInterna());
		itemEstoque.setQuantidade(itemPedido.getQuantidade());
		return itemEstoque;
	}

	@PostConstruct
	public void init() {
		itemEstoqueDAO = new ItemEstoqueDAO(entityManager);
	}

	@Override
	public Integer inserirItemEstoque(ItemEstoque itemEstoque) throws BusinessException {
		return null;
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public void inserirItemPedido(Integer idItemPedido) throws BusinessException {
		ItemPedido itemPedido = pedidoService.pesquisarItemPedido(idItemPedido);
		if (itemPedido == null) {
			throw new BusinessException("O item de pedido No: " + idItemPedido + " n�o existe no sistema");
		}
		Pedido pedido = itemPedido.getPedido();

		long qtdePendente = pedidoService.pesquisarTotalItemPendente(pedido.getId());
		if (qtdePendente <= 1) {
			pedido.setSituacaoPedido(SituacaoPedido.COMPRA_RECEBIDA);
		}
		itemPedido.setRecebido(true);

		Integer idMaterial = itemPedido.getMaterial().getId();
		FormaMaterial formaMaterial = itemPedido.getFormaMaterial();
		Double medidaExt = itemPedido.getMedidaExterna();
		Double medidaInt = itemPedido.getMedidaInterna();
		Double comp = itemPedido.getComprimento();

		// Verificando se existe item equivalente no estoque, caso nao exista vamos
		// criar um novo.
		ItemEstoque itemEstoque = pesquisarItemEstoque(idMaterial, formaMaterial, medidaExt, medidaInt, comp);
		boolean isNovo = itemEstoque == null;
		if (isNovo) {
			itemEstoque = gerarItemEstoque(itemPedido);
		}
		calcularValorMedio(itemEstoque, itemPedido);

		if (isNovo) {
			itemEstoqueDAO.inserir(itemEstoque);
		} else {
			itemEstoqueDAO.alterar(itemEstoque);
		}
	}

	private boolean isEquivalente(Double val1, Double val2) {
		if (val1 == null && val2 == null) {
			return true;
		}

		if ((val1 != null && val2 == null) || (val1 == null && val2 != null)) {
			return false;
		}
		final double tolerancia = 0.001;
		return Math.abs(1 - val1 / val2) <= tolerancia;
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<ItemEstoque> pesquisarItemEstoque(Integer idMaterial, FormaMaterial formaMaterial) {
		StringBuilder select = new StringBuilder();
		select.append("select i from ItemEstoque i ");
		if (idMaterial != null || formaMaterial != null) {
			select.append("where ");
		}

		if (idMaterial != null) {
			select.append("i.material.id = :idMaterial ");
		}

		if (formaMaterial != null && idMaterial != null) {
			select.append("and i.formaMaterial = :formaMaterial ");
		} else if (formaMaterial != null) {
			select.append("i.formaMaterial = :formaMaterial ");
		}

		Query query = entityManager.createQuery(select.toString());
		if (idMaterial != null) {
			query.setParameter("idMaterial", idMaterial);
		}

		if (formaMaterial != null) {
			query.setParameter("formaMaterial", formaMaterial);
		}

		return query.getResultList();
	}

	@Override
	public ItemEstoque pesquisarItemEstoque(Integer idMaterial, FormaMaterial formaMaterial, Double medidaExterna,
			Double medidaInterna, Double comprimento) {
		List<ItemEstoque> listItem = pesquisarItemEstoque(idMaterial, formaMaterial);
		for (ItemEstoque item : listItem) {
			if (!isEquivalente(medidaExterna, item.getMedidaExterna())) {
				continue;
			}
			if (!isEquivalente(medidaInterna, item.getMedidaInterna())) {
				continue;
			}
			if (!isEquivalente(comprimento, item.getComprimento())) {
				continue;
			}
			return item;
		}
		return null;
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public ItemEstoque pesquisarItemEstoqueById(Integer idItemEstoque) {
		return itemEstoqueDAO.pesquisarById(idItemEstoque);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Material> pesquisarMateriaEstoque(String sigla) {
		Query query = this.entityManager
				.createQuery("select distinct new Material(m.id, m.sigla, m.descricao) from ItemEstoque i inner join i.material m where m.sigla like :sigla order by m.sigla ");
		query.setParameter("sigla", "%" + sigla + "%");
		return query.getResultList();
	}
}
