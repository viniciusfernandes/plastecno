package br.com.plastecno.service;

import java.util.List;

import javax.ejb.Local;

import br.com.plastecno.service.entity.ContatoRepresentada;
import br.com.plastecno.service.entity.Logradouro;
import br.com.plastecno.service.entity.Representada;
import br.com.plastecno.service.exception.BusinessException;

@Local
public interface RepresentadaService {
    List<ContatoRepresentada> pesquisarContato(Integer id);

    Representada pesquisarById(Integer id);

    Logradouro pesquisarLogradorouro(Integer id);

    Integer desativar(Integer id);

    List<Representada> pesquisar();

    boolean isCNPJExistente(Integer id, String cnpj);

    List<Representada> pesquisarBy(Representada filtro, Boolean apenasAtivos, Integer indiceRegistroInicial,
            Integer numeroMaximoRegistros);

    Long pesquisarTotalRegistros(Representada filtro, Boolean apenasAtivos);

    List<Representada> pesquisar(Boolean ativo);

    List<Representada> pesquisarAtivo();

    Integer inserir(Representada representada) throws BusinessException;

    boolean isNomeFantasiaExistente(Integer id, String nomeFantasia);

    Boolean isCalculoIPIHabilitado(Integer idRepresentada);

    List<Representada> pesquisarById(List<Integer> listaIdRepresentada);
}
