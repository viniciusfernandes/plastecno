package br.com.svr.service.impl;

import java.util.Arrays;
import java.util.List;

import javax.ejb.Stateless;

import br.com.svr.service.FormaMaterialService;
import br.com.svr.service.constante.FormaMaterial;

@Stateless
public class FormaMaterialServiceImpl implements FormaMaterialService {

	@Override
	public List<FormaMaterial> pesquisar() {
		return Arrays.asList(FormaMaterial.values());
	}

}
