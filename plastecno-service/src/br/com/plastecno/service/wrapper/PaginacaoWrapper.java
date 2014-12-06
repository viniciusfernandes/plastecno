package br.com.plastecno.service.wrapper;

import java.util.Collection;

public final class PaginacaoWrapper<T> extends ChaveValorWrapper<Long, Collection<T>>{

	public PaginacaoWrapper(Long chave, Collection<T> valor) {
		super(chave, valor);		
	}
	
	public Collection<T> getLista(){
		return this.valor;
	}
	
	public Long getTotalPaginado() {
		return this.chave;
	} 
}
