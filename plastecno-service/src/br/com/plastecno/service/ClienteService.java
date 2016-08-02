package br.com.plastecno.service;

import java.util.Collection;
import java.util.List;

import javax.ejb.Local;

import br.com.plastecno.service.entity.Cliente;
import br.com.plastecno.service.entity.ComentarioCliente;
import br.com.plastecno.service.entity.ContatoCliente;
import br.com.plastecno.service.entity.LogradouroCliente;
import br.com.plastecno.service.entity.Transportadora;
import br.com.plastecno.service.exception.BusinessException;
import br.com.plastecno.service.wrapper.PaginacaoWrapper;

@Local
public interface ClienteService {

	Cliente alterarRevendedor(Cliente cliente) throws BusinessException;

	Integer contactarCliente(Integer id);

	Cliente inserir(Cliente cliente) throws BusinessException;

	void inserirComentario(Integer idProprietario, Integer idCliente, String comentario) throws BusinessException;

	void inserirComentario(Integer idCliente, String comentario) throws BusinessException;

	boolean isCNPJExistente(Integer idCliente, String cnpj);

	boolean isCPFExistente(Integer idCliente, String cpf);

	boolean isEmailExistente(Integer idCliente, String email);

	boolean isInscricaoEstadualExistente(Integer idCliente, String inscricaoEstadual);

	boolean isNomeFantasiaExistente(Integer id, String nomeFantasia);

	PaginacaoWrapper<Cliente> paginarCliente(Cliente filtro, boolean carregarVendedor, Integer indiceRegistroInicial,
			Integer numeroMaximoRegistros);

	List<Cliente> pesquisarBy(Cliente filtro, boolean carregarVendedor, Integer indiceRegistroInicial,
			Integer numeroMaximoRegistros);

	List<Cliente> pesquisarBy(Cliente filtro, Integer indiceRegistroInicial, Integer numeroMaximoRegistros);

	Cliente pesquisarById(Integer id);

	List<Cliente> pesquisarByIdVendedor(Integer idVendedor);

	List<Cliente> pesquisarByIdVendedor(Integer idVendedor, boolean isPesquisaClienteInativo) throws BusinessException;

	List<Cliente> pesquisarByNomeFantasia(String nomeFantasia);

	List<Cliente> pesquisarByRamoAtividade(Integer idRamoAtividade);

	List<Cliente> pesquisarClienteByIdRegiao(Integer idRegiao) throws BusinessException;

	Cliente pesquisarClienteResumidoById(Integer idCliente);

	Cliente pesquisarClienteResumidoEContatoById(Integer idCliente);

	List<Cliente> pesquisarClientesAssociados(Integer idVendedor);

	List<Cliente> pesquisarClientesById(List<Integer> listaIdCliente);

	List<Cliente> pesquisarClientesDesassociados();

	List<ComentarioCliente> pesquisarComentarioByIdCliente(Integer idCliente);

	List<ContatoCliente> pesquisarContato(Integer idCliente);

	List<Cliente> pesquisarInativosByIdVendedor(Integer idVendedor) throws BusinessException;

	List<LogradouroCliente> pesquisarLogradouro(Integer idCliente);

	LogradouroCliente pesquisarLogradouroById(Integer idLogradouro);

	LogradouroCliente pesquisarLogradouroFaturamentoById(Integer idCliente);

	String pesquisarNomeFantasia(Integer idCliente);

	Cliente pesquisarRevendedor();

	Long pesquisarTotalRegistros(Cliente filtro);

	List<Transportadora> pesquisarTransportadorasDesassociadas(Integer idCliente);

	List<Transportadora> pesquisarTransportadorasRedespacho(Integer idCliente);

	void removerLogradouro(Integer idLogradouro);

	void removerLogradourosAusentes(Integer idCliente, Collection<LogradouroCliente> listaLogradouro);

	void validarListaLogradouroPreenchida(Cliente cliente) throws BusinessException;
}
