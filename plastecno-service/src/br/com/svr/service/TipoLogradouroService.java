package br.com.svr.service;

import java.util.List;

import javax.ejb.Local;

import br.com.svr.service.constante.TipoLogradouro;

@Local
public interface TipoLogradouroService {
    List<TipoLogradouro> pesquisar();

    TipoLogradouro pesquisarByDescricao(String descricao);

    TipoLogradouro pesquisarById(String id);
}
