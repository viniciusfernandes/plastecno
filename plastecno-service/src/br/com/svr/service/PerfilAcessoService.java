package br.com.svr.service;

import java.util.List;

import javax.ejb.Local;

import br.com.svr.service.entity.PerfilAcesso;

@Local
public interface PerfilAcessoService {
    Integer inserir(PerfilAcesso perfilAcesso);

    List<PerfilAcesso> pesquisar();

    PerfilAcesso pesquisarById(Integer id);

    List<PerfilAcesso> pesquisarById(List<Integer> listaIdPerfilAcesso);

	List<PerfilAcesso> pesquisarComplementaresById(List<Integer> listaIdPerfilAcesso);
    
    
}
