package br.com.svr.service.impl;

import java.util.Arrays;
import java.util.List;

import javax.ejb.Stateless;

import br.com.svr.service.SituacaoPedidoService;
import br.com.svr.service.constante.SituacaoPedido;

@Stateless
public class SituacaoPedidoServiceImpl implements SituacaoPedidoService {

	@Override
	public List<SituacaoPedido> pesquisar() {
		return Arrays.asList(SituacaoPedido.values());
	}

	@Override
	public SituacaoPedido pesquisarById(String id) {
		return SituacaoPedido.valueOf(id);
	}

}
