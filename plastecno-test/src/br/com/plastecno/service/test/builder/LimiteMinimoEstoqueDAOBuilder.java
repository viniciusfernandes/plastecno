package br.com.plastecno.service.test.builder;

import br.com.plastecno.service.dao.LimiteMinimoEstoqueDAO;

public class LimiteMinimoEstoqueDAOBuilder extends DAOBuilder<LimiteMinimoEstoqueDAO> {

	@Override
	public LimiteMinimoEstoqueDAO build() {
		return new LimiteMinimoEstoqueDAO(null);
	}

}
