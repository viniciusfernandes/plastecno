package br.com.plastecno.service;

import java.util.List;

import javax.ejb.Local;

import br.com.plastecno.service.entity.PerfilAcesso;

@Local
public interface PerfilAcessoService {
    List<PerfilAcesso> pesquisar();

    PerfilAcesso pesquisarById(Integer id);

    List<PerfilAcesso> pesquisarById(List<Integer> listaIdPerfilAcesso);

    List<PerfilAcesso> pesquisarComplementaresById(List<Integer> listaIdPerfilAcesso);
}
