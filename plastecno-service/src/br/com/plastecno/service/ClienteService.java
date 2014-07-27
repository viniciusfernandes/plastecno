package br.com.plastecno.service;

import java.util.Collection;
import java.util.List;

import javax.ejb.Local;

import br.com.plastecno.service.entity.Cliente;
import br.com.plastecno.service.entity.ContatoCliente;
import br.com.plastecno.service.entity.LogradouroCliente;
import br.com.plastecno.service.entity.ObservacaoCliente;
import br.com.plastecno.service.entity.Transportadora;
import br.com.plastecno.service.exception.BusinessException;
import br.com.plastecno.service.wrapper.PaginacaoWrapper;

@Local
public interface ClienteService {

    Cliente inserir(Cliente cliente) throws BusinessException;

    Integer contactarCliente(Integer id);

    Cliente pesquisarById(Integer id);
    
    List<LogradouroCliente> pesquisarLogradouro(Integer idCliente);

    List<Transportadora> pesquisarTransportadorasAssociadas(Integer idCliente);

    List<Transportadora> pesquisarTransportadorasDesassociadas(Integer idCliente);

    boolean isClienteAtivo(Integer idCliente);

    List<ContatoCliente> pesquisarContato(Integer idCliente);

    List<Cliente> pesquisarBy(Cliente filtro, Integer indiceRegistroInicial, Integer numeroMaximoRegistros);

    Long pesquisarTotalRegistros(Cliente filtro);

    boolean isEmailExistente(Integer idCliente, String email);

    boolean isCPFExistente(Integer idCliente, String cpf);

    List<Cliente> pesquisarByNomeFantasia(String nomeFantasia);

    boolean isInscricaoEstadualExistente(Integer idCliente, String inscricaoEstadual);

    boolean isCNPJExistente(Integer idCliente, String cnpj);

    List<Cliente> pesquisarBy(Cliente filtro, boolean carregarVendedor, Integer indiceRegistroInicial,
            Integer numeroMaximoRegistros);

    List<Cliente> pesquisarClientesById(List<Integer> listaIdCliente);

    List<Cliente> pesquisarClientesAssociados(Integer idVendedor);

    List<Cliente> pesquisarClientesDesassociados();

    List<Cliente> pesquisarByIdVendedor(Integer idVendedor);

    List<Cliente> pesquisarInativosByIdVendedor(Integer idVendedor) throws BusinessException;

    List<Cliente> pesquisarByIdVendedor(Integer idVendedor, boolean isPesquisaClienteInativo) throws BusinessException;

    List<Cliente> pesquisarClienteByIdRegiao(Integer idRegiao) throws BusinessException;

    boolean isClienteProspectado(Integer idCliente);

    void removerLogradourosAusentes(Integer idCliente, Collection<LogradouroCliente> listaLogradouro);

    PaginacaoWrapper<Cliente> paginarCliente(Cliente filtro, boolean carregarVendedor, Integer indiceRegistroInicial,
            Integer numeroMaximoRegistros);

    boolean isNomeFantasiaExistente(Integer id, String nomeFantasia);

    LogradouroCliente pesquisarLogradouroById(Integer idLogradouro);

    void removerLogradouro(Integer idLogradouro);

    List<Cliente> pesquisarByRamoAtividade(Integer idRamoAtividade);

    List<ObservacaoCliente> pesquisarObservacaoCliente(Integer idCliente);
}
