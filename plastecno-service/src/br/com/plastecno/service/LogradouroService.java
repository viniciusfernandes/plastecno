package br.com.plastecno.service;

import java.util.Collection;
import java.util.List;

import javax.ejb.Local;

import br.com.plastecno.service.entity.Logradouro;
import br.com.plastecno.service.entity.LogradouroEndereco;
import br.com.plastecno.service.exception.BusinessException;

@Local
public interface LogradouroService {
	void importarLogradouro();

	<T extends Logradouro> T inserir(T logradouro) throws BusinessException;

	void inserirEnderecoBaseCEP(List<? extends Logradouro> lLogradouro) throws BusinessException;

	void inserirEnderecoBaseCEP(Logradouro logradouro) throws BusinessException;

	List<? extends LogradouroEndereco> pesquisar(Integer id, Class<? extends LogradouroEndereco> classe);

	<T extends LogradouroEndereco> List<T> pesquisarAusentes(Integer id, Collection<T> listaLogradouro, Class<T> classe);

	String pesquisarCodigoIBGEByCEP(String cep);

	String pesquisarCodigoMunicipioByCep(String cep);

	/**
	 * Metodo que remove todos os logradouros cadastrados no sistema que nao
	 * esteja presentes na lista passada como parametro. Os logradouros que
	 * serao removidos sao os que esta de acordo com o tipo parametro "classe",
	 * por exemplo: LogradouroCliente, LogradouroRepresentada,
	 * LogradouroUsuario, etc.
	 * 
	 * @param id
	 *            codigo da classe que o logradouro esta associado, por exemplo:
	 *            id do cliente, id do usuario, id da transportadora, etc.
	 * @param listaLogradouro
	 *            logradouros que NAO SERAO removidos, qualquer logradouro que
	 *            nao esteja na lista sera removido
	 * @param classe
	 *            tipo de logradouro que sera removido
	 */
	<T extends LogradouroEndereco> void removerAusentes(Integer id, Collection<T> listaLogradouro, Class<T> classe);

	void removerLogradouro(Logradouro logradouro);

	void validarListaLogradouroPreenchida(List<? extends Logradouro> listaLogradouro) throws BusinessException;

}