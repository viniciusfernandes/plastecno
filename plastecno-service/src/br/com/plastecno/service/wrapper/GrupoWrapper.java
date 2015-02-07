package br.com.plastecno.service.wrapper;

import java.util.List;

public class GrupoWrapper<T, K> extends ChaveValorWrapper<T, List<K>> {

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

	public int getTotalElemento() {
		return valor.size();
	}

	@Override
	public int hashCode() {
		return chave != null ? chave.hashCode() : super.hashCode();
	}

}
