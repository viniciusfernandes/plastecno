package br.com.plastecno.service.test.builder;

import java.util.ArrayList;
import java.util.List;

import mockit.Mock;
import mockit.MockUp;
import br.com.plastecno.service.constante.FormaMaterial;
import br.com.plastecno.service.dao.ItemEstoqueDAO;
import br.com.plastecno.service.entity.ItemEstoque;

public class ItemEstoqueDAOBuilder extends DAOBuilder<ItemEstoqueDAO> {

	@Override
	public ItemEstoqueDAO build() {
		new MockUp<ItemEstoqueDAO>() {
			@Mock
			public List<ItemEstoque> pesquisarEscassezItemEstoque() {
				List<ItemEstoque> lista = REPOSITORY.pesquisarTodos(ItemEstoque.class);
				List<ItemEstoque> listaItem = new ArrayList<ItemEstoque>();
				for (ItemEstoque i : lista) {
					if (i.isItemEscasso()) {
						listaItem.add(i);
					}
				}
				return listaItem;
			}

			@Mock
			public List<ItemEstoque> pesquisarItemEstoque(Integer idMaterial, FormaMaterial formaMaterial,
					String descricaoPeca) {
				List<ItemEstoque> lista = REPOSITORY.pesquisarEntidadeByRelacionamento(ItemEstoque.class, "formaMaterial",
						formaMaterial);
				List<ItemEstoque> itens = new ArrayList<ItemEstoque>();
				boolean isMaterialSelecionado = false;
				boolean isPecaSelecionada = false;
				for (ItemEstoque item : lista) {
					// A primeira condicao indica que se deseja todas as formas de
					// materiais.
					isMaterialSelecionado = idMaterial == null
							|| (item.getMaterial() != null && idMaterial.equals(item.getMaterial().getId()));
					if (!item.isPeca() && isMaterialSelecionado) {
						itens.add(item);
						continue;
					}

					isPecaSelecionada = item.isPeca() && descricaoPeca != null && descricaoPeca.equals(item.getDescricaoPeca());
					if (isMaterialSelecionado && isPecaSelecionada) {
						itens.add(item);
						continue;
					}
				}
				return itens;
			}

			@Mock
			Double pesquisarValorEQuantidadeItemEstoque(Integer idMaterial, FormaMaterial formaMaterial) {
				List<ItemEstoque> l = REPOSITORY.pesquisarTodos(ItemEstoque.class);
				List<Double[]> listaValores = new ArrayList<Double[]>();
				boolean isAmbosNulos = false;
				boolean isMaterialIgual = false;
				boolean isFormaIgual = false;
				for (ItemEstoque i : l) {
					isAmbosNulos = idMaterial == null && formaMaterial == null;
					isMaterialIgual = idMaterial != null && idMaterial.equals(i.getMaterial().getId());
					isFormaIgual = formaMaterial != null && formaMaterial.equals(i.getFormaMaterial());
					if (isAmbosNulos || isMaterialIgual || isFormaIgual) {
						listaValores.add(new Double[] { i.getPrecoUnidade(), (double) i.getQuantidade() });
					}
				}

				double total = 0d;
				double val = 0;
				double quant = 0;
				for (Double[] valor : listaValores) {
					val = (Double) valor[0];
					quant = (Double) valor[1];
					total += val * quant;
				}
				return total;
			}

		};
		return new ItemEstoqueDAO(null);
	}

}
