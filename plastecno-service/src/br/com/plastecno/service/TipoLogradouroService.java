package br.com.plastecno.service;

import java.util.List;

import javax.ejb.Local;

import br.com.plastecno.service.constante.TipoLogradouro;

@Local
public interface TipoLogradouroService {
    List<TipoLogradouro> pesquisar();

    TipoLogradouro pesquisarByDescricao(String descricao);

    TipoLogradouro pesquisarById(String id);
}
