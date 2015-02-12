package br.com.plastecno.service;

import java.util.List;

import javax.ejb.Local;

import br.com.plastecno.service.entity.Material;
import br.com.plastecno.service.entity.Representada;
import br.com.plastecno.service.exception.BusinessException;
import br.com.plastecno.service.wrapper.PaginacaoWrapper;

@Local
public interface MaterialService {
	void desativar(Integer id);

	Integer inserir(Material material) throws BusinessException;

	Integer inserir(Material material, List<Integer> listaIdRepresentadaAssociada) throws BusinessException;

	boolean isMaterialExistente(String sigla, Integer idMaterial);

	boolean isMaterialImportado(Integer idMaterial);

	PaginacaoWrapper<Material> paginarMaterial(Material filtro, Boolean apenasAtivos, Integer indiceRegistroInicial,
			Integer numeroMaximoRegistros);

	List<Material> pesquisarBy(Material filtro, Boolean apenasAtivos, Integer indiceRegistroInicial,
			Integer numeroMaximoRegistros);

	Material pesquisarById(Integer id);

	List<Material> pesquisarBySigla(String sigla);

	List<Material> pesquisarBySigla(String sigla, Integer idRepresentada);

	List<Representada> pesquisarRepresentadasAssociadas(Integer idMaterial);

	List<Representada> pesquisarRepresentadasNaoAssociadas(Integer idMaterial);

	Long pesquisarTotalRegistros(Material filtro, Boolean apenasAtivos);
}
