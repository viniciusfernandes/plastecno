package br.com.plastecno.service.dao;

public interface GenericDAO {
	void alterar(Object entidade);

	void inserir(Object entidade);

	<T> boolean isEntidadeExistente(Class<T> classe, Object idEntidade, String nomeAtributo, Object valorAtributo);

	<T> boolean isEntidadeExistente(Class<T> classe, String nomeAtributo, Object valorAtributo);

	<T> boolean isEntidadeExistente(Class<T> classe, String nomeAtributo, Object valorAtributo, Object nomeIdEntidade,
			Object valorIdEntidade);
}
