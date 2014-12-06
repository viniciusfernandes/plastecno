package br.com.plastecno.service;

import java.util.List;

import javax.ejb.Local;

import br.com.plastecno.service.entity.Regiao;
import br.com.plastecno.service.exception.BusinessException;
import br.com.plastecno.service.wrapper.PaginacaoWrapper;

@Local
public interface RegiaoService {

    Integer inserir(Regiao regiao, List<Integer> listaIdBairroAssociado) throws BusinessException;

    boolean isNomeRegiaoExistente(Integer idRegiao, String nomeRegiao);

    PaginacaoWrapper<Regiao> paginarRegiao(Regiao filtro, Integer indiceRegistroInicial, Integer numeroMaximoRegistros);

    List<Regiao> pesquisarBy(Regiao filtro);

    List<Regiao> pesquisarBy(Regiao filtro, Integer indiceRegistroInicial, Integer numeroMaximoRegistros);

    Regiao pesquisarById(Integer id);

    Long pesquisatTotalRegistros(Regiao filtro);

    void remover(Integer id);

}
