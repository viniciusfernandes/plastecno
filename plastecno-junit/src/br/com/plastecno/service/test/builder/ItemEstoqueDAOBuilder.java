package br.com.plastecno.service.test.builder;

import java.util.ArrayList;
import java.util.List;

import mockit.Mock;
import mockit.MockUp;
import br.com.plastecno.service.constante.FormaMaterial;
import br.com.plastecno.service.dao.ItemEstoqueDAO;
import br.com.plastecno.service.entity.ItemEstoque;
import br.com.plastecno.service.exception.BusinessException;
import br.com.plastecno.util.StringUtils;

public class ItemEstoqueDAOBuilder extends DAOBuilder<ItemEstoqueDAO> {

	@Override
	public ItemEstoqueDAO build() {
		new MockUp<ItemEstoqueDAO>() {
			@Mock
			public void alterarPrecoMedioFatorICMS(List<ItemEstoque> listaItem) {
				// Esse metodo executa apenas um update dos dados em banco e como as
				// entidades estao em memoria, elas ja estao com os valores alterados.
			}

			@Mock
			public Double calcularValorEstoque(Integer idMaterial, FormaMaterial formaMaterial) {
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
						listaValores.add(new Double[] { i.getPrecoMedio(), (double) i.getQuantidade(), i.getAliquotaIPI() });
					}
				}

				double total = 0d;
				double val = 0;
				double quant = 0;
				double ipi = 0d;
				for (Double[] valor : listaValores) {
					val = (Double) valor[0];
					quant = (Double) valor[1];
					total += val * quant * (1 + ipi);
				}
				return total;
			}

			@Mock
			public void inserirConfiguracaoEstoque(ItemEstoque configuracao) throws BusinessException {
				List<ItemEstoque> lista = REPOSITORY.pesquisarTodos(ItemEstoque.class);

				boolean contemMedida = configuracao.contemMedida();
				boolean isIgual = false;

				for (ItemEstoque i : lista) {

					if (!configuracao.getFormaMaterial().equals(i.getFormaMaterial())
							|| !configuracao.getMaterial().getId().equals(i.getMaterial().getId())) {
						continue;
					}

					// Se nao contem medida isso indica que faremos atualizacao de todos
					// os itens cujo material e forma coincidem, mas no caso em que contem
					// medida, faremos atualizacao apenas dos itens que possuem medidas
					// iguais.

					isIgual = configuracao.isEqual(i);

					if (!contemMedida || isIgual) {
						i.copiar(configuracao);
					}
				}
			}

			@Mock
			public void inserirConfiguracaoNcmEstoque(Integer idMaterial, FormaMaterial formaMaterial, String ncm) {
				if (idMaterial == null || formaMaterial == null) {
					return;
				}
				List<ItemEstoque> l = REPOSITORY.pesquisarTodos(ItemEstoque.class);
				for (ItemEstoque i : l) {
					if (idMaterial.equals(i.getMaterial().getId()) && formaMaterial.equals(i.getFormaMaterial())) {
						i.setNcm(ncm);
					}
				}
			}

			@Mock
			public FormaMaterial pesquisarFormaMaterialItemEstoque(Integer idItemEstoque) {
				ItemEstoque i = REPOSITORY.pesquisarEntidadeById(ItemEstoque.class, idItemEstoque);
				return i == null ? null : i.getFormaMaterial();
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
			public ItemEstoque pesquisarItemEstoqueByMedida(Integer idMaterial, FormaMaterial formaMaterial,
					Double medidaExterna, Double medidaInterna, Double comprimento, boolean apenasID) {

				boolean conteMedida = medidaExterna != null || medidaInterna != null || comprimento != null;
				boolean conteMaterial = idMaterial != null || formaMaterial != null;

				// Pois sao parametros que todo item deve conter
				if (!conteMaterial || !conteMedida) {
					return null;
				}

				List<ItemEstoque> l = REPOSITORY.pesquisarTodos(ItemEstoque.class);
				for (ItemEstoque i : l) {
					if ((idMaterial != null && i.getMaterial() != null && !idMaterial.equals(i.getMaterial().getId()))
							|| (formaMaterial != null && !formaMaterial.equals(i.getFormaMaterial()))) {
						continue;
					}

					if (medidaExterna != null && !medidaExterna.equals(i.getMedidaExterna())) {
						continue;
					}

					if (medidaInterna != null && !medidaInterna.equals(i.getMedidaInterna())) {
						continue;
					}

					if (comprimento != null && !comprimento.equals(i.getComprimento())) {
						continue;
					}

					return i;
				}
				return null;
			}

			@Mock
			public List<ItemEstoque> pesquisarItemEstoqueEscasso() {
				List<ItemEstoque> estoque = REPOSITORY.pesquisarTodos(ItemEstoque.class);
				List<ItemEstoque> escassos = new ArrayList<ItemEstoque>();
				for (ItemEstoque i : estoque) {
					if (i.isItemEscasso()) {
						escassos.add(i);
					}
				}
				return escassos;
			}

			@Mock
			public Object[] pesquisarMargemMininaEPrecoMedio(Integer idItemEstoque) {
				List<ItemEstoque> l = REPOSITORY.pesquisarTodos(ItemEstoque.class);
				for (ItemEstoque i : l) {
					if (idItemEstoque.equals(i.getId())) {
						return new Object[] { i.getMargemMinimaLucro(), i.getPrecoMedioFatorICMS(), i.getPrecoMedio(),
								i.getAliquotaIPI() };
					}
				}

				return null;
			}

			@Mock
			public String pesquisarNcmItemEstoque(Integer idMaterial, FormaMaterial formaMaterial) {
				if (idMaterial == null || formaMaterial == null) {
					return null;
				}
				List<ItemEstoque> l = REPOSITORY.pesquisarTodos(ItemEstoque.class);
				for (ItemEstoque i : l) {
					if (idMaterial.equals(i.getMaterial().getId()) && formaMaterial.equals(i.getFormaMaterial())) {
						return i.getNcm();
					}
				}
				return null;
			}

			@Mock
			public ItemEstoque pesquisarPecaByDescricao(Integer idMaterial, String descricaoPeca, boolean apenasID) {
				if (StringUtils.isEmpty(descricaoPeca) || idMaterial == null) {
					return null;
				}
				List<ItemEstoque> l = REPOSITORY.pesquisarTodos(ItemEstoque.class);
				for (ItemEstoque i : l) {
					if (!idMaterial.equals(i.getMaterial().getId())) {
						continue;
					}

					if (!descricaoPeca.equals(i.getDescricaoPeca())) {
						continue;
					}
					return i;
				}
				return null;
			}

			@Mock
			public List<ItemEstoque> pesquisarPrecoMedioAliquotaICMSItemEstoque(Integer idItemEstoque, Integer idMaterial,
					FormaMaterial formaMaterial) {
				List<ItemEstoque> lista = new ArrayList<ItemEstoque>();
				if (idItemEstoque != null) {
					lista.add(REPOSITORY.pesquisarEntidadeById(ItemEstoque.class, idItemEstoque));
					return lista;
				}

				for (ItemEstoque i : lista) {
					if (idMaterial != null && formaMaterial != null && i.getMaterial() != null
							&& idMaterial.equals(i.getMaterial().getId()) && formaMaterial.equals(i.getFormaMaterial())) {
						lista.add(i);
					}
				}
				return lista;
			}

		};
		return new ItemEstoqueDAO(null);
	}
}
