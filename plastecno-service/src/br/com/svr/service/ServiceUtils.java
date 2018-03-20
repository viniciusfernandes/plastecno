package br.com.svr.service;

public interface ServiceUtils {

	<T> T recarregarEntidade(Class<T> classe, Integer id);

}
