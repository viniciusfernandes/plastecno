package br.com.plastecno.service;

import java.util.List;

import javax.ejb.Local;

import br.com.plastecno.service.entity.ContatoTransportadora;
import br.com.plastecno.service.entity.Logradouro;
import br.com.plastecno.service.entity.Transportadora;
import br.com.plastecno.service.exception.BusinessException;
import br.com.plastecno.service.wrapper.PaginacaoWrapper;

@Local
public interface TransportadoraService {

    List<Transportadora> pesquisar();

    Transportadora pesquisarById(Integer id);

    Logradouro pesquisarLogradorouro(Integer id);

    List<ContatoTransportadora> pesquisarContato(Integer id);

    Integer desativar(Integer id);

    boolean isCNPJExistente(Integer idTransportadora, String cnpj);

    List<Transportadora> pesquisarBy(Transportadora filtro, Boolean apenasAtivos, Integer indiceRegistroInicial,
            Integer numeroMaximoRegistros);

    Long pesquisarTotalRegistros(Transportadora filtro, Boolean apenasAtivos);

    Integer inserir(Transportadora transportadora) throws BusinessException;

    List<Transportadora> pesquisarTransportadoraByIdCliente(Integer idCliente);

    PaginacaoWrapper<Transportadora> paginarTransportadora(Transportadora filtro, Boolean apenasAtivos,
            Integer indiceRegistroInicial, Integer numeroMaximoRegistros);

    boolean isNomeFantasiaExistente(Integer idTransportadora, String nomeFantasia);

    List<Transportadora> pesquisarByNomeFantasia(String nomeFantasia);

    List<Transportadora> pesquisarById(List<Integer> listaId);

}
