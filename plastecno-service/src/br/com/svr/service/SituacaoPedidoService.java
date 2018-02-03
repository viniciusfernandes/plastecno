package br.com.svr.service;

import java.util.List;

import javax.ejb.Local;

import br.com.svr.service.constante.SituacaoPedido;

@Local
public interface SituacaoPedidoService {
    List<SituacaoPedido> pesquisar();

    SituacaoPedido pesquisarById(String id);
}
