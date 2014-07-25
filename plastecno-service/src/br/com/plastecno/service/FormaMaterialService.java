package br.com.plastecno.service;

import java.util.List;

import javax.ejb.Local;

import br.com.plastecno.service.constante.FormaMaterial;

@Local
public interface FormaMaterialService {
    List<FormaMaterial> pesquisar();
}
