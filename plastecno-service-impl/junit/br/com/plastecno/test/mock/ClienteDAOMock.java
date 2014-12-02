package br.com.plastecno.test.mock;

import java.util.ArrayList;
import java.util.List;

import br.com.plastecno.service.dao.ClienteDAO;
import br.com.plastecno.service.entity.Endereco;
import br.com.plastecno.service.entity.LogradouroCliente;

public class ClienteDAOMock implements ClienteDAO {
	@Override
	public List<LogradouroCliente> pesquisarLogradouro(Integer idCliente) {
		Endereco endereco = new Endereco();
		LogradouroCliente logradouro = new LogradouroCliente(endereco);
		List<LogradouroCliente> lista = new ArrayList<LogradouroCliente>();
		lista.add(logradouro);
		return lista;
	}
}
