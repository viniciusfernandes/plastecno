package br.com.plastecno.service.dao;

import java.util.List;

import br.com.plastecno.service.entity.LogradouroCliente;

public interface ClienteDAO {

	List<LogradouroCliente> pesquisarLogradouro(Integer idCliente);
}
