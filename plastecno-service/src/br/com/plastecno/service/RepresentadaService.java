package br.com.plastecno.service;

import java.util.List;

import javax.ejb.Local;

import br.com.plastecno.service.entity.ContatoRepresentada;
import br.com.plastecno.service.entity.Logradouro;
import br.com.plastecno.service.entity.Representada;
import br.com.plastecno.service.exception.BusinessException;

@Local
public interface RepresentadaService {
    Integer desativar(Integer id);

    Integer inserir(Representada representada) throws BusinessException;

    Boolean isCalculoIPIHabilitado(Integer idRepresentada);

    boolean isCNPJExistente(Integer id, String cnpj);

    boolean isNomeFantasiaExistente(Integer id, String nomeFantasia);

    List<Representada> pesquisar();

    List<Representada> pesquisar(Boolean ativo);

    List<Representada> pesquisarAtivo();

    List<Representada> pesquisarBy(Representada filtro, Boolean apenasAtivos, Integer indiceRegistroInicial,
            Integer numeroMaximoRegistros);

    Representada pesquisarById(Integer id);

    List<Representada> pesquisarById(List<Integer> listaIdRepresentada);

    List<ContatoRepresentada> pesquisarContato(Integer id);

    Logradouro pesquisarLogradorouro(Integer id);

    Long pesquisarTotalRegistros(Representada filtro, Boolean apenasAtivos);
}
