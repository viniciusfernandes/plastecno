package br.com.plastecno.service.test.builder;

import java.util.ArrayList;
import java.util.List;

import mockit.Mock;
import mockit.MockUp;
import br.com.plastecno.service.dao.LimiteMinimoEstoqueDAO;
import br.com.plastecno.service.entity.ItemEstoque;
import br.com.plastecno.service.entity.LimiteMinimoEstoque;

public class LimiteMinimoEstoqueDAOBuilder extends
		DAOBuilder<LimiteMinimoEstoqueDAO> {

	@Override
	public LimiteMinimoEstoqueDAO build() {
		new MockUp<LimiteMinimoEstoqueDAO>() {

			@Mock
			public void associarLimiteMinimoItemEstoque(Integer idLimiteMinimo,
					List<Integer> listaIdItemEstoque) {
				LimiteMinimoEstoque limite = REPOSITORY.pesquisarEntidadeById(
						LimiteMinimoEstoque.class, idLimiteMinimo);
				if (limite == null) {
					return;
				}

				ItemEstoque i = null;
				for (Integer idItemEstoque : listaIdItemEstoque) {
					i = REPOSITORY.pesquisarEntidadeById(ItemEstoque.class,
							idItemEstoque);
					if (i != null) {
						limite.addItemEstoque(i);

					}
				}
			}

			@Mock
			public List<Integer> pesquisarIdItemEstoqueDentroLimiteMinimo(
					LimiteMinimoEstoque limite, double tolerancia) {
				List<ItemEstoque> listaItem = REPOSITORY
						.pesquisarTodos(ItemEstoque.class);
				List<Integer> listaId = new ArrayList<Integer>();
				Double medidaExterna = limite.getMedidaExterna();
				Double medidaInterna = limite.getMedidaInterna();
				Double comprimento = limite.getComprimento();
				double diferenca = 0;
				final boolean contemMedida = medidaExterna != null
						|| medidaInterna != null || comprimento != null;
				for (ItemEstoque i : listaItem) {
					if (!contemMedida) {
						listaId.add(i.getId());
						continue;
					}

					if (medidaExterna != null) {
						diferenca = medidaExterna
								- (i.getMedidaExterna() == null ? 0 : i
										.getMedidaExterna());
						if (Math.abs(diferenca) > tolerancia) {
							continue;
						}
					}

					if (medidaInterna != null) {
						diferenca = medidaInterna
								- (i.getMedidaInterna() == null ? 0 : i
										.getMedidaInterna());
						if (Math.abs(diferenca) > tolerancia) {
							continue;
						}
					}

					if (comprimento != null) {
						diferenca = comprimento
								- (i.getComprimento() == null ? 0 : i
										.getComprimento());
						if (Math.abs(diferenca) > tolerancia) {
							continue;
						}
					}
					listaId.add(i.getId());
				}
				return listaId;
			}

			@Mock
			public Integer pesquisarIdLimiteMinimoEstoque(
					LimiteMinimoEstoque filtro) {
				List<LimiteMinimoEstoque> listalimite = REPOSITORY
						.pesquisarTodos(LimiteMinimoEstoque.class);
				boolean contemMedida = false;
				for (LimiteMinimoEstoque limite : listalimite) {
					if (!limite.getFormaMaterial().equals(
							filtro.getFormaMaterial())) {
						continue;
					}

					if (limite.getMaterial().getId()
							.equals(filtro.getMaterial().getId())) {
						continue;
					}

					contemMedida = limite.getMedidaExterna() != null
							&& filtro.getMedidaExterna() != null;
					if (contemMedida
							&& limite.getMedidaExterna().equals(
									filtro.getMedidaExterna())) {
						continue;
					}

					contemMedida = limite.getMedidaInterna() != null
							&& filtro.getMedidaInterna() != null;
					if (contemMedida
							&& limite.getMedidaInterna().equals(
									filtro.getMedidaInterna())) {
						continue;
					}

					contemMedida = limite.getComprimento() != null
							&& filtro.getComprimento() != null;
					if (contemMedida
							&& limite.getComprimento().equals(
									filtro.getComprimento())) {
						continue;
					}

					return limite.getId();
				}
				return null;
			}
		};
		return new LimiteMinimoEstoqueDAO(null);
	}

}
