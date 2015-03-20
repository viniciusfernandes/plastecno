package br.com.plastecno.service;

import java.util.Collection;
import java.util.List;

import javax.ejb.Local;

import br.com.plastecno.service.entity.Logradouro;
import br.com.plastecno.service.exception.BusinessException;

@Local
public interface LogradouroService {
    <T extends Logradouro> List<T> inserir(List<T> listaLogradouro) throws BusinessException;

    /**
     * Metodo criado para recuperaros ids do pais, estado, cidade e bairro e no
     * caso em que nao existam ele cria um novo registro no sistema.
     * @param logradouro
     *            logradouro contendo os ids do pais, estado, cidade e bairro
     * @return
     * @throws BusinessException
     */
    <T extends Logradouro> T inserir(T logradouro) throws BusinessException;

    List<? extends Logradouro> pesquisar(Integer id, Class<? extends Logradouro> classe);

    <T extends Logradouro> List<T> pesquisarAusentes(Integer id, Collection<T> listaLogradouro, Class<T> classe);

    <T extends Logradouro> T pesquisarById(Integer idLogradouro, Class<T> classe);

    /**
     * Metodo que remove todos os logradouros cadastrados no sistema que nao
     * esteja presentes na lista passada como parametro. Os logradouros que
     * serao removidos sao os que esta de acordo com o tipo parametro "classe",
     * por exemplo: LogradouroCliente, LogradouroRepresentada,
     * LogradouroUsuario, etc.
     * @param id
     *            codigo da classe que o logradouro esta associado, por exemplo:
     *            id do cliente, id do usuario, id da transportadora, etc.
     * @param listaLogradouro
     *            logradouros que NAO SERAO removidos, qualquer logradouro que
     *            nao esteja na lista sera removido
     * @param classe
     *            tipo de logradouro que sera removido
     */
    <T extends Logradouro> void removerAusentes(Integer id, Collection<T> listaLogradouro, Class<T> classe);

    void validarListaLogradouroPreenchida(Collection<? extends Logradouro> listaLogradouro) throws BusinessException;

}
