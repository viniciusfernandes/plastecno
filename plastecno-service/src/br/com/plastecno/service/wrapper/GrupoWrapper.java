package br.com.plastecno.service.wrapper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GrupoWrapper<T, K> extends ChaveValorWrapper<T, List<K>> {
	private Map<String, Object> properties;
	public GrupoWrapper(T id, List<K> listaValor) {
		super(id, listaValor);
	}

	public void addElemento(K elemento) {
		valor.add(elemento);
	}

	@Override
	public boolean equals(Object o) {
		return o instanceof GrupoWrapper && chave != null && chave.equals(((GrupoWrapper<?, ?>) o).chave);
	}
	public Object getId() {
		return chave;
	}

	public List<K> getListaElemento() {
		return valor;
	}

	public Object getPropriedade(String nome) {
		if (properties == null) {
			return null;
		}
		return properties.get(nome);
	}

	public int getTotalElemento() {
		return valor.size();
	}

	@Override
	public int hashCode() {
		return chave != null ? chave.hashCode() : super.hashCode();
	}

	public void setProperty(String nome, Object valor) {
		if (properties == null) {
			properties = new HashMap<String, Object>();
		}
		properties.put(nome, valor);
	}

}
