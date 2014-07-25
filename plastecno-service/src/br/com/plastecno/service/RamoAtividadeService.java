package br.com.plastecno.service;

import java.util.List;

import javax.ejb.Local;

import br.com.plastecno.service.entity.RamoAtividade;
import br.com.plastecno.service.exception.BusinessException;
import br.com.plastecno.service.wrapper.PaginacaoWrapper;

@Local
public interface RamoAtividadeService {
    RamoAtividade inserir(RamoAtividade ramoAtividade) throws BusinessException;

    boolean isSiglaExistente(String sigla);

    List<RamoAtividade> pesquisar();

    List<RamoAtividade> pesquisarBy(RamoAtividade filtro);

    RamoAtividade pesquisarById(Integer id);

    void remover(RamoAtividade ramoAtividade);

    List<RamoAtividade> pesquisarBy(RamoAtividade filtro, Boolean apenasAtivos, Integer indiceRegistroInicial,
            Integer numeroMaximoRegistros);

    void desativar(Integer id);

    Long pesquisarTotalRegistros(RamoAtividade filtro, Boolean apenasRamoAtividadeAtivo);

    List<RamoAtividade> pesquisar(Boolean ativo);

    List<RamoAtividade> pesquisarAtivo();

    PaginacaoWrapper<RamoAtividade> paginarRamoAtividade(RamoAtividade filtro, Boolean apenasAtivos,
            Integer indiceRegistroInicial, Integer numeroMaximoRegistros);

    boolean isSiglaExistente(Integer id, String sigla);
}
