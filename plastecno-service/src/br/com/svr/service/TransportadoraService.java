package br.com.svr.service;

import java.util.List;

import javax.ejb.Local;

import br.com.svr.service.entity.ContatoTransportadora;
import br.com.svr.service.entity.LogradouroTransportadora;
import br.com.svr.service.entity.Transportadora;
import br.com.svr.service.exception.BusinessException;
import br.com.svr.service.wrapper.PaginacaoWrapper;

@Local
public interface TransportadoraService {

    Integer desativar(Integer id);

    Integer inserir(Transportadora transportadora) throws BusinessException;

    boolean isCNPJExistente(Integer idTransportadora, String cnpj);

    boolean isNomeFantasiaExistente(Integer idTransportadora, String nomeFantasia);

    PaginacaoWrapper<Transportadora> paginarTransportadora(Transportadora filtro, Boolean apenasAtivos,
            Integer indiceRegistroInicial, Integer numeroMaximoRegistros);

    List<Transportadora> pesquisarTransportadoraAtiva();

    List<Transportadora> pesquisarBy(Transportadora filtro, Boolean apenasAtivos, Integer indiceRegistroInicial,
            Integer numeroMaximoRegistros);

    Transportadora pesquisarByCnpj(String cnpj);

    Transportadora pesquisarTransportadoraLogradouroById(Integer id);

    List<Transportadora> pesquisarById(List<Integer> listaId);

    List<Transportadora> pesquisarByNomeFantasia(String nomeFantasia);

    List<ContatoTransportadora> pesquisarContato(Integer id);

    LogradouroTransportadora pesquisarLogradorouro(Integer id);

    Long pesquisarTotalRegistros(Transportadora filtro, Boolean apenasAtivos);

	List<Transportadora> pesquisarTransportadoraByIdCliente(Integer idCliente);

}
