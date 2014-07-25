package br.com.plastecno.service;

import java.util.List;

import javax.ejb.Local;

import br.com.plastecno.service.constante.SituacaoPedido;

@Local
public interface SituacaoPedidoService {
    List<SituacaoPedido> pesquisar();

    SituacaoPedido pesquisarById(String id);
}
