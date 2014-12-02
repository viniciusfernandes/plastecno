package br.com.plastecno.test.mock;

import br.com.plastecno.service.dao.UsuarioDAO;

public class UsuarioDAOMock implements UsuarioDAO {

	@Override
	public boolean isClienteAssociadoVendedor(Integer idCliente, Integer idVendedor) {
		return true;
	}

}
