package br.com.plastecno.service.wrapper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GrupoWrapper<T, K> {
	private T id;
	private List<K> listaElementos;
	private Map<String, Object> propriedades;

	public GrupoWrapper(T id, List<K> listaValor) {
		this.id = id;
	}

	public void addElemento(K elemento) {
		if (listaElementos == null) {
			listaElementos = new ArrayList<K>();
		}
		listaElementos.add(elemento);
	}

	@Override
	public boolean equals(Object o) {
		return o instanceof GrupoWrapper && id != null && id.equals(((GrupoWrapper<?, ?>) o).id);
	}

	public K getFirstElement() {
		return listaElementos.get(0);
	}

	// Aqui o tipo de retorno foi alterado para estar de acordo com o tipagem do
	// grupo, porem, originalmente estava retornando um Obejct e nao sabemos a
	// razao.
	public T getId() {
		return id;
	}

	public List<K> getListaElemento() {
		return listaElementos;
	}

	public Object getPropriedade(String nome) {
		if (propriedades == null) {
			return null;
		}
		return propriedades.get(nome);
	}

	public Map<String, Object> getPropriedades() {
		return propriedades;
	}

	public int getTotalElemento() {
		return listaElementos.size();
	}

	@Override
	public int hashCode() {
		return id != null ? id.hashCode() : super.hashCode();
	}

	public void setPropriedade(String nome, Object valor) {
		if (propriedades == null) {
			propriedades = new HashMap<String, Object>();
		}
		propriedades.put(nome, valor);
	}

}
