package br.com.plastecno.service;

import java.util.List;

import javax.ejb.Local;

import br.com.plastecno.service.entity.ContatoUsuario;
import br.com.plastecno.service.entity.LogradouroUsuario;
import br.com.plastecno.service.entity.PerfilAcesso;
import br.com.plastecno.service.entity.Usuario;
import br.com.plastecno.service.exception.BusinessException;
import br.com.plastecno.service.wrapper.PaginacaoWrapper;

@Local
public interface UsuarioService {

	void associarCliente(Integer idVendedor, List<Integer> listaIdClienteAssociado) throws BusinessException;

	void associarCliente(Integer idVendedor, List<Integer> listaIdClienteAssociado,
			List<Integer> listaIdClienteDesassociado) throws BusinessException;

	int desabilitar(Integer id) throws BusinessException;

	void desassociarCliente(Integer idVendedor, List<Integer> listaIdClienteDesassociado) throws BusinessException;

	Integer inserir(Usuario usuario, boolean isAlteracaoSenha) throws BusinessException;

	boolean isAdministrador(Integer idUsuario);

	boolean isClienteAssociadoVendedor(Integer idCliente, Integer idVendedor);

	boolean isCPF(Integer id, String cpf);

	boolean isEmailExistente(Integer id, String email);

	boolean isVendaPermitida(Integer idCliente, Integer idVendedor);

	boolean isVendedorAtivo(Integer idVendedor);

	PaginacaoWrapper<Usuario> paginarUsuario(Usuario filtro, boolean isVendedor, Boolean apenasAtivos,
			Integer indiceRegistroInicial, Integer numeroMaximoRegistros);

	PaginacaoWrapper<Usuario> paginarVendedor(Usuario filtro, Boolean apenasAtivos, Integer indiceRegistroInicial,
			Integer numeroMaximoRegistros);

	List<Usuario> pesquisarBy(Usuario filtro);

	List<Usuario> pesquisarBy(Usuario filtro, Boolean apenasAtivos, Integer indiceRegistroInicial,
			Integer numeroMaximoRegistros);

	Usuario pesquisarById(Integer id);

	List<Usuario> pesquisarByNome(String nome);

	List<ContatoUsuario> pesquisarContatos(Integer id);

	LogradouroUsuario pesquisarLogradouro(Integer id);

	List<PerfilAcesso> pesquisarPerfisAssociados(Integer id);

	List<PerfilAcesso> pesquisarPerfisNaoAssociados(Integer id);

	String pesquisarSenhaByEmail(String email);

	Long pesquisarTotalRegistros(Usuario filtro, Boolean apenasAtivos, boolean isVendedor);

	Usuario pesquisarUsuarioResumidoById(Integer idUsuario);

	Usuario pesquisarVendedorById(Integer idVendedor);

	Usuario pesquisarVendedorByIdCliente(Integer idCliente);

	List<Usuario> pesquisarVendedorByNome(String nome);

	List<Usuario> pesquisarVendedores(Usuario filtro, Boolean apenasAtivos, Integer indiceRegistroInicial,
			Integer numeroMaximoRegistros);
}
