package br.com.plastecno.service.wrapper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class RelatorioWrapper<T, K> {
	private final List<GrupoWrapper<T, K>> listaGrupo = new ArrayList<GrupoWrapper<T, K>>();
	private final HashMap<Object, GrupoWrapper<T, K>> mapaGrupo = new HashMap<Object, GrupoWrapper<T, K>>();
	private final String titulo;
	private Object valorTotal;

	public RelatorioWrapper(String titulo) {
		this.titulo = titulo;
	}

	public void addElemento(T idGrupo, K elemento) {
		GrupoWrapper<T, K> grupo = mapaGrupo.get(idGrupo);
		if (grupo != null) {
			grupo.addElemento(elemento);
			return;
		}

		grupo = new GrupoWrapper<T, K>(idGrupo, new ArrayList<K>());
		grupo.addElemento(elemento);
		listaGrupo.add(grupo);
		mapaGrupo.put(idGrupo, grupo);
	}

	public List<GrupoWrapper<T, K>> getListaGrupo() {
		return listaGrupo;
	}

	public String getTitulo() {
		return titulo;
	}

	public Object getValorTotal() {
		return valorTotal;
	}

	public void setValorTotal(Object valorTotal) {
		this.valorTotal = valorTotal;
	}

}
