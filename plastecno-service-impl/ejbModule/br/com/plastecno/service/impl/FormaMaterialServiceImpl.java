package br.com.plastecno.service.impl;

import java.util.Arrays;
import java.util.List;

import javax.ejb.Stateless;

import br.com.plastecno.service.FormaMaterialService;
import br.com.plastecno.service.constante.FormaMaterial;

@Stateless
public class FormaMaterialServiceImpl implements FormaMaterialService {

	@Override
	public List<FormaMaterial> pesquisar() {
		return Arrays.asList(FormaMaterial.values());
	}

}
