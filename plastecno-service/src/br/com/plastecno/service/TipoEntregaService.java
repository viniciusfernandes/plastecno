package br.com.plastecno.service;

import java.util.List;

import javax.ejb.Local;

import br.com.plastecno.service.constante.TipoEntrega;

@Local
public interface TipoEntregaService {
    List<TipoEntrega> pesquisar();
}
