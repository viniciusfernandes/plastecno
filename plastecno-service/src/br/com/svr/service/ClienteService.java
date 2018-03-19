package br.com.svr.service;

import java.util.Date;
import java.util.List;

import javax.ejb.Local;

import br.com.svr.service.entity.Cliente;
import br.com.svr.service.entity.ComentarioCliente;
import br.com.svr.service.entity.ContatoCliente;
import br.com.svr.service.entity.LogradouroCliente;
import br.com.svr.service.entity.Transportadora;
import br.com.svr.service.exception.BusinessException;
import br.com.svr.service.wrapper.PaginacaoWrapper;

@Local
public interface ClienteService {

	Cliente alterarRevendedor(Cliente cliente) throws BusinessException;

	Integer contactarCliente(Integer id);

	Date gerarDataInatividadeCliente() throws BusinessException;

	Cliente inserir(Cliente cliente) throws BusinessException;

	void inserirComentario(Integer idProprietario, Integer idCliente, String comentario) throws BusinessException;

	void inserirComentario(Integer idCliente, String comentario, Integer idUsuario) throws BusinessException;

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

	List<Cliente> pesquisarByNomeFantasia(String nomeFantasia);

	List<Cliente> pesquisarClienteByIdRamoAtividade(Integer idRamoAtividade);

	List<Cliente> pesquisarClienteByIdRegiao(Integer idRegiao) throws BusinessException;

	List<Cliente> pesquisarClienteCompradorByIdVendedor(Integer idVendedor, boolean inativos) throws BusinessException;

	List<Cliente> pesquisarClienteContatoByIdVendedor(Integer idVendedor);

	Cliente pesquisarClienteEContatoById(Integer idCliente);

	Cliente pesquisarClienteResumidoByCnpj(String cnpj);

	Cliente pesquisarClienteResumidoById(Integer idCliente);

	Cliente pesquisarClienteResumidoLogradouroById(Integer idCliente);

	List<Cliente> pesquisarClientesAssociados(Integer idVendedor);

	List<Cliente> pesquisarClientesById(List<Integer> listaIdCliente);

	List<Cliente> pesquisarClientesDesassociados();

	List<ComentarioCliente> pesquisarComentarioByIdCliente(Integer idCliente);

	List<ContatoCliente> pesquisarContato(Integer idCliente);

	ContatoCliente pesquisarContatoByIdContato(Integer idContato);

	ContatoCliente pesquisarContatoPrincipalResumidoByIdCliente(Integer idCliente);

	List<ContatoCliente> pesquisarContatoResumidoByNomeFantasia(Integer idCliente, String nome);

	Integer pesquisarIdVendedorByIdCliente(Integer idCliente);

	List<LogradouroCliente> pesquisarLogradouroCliente(Integer idCliente);

	LogradouroCliente pesquisarLogradouroClienteById(Integer idLogradouro);

	LogradouroCliente pesquisarLogradouroFaturamentoById(Integer idCliente);

	String pesquisarNomeFantasia(Integer idCliente);

	Cliente pesquisarNomeRevendedor();

	Long pesquisarTotalRegistros(Cliente filtro);

	List<Transportadora> pesquisarTransportadorasDesassociadas(Integer idCliente);

	List<Transportadora> pesquisarTransportadorasRedespacho(Integer idCliente);

	void removerLogradouro(Integer idLogradouro);

	void validarListaLogradouroPreenchida(Cliente cliente) throws BusinessException;
}
