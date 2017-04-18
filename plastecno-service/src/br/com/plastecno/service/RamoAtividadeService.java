package br.com.plastecno.service;

import java.util.List;

import javax.ejb.Local;

import br.com.plastecno.service.entity.RamoAtividade;
import br.com.plastecno.service.exception.BusinessException;
import br.com.plastecno.service.wrapper.PaginacaoWrapper;

@Local
public interface RamoAtividadeService {
    void desativar(Integer id);

    RamoAtividade inserir(RamoAtividade ramoAtividade) throws BusinessException;

    boolean isSiglaExistente(Integer id, String sigla);

    boolean isSiglaExistente(String sigla);

    PaginacaoWrapper<RamoAtividade> paginarRamoAtividade(RamoAtividade filtro, Boolean apenasAtivos,
            Integer indiceRegistroInicial, Integer numeroMaximoRegistros);

    List<RamoAtividade> pesquisar();

    List<RamoAtividade> pesquisar(Boolean ativo);

    List<RamoAtividade> pesquisarAtivo();

    List<RamoAtividade> pesquisarBy(RamoAtividade filtro);

    List<RamoAtividade> pesquisarBy(RamoAtividade filtro, Boolean apenasAtivos, Integer indiceRegistroInicial,
            Integer numeroMaximoRegistros);

    RamoAtividade pesquisarById(Integer id);

    RamoAtividade pesquisarRamoAtividadePadrao() throws BusinessException;

    String pesquisarSigleById(Integer idRamo);

	Long pesquisarTotalRegistros(RamoAtividade filtro, Boolean apenasRamoAtividadeAtivo);

	void remover(RamoAtividade ramoAtividade);
}
