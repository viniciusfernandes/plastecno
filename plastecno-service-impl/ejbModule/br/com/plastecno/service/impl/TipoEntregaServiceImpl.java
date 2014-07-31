package br.com.plastecno.service.impl;

import java.util.Arrays;
import java.util.List;

import javax.ejb.Stateless;

import br.com.plastecno.service.TipoEntregaService;
import br.com.plastecno.service.constante.TipoEntrega;

@Stateless
public class TipoEntregaServiceImpl implements TipoEntregaService {

	@Override
	public List<TipoEntrega> pesquisar() {
		return Arrays.asList(TipoEntrega.values());
	}
}
