package br.com.svr.service;

import java.util.List;

import javax.ejb.Local;

import br.com.svr.service.constante.TipoEntrega;

@Local
public interface TipoEntregaService {
    List<TipoEntrega> pesquisar();
}
