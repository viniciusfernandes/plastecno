package br.com.svr.service.test.builder;

import java.util.ArrayList;
import java.util.List;

import mockit.Mock;
import mockit.MockUp;
import br.com.svr.service.constante.TipoCliente;
import br.com.svr.service.dao.ClienteDAO;
import br.com.svr.service.entity.Cliente;
import br.com.svr.service.entity.LogradouroCliente;

public class ClienteDAOBuilder extends DAOBuilder<ClienteDAO> {

	@Override
	public ClienteDAO build() {
		new MockUp<ClienteDAO>() {
			@Mock
			public boolean isEmailExistente(Integer idCliente, String email) {
				return REPOSITORY.contemEntidade(Cliente.class, "email", email, idCliente);
			}

			@Mock
			public List<LogradouroCliente> pesquisarLogradouroById(Integer idCliente) {
				Cliente cliente = REPOSITORY.pesquisarEntidadeById(Cliente.class, idCliente);
				return cliente == null ? new ArrayList<LogradouroCliente>() : cliente.getListaLogradouro();
			};

			@Mock
			Cliente pesquisarRevendedor() {
				List<Cliente> l = REPOSITORY.pesquisarEntidadeByRelacionamento(Cliente.class, "tipoCliente",
						TipoCliente.REVENDEDOR);
				return !l.isEmpty() ? l.get(0) : null;
			}
		};

		return new ClienteDAO(null);
	}

}
