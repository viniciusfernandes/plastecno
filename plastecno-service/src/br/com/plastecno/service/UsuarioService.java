package br.com.plastecno.service;

import java.util.List;

import javax.ejb.Local;

import br.com.plastecno.service.entity.ContatoUsuario;
import br.com.plastecno.service.entity.Logradouro;
import br.com.plastecno.service.entity.PerfilAcesso;
import br.com.plastecno.service.entity.Remuneracao;
import br.com.plastecno.service.entity.Usuario;
import br.com.plastecno.service.exception.BusinessException;
import br.com.plastecno.service.wrapper.PaginacaoWrapper;

@Local
public interface UsuarioService {

    List<Usuario> pesquisarBy(Usuario filtro);

    Usuario pesquisarById(Integer id);

    Logradouro pesquisarLogradouro(Integer id);

    List<PerfilAcesso> pesquisarPerfisAssociados(Integer id);

    List<PerfilAcesso> pesquisarPerfisNaoAssociados(Integer id);

    List<ContatoUsuario> pesquisarContatos(Integer id);

    int desabilitar(Integer id) throws BusinessException;

    List<Remuneracao> pesquisarRemuneracaoById(Integer id);

    boolean isEmailExistente(Integer id, String email);

    List<Usuario> pesquisarVendedores(Usuario filtro, Boolean apenasAtivos, Integer indiceRegistroInicial,
            Integer numeroMaximoRegistros);

    Long pesquisarTotalRegistros(Usuario filtro, Boolean apenasAtivos, boolean isVendedor);

    List<Usuario> pesquisarBy(Usuario filtro, Boolean apenasAtivos, Integer indiceRegistroInicial,
            Integer numeroMaximoRegistros);

    Usuario pesquisarVendedorByIdCliente(Integer idCliente);

    void associarCliente(Integer idVendedor, List<Integer> listaIdClienteAssociado,
            List<Integer> listaIdClienteDesassociado) throws BusinessException;

    void desassociarCliente(Integer idVendedor, List<Integer> listaIdClienteDesassociado) throws BusinessException;

    void associarCliente(Integer idVendedor, List<Integer> listaIdClienteAssociado) throws BusinessException;

    boolean isVendedorAtivo(Integer idVendedor);

    List<Usuario> pesquisarByNome(String nome);

    Usuario pesquisarUsuarioResumidoById(Integer idUsuario);

    Integer inserir(Usuario usuario, boolean isAlteracaoSenha) throws BusinessException;

    Usuario pesquisarVendedorById(Integer idVendedor);

    boolean isClienteAssociadoVendedor(Integer idCliente, Integer idVendedor);

    PaginacaoWrapper<Usuario> paginarUsuario(Usuario filtro, boolean isVendedor, Boolean apenasAtivos,
            Integer indiceRegistroInicial, Integer numeroMaximoRegistros);

    PaginacaoWrapper<Usuario> paginarVendedor(Usuario filtro, Boolean apenasAtivos, Integer indiceRegistroInicial,
            Integer numeroMaximoRegistros);

    String pesquisarSenhaByEmail(String email);

    boolean isCPF(Integer id, String cpf);

    List<Usuario> pesquisarVendedorByNome(String nome);
}
