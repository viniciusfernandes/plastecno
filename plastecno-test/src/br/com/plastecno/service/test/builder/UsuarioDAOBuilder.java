package br.com.plastecno.service.test.builder;

import java.util.ArrayList;
import java.util.List;

import mockit.Mock;
import mockit.MockUp;
import br.com.plastecno.service.dao.UsuarioDAO;
import br.com.plastecno.service.entity.Cliente;
import br.com.plastecno.service.entity.PerfilAcesso;
import br.com.plastecno.service.entity.Usuario;

public class UsuarioDAOBuilder extends DAOBuilder<UsuarioDAO> {

	@Override
	public UsuarioDAO build() {
		new MockUp<UsuarioDAO>() {
			@Mock
			public Usuario pesquisarByEmailSenha(String email, String senha) {
				List<Usuario> lista = REPOSITORY.pesquisarEntidadeByRelacionamento(Usuario.class, "email", email);
				for (Usuario u : lista) {
					if (u != null && u.getEmail() != null && u.getSenha().equals(senha)) {
						return u;
					}
				}
				return null;
			}

			@Mock
			public Integer pesquisarIdVendedorByIdCliente(Integer idCliente, Integer idVendedor) {
				return idVendedor;
			}

			@Mock
			public List<PerfilAcesso> pesquisarPerfisAssociados(Integer id) {
				return new ArrayList<PerfilAcesso>();
			}

			@Mock
			public String pesquisarSenha(Integer idUsuario) {
				return REPOSITORY.pesquisarEntidadeAtributoById(Usuario.class, idUsuario, "senha", String.class);
			}

			@Mock
			Usuario pesquisarUsuarioResumidoById(Integer idUsuario) {
				return REPOSITORY.pesquisarEntidadeById(Usuario.class, idUsuario);
			}

			@Mock
			public boolean pesquisarVendedorAtivo(Integer idVendedor) {
				List<Usuario> listaUsuario = REPOSITORY.pesquisarTodos(Usuario.class);
				for (Usuario usuario : listaUsuario) {
					if (usuario.isVendedor()) {
						return usuario.isAtivo();
					}
				}
				return false;
			}

			@Mock
			public Usuario pesquisarVendedorByIdCliente(Integer idCliente) {
				Cliente c = REPOSITORY.pesquisarEntidadeById(Cliente.class, idCliente);
				return c != null ? c.getVendedor() : null;
			}

		};

		return new UsuarioDAO(null);
	}

}
