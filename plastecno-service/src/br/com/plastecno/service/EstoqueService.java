package br.com.plastecno.service;

import java.util.List;

import javax.ejb.Local;

import br.com.plastecno.service.constante.FormaMaterial;
import br.com.plastecno.service.entity.ItemEstoque;
import br.com.plastecno.service.entity.Material;

@Local
public interface EstoqueService {
	void inserirItemEstoque(Integer idItemPedido);

	List<ItemEstoque> pesquisarItemEstoque(Integer idMaterial, FormaMaterial formaMaterial);

	ItemEstoque pesquisarItemEstoque(Integer idMaterial, FormaMaterial formaMaterial, Double medidaExterna,
			Double medidaInterna, Double comprimento);

	List<Material> pesquisarMateriaEstoque(String sigla);

	ItemEstoque pesquisarItemEstoqueById(Integer idItemEstoque);
}
