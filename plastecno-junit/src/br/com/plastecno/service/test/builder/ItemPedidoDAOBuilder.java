package br.com.plastecno.service.test.builder;

import java.util.ArrayList;
import java.util.List;

import mockit.Mock;
import mockit.MockUp;
import br.com.plastecno.service.dao.ItemPedidoDAO;
import br.com.plastecno.service.entity.ItemPedido;

public class ItemPedidoDAOBuilder extends DAOBuilder<ItemPedidoDAO> {

	@Override
	public ItemPedidoDAO build() {
		new MockUp<ItemPedidoDAO>() {
			@Mock
			public Integer inserirNcmItemAguardandoMaterialAssociadoItemCompra(Integer idItemPedidoCompra, String ncm) {
				ItemPedido iCompra = REPOSITORY.pesquisarEntidadeById(ItemPedido.class, idItemPedidoCompra);
				List<ItemPedido> lVenda = REPOSITORY.pesquisarEntidadeByAtributo(ItemPedido.class, "idPedidoCompra",
						iCompra.getPedido().getId());
				for (ItemPedido i : lVenda) {
					if (i.getMaterial().getId().equals(iCompra.getMaterial().getId())
							&& i.getFormaMaterial().equals(iCompra.getFormaMaterial())) {
						i.setNcm(ncm);
					}
				}
				return -1;
			}

			@Mock
			public double pesquisarAliquotaIPIByIdItemPedido(Integer idItemPedido) {
				ItemPedido i = REPOSITORY.pesquisarEntidadeById(ItemPedido.class, idItemPedido);
				if (i == null) {
					return 0;
				}
				return i.getAliquotaIPI();
			}

			@Mock
			public Object[] pesquisarIdMaterialFormaMaterialItemPedido(Integer idItemPedido) {
				ItemPedido i = REPOSITORY.pesquisarEntidadeById(ItemPedido.class, idItemPedido);
				if (i == null) {
					return new Object[] {};
				}
				return new Object[] { i.getMaterial().getId(), i.getFormaMaterial() };
			}

			@Mock
			public List<Integer[]> pesquisarQuantidadeItemPedidoByIdPedido(Integer idPedido) {
				if (idPedido == null) {
					return new ArrayList<Integer[]>();
				}
				List<ItemPedido> lItem = REPOSITORY.pesquisarTodos(ItemPedido.class);
				List<Integer[]> l = new ArrayList<Integer[]>();

				for (ItemPedido i : lItem) {
					if (!idPedido.equals(i.getPedido().getId())) {
						continue;
					}
					l.add(new Integer[] { i.getId(), i.getQuantidade(), i.getSequencial() });
				}

				return l;
			}

			@Mock
			public Integer pesquisarQuantidadeRecepcionadaItemPedido(Integer idItemPedido) {
				ItemPedido i = REPOSITORY.pesquisarEntidadeById(ItemPedido.class, idItemPedido);
				return i != null ? i.getQuantidadeRecepcionada() : null;
			}

			@Mock
			public Double[] pesquisarValorPedidoByItemPedido(Integer idItemPedido) {
				ItemPedido itemPedido = REPOSITORY.pesquisarEntidadeById(ItemPedido.class, idItemPedido);
				if (itemPedido == null || itemPedido.getPedido() == null) {
					return new Double[] { 0d, 0d };
				}

				return new Double[] { itemPedido.getPedido().getValorPedido(),
						itemPedido.getPedido().getValorPedidoIPI() };
			}
		};

		return new ItemPedidoDAO(null);
	}

}
