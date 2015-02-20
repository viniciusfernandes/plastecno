package br.com.plastecno.service.wrapper;

import java.util.ArrayList;
import java.util.List;

public class RelatorioWrapper<T, K> {
	private final String titulo;
	private final List<GrupoWrapper<T, K>> listaGrupo = new ArrayList<GrupoWrapper<T, K>>();

	public RelatorioWrapper(String titulo) {
		this.titulo = titulo;
	}

	public void addElemento(T idGrupo, K elemento) {

		for (GrupoWrapper<T, K> grupo : listaGrupo) {
			if (idGrupo != null && idGrupo.equals(grupo.getId())) {
				grupo.addElemento(elemento);
				return;
			}
		}

		GrupoWrapper<T, K> grupo = new GrupoWrapper<T, K>(idGrupo, new ArrayList<K>());
		grupo.addElemento(elemento);
		listaGrupo.add(grupo);
	}

	public List<GrupoWrapper<T, K>> getListaGrupo() {
		return listaGrupo;
	}

	public String getTitulo() {
		return titulo;
	}

}
