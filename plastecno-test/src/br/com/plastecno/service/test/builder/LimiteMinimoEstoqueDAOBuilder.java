package br.com.plastecno.service.test.builder;

import java.util.ArrayList;
import java.util.List;

import mockit.Mock;
import mockit.MockUp;
import br.com.plastecno.service.dao.LimiteMinimoEstoqueDAO;
import br.com.plastecno.service.entity.ItemEstoque;
import br.com.plastecno.service.entity.LimiteMinimoEstoque;

public class LimiteMinimoEstoqueDAOBuilder extends DAOBuilder<LimiteMinimoEstoqueDAO> {

	@Override
	public LimiteMinimoEstoqueDAO build() {
		new MockUp<LimiteMinimoEstoqueDAO>() {

			@Mock
			public void associarLimiteMinimoItemEstoque(Integer idLimiteMinimo, List<Integer> listaIdItemEstoque) {
				List<ItemEstoque> listaItem = REPOSITORY.pesquisarTodos(ItemEstoque.class);
				LimiteMinimoEstoque limite = REPOSITORY.pesquisarEntidadeById(LimiteMinimoEstoque.class, idLimiteMinimo);
				if (limite == null) {
					return;
				}

				for (ItemEstoque i : listaItem) {
					i.setLimiteMinimoEstoque(limite);
				}
			}

			@Mock
			public List<Integer> pesquisarIdItemEstoqueDentroLimiteMinimo(LimiteMinimoEstoque limite, double tolerancia) {
				List<ItemEstoque> listaItem = REPOSITORY.pesquisarTodos(ItemEstoque.class);
				List<Integer> listaId = new ArrayList<Integer>();
				for (ItemEstoque i : listaItem) {
					Double medidaExterna = limite.getMedidaExterna();
					Double medidaInterna = limite.getMedidaInterna();
					Double comprimento = limite.getComprimento();
					final boolean contemMedida = medidaExterna != null || medidaInterna != null || comprimento != null;

					if (contemMedida) {
						if (medidaExterna != null) {
							medidaExterna -= i.getMedidaExterna() == null ? 0 : i.getMedidaExterna();
							if (Math.abs(medidaExterna) > tolerancia) {
								continue;
							}
						}

						if (medidaInterna != null) {
							medidaInterna -= i.getMedidaInterna() == null ? 0 : i.getMedidaInterna();
							if (Math.abs(medidaInterna) > tolerancia) {
								continue;
							}
						}

						if (comprimento != null) {
							comprimento -= i.getComprimento() == null ? 0 : i.getComprimento();
							if (Math.abs(comprimento) > tolerancia) {
								continue;
							}
						}
						listaId.add(i.getId());
					}
				}
				return listaId;
			}

			@Mock
			public Integer pesquisarIdLimiteMinimoEstoque(LimiteMinimoEstoque filtro) {
				List<LimiteMinimoEstoque> listalimite = REPOSITORY.pesquisarTodos(LimiteMinimoEstoque.class);
				boolean contemMedida = false;
				for (LimiteMinimoEstoque limite : listalimite) {
					if (!limite.getFormaMaterial().equals(filtro.getFormaMaterial())) {
						continue;
					}

					if (limite.getMaterial().getId().equals(filtro.getMaterial().getId())) {
						continue;
					}

					contemMedida = limite.getMedidaExterna() != null && filtro.getMedidaExterna() != null;
					if (contemMedida && limite.getMedidaExterna().equals(filtro.getMedidaExterna())) {
						continue;
					}

					contemMedida = limite.getMedidaInterna() != null && filtro.getMedidaInterna() != null;
					if (contemMedida && limite.getMedidaInterna().equals(filtro.getMedidaInterna())) {
						continue;
					}

					contemMedida = limite.getComprimento() != null && filtro.getComprimento() != null;
					if (contemMedida && limite.getComprimento().equals(filtro.getComprimento())) {
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
