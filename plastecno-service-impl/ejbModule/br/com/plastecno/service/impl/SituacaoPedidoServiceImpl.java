package br.com.plastecno.service.impl;

import java.util.Arrays;
import java.util.List;

import javax.ejb.Stateless;

import br.com.plastecno.service.SituacaoPedidoService;
import br.com.plastecno.service.constante.SituacaoPedido;

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
