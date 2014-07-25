package br.com.plastecno.service;

import java.util.List;

import javax.ejb.Local;

import br.com.plastecno.service.entity.Regiao;
import br.com.plastecno.service.exception.BusinessException;
import br.com.plastecno.service.wrapper.PaginacaoWrapper;

@Local
public interface RegiaoService {

    List<Regiao> pesquisarBy(Regiao filtro);

    Regiao pesquisarById(Integer id);

    Integer inserir(Regiao regiao, List<Integer> listaIdBairroAssociado) throws BusinessException;

    void remover(Integer id);

    boolean isNomeRegiaoExistente(Integer idRegiao, String nomeRegiao);

    Long pesquisatTotalRegistros(Regiao filtro);

    PaginacaoWrapper<Regiao> paginarRegiao(Regiao filtro, Integer indiceRegistroInicial, Integer numeroMaximoRegistros);

    List<Regiao> pesquisarBy(Regiao filtro, Integer indiceRegistroInicial, Integer numeroMaximoRegistros);

}
