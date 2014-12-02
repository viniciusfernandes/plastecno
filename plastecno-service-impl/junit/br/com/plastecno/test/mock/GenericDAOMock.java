package br.com.plastecno.test.mock;

import br.com.plastecno.service.dao.GenericDAO;

public class GenericDAOMock implements GenericDAO {

	@Override
	public void alterar(Object entidade) {

	}

	@Override
	public void inserir(Object entidade) {

	}

	@Override
	public <T> boolean isEntidadeExistente(Class<T> classe, Object idEntidade, String nomeAtributo, Object valorAtributo) {
		return false;
	}

	@Override
	public <T> boolean isEntidadeExistente(Class<T> classe, String nomeAtributo, Object valorAtributo) {
		return false;
	}

	@Override
	public <T> boolean isEntidadeExistente(Class<T> classe, String nomeAtributo, Object valorAtributo,
			Object nomeIdEntidade, Object valorIdEntidade) {
		return false;
	}

}
