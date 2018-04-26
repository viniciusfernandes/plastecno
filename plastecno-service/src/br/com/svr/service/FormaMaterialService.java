package br.com.svr.service;

import java.util.List;

import javax.ejb.Local;

import br.com.svr.service.constante.FormaMaterial;

@Local
public interface FormaMaterialService {
    List<FormaMaterial> pesquisar();
}
