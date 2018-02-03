package br.com.svr.service.test.builder;

import java.util.ArrayList;
import java.util.List;

import mockit.Mock;
import mockit.MockUp;
import br.com.svr.service.dao.MaterialDAO;
import br.com.svr.service.entity.Material;
import br.com.svr.service.entity.Representada;

public class MaterialDAOBuilder extends DAOBuilder<MaterialDAO> {

	@Override
	public MaterialDAO build() {
		new MockUp<MaterialDAO>() {
			@Mock
			boolean isMaterialAssociadoRepresentada(Integer idMaterial, Integer idRepresentada) {
				Material m = pesquisarById(idMaterial);
				if (m == null) {
					return false;
				}
				for (Representada r : m.getListaRepresentada()) {
					if (idRepresentada != null && idRepresentada.equals(r.getId())) {
						return true;
					}
				}
				return false;
			}

			@Mock
			boolean isMaterialImportado(Integer idMaterial) {
				Material material = REPOSITORY.pesquisarEntidadeById(Material.class, idMaterial);
				return material != null ? material.isImportado() : false;
			}

			@Mock
			Material pesquisarById(Integer id) {
				return REPOSITORY.pesquisarEntidadeById(Material.class, id);
			}

			@Mock
			public List<Material> pesquisarBySigla(String sigla) {
				List<Material> l = REPOSITORY.pesquisarTodos(Material.class);
				List<Material> lista = new ArrayList<Material>();
				for (Material material : l) {
					if (material.getSigla().contains(sigla)) {
						lista.add(material);
					}
				}
				return lista;
			}
		};
		return new MaterialDAO(null);
	}
}
